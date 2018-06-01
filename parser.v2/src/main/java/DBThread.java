import com.mysql.fabric.jdbc.FabricMySQLDriver;

import java.sql.*;

public class DBThread implements Runnable {
    Downloader pr;
    int ar_i;
    int thread_i;
    int shift;
    Thread t;

    DBThread(Downloader pr, int ar_i, int thread_i) {
        this.pr = pr;
        this.ar_i = ar_i;
        this.thread_i = thread_i;
        shift = thread_i * pr.PORTION_SIZE;
        t = new Thread(this, "DBThread_" + ar_i + "." + thread_i);
        t.start();
    }

    static private final String HOST = "jdbc:postgresql://localhost:5432/dmdproject";
    static private final String USERNAME = "postgres";
    static private final String PASSWORD = "postgres";

    static private final String INSERT_MAIN = "INSERT INTO main (id, title, summary, primary_category, ref) VALUES (?,?,?,?,?);";
    static private final String INSERT_AUTHORS = "INSERT INTO authors (article_id, name) VALUES (?,?);";
    static private final String INSERT_CATEGORIES = "INSERT INTO categories (article_id, category) VALUES (?,?);";

    @Override
    public void run() {
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);

            for (int pack = ar_i; pack < pr.packets; pack += pr.N_BUF) {
                try {
                    pr.isReadable[ar_i][thread_i].acquire(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long time = System.currentTimeMillis();

                try (Connection connection = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
                     PreparedStatement stMain = connection.prepareStatement(INSERT_MAIN);
                     PreparedStatement stAuthors = connection.prepareStatement(INSERT_AUTHORS);
                     PreparedStatement stCategories = connection.prepareStatement(INSERT_CATEGORIES)) {

                    for (int i = shift; i < shift + pr.PORTION_SIZE; ++i) {
                        if (!pr.ar[ar_i][i].flag) break;

                        stMain.setString(1, pr.ar[ar_i][i].arxiv_id);
                        stMain.setString(2, pr.ar[ar_i][i].title);
                        stMain.setString(3, pr.ar[ar_i][i].summary);
                        stMain.setString(4, pr.ar[ar_i][i].primary_category);
                        stMain.setString(5, pr.ar[ar_i][i].ref);

                        int x = stMain.executeUpdate();

                        int y = 0;
                        int z = 0;

                        for (String name : pr.ar[ar_i][i].authors) {
                            stAuthors.setString(1, pr.ar[ar_i][i].arxiv_id);
                            stAuthors.setString(2, name);
                            y += stAuthors.executeUpdate();
                        }

                        for (String category : pr.ar[ar_i][i].categories) {
                            stCategories.setString(1, pr.ar[ar_i][i].arxiv_id);
                            stCategories.setString(2, category);
                            z += stCategories.executeUpdate();
                        }

                        if (x == 1 && y == pr.ar[ar_i][i].authors.size() && z == pr.ar[ar_i][i].categories.size())
                            /*System.out.println(pr.ar[ar_i][i].arxiv_id)*/ ;
                        else
                            System.out.println("ERROR");
                    }
                }

                System.out.println(pr.PORTION_SIZE + " entries are sent to DB in " + (System.currentTimeMillis() - time) + " ms");

                pr.isWritable[ar_i].release(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
