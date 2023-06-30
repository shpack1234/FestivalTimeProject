package com.festivaltime.festivaltimeproject;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParsingApiData {

    private static List<HashMap<String, String>> festivalList = new ArrayList<>();

    public static void parseXmlDataFromSearchKeyword(String xmlData) {
        festivalList.clear();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlData));
            Document document = builder.parse(is);

            Element rootElement = document.getDocumentElement();
            NodeList itemList = rootElement.getElementsByTagName("item");

            for (int i = 0; i < itemList.getLength(); i++) {
                Node itemNode = itemList.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;
                    HashMap<String, String> festivalInfo = new HashMap<>();

                    String title = getElementText(itemElement, "title");
                    String address = getElementText(itemElement, "addr1");
                    String tel = getElementText(itemElement, "tel");
                    String img = getElementText(itemElement, "firstimage");
                    String contentid = getElementText(itemElement, "contentid");

                    festivalInfo.put("title", title);
                    festivalInfo.put("address", address);
                    festivalInfo.put("tel", tel);
                    festivalInfo.put("img", img);
                    festivalInfo.put("contentid", contentid);

                    festivalList.add(festivalInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<HashMap<String, String>> getFestivalList() {
        return festivalList;
    }

    private static String getElementText(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}