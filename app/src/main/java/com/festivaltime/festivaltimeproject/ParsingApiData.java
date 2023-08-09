package com.festivaltime.festivaltimeproject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ParsingApiData {
    private static List<LinkedHashMap<String, String>> festivalList = new ArrayList<>();

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
                    LinkedHashMap<String, String> festivalInfo = new LinkedHashMap<>();

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

    public static void parseXmlDataFromDetailCommon(String xmlData) {
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
                    LinkedHashMap<String, String> festivalInfo = new LinkedHashMap<>();

                    String title = getElementText(itemElement, "title");
                    String address1 = getElementText(itemElement, "addr1");
                    String address2 = getElementText(itemElement, "addr2");
                    String img = getElementText(itemElement, "firstimage2");
                    String overview=getElementText(itemElement, "overview");
                    String contentid=getElementText(itemElement, "contentid");

                    festivalInfo.put("title", title);
                    festivalInfo.put("address1", address1);
                    festivalInfo.put("address2", address2);
                    festivalInfo.put("img", img);
                    festivalInfo.put("overview", overview);
                    festivalInfo.put("contentid", contentid);

                    festivalList.add(festivalInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseXmlDataFromCategoryCode(String xmlData, String cat2Filter) {
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
                    LinkedHashMap<String, String> festivalInfo = new LinkedHashMap<>();

                    String title = getElementText(itemElement, "title");
                    String img = getElementText(itemElement, "firstimage2");
                    String overview=getElementText(itemElement, "overview");
                    String contentid=getElementText(itemElement, "contentid");
                    String cat2 = getElementText(itemElement, "cat2");

                    if (cat2Filter != null || cat2Filter.equals(cat2)) {
                        festivalInfo.put("title", title);
                        festivalInfo.put("img", img);
                        festivalInfo.put("overview", overview);
                        festivalInfo.put("contentid", contentid);
                        festivalInfo.put("cat2", cat2);

                        festivalList.add(festivalInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<LinkedHashMap<String, String>> getFestivalList() {
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