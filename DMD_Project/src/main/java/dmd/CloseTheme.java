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

@WebServlet("/close-theme")
public class CloseTheme extends HttpServlet {

    static private final String SELECT_MAIN = "SELECT title FROM main WHERE id = ?;";

    static private final String TSQUERY = "SELECT plainto_tsquery(?)";
    static private final String SELECT_CLOSE_THEME = "SELECT id, title, substring(summary FROM 1 FOR 250) AS summary, " +
            "to_char(published, 'DD Mon YYYY') AS published FROM main WHERE tsv @@ to_tsquery(?) ORDER BY ts_rank(tsv, to_tsquery(?)) DESC LIMIT 6";


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
             PreparedStatement selectMain = connection.prepareStatement(SELECT_MAIN);
             PreparedStatement selectCloseTheme = connection.prepareStatement(SELECT_CLOSE_THEME)) {

            selectMain.setInt(1, id);
            log(selectMain.toString());
            ResultSet res = selectMain.executeQuery();

            String title = "";

            while (res.next()) {
                title = res.getString("title");
            }

            if (title == null) {
                response.sendError(404);
                return;
            }


            ArrayList<ShortData> closeTheme;

            String tsquery = "";

            try (PreparedStatement selectTsquery = connection.prepareStatement(TSQUERY)) {
                selectTsquery.setString(1, title);
                res = selectTsquery.executeQuery();
                while (res.next())
                    tsquery = res.getString(1);
            }

            tsquery = tsquery.replace('&', '|');

            selectCloseTheme.setString(1, tsquery);
            selectCloseTheme.setString(2, tsquery);
            log(selectCloseTheme.toString());
            res = selectCloseTheme.executeQuery();
            res.next();


            MessageSearch message = new MessageSearch(5, res, connection);

            closeTheme = message.articles;

            request.setAttribute("closeTheme", closeTheme);

            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/close-theme.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
