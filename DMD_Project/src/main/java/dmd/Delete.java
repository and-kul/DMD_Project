package dmd;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/delete")
public class Delete extends HttpServlet {

    static private final String SELECT_MAIN = "SELECT id FROM main WHERE id = ?";
    static private final String SELECT_USERS = "SELECT article_id FROM users_articles WHERE user_id = ? AND article_id = ?";
    static private final String DELETE_ARTICLE = "DELETE FROM main WHERE id = ?";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        request.setAttribute("title", "Delete");

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
             PreparedStatement selectMain = connection.prepareStatement(SELECT_MAIN);
             PreparedStatement selectUsers = connection.prepareStatement(SELECT_USERS);
             PreparedStatement deleteArticle = connection.prepareStatement(DELETE_ARTICLE)) {

            selectMain.setInt(1, id);
            ResultSet res = selectMain.executeQuery();

            int res_id = 0;
            while (res.next()) {
                res_id = res.getInt("id");
            }

            if (res_id == 0) {
                message = "Article with id = " + id + " doesn't exist";
                request.setAttribute("message", message);
                RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/message.jsp");
                dispatcher.forward(request, response);
                return;
            }

            selectUsers.setInt(1, userID);
            selectUsers.setInt(2, id);

            res = selectUsers.executeQuery();

            res_id = 0;
            while (res.next()) {
                res_id = res.getInt("article_id");
            }

            if (res_id == 0) {
                message = "Sorry, you are not allowed to delete article " + id;
                request.setAttribute("message", message);
                RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/message.jsp");
                dispatcher.forward(request, response);
                return;
            }

            deleteArticle.setInt(1, id);
            int x = deleteArticle.executeUpdate();

            if (x == 0) {
                message = "Unfortunately, something has gone wrong with deleting article " + id;
                request.setAttribute("message", message);
                RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/message.jsp");
                dispatcher.forward(request, response);
                return;
            }

            message = "Article " + id + " was successfully deleted";
            request.setAttribute("message", message);
            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/message.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
