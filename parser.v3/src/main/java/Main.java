import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static ArrayList<Data> ar = new ArrayList<>();

    public static void main(String[] args) {

        Handler.map.put("", 0);

        try (Scanner in = new Scanner(new FileInputStream("src/main/resources/categories.txt"))) {
            while (in.hasNext()) {
                String x = in.next();
                int y = in.nextInt();
                Handler.map.put(x, y);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        Handler handler = new Handler();

        try {
            SAXParser saxParser = parserFactory.newSAXParser();

            for (int y = 97; y <= 115; ++y) {
                int year = y % 100;
                System.out.println(String.format("Year %02d:", year));
                ar.clear();
                try (FileInputStream input = new FileInputStream("src/main/resources/data/" + String.format("%02d", year) + ".xml")) {
                    saxParser.parse(input, handler);
                    DBThread dbThread = new DBThread();
                    dbThread.t.join();
                }
            }


        } catch (ParserConfigurationException | SAXException | IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
