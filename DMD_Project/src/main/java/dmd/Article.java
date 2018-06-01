package dmd;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

@WebServlet("/article")
public class Article extends HttpServlet {

    static private final String SELECT_MAIN = "SELECT id, title, to_char(published, 'DD Mon YYYY') AS published, summary, " +
            "comment, doi, pdf, journal_ref FROM main WHERE id = ?;";
    static private final String SELECT_AUTHORS = "SELECT author_name FROM authors_dict WHERE author_id IN " +
            "(SELECT author_id FROM authors_articles WHERE article_id = ?);";
    static private final String SELECT_AFFIL = "SELECT affil_name FROM affiliations_dict WHERE affil_id IN " +
            "(SELECT affil_id FROM affiliations_articles WHERE article_id = ?)";
    static private final String SELECT_CATEGORY_NAME = "SELECT category_name FROM categories_dict WHERE category_id = " +
            "(SELECT category_id FROM categories_articles WHERE article_id = ?)";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        long time = System.currentTimeMillis();

        Boolean authorized = (Boolean) request.getSession().getAttribute("authorized");
        if (authorized == null || !authorized) {
            response.sendRedirect(response.encodeRedirectURL("/"));
            return;
        }

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

        try (Connection connection = DriverManager.getConnection(DB.HOST, DB.USERNAME, DB.PASSWORD);
             PreparedStatement selectMain = connection.prepareStatement(SELECT_MAIN);
             PreparedStatement selectAuthors = connection.prepareStatement(SELECT_AUTHORS);
             PreparedStatement selectAffil = connection.prepareStatement(SELECT_AFFIL);
             PreparedStatement selectCategoryName = connection.prepareStatement(SELECT_CATEGORY_NAME)) {

            Data d = new Data();

            selectMain.setInt(1, id);
            log(selectMain.toString());
            ResultSet res = selectMain.executeQuery();
            log(String.valueOf(System.currentTimeMillis() - time));
            while (res.next()) {
                d.id = id;
                d.title = res.getString("title");
                d.published = res.getString("published");
                d.summary = res.getString("summary");
                d.comment = res.getString("comment");
                d.doi = res.getString("doi");
                d.pdf = res.getString("pdf");
                d.journal_ref = res.getString("journal_ref");

                selectAuthors.setInt(1, id);
                log(selectAuthors.toString());
                ResultSet resAuthors = selectAuthors.executeQuery();
                log(String.valueOf(System.currentTimeMillis() - time));
                while (resAuthors.next()) {
                    d.authors.add(resAuthors.getString("author_name"));
                }

                selectAffil.setInt(1, id);
                log(selectAffil.toString());
                ResultSet resAffil = selectAffil.executeQuery();
                log(String.valueOf(System.currentTimeMillis() - time));
                while (resAffil.next()) {
                    d.affiliations.add(resAffil.getString("affil_name"));
                }

                log(selectCategoryName.toString());
                selectCategoryName.setInt(1, id);
                ResultSet resCategory = selectCategoryName.executeQuery();
                log(String.valueOf(System.currentTimeMillis() - time));
                while (resCategory.next()) {
                    d.category_name = resCategory.getString("category_name");
                }

            }

            if (d.title == null) {
                response.sendError(404);
                return;
            }

            String authors = "";
            for (int i = 0; i < d.authors.size() - 1; ++i)
                authors += d.authors.get(i) + ", ";
            authors += d.authors.get(d.authors.size() - 1);


            String affiliations = null;
            if (d.affiliations.size() != 0) {
                affiliations = "(";
                for (int i = 0; i < d.affiliations.size() - 1; ++i)
                    affiliations += d.affiliations.get(i) + ", ";
                affiliations += d.affiliations.get(d.affiliations.size() - 1) + ")";
            }


            request.setAttribute("title", d.title);
            request.setAttribute("authors", authors);
            request.setAttribute("affiliations", affiliations);
            request.setAttribute("summary", d.summary);
            request.setAttribute("category_name", d.category_name);
            request.setAttribute("published", d.published);
            request.setAttribute("comment", d.comment);
            request.setAttribute("doi", d.doi);
            request.setAttribute("journal_ref", d.journal_ref);
            request.setAttribute("pdf", d.pdf);
            request.setAttribute("id", d.id);


            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/article.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
