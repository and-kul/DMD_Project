package dmd;

import com.google.gson.Gson;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.sql.*;
import java.util.ArrayList;

@WebServlet("/my")
public class My extends HttpServlet {

    static private final String SELECT = "SELECT id, title, substring(summary FROM 1 FOR 250) AS summary, " +
            "to_char(published, 'DD Mon YYYY') AS published, published AS pub FROM main WHERE " +
            "id IN (SELECT article_id FROM users_articles WHERE user_id = ?) ORDER BY pub DESC limit 100";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        Boolean authorized = (Boolean) request.getSession().getAttribute("authorized");
        if (authorized == null || !authorized) {
            response.sendRedirect(response.encodeRedirectURL("/"));
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/my.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Boolean authorized = (Boolean) request.getSession().getAttribute("authorized");
        if (authorized == null || !authorized) {
            response.sendError(401);
            return;
        }
        response.setContentType("application/json");
        String searchID = request.getParameter("searchID");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Connection connection = DriverManager.getConnection(DB.HOST, DB.USERNAME, DB.PASSWORD);
            ShortData.connectionMap.put(request.getSession().getId() + searchID, connection);

            PreparedStatement statement = connection.prepareStatement(SELECT);
            statement.setInt(1,(Integer)request.getSession().getAttribute("user_id"));

            ShortData.statementMap.put(request.getSession().getId() + searchID, statement);


            ResultSet res = statement.executeQuery();
            ShortData.resultSetMap.put(request.getSession().getId() + searchID, res);

            try (Writer pw = response.getWriter()) {
                MessageSearch message = new MessageSearch(5, res, connection);

                if (!message.moreResults) {
                    res.close();
                    ShortData.resultSetMap.remove(request.getSession().getId() + searchID);
                    statement.close();
                    ShortData.statementMap.remove(request.getSession().getId() + searchID);
                    connection.close();
                    ShortData.connectionMap.remove(request.getSession().getId() + searchID);
                }
                Gson gson = new Gson();
                gson.toJson(message, pw);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
