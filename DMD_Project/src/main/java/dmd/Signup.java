package dmd;

import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.sql.*;

@WebServlet("/signup")
public class Signup extends HttpServlet {

    class Message {
        boolean ok;
        String error;

        Message(boolean ok, String error) {
            this.ok = ok;
            this.error = error;
        }
    }

    static private final String USER_ID = "SELECT user_id AS id FROM users_dict WHERE login = ?;";
    static private final String NEW_USER = "INSERT INTO users_dict (login, hash) VALUES (?,?);";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        String newLogin = request.getParameter("newLogin");
        String newPassword = request.getParameter("newPassword");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(DB.HOST, DB.USERNAME, DB.PASSWORD);
             PreparedStatement selectUserID = connection.prepareStatement(USER_ID);
             PreparedStatement newUser = connection.prepareStatement(NEW_USER)) {

            int userID = 0;
            selectUserID.setString(1, newLogin);
            ResultSet res = selectUserID.executeQuery();
            while (res.next()) userID = res.getInt("id");

            Message message = null;

            if (userID != 0) {
                message = new Message(false, "A user with that username already exists");

                try (Writer pw = response.getWriter()) {
                    Gson gson = new Gson();
                    gson.toJson(message, pw);
                }

                return;
            }

            newUser.setString(1, newLogin);
            newUser.setString(2, DigestUtils.md5Hex(newPassword));
            newUser.executeUpdate();

            selectUserID.setString(1, newLogin);
            res = selectUserID.executeQuery();
            while (res.next()) userID = res.getInt("id");

            HttpSession session = request.getSession();

            session.setAttribute("user_id", userID);
            session.setAttribute("authorized", Boolean.TRUE);
            session.setAttribute("login", newLogin);

            message = new Message(true, "");
            try (Writer pw = response.getWriter()) {
                Gson gson = new Gson();
                gson.toJson(message, pw);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
