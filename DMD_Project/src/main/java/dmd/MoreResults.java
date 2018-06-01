package dmd;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/more-results")
public class MoreResults extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Boolean authorized = (Boolean) request.getSession().getAttribute("authorized");
        if (authorized == null || !authorized) {
            response.sendError(401);
            return;
        }
        response.setContentType("application/json");

        String searchID = request.getParameter("searchID");

        ResultSet res = ShortData.resultSetMap.get(request.getSession().getId() + searchID);
        PreparedStatement statement = ShortData.statementMap.get(request.getSession().getId() + searchID);
        Connection connection = ShortData.connectionMap.get(request.getSession().getId() + searchID);

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

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
