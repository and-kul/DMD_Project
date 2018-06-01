import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Downloader implements Runnable {
    final int N_BUF = 10;
    final int BUF_SIZE = 200;
    final int N_THREADS = 10;
    final int PORTION_SIZE = BUF_SIZE / N_THREADS;

    ArrayList<String> idList;
    Data[][] ar = new Data[N_BUF][BUF_SIZE];
    Semaphore[][] isReadable = new Semaphore[N_BUF][N_THREADS];
    Semaphore[] isWritable = new Semaphore[N_BUF];
    DBThread[][] dbThreads = new DBThread[N_BUF][N_THREADS];
    int packets;
    int cur_ar;
    Thread t;

    long running = 0;
    long runningCount = 0;
    long downtime = 0;
    long downtimeCount = 0;

    Downloader(ArrayList<String> idList) {
        this.idList = idList;
        packets = (idList.size() + BUF_SIZE - 1) / BUF_SIZE;
        cur_ar = 0;
        for (int i = 0; i < N_BUF; ++i)
            for (int j = 0; j < BUF_SIZE; ++j)
                ar[i][j] = new Data();

        for (int i = 0; i < N_BUF; ++i)
            for (int j = 0; j < N_THREADS; ++j) {
                isReadable[i][j] = new Semaphore(1);
                try {
                    isReadable[i][j].acquire(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        for (int i = 0; i < N_BUF; ++i)
            isWritable[i] = new Semaphore(N_THREADS);

        t = new Thread(this, "Downloader");
        t.start();
    }

    @Override
    public void run() {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        Handler handler = new Handler(this);
        try {
            SAXParser saxParser = parserFactory.newSAXParser();

            for (int i = 0; i < N_BUF; ++i)
                for (int j = 0; j < N_THREADS; ++j)
                    dbThreads[i][j] = new DBThread(this, i, j);

            StringBuilder s = new StringBuilder();
            long time = 0;

            for (int pack = 0; pack < packets; ++pack) {
                try {
                    isWritable[cur_ar].acquire(N_THREADS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (time != 0) {
                    System.out.println("Downloader downtime = " + (System.currentTimeMillis() - time));
                    downtime += System.currentTimeMillis() - time;
                    ++downtimeCount;
                }

                time = System.currentTimeMillis();

                s.setLength(0);

                s.append("http://export.arxiv.org/api/query?id_list=");

                for (int i = BUF_SIZE * pack; i < Math.min(BUF_SIZE * pack + BUF_SIZE, idList.size()); ++i) {
                    s.append(idList.get(i));
                    s.append(",");
                    ar[cur_ar][i - BUF_SIZE * pack].arxiv_id = idList.get(i);
                }

                s.append("&start=0&max_results=1000");

                for (int i = 0; i < BUF_SIZE; ++i)
                    ar[cur_ar][i].flag = false;

                URL url = new URL(s.toString());
                URLConnection arcon = url.openConnection();

                InputStream input = arcon.getInputStream();
                saxParser.parse(input, handler);
                input.close();

                if (handler.n < BUF_SIZE)
                    packets = pack + 1;

                System.out.println(BUF_SIZE + " entries are downloaded in " + (System.currentTimeMillis() - time) + " ms");
                running += (System.currentTimeMillis() - time);
                ++runningCount;

                for (int i = 0; i < N_THREADS; ++i)
                    isReadable[cur_ar][i].release(1);
                cur_ar = (cur_ar + 1) % N_BUF;
                time = System.currentTimeMillis();
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < N_BUF; ++i)
            for (int j = 0; j < N_THREADS; ++j)
                try {
                    dbThreads[i][j].t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        System.out.println("Average download time for " + BUF_SIZE + " entries = " + String.format("%10.3f",(double) running / runningCount));
        System.out.println("Average downtime = " + String.format("%10.3f",(double) downtime / downtimeCount));

    }
}
