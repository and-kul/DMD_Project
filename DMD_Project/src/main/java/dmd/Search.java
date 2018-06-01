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
import java.time.LocalDate;
import java.util.ArrayList;

@WebServlet("/search")
public class Search extends HttpServlet {

    static private final String SELECT_AUTHOR = "(select article_id as id from authors_articles where author_id = " +
            "(select author_id from authors_dict where lower(author_name) = lower(?)))";
    static private final String SELECT_CATEGORY = "(select article_id as id from categories_articles where category_id = to_number(?, '9'))";
    static private final String TSQUERY = "SELECT plainto_tsquery(?)";
    static private final String SELECT_AFFIL = "(select distinct article_id as id from affiliations_articles where affil_id in " +
            "(select affil_id from affiliations_dict where tsv @@ plainto_tsquery(?)))";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        Boolean authorized = (Boolean) request.getSession().getAttribute("authorized");
        if (authorized == null || !authorized) {
            response.sendRedirect(response.encodeRedirectURL("/"));
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/search.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Boolean authorized = (Boolean) request.getSession().getAttribute("authorized");
        if (authorized == null || !authorized) {
            response.sendError(401);
            return;
        }
        response.setContentType("application/json");

        String categoryID = request.getParameter("category");
        String author0 = request.getParameter("author0");
        String author1 = request.getParameter("author1");
        String yearFrom = request.getParameter("yearFrom");
        String yearTo = request.getParameter("yearTo");
        String keywords = request.getParameter("keywords");
        String affil = request.getParameter("affil");
        String sortBy = request.getParameter("sortBy");
        String searchID = request.getParameter("searchID");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Connection connection = DriverManager.getConnection(DB.HOST, DB.USERNAME, DB.PASSWORD);
            ShortData.connectionMap.put(request.getSession().getId() + searchID, connection);

            ArrayList<String> param = new ArrayList<>();

            String query = "";
            if (!categoryID.equals("0")) {
                query = SELECT_CATEGORY;
                param.add(categoryID);
            }
            if (!author0.equals("")) {
                if (query.equals("")) {
                    query = SELECT_AUTHOR;
                } else {
                    query = "(select * from " + query + " as t1 natural join " + SELECT_AUTHOR + " as t2)";
                }
                param.add(author0);
            }

            if (!author1.equals("")) {
                if (query.equals("")) {
                    query = SELECT_AUTHOR;
                } else {
                    query = "(select * from " + query + " as t1 natural join " + SELECT_AUTHOR + " as t2)";
                }
                param.add(author1);
            }

            if (!affil.equals("")) {
                if (query.equals("")) {
                    query = SELECT_AFFIL;
                } else {
                    query = "(select * from " + query + " as t1 natural join " + SELECT_AFFIL + " as t2)";
                }
                param.add(affil);
            }

            String tsquery = "";

            try (PreparedStatement selectTsquery = connection.prepareStatement(TSQUERY)) {
                selectTsquery.setString(1, keywords);
                ResultSet res = selectTsquery.executeQuery();
                while (res.next())
                    tsquery = res.getString(1);
            }

            tsquery = tsquery.replace('&','|');

            if (tsquery.equals("")) sortBy = "date";

            String mainQuery = "select id, title, substring(summary from 1 for 250) as summary, to_char(published, 'DD Mon YYYY') as published, " +
                    "published as pub from main";

            boolean where = false;

            if (!yearFrom.equals("1997")) {
                mainQuery += " where published >= to_date(?, 'YYYY')";
                where = true;
                param.add(0,yearFrom);
            }

            if (!yearTo.equals(String.valueOf(LocalDate.now().getYear() + 1))) {
                if (where) {
                    mainQuery += " and published < to_date(?, 'YYYY')";
                    param.add(1, yearTo);
                } else {
                    mainQuery += " where published < to_date(?, 'YYYY')";
                    param.add(0,yearTo);
                    where = true;
                }
            }

            if (!query.equals("")) {
                if (where) {
                    mainQuery += " and id in " + query;
                } else {
                    mainQuery += " where id in " + query;
                    where = true;
                }

            }
            if (!tsquery.equals("")) {
                if (where)
                    mainQuery += " and tsv @@ to_tsquery(?)";
                else {
                    mainQuery += " where tsv @@ to_tsquery(?)";
                    where = true;
                }
                param.add(tsquery);
            }

            if (sortBy.equals("relevance")) {
                mainQuery += " order by ts_rank(tsv, to_tsquery(?)) desc limit 100";
                param.add(tsquery);
            } else {
                mainQuery += " order by pub desc limit 100";
            }

            PreparedStatement selectMain = connection.prepareStatement(mainQuery);
            ShortData.statementMap.put(request.getSession().getId() + searchID, selectMain);

            for (int i = 0; i < param.size(); ++i)
                selectMain.setString(i + 1, param.get(i));
            log(selectMain.toString());
            ResultSet res = selectMain.executeQuery();
            ShortData.resultSetMap.put(request.getSession().getId() + searchID, res);

            try (Writer pw = response.getWriter()) {
                MessageSearch message = new MessageSearch(5, res, connection);

                if (!message.moreResults) {
                    res.close();
                    ShortData.resultSetMap.remove(request.getSession().getId() + searchID);
                    selectMain.close();
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
