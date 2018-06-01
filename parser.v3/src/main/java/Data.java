import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;

public class Data {
    String title;
    String published;
    String summary;
    String comment;
    String doi;
    String pdf;
    String journal_ref;
    int category_id;
    ArrayList<String> authors;
    HashSet<String> users;
    HashSet<String> affiliations;

    Data() {
        title = null;
        published = null;
        summary = null;
        comment = null;
        doi = null;
        pdf = null;
        journal_ref = null;
        category_id = 0;
        authors = new ArrayList<>();
        users = new HashSet<>();
        affiliations = new HashSet<>();
    }

}
