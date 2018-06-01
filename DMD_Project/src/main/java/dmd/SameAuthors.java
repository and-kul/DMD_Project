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

@WebServlet("/same-authors")
public class SameAuthors extends HttpServlet {

    static private final String SELECT_SAME_AUTHORS = "SELECT id, title, substring(summary FROM 1 FOR 250) AS summary, " +
            "to_char(published, 'DD Mon YYYY') AS published FROM main NATURAL JOIN " +
            "(SELECT article_id as id, count(*) AS rank FROM authors_articles WHERE author_id IN " +
            "(SELECT author_id FROM authors_articles WHERE article_id = ?) GROUP BY article_id) AS t ORDER BY rank DESC LIMIT 6";


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

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
             PreparedStatement selectSameAuthors = connection.prepareStatement(SELECT_SAME_AUTHORS)) {

            ArrayList<ShortData> sameAuthors;

            selectSameAuthors.setInt(1, id);
            log(selectSameAuthors.toString());
            ResultSet res = selectSameAuthors.executeQuery();
            res.next();
            MessageSearch message = new MessageSearch(5, res, connection);

            sameAuthors = message.articles;

            request.setAttribute("sameAuthors", sameAuthors);

            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/same-authors.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
