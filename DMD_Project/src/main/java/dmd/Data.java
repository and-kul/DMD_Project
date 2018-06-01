package dmd;

import java.util.ArrayList;

public class Data {
    int id;
    String title;
    String published;
    String summary;
    String comment;
    String doi;
    String pdf;
    String journal_ref;
    int category_id;
    String category_name;
    ArrayList<String> authors;
    ArrayList<String> users;
    ArrayList<String> affiliations;

    Data() {
        id = 0;
        title = null;
        published = null;
        summary = null;
        comment = null;
        doi = null;
        pdf = null;
        journal_ref = null;
        category_id = 0;
        category_name = null;
        authors = new ArrayList<>();
        users = new ArrayList<>();
        affiliations = new ArrayList<>();
    }

}
