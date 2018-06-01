import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.CharArrayWriter;

public class Handler extends DefaultHandler {
    Downloader pr;
    int cur_ar;

    int n;
    private boolean entry;
    private CharArrayWriter buf = new CharArrayWriter(100000);

    Handler(Downloader pr) {
        this.pr = pr;
        cur_ar = 0;
    }

    @Override
    public void startDocument() throws SAXException {
        entry = false;
        n = 0;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!entry)
            if (qName.equals("entry")) entry = true;
            else return;

        if (qName.equals("entry")) {
            pr.ar[cur_ar][n].title = null;
            pr.ar[cur_ar][n].summary = null;
            pr.ar[cur_ar][n].primary_category = null;
            pr.ar[cur_ar][n].ref = null;
            pr.ar[cur_ar][n].authors.clear();
            pr.ar[cur_ar][n].categories.clear();
            return;
        }

        buf.reset();

        if (qName.equals("link") && attributes.getValue("rel").equals("alternate")) {
            pr.ar[cur_ar][n].ref = attributes.getValue("href");
            return;
        }

        if (qName.equals("category")) {
            pr.ar[cur_ar][n].categories.add(attributes.getValue("term"));
            return;
        }

        if (qName.equals("arxiv:primary_category")) {
            pr.ar[cur_ar][n].primary_category = attributes.getValue("term");
            return;
        }

    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!entry) return;
        buf.write(ch, start, length);
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!entry) return;
        if (qName.equals("entry"))
            if (pr.ar[cur_ar][n].title != null) {
                pr.ar[cur_ar][n].flag = true;
                ++n;
            }

        if (qName.equals("title"))
            pr.ar[cur_ar][n].title = buf.toString();
        if (qName.equals("summary"))
            pr.ar[cur_ar][n].summary = buf.toString();
        if (qName.equals("name"))
            pr.ar[cur_ar][n].authors.add(buf.toString());

        buf.reset();
    }


    @Override
    public void endDocument() throws SAXException {
        cur_ar = (cur_ar + 1) % pr.N_BUF;
    }


}
