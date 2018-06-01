package dmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MessageSearch {
    static private final String SELECT_AUTHORS = "select author_name as name from authors_dict where author_id in (select author_id from authors_articles where article_id = ?)";

    boolean moreResults;
    ArrayList<ShortData> articles;

    MessageSearch() {
        moreResults = false;
        articles = new ArrayList<>();
    }

    MessageSearch(int n, ResultSet res, Connection connection) {
        this();
        try {
            while (articles.size() < n && res.next()) {
                ShortData article = new ShortData();
                article.id = res.getInt("id");
                article.title = res.getString("title");
                article.summary = res.getString("summary") + "...";
                article.published = res.getString("published");

                try (PreparedStatement statement = connection.prepareStatement(SELECT_AUTHORS)) {
                    statement.setInt(1,article.id);
                    ResultSet resAuthors = statement.executeQuery();
                    while (resAuthors.next())
                        article.authors.add(resAuthors.getString("name"));
                }

                articles.add(article);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        moreResults = articles.size() == n;
    }
}
