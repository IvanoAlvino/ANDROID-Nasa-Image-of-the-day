package com.example.ivano.nasaiotd;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class IotdHandler extends DefaultHandler {

    private String url = "http://www.nasa.gov/rss/image_of_the_day.rss";
    private boolean inUrl = false;
    private boolean inTitle = false;
    private boolean inDescription = false;
    private boolean inItem = false;
    private boolean inDate = false;
    private Bitmap image = null;
    private String title = null;
    private StringBuffer description = new StringBuffer();
    private String date = null;
    private String imageUrl = null;

    public void processFeed() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(this);
            InputStream inputStream = new URL(url).openStream();
            description.delete(0, description.length());
            reader.parse(new InputSource(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Bitmap getBitmap(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            // scale bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;

            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            input.close();
            return bitmap;
        } catch (IOException ioe) {
            return null;
        }
    }


    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (localName.equals("item")) {
            inItem = true;
        }

        if (inItem) {
            if (localName.equals("title")) {
                inTitle = true;
            } else {
                inTitle = false;
            }

            if (localName.equals("description")) {
                inDescription = true;
            } else {
                inDescription = false;
            }

            if (localName.equals("pubDate")) {
                inDate = true;
            } else {
                inDate = false;
            }

            if (localName.startsWith("enclosure")) {
                inUrl = true;
                imageUrl = attributes.getValue("url");
            } else {
                inUrl = false;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("item")) {
            inItem = false;
            throw new SAXException();
        }
        super.endElement(uri, localName, qName);
    }

    public void characters(char ch[], int start, int length) {
        String chars = new String(ch).substring(start, start + length);
        if (inUrl && image == null) {
            image = getBitmap(imageUrl);
        }
        if (inTitle && title == null) {
            title = chars;
        }
        if (inDescription ) {
            description.append(chars);
        }
        if (inDate && date == null) {
            date = chars;
        }
    }

    public Bitmap getImage() { return image; }
    public String getTitle() { return title; }
    public StringBuffer getDescription() { return description; }
    public String getDate() { return date; }
}