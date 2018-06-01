import java.sql.*;

public class DBThread implements Runnable {
    Thread t;
    long time;

    DBThread() {
        time = System.currentTimeMillis();
        t = new Thread(this);
        t.start();
    }

    static private final String HOST = "jdbc:postgresql://localhost:5432/dmdproject";
    static private final String USERNAME = "postgres";
    static private final String PASSWORD = "postgres";

    static private final String INSERT_MAIN = "INSERT INTO main (title, published, summary, comment, doi, pdf, journal_ref) VALUES (?,to_date(?,'YYYY-MM-DD'),?,?,?,?,?);";
    static private final String AUTHOR_ID_SEC = "SELECT currval('authors_dict_author_id_seq') AS id;";
    static private final String INSERT_CATEGORY = "INSERT INTO categories_articles (article_id, category_id) VALUES (?,?);";
    static private final String AUTHOR_ID = "SELECT author_id AS id FROM authors_dict WHERE lower(author_name) = ?;";
    static private final String NEW_AUTHOR = "INSERT INTO authors_dict (author_name) VALUES (?);";
    static private final String MAIN_ID_SEC = "SELECT currval('main_id_seq') AS id;";
    static private final String INSERT_AUTHOR = "INSERT INTO authors_articles (article_id, author_id) VALUES (?,?);";
    static private final String AFFIL_ID_SEC = "SELECT currval('affiliations_dict_affil_id_seq') AS id;";
    static private final String AFFIL_ID = "SELECT affil_id AS id FROM affiliations_dict WHERE lower(affil_name) = ?;";
    static private final String NEW_AFFIL = "INSERT INTO affiliations_dict (affil_name) VALUES (?);";
    static private final String INSERT_AFFIL = "INSERT INTO affiliations_articles (article_id, affil_id) VALUES (?,?);";
    static private final String USER_ID = "SELECT user_id AS id FROM users_dict WHERE login = ?;";
    static private final String INSERT_USER = "INSERT INTO users_articles (article_id, user_id) VALUES (?,?);";


    @Override
    public void run() {
        try (Connection connection = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
             PreparedStatement insertMain = connection.prepareStatement(INSERT_MAIN);
             Statement statement = connection.createStatement();
             PreparedStatement insertCategory = connection.prepareStatement(INSERT_CATEGORY);
             PreparedStatement selectAuthorID = connection.prepareStatement(AUTHOR_ID);
             PreparedStatement newAuthor = connection.prepareStatement(NEW_AUTHOR);
             PreparedStatement insertAuthor = connection.prepareStatement(INSERT_AUTHOR);
             PreparedStatement selectAffilID = connection.prepareStatement(AFFIL_ID);
             PreparedStatement newAffil = connection.prepareStatement(NEW_AFFIL);
             PreparedStatement insertAffil = connection.prepareStatement(INSERT_AFFIL);
             PreparedStatement selectUserID = connection.prepareStatement(USER_ID);
             PreparedStatement insertUser = connection.prepareStatement(INSERT_USER)) {

            for (Data d : Main.ar) {
                insertMain.setString(1, d.title);
                insertMain.setString(2, d.published);
                insertMain.setString(3, d.summary);
                if (d.comment != null) insertMain.setString(4, d.comment);
                else insertMain.setNull(4, Types.VARCHAR);
                if (d.doi != null) insertMain.setString(5, d.doi);
                else insertMain.setNull(5, Types.VARCHAR);
                if (d.pdf != null) insertMain.setString(6, d.pdf);
                else insertMain.setNull(6, Types.VARCHAR);
                if (d.journal_ref != null) insertMain.setString(7, d.journal_ref);
                else insertMain.setNull(7, Types.VARCHAR);
                insertMain.executeUpdate();

                int id = 0;
                ResultSet res = statement.executeQuery(MAIN_ID_SEC);
                while (res.next()) id = res.getInt("id");
                if (id % 1000 == 0) {
                    System.out.println(id);
                    System.out.println("last 1000 entries are sent to DB in " + (System.currentTimeMillis() - time) + " ms");
                    time = System.currentTimeMillis();
                }

                insertCategory.setInt(1, id);
                insertCategory.setInt(2, d.category_id);
                insertCategory.executeUpdate();

                for (String name : d.authors) {
                    int authorID = 0;
                    selectAuthorID.setString(1, name.toLowerCase());
                    res = selectAuthorID.executeQuery();
                    while (res.next()) authorID = res.getInt("id");
                    if (authorID == 0) {
                        newAuthor.setString(1, name);
                        newAuthor.executeUpdate();

                        res = statement.executeQuery(AUTHOR_ID_SEC);
                        while (res.next()) authorID = res.getInt("id");
                    }

                    insertAuthor.setInt(1, id);
                    insertAuthor.setInt(2, authorID);
                    insertAuthor.executeUpdate();

                }

                for (String name : d.affiliations) {
                    int affilID = 0;
                    selectAffilID.setString(1, name.toLowerCase());
                    res = selectAffilID.executeQuery();
                    while (res.next()) affilID = res.getInt("id");
                    if (affilID == 0) {
                        newAffil.setString(1, name);
                        newAffil.executeUpdate();

                        res = statement.executeQuery(AFFIL_ID_SEC);
                        while (res.next()) affilID = res.getInt("id");
                    }

                    insertAffil.setInt(1, id);
                    insertAffil.setInt(2, affilID);
                    insertAffil.executeUpdate();

                }

                for (String login : d.users) {
                    int userID = 0;
                    selectUserID.setString(1, login);
                    res = selectUserID.executeQuery();
                    while (res.next()) userID = res.getInt("id");
                    if (userID == 0) {
                        System.out.println("ERROR: User does not exist");
                        break;
                    }
                    insertUser.setInt(1, id);
                    insertUser.setInt(2, userID);
                    insertUser.executeUpdate();
                }


            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}