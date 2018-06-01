import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
    static final int PACKET_SIZE = 400;

    static boolean checkID(String id) throws IOException {
        URL url = new URL("http://export.arxiv.org/api/query?id_list=" + id);
        URLConnection arcon = url.openConnection();
        InputStream in = arcon.getInputStream();

        StringBuilder sb = new StringBuilder();

        int x;
        while ((x = in.read()) != -1) {
            sb.append((char) x);
        }

        return sb.indexOf("author") >= 0;

    }

    static int endID(String prefix, int d, int l, int r) throws IOException {
        //System.out.println(l + " - " + r);
        if (l == r) return l;

        int m = (l + r) / 2;

        if (checkID(prefix + String.format("%0" + d + "d", m))) return endID(prefix, d, m + 1, r);
        else return endID(prefix, d, l, m);
    }


    public static void main(String[] args) throws IOException {

        HashSet<String> set = new HashSet<>();

        Scanner categories = new Scanner(new File("categories_full.txt"));

        while (categories.hasNext()) {
            String s = categories.nextLine();
            s = s.substring(0,s.indexOf("\t"));
            if (s.contains("."))
                s = s.substring(0, s.indexOf("."));
            set.add(s);
        }

        categories.close();

        /*

        for (int year = 97; year <= 97; ++year)
            for (int month = 1; month <= 12; ++month) {
                try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(String.format("data/%02d.xml",year),true))) {
                    long time0 = System.currentTimeMillis();
                    int count = 0;

                    StringBuilder s = new StringBuilder("http://export.arxiv.org/api/query?start=0&max_results=1000&id_list=");
                    final int s0 = s.length();
                    StringBuilder buf = new StringBuilder();
                    for (String category : set) {
                        String prefix = category + String.format("/%02d%02d", year, month);

                        System.out.println(prefix);

                        long time = System.currentTimeMillis();

                        int end = endID(prefix, 3, 1, 999);

                        System.out.println(String.format("    analysis:    %7.3f ms", (double)(System.currentTimeMillis() - time) / 1000));

                        time = System.currentTimeMillis();

                        int packets = (end + PACKET_SIZE - 2) / PACKET_SIZE;

                        for (int pack = 0; pack < packets; ++pack) {
                            s.setLength(s0);

                            count += Math.min(pack * PACKET_SIZE + PACKET_SIZE + 1, end) - (pack * PACKET_SIZE + 1);

                            for (int i = pack * PACKET_SIZE + 1; i < Math.min(pack * PACKET_SIZE + PACKET_SIZE + 1, end); ++i)
                                s.append(prefix + String.format("%03d,",i));

                            URL url = new URL(s.toString());
                            URLConnection arcon = url.openConnection();

                            try (BufferedInputStream in = new BufferedInputStream(arcon.getInputStream())) {
                                buf.setLength(0);

                                int x;
                                while ((x = in.read()) != -1) {
                                    buf.append((char) x);
                                }

                                for (int i = buf.indexOf("<entry>"); i < buf.lastIndexOf("</feed>"); ++i)
                                    out.write(buf.charAt(i));
                            }

                        }

                        System.out.println(String.format("    downloading: %7.3f ms", (double)(System.currentTimeMillis() - time) / 1000));
                        System.out.println(String.format("    count = %d", end - 1));
                        System.out.println();

                    }

                    int oldCount;
                    try (Scanner inCount = new Scanner(new FileInputStream(String.format("count/%02d.txt",year)))) {
                        oldCount = inCount.nextInt();
                    } catch (IOException e) {
                        oldCount = 0;
                    }

                    try (PrintStream outCount = new PrintStream(new FileOutputStream(String.format("count/%02d.txt",year),false))) {
                        outCount.println(oldCount + count);
                    }

                    System.out.println(String.format("total %02d%02d count = %d", year, month, count));
                    System.out.println(String.format("total %02d%02d time = %8.3f ms", year, month, (double)(System.currentTimeMillis() - time0) / 1000));

                }

            }

        */


        for (int year = 14; year <= 14; ++year)
            for (int month = 1; month <= 12; ++month) {
                try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(String.format("data/%02d.xml",year),true))) {
                    long time0 = System.currentTimeMillis();
                    int count = 0;

                    StringBuilder s = new StringBuilder("http://export.arxiv.org/api/query?start=0&max_results=1000&id_list=");
                    final int s0 = s.length();
                    StringBuilder buf = new StringBuilder();

                    String prefix = String.format("%02d%02d.", year, month);
                    //System.out.println(prefix);

                    long time = System.currentTimeMillis();

                    int end = endID(prefix, 4, 1, 9999);

                    //System.out.println(String.format("    analysis:    %7.3f ms", (double)(System.currentTimeMillis() - time) / 1000));

                    time = System.currentTimeMillis();

                    int packets = (end + PACKET_SIZE - 2) / PACKET_SIZE;

                    for (int pack = 0; pack < packets; ++pack) {
                        s.setLength(s0);

                        count += Math.min(pack * PACKET_SIZE + PACKET_SIZE + 1, end) - (pack * PACKET_SIZE + 1);

                        for (int i = pack * PACKET_SIZE + 1; i < Math.min(pack * PACKET_SIZE + PACKET_SIZE + 1, end); ++i)
                            s.append(prefix + String.format("%04d,",i));

                        URL url = new URL(s.toString());
                        URLConnection arcon = url.openConnection();

                        try (BufferedInputStream in = new BufferedInputStream(arcon.getInputStream())) {
                            buf.setLength(0);

                            int x;
                            while ((x = in.read()) != -1) {
                                buf.append((char) x);
                            }

                            for (int i = buf.indexOf("<entry>"); i < buf.lastIndexOf("</feed>"); ++i)
                                out.write(buf.charAt(i));
                        }

                    }

                    //System.out.println(String.format("    downloading: %7.3f ms", (double)(System.currentTimeMillis() - time) / 1000));
                    //System.out.println(String.format("    count = %d", end - 1));
                    //System.out.println();


                    int oldCount;
                    try (Scanner inCount = new Scanner(new FileInputStream(String.format("count/%02d.txt",year)))) {
                        oldCount = inCount.nextInt();
                    } catch (IOException e) {
                        oldCount = 0;
                    }

                    try (PrintStream outCount = new PrintStream(new FileOutputStream(String.format("count/%02d.txt",year),false))) {
                        outCount.println(oldCount + count);
                    }

                    System.out.println(String.format("total %02d%02d count = %d", year, month, count));
                    System.out.println(String.format("total %02d%02d time = %8.3f ms", year, month, (double)(System.currentTimeMillis() - time0) / 1000));

                }

            }



    }
}
