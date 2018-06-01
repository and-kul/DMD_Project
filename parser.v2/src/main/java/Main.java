import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ArrayList<String> idList = new ArrayList<>();

        for (int year = 15; year <= 15; ++year)
            for (int month = 1; month <= 1; ++month)
                for (int i = 50000; i <= 60000; ++i)
                    idList.add(String.format("%02d%02d.%05d", year, month, i));


        long start = System.currentTimeMillis();


        Downloader d = new Downloader(idList);

        try {
            d.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Everything is OK");

        System.out.println(System.currentTimeMillis() - start);


    }
}
