import java.util.ArrayList;

public class Data {
    boolean flag;
    String arxiv_id;
    String title;
    String summary;
    String primary_category;
    String ref;
    ArrayList<String> authors;
    ArrayList<String> categories;

    Data() {
        flag = false;
        arxiv_id = null;
        title = null;
        summary = null;
        primary_category = null;
        ref = null;
        authors = new ArrayList<>();
        categories = new ArrayList<>();
    }
}
