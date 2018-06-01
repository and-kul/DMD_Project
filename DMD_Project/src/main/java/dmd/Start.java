package dmd;

import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.*;

@WebServlet("")
public class Start extends HttpServlet {

    static private final String USER_ID = "SELECT user_id, hash FROM users_dict WHERE login = ?;";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        Boolean authorized = (Boolean) request.getSession().getAttribute("authorized");
        if (authorized != null && authorized) {
            response.sendRedirect(response.encodeRedirectURL("/search"));
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/start.jsp");
        dispatcher.forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        String login = request.getParameter("login");
        String password = request.getParameter("password");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(DB.HOST, DB.USERNAME, DB.PASSWORD);
             PreparedStatement selectUser = connection.prepareStatement(USER_ID)) {

            int user_id = 0;
            String hash_fromDB = "";

            selectUser.setString(1, login);

            ResultSet res = selectUser.executeQuery();
            while (res.next()) {
                user_id = res.getInt("user_id");
                hash_fromDB = res.getString("hash");
            }

            MessageStart message = null;


            if (user_id == 0)
                message = new MessageStart(false, "User does not exist");
            else if (!DigestUtils.md5Hex(password).equals(hash_fromDB))
                message = new MessageStart(false, "Wrong password");

            if (message != null) {
                try (Writer pw = response.getWriter()) {
                    Gson gson = new Gson();
                    gson.toJson(message, pw);
                }
                return;
            }

            HttpSession session = request.getSession();

            session.setAttribute("user_id", user_id);
            session.setAttribute("authorized", Boolean.TRUE);
            session.setAttribute("login", login);

            message = new MessageStart(true, "");
            try (Writer pw = response.getWriter()) {
                Gson gson = new Gson();
                gson.toJson(message, pw);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}