import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.CharArrayWriter;
import java.util.HashMap;

public class Handler extends DefaultHandler {
    static HashMap<String, Integer> map = new HashMap<>();
    private int n;
    private CharArrayWriter buf = new CharArrayWriter(100000);

    @Override
    public void startDocument() throws SAXException {
        n = 0;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        buf.reset();
        if (qName.equals("entry")) {
            Main.ar.add(new Data());
        } else if (qName.equals("link") && attributes.getValue("rel").equals("related") && attributes.getValue("title").equals("pdf")) {
            Main.ar.get(n).pdf = attributes.getValue("href");
        } else if (qName.equals("arxiv:primary_category")) {
            String s = attributes.getValue("term");
            if (s.contains(".")) s = s.substring(0, s.indexOf("."));
            if (!map.containsKey(s)) System.out.println("ERROR: " + s);

            Main.ar.get(n).category_id = map.get(s);
        }
    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        buf.write(ch, start, length);
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("title"))
            Main.ar.get(n).title = buf.toString().replace("\n ", "");
        else if (qName.equals("updated"))
            Main.ar.get(n).published = buf.toString().substring(0, 10);
        else if (qName.equals("summary"))
            Main.ar.get(n).summary = buf.toString().replace("\n", "");
        else if (qName.equals("arxiv:comment"))
            Main.ar.get(n).comment = buf.toString();
        else if (qName.equals("arxiv:doi"))
            Main.ar.get(n).doi = buf.toString();
        else if (qName.equals("arxiv:journal_ref"))
            Main.ar.get(n).journal_ref = buf.toString();
        else if (qName.equals("name"))
            Main.ar.get(n).authors.add(buf.toString());
        else if (qName.equals("arxiv:affiliation"))
            Main.ar.get(n).affiliations.add(buf.toString());
        else if (qName.equals("entry")) {
            if (Main.ar.get(n).category_id == 0)
                Main.ar.remove(n);
            else {
                Main.ar.get(n).users.add("root");
                ++n;
            }
        }

        buf.reset();
    }


    @Override
    public void endDocument() throws SAXException {

    }


}
