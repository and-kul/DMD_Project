package dmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class ShortData {
    public int id;
    public String title;
    public String published;
    public String summary;
    public ArrayList<String> authors;

    ShortData() {
        id = 0;
        title = null;
        published = null;
        summary = null;
        authors = new ArrayList<>();
    }

    static HashMap<String, ResultSet> resultSetMap = new HashMap<>();
    static HashMap<String, Connection> connectionMap = new HashMap<>();
    static HashMap<String, PreparedStatement> statementMap = new HashMap<>();


}
