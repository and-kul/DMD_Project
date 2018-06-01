package dmd;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

@WebServlet("/update")
public class Update extends HttpServlet {

    static private final String CHECK_ARTICLE_ID = "SELECT id FROM main WHERE id = ?";
    static private final String CHECK_USER_ID = "SELECT article_id FROM users_articles WHERE user_id = ? AND article_id = ?";

    static private final String SELECT_MAIN = "SELECT id, title, to_char(published, 'DD Mon YYYY') AS published, summary, " +
            "comment, doi, pdf, journal_ref FROM main WHERE id = ?;";
    static private final String SELECT_AUTHORS = "SELECT author_name FROM authors_dict WHERE author_id IN " +
            "(SELECT author_id FROM authors_articles WHERE article_id = ?);";
    static private final String SELECT_AFFIL = "SELECT affil_name FROM affiliations_dict WHERE affil_id IN " +
            "(SELECT affil_id FROM affiliations_articles WHERE article_id = ?)";
    static private final String SELECT_USERS = "SELECT login FROM users_dict WHERE user_id IN " +
            "(SELECT user_id FROM users_articles WHERE article_id = ?)";
    static private final String SELECT_CATEGORY_ID = "SELECT category_id FROM categories_articles WHERE article_id = ?";

    static private final String SELECT_PUBLISHED = "SELECT to_char(published, 'YYYY-MM-DD') AS published FROM main WHERE id = ?;";


    static private final String DELETE_ARTICLE = "DELETE FROM main WHERE id = ?";

    static private final String INSERT_MAIN = "INSERT INTO main (title, published, summary, comment, doi, pdf, journal_ref, id) VALUES (?,to_date(?,'YYYY-MM-DD'),?,?,?,?,?,?);";
    static private final String AUTHOR_ID_SEC = "SELECT currval('authors_dict_author_id_seq') AS id;";
    static private final String INSERT_CATEGORY = "INSERT INTO categories_articles (article_id, category_id) VALUES (?,?);";
    static private final String AUTHOR_ID = "SELECT author_id AS id FROM authors_dict WHERE lower(author_name) = ?;";
    static private final String NEW_AUTHOR = "INSERT INTO authors_dict (author_name) VALUES (?);";
    static private final String INSERT_AUTHOR = "INSERT INTO authors_articles (article_id, author_id) VALUES (?,?);";
    static private final String AFFIL_ID_SEC = "SELECT currval('affiliations_dict_affil_id_seq') AS id;";
    static private final String AFFIL_ID = "SELECT affil_id AS id FROM affiliations_dict WHERE lower(affil_name) = ?;";
    static private final String NEW_AFFIL = "INSERT INTO affiliations_dict (affil_name) VALUES (?);";
    static private final String INSERT_AFFIL = "INSERT INTO affiliations_articles (article_id, affil_id) VALUES (?,?);";
    static private final String USER_ID = "SELECT user_id AS id FROM users_dict WHERE login = ?;";
    static private final String INSERT_USER = "INSERT INTO users_articles (article_id, user_id) VALUES (?,?);";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        Boolean authorized = (Boolean) request.getSession().getAttribute("authorized");
        if (authorized == null || !authorized) {
            response.sendRedirect(response.encodeRedirectURL("/"));
            return;
        }

        int userID = (Integer) request.getSession().getAttribute("user_id");

        int id = 0;
        if (request.getParameter("id") == null) {
            response.sendError(404);
            return;
        }

        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(404);
            return;
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String message = "";

        try (Connection connection = DriverManager.getConnection(DB.HOST, DB.USERNAME, DB.PASSWORD);
             PreparedStatement checkArticleID = connection.prepareStatement(CHECK_ARTICLE_ID);
             PreparedStatement checkUserID = connection.prepareStatement(CHECK_USER_ID);
             PreparedStatement selectMain = connection.prepareStatement(SELECT_MAIN);
             PreparedStatement selectAuthors = connection.prepareStatement(SELECT_AUTHORS);
             PreparedStatement selectAffil = connection.prepareStatement(SELECT_AFFIL);
             PreparedStatement selectCategoryID = connection.prepareStatement(SELECT_CATEGORY_ID);
             PreparedStatement selectUsers = connection.prepareStatement(SELECT_USERS)) {

            checkArticleID.setInt(1, id);
            ResultSet res = checkArticleID.executeQuery();

            int res_id = 0;
            while (res.next()) {
                res_id = res.getInt("id");
            }

            if (res_id == 0) {
                message = "Article with id = " + id + " doesn't exist";
                request.setAttribute("message", message);
                request.setAttribute("title", "Update");
                RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/message.jsp");
                dispatcher.forward(request, response);
                return;
            }

            checkUserID.setInt(1, userID);
            checkUserID.setInt(2, id);

            res = checkUserID.executeQuery();

            res_id = 0;
            while (res.next()) {
                res_id = res.getInt("article_id");
            }

            if (res_id == 0) {
                message = "Sorry, you are not allowed to change article " + id;
                request.setAttribute("message", message);
                request.setAttribute("title", "Update");
                RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/message.jsp");
                dispatcher.forward(request, response);
                return;
            }

            Data d = new Data();

            selectMain.setInt(1, id);
            res = selectMain.executeQuery();
            while (res.next()) {
                d.id = id;
                d.title = res.getString("title");
                d.published = res.getString("published");
                d.summary = res.getString("summary");
                d.comment = res.getString("comment");
                d.doi = res.getString("doi");
                d.pdf = res.getString("pdf");
                d.journal_ref = res.getString("journal_ref");

                log(((Integer) id).toString());

                selectAuthors.setInt(1, id);
                ResultSet resAuthors = selectAuthors.executeQuery();
                while (resAuthors.next()) {
                    d.authors.add(resAuthors.getString("author_name"));
                }

                selectAffil.setInt(1, id);
                ResultSet resAffil = selectAffil.executeQuery();
                while (resAffil.next()) {
                    d.affiliations.add(resAffil.getString("affil_name"));
                }

                selectUsers.setInt(1, id);
                ResultSet resUsers = selectUsers.executeQuery();
                while (resUsers.next()) {
                    d.users.add(resUsers.getString("login"));
                }

                selectCategoryID.setInt(1, id);
                ResultSet resCategory = selectCategoryID.executeQuery();
                while (resCategory.next()) {
                    d.category_id = resCategory.getInt("category_id");
                }
            }

            if (((String) request.getSession().getAttribute("login")).equals("root")) {
                int pos = d.users.indexOf("root");
                String str = d.users.get(0);
                d.users.set(0, "root");
                d.users.set(pos, str);

                d.users.add(1, "root");

            } else {
                int pos = d.users.indexOf("root");
                String str = d.users.get(0);
                d.users.set(0, "root");
                d.users.set(pos, str);

                pos = d.users.indexOf((String) request.getSession().getAttribute("login"));
                str = d.users.get(1);
                d.users.set(1, (String) request.getSession().getAttribute("login"));
                d.users.set(pos, str);
            }


            request.setAttribute("title", d.title);
            request.setAttribute("authors", d.authors);
            request.setAttribute("affiliations", d.affiliations);
            request.setAttribute("summary", d.summary);
            request.setAttribute("category_id", d.category_id);
            request.setAttribute("published", d.published);
            request.setAttribute("comment", d.comment);
            request.setAttribute("doi", d.doi);
            request.setAttribute("pdf", d.pdf);
            request.setAttribute("journal_ref", d.journal_ref);
            request.setAttribute("pdf", d.pdf);
            request.setAttribute("id", d.id);
            request.setAttribute("logins", d.users);

            request.setAttribute("authorsCount", d.authors.size());
            request.setAttribute("affilsCount", Math.max(d.affiliations.size(), 1));
            request.setAttribute("loginsCount", d.users.size());

            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/update.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Boolean authorized = (Boolean) request.getSession().getAttribute("authorized");
        if (authorized == null || !authorized) {
            response.sendError(401);
            return;
        }
        response.setContentType("text/html");
        request.setAttribute("title", "Update");

        int userID = (Integer) request.getSession().getAttribute("user_id");

        int id = 0;
        if (request.getParameter("id") == null) {
            response.sendError(404);
            return;
        }

        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(404);
            return;
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String message = "";

        try (Connection connection = DriverManager.getConnection(DB.HOST, DB.USERNAME, DB.PASSWORD);
             PreparedStatement selectPublished = connection.prepareStatement(SELECT_PUBLISHED);
             PreparedStatement checkUserID = connection.prepareStatement(CHECK_USER_ID);
             PreparedStatement deleteArticle = connection.prepareStatement(DELETE_ARTICLE);
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

            selectPublished.setInt(1, id);
            ResultSet res = selectPublished.executeQuery();

            String published = "";
            while (res.next()) {
                published = res.getString("published");
            }

            if (published.equals("")) {
                message = "Article with id = " + id + " doesn't exist";
                request.setAttribute("message", message);
                RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/message.jsp");
                dispatcher.forward(request, response);
                return;
            }

            checkUserID.setInt(1, userID);
            checkUserID.setInt(2, id);

            res = checkUserID.executeQuery();

            int res_id = 0;
            while (res.next()) {
                res_id = res.getInt("article_id");
            }

            if (res_id == 0) {
                message = "Sorry, you are not allowed to change article " + id;
                request.setAttribute("message", message);
                RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/message.jsp");
                dispatcher.forward(request, response);
                return;
            }

            deleteArticle.setInt(1, id);
            int x = deleteArticle.executeUpdate();

            if (x == 0) {
                message = "Unfortunately, something has gone wrong with updating article " + id;
                request.setAttribute("message", message);
                RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/message.jsp");
                dispatcher.forward(request, response);
                return;
            }

            Data d = new Data();
            d.id = id;
            d.title = request.getParameter("title");
            d.category_id = Integer.parseInt(request.getParameter("category"));

            int authorsCount = 0;
            while (request.getParameter("author" + authorsCount) != null) {
                if (!request.getParameter("author" + authorsCount).equals("") && !d.authors.contains(request.getParameter("author" + authorsCount))) {
                    d.authors.add(request.getParameter("author" + authorsCount));
                }
                ++authorsCount;
            }

            d.published = published;
            d.summary = request.getParameter("summary");
            if (!request.getParameter("comment").equals(""))
                d.comment = request.getParameter("comment");

            if (!request.getParameter("journal").equals(""))
                d.journal_ref = request.getParameter("journal");

            if (!request.getParameter("doi").equals(""))
                d.doi = request.getParameter("doi");

            if (!request.getParameter("pdf").equals(""))
                d.pdf = request.getParameter("pdf");

            int affilsCount = 0;
            while (request.getParameter("affil" + affilsCount) != null) {
                if (!request.getParameter("affil" + affilsCount).equals("") && !d.affiliations.contains(request.getParameter("affil" + affilsCount))) {
                    d.affiliations.add(request.getParameter("affil" + affilsCount));
                }
                ++affilsCount;
            }

            int loginsCount = 0;
            while (request.getParameter("login" + loginsCount) != null) {
                if (!request.getParameter("login" + loginsCount).equals("") && !d.users.contains(request.getParameter("login" + loginsCount))) {
                    d.users.add(request.getParameter("login" + loginsCount));
                }
                ++loginsCount;
            }

            /**
             * Sending to Postgres
             */

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
            insertMain.setInt(8, id);
            log(insertMain.toString());
            insertMain.executeUpdate();

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
                userID = 0;
                selectUserID.setString(1, login);
                res = selectUserID.executeQuery();
                while (res.next()) userID = res.getInt("id");
                if (userID == 0) {
                    //log("ERROR: User does not exist");
                    continue;
                }
                insertUser.setInt(1, id);
                insertUser.setInt(2, userID);
                insertUser.executeUpdate();
            }

            message = "Article " + id + " was successfully updated";
            request.setAttribute("message", message);
            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/message.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
