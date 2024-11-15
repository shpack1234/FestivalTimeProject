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
    private static List<LinkedHashMap<String, String>> holidaylist = new ArrayList<>();

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
                    String mapx = getElementText(itemElement, "mapx");
                    String mapy = getElementText(itemElement, "mapy");

                    festivalInfo.put("title", title);
                    festivalInfo.put("address", address);
                    festivalInfo.put("tel", tel);
                    festivalInfo.put("img", img);
                    festivalInfo.put("contentid", contentid);
                    festivalInfo.put("mapx", mapx);
                    festivalInfo.put("mapy", mapy);

                    festivalList.add(festivalInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseXmlDataFromSearchKeyword(String xmlData, String cat2Filter, String cat3Filter) {
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
                    String overview = getElementText(itemElement, "overview");
                    String contentid = getElementText(itemElement, "contentid");
                    String cat2 = getElementText(itemElement, "cat2");
                    String cat3 = getElementText(itemElement, "cat3");

                    // cat2Filter와 cat3Filter를 기반으로 데이터를 선택적으로 추가
                    if ((cat2Filter == null || cat2.equals(cat2Filter)) &&
                            (cat3Filter == null || cat3.equals(cat3Filter))) {
                        festivalInfo.put("title", title);
                        festivalInfo.put("img", img);
                        festivalInfo.put("overview", overview);
                        festivalInfo.put("contentid", contentid);
                        festivalInfo.put("cat2", cat2);
                        festivalInfo.put("cat3", cat3);

                        festivalList.add(festivalInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseXmlDataFromSearchKeyword3(String xmlData, String cat2Filter, String cat3Filter) {
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
                    String overview = getElementText(itemElement, "overview");
                    String contentid = getElementText(itemElement, "contentid");
                    String address = getElementText(itemElement, "addr1");
                    String areacode = getElementText(itemElement, "areacode");
                    String cat2 = getElementText(itemElement, "cat2");
                    String cat3 = getElementText(itemElement, "cat3");

                    // cat2Filter와 cat3Filter를 기반으로 데이터를 선택적으로 추가
                    if ((cat2Filter == null || cat2.equals(cat2Filter)) &&
                            (cat3Filter == null || cat3.equals(cat3Filter))) {
                        festivalInfo.put("title", title);
                        festivalInfo.put("img", img);
                        festivalInfo.put("overview", overview);
                        festivalInfo.put("contentid", contentid);
                        festivalInfo.put("areacode", areacode);
                        festivalInfo.put("address", address);
                        festivalInfo.put("cat2", cat2);
                        festivalInfo.put("cat3", cat3);

                        festivalList.add(festivalInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<LinkedHashMap<String, String>> parseXmlDataFromSearchKeyword2(String xmlData, String cat2Filter, String cat3Filter) {
        List<LinkedHashMap<String, String>> resultList = new ArrayList<>();

        resultList.clear();
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
                    String overview = getElementText(itemElement, "overview");
                    String contentid = getElementText(itemElement, "contentid");
                    String cat2 = getElementText(itemElement, "cat2");
                    String cat3 = getElementText(itemElement, "cat3");

                    // cat2Filter와 cat3Filter를 기반으로 데이터를 선택적으로 추가
                    if ((cat2Filter == null || cat2.equals(cat2Filter)) &&
                            (cat3Filter == null || cat3.equals(cat3Filter))) {
                        festivalInfo.put("title", title);
                        festivalInfo.put("img", img);
                        festivalInfo.put("overview", overview);
                        festivalInfo.put("contentid", contentid);
                        festivalInfo.put("cat2", cat2);
                        festivalInfo.put("cat3", cat3);

                        resultList.add(festivalInfo); // 변경된 부분: resultList에 추가
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;  // 변경된 부분: 결과 반환
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
                    String img1 = getElementText(itemElement, "firstimage");
                    String img = getElementText(itemElement, "firstimage2");
                    String overview = getElementText(itemElement, "overview");
                    String contentid = getElementText(itemElement, "contentid");
                    String mapx=getElementText(itemElement, "mapx");
                    String mapy=getElementText(itemElement, "mapy");
                    String homepage=getElementText(itemElement, "homepage");

                    festivalInfo.put("title", title);
                    festivalInfo.put("address1", address1);
                    festivalInfo.put("address2", address2);
                    festivalInfo.put("img", img);
                    festivalInfo.put("img1", img1);
                    festivalInfo.put("overview", overview);
                    festivalInfo.put("contentid", contentid);
                    festivalInfo.put("mapx", mapx);
                    festivalInfo.put("mapy", mapy);
                    festivalInfo.put("homepage", homepage);

                    festivalList.add(festivalInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //지역기반
    public static void parseXmlDataFromDetail2(String xmlData) {
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
                    String img = getElementText(itemElement, "firstimage");
                    String contentid = getElementText(itemElement, "contentid");
                    String startdate = getElementText(itemElement, "eventstartdate");
                    String enddate = getElementText(itemElement, "eventenddate");

                    festivalInfo.put("eventstartdate", startdate);
                    festivalInfo.put("eventenddate", enddate);
                    festivalInfo.put("title", title);
                    festivalInfo.put("address1", address1);
                    festivalInfo.put("address2", address2);
                    festivalInfo.put("img", img);
                    festivalInfo.put("contentid", contentid);

                    festivalList.add(festivalInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //축제 상세정보 불러옴
    public static void parseXmlDataFromDetailInfo(String xmlData) {
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

                    String tel = getElementText(itemElement, "sponsor1tel");
                    String startdate = getElementText(itemElement, "eventstartdate");
                    String enddate = getElementText(itemElement, "eventenddate");
                    String playtime = getElementText(itemElement, "playtime");
                    String place = getElementText(itemElement, "eventplace");
                    String usetime = getElementText(itemElement, "usetimefestival");//입장 가격
                    String contentid = getElementText(itemElement, "contentid");

                    festivalInfo.put("sponsor1tel", tel);
                    festivalInfo.put("eventstartdate", startdate);
                    festivalInfo.put("eventenddate", enddate);
                    festivalInfo.put("playtime", playtime);
                    festivalInfo.put("eventplace", place);
                    festivalInfo.put("usetimefestival", usetime);
                    festivalInfo.put("contentid", contentid);

                    festivalList.add(festivalInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //축제 상세정보2 불러옴
    public static void parseXmlDataFromDetailInfo2(String xmlData) {
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

                    String serialnum = getElementText(itemElement, "serialnum");
                    String infotext = getElementText(itemElement, "infotext");
                    String contentid = getElementText(itemElement, "contentid");

                    festivalInfo.put("serialnum", serialnum);
                    festivalInfo.put("infotext", infotext);
                    festivalInfo.put("contentid", contentid);

                    festivalList.add(festivalInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void parseXmlDataFromCategoryCode(String xmlData, String cat3Filter) {
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
                    String overview = getElementText(itemElement, "overview");
                    String contentid = getElementText(itemElement, "contentid");
                    String cat3 = getElementText(itemElement, "cat3");

                    if (cat3Filter != null || cat3Filter.equals(cat3)) {
                        festivalInfo.put("title", title);
                        festivalInfo.put("img", img);
                        festivalInfo.put("overview", overview);
                        festivalInfo.put("contentid", contentid);
                        festivalInfo.put("cat3", cat3);

                        festivalList.add(festivalInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseXmlDataFromSearchFestival(String xmlData, String cat2Filter, String cat3Filter) {
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
                    String overview = getElementText(itemElement, "overview");
                    String contentid = getElementText(itemElement, "contentid");
                    String areacode = getElementText(itemElement, "areacode");
                    String startdate = getElementText(itemElement, "eventstartdate");
                    String enddate = getElementText(itemElement, "eventenddate");
                    String cat2 = getElementText(itemElement, "cat2");
                    String cat3 = getElementText(itemElement, "cat3");


                    if (cat2Filter != null && cat2Filter.equals("A0207")) {
                        festivalInfo.put("title", title);
                        festivalInfo.put("img", img);
                        festivalInfo.put("overview", overview);
                        festivalInfo.put("contentid", contentid);
                        festivalInfo.put("areacode", areacode);
                        festivalInfo.put("eventstartdate", startdate);
                        festivalInfo.put("eventenddate", enddate);
                        festivalInfo.put("cat3", cat3);

                        festivalList.add(festivalInfo);
                    }
                    if (cat3Filter != null && cat3Filter.equals(cat3)) {
                        festivalInfo.put("title", title);
                        festivalInfo.put("img", img);
                        festivalInfo.put("overview", overview);
                        festivalInfo.put("contentid", contentid);
                        festivalInfo.put("areacode", areacode);
                        festivalInfo.put("eventstartdate", startdate);
                        festivalInfo.put("eventenddate", enddate);
                        festivalInfo.put("cat2", cat2);

                        festivalList.add(festivalInfo);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<LinkedHashMap<String, String>> parseXmlDataFromSearchFestival2(String xmlData) {
        List<LinkedHashMap<String, String>> resultList = new ArrayList<>();
        resultList.clear();
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
                    String overview = getElementText(itemElement, "overview");
                    String contentid = getElementText(itemElement, "contentid");
                    String areacode = getElementText(itemElement, "areacode");
                    String startdate = getElementText(itemElement, "eventstartdate");
                    String enddate = getElementText(itemElement, "eventenddate");
                    String cat2 = getElementText(itemElement, "cat2");
                    String cat3 = getElementText(itemElement, "cat3");


                    /**
                     if (cat2Filter != null && cat2Filter.equals("A0207")) {
                     festivalInfo.put("title", title);
                     festivalInfo.put("img", img);
                     festivalInfo.put("overview", overview);
                     festivalInfo.put("contentid", contentid);
                     festivalInfo.put("areacode", areacode);
                     festivalInfo.put("eventstartdate", startdate);
                     festivalInfo.put("eventenddate", enddate);
                     festivalInfo.put("cat3", cat3);

                     resultList.add(festivalInfo);
                     }
                     if (cat3Filter != null && cat3Filter.equals(cat3)) {
                     festivalInfo.put("title", title);
                     festivalInfo.put("img", img);
                     festivalInfo.put("overview", overview);
                     festivalInfo.put("contentid", contentid);
                     festivalInfo.put("areacode", areacode);
                     festivalInfo.put("eventstartdate", startdate);
                     festivalInfo.put("eventenddate", enddate);
                     festivalInfo.put("cat2", cat2);

                     resultList.add(festivalInfo);
                     }
                     **/

                    festivalInfo.put("title", title);
                    festivalInfo.put("img", img);
                    festivalInfo.put("overview", overview);
                    festivalInfo.put("contentid", contentid);
                    festivalInfo.put("areacode", areacode);
                    festivalInfo.put("eventstartdate", startdate);
                    festivalInfo.put("eventenddate", enddate);

                    resultList.add(festivalInfo);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static void parseXmlDataFromAreaBasedSync(String xmlData) {
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
                    String mapx = getElementText(itemElement, "mapx");
                    String mapy = getElementText(itemElement, "mapy");
                    String contentid = getElementText(itemElement, "contentid");

                    festivalInfo.put("title", title);
                    festivalInfo.put("mapx", mapx);
                    festivalInfo.put("mapy", mapy);
                    festivalInfo.put("contentid", contentid);

                    festivalList.add(festivalInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //행사정보조회 피싱
    public static void parseXmlDataFromFestivalA(String xmlData) {
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
                    LinkedHashMap<String, String> festivalInfo_cat = new LinkedHashMap<>();

                    String title = getElementText(itemElement, "title");
                    String mapx = getElementText(itemElement, "mapx");
                    String mapy = getElementText(itemElement, "mapy");
                    String contentid = getElementText(itemElement, "contentid");
                    String img = getElementText(itemElement, "firstimage");
                    //분류위한 카테고리받아옴
                    String cat2 = getElementText(itemElement, "cat2");
                    String cat3 = getElementText(itemElement, "cat3");
                    String startdate = getElementText(itemElement, "eventstartdate");
                    String enddate = getElementText(itemElement, "eventenddate");
                    String address = getElementText(itemElement, "addr1");

                    festivalInfo_cat.put("address", address);
                    festivalInfo_cat.put("title", title);
                    festivalInfo_cat.put("mapx", mapx);
                    festivalInfo_cat.put("mapy", mapy);
                    festivalInfo_cat.put("contentid", contentid);
                    festivalInfo_cat.put("img", img);
                    festivalInfo_cat.put("cat2", cat2);
                    festivalInfo_cat.put("cat3", cat3);
                    festivalInfo_cat.put("eventstartdate", startdate);
                    festivalInfo_cat.put("eventenddate", enddate);

                    if ("A0207".equals(cat2)) {
                        festivalList.add(festivalInfo_cat);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseXmlDataFromFestival(String xmlData, String cat3Filter) {
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
                    LinkedHashMap<String, String> festivalInfo_cat = new LinkedHashMap<>();

                    String title = getElementText(itemElement, "title");
                    String mapx = getElementText(itemElement, "mapx");
                    String mapy = getElementText(itemElement, "mapy");
                    String contentid = getElementText(itemElement, "contentid");
                    String img = getElementText(itemElement, "firstimage");
                    //분류위한 카테고리받아옴
                    String cat2 = getElementText(itemElement, "cat2");
                    String cat3 = getElementText(itemElement, "cat3");
                    String startdate = getElementText(itemElement, "eventstartdate");
                    String enddate = getElementText(itemElement, "eventenddate");
                    String address = getElementText(itemElement, "addr1");

                    festivalInfo_cat.put("address", address);
                    festivalInfo_cat.put("title", title);
                    festivalInfo_cat.put("mapx", mapx);
                    festivalInfo_cat.put("mapy", mapy);
                    festivalInfo_cat.put("contentid", contentid);
                    festivalInfo_cat.put("img", img);
                    festivalInfo_cat.put("cat2", cat2);
                    festivalInfo_cat.put("cat3", cat3);
                    festivalInfo_cat.put("eventstartdate", startdate);
                    festivalInfo_cat.put("eventenddate", enddate);

                    if (cat3Filter.equals(cat3)) {
                        festivalList.add(festivalInfo_cat);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseXmlDataFromFestival(String xmlData) {
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
                    LinkedHashMap<String, String> festivalInfo_cat = new LinkedHashMap<>();

                    String title = getElementText(itemElement, "title");
                    String mapx = getElementText(itemElement, "mapx");
                    String mapy = getElementText(itemElement, "mapy");
                    String contentid = getElementText(itemElement, "contentid");
                    String img = getElementText(itemElement, "firstimage");
                    String img2 = getElementText(itemElement, "firstimage2");
                    String startdate = getElementText(itemElement, "eventstartdate");
                    String enddate = getElementText(itemElement, "eventenddate");
                    String address = getElementText(itemElement, "addr1");

                    festivalInfo_cat.put("address", address);
                    festivalInfo_cat.put("title", title);
                    festivalInfo_cat.put("mapx", mapx);
                    festivalInfo_cat.put("mapy", mapy);
                    festivalInfo_cat.put("contentid", contentid);
                    if (img ==null){
                        festivalInfo_cat.put("img", img2);
                    }else{
                    festivalInfo_cat.put("img", img);}
                    festivalInfo_cat.put("eventstartdate", startdate);
                    festivalInfo_cat.put("eventenddate", enddate);

                    festivalList.add(festivalInfo_cat);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseXmlDataFromdetailIntro(String xmlData) {
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

                    String eventstartdate = getElementText(itemElement, "eventstartdate");
                    String eventenddate = getElementText(itemElement, "eventenddate");
                    String eventplace = getElementText(itemElement, "eventplace");

                    festivalInfo.put("eventstartdate", eventstartdate);
                    festivalInfo.put("eventenddate", eventenddate);
                    festivalInfo.put("eventplace", eventplace);

                    festivalList.add(festivalInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseXmlDataFromHoliday(String xmlData) {
        holidaylist.clear();
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
                    LinkedHashMap<String, String> holidayInfo = new LinkedHashMap<>();

                    String dateName = getElementText(itemElement, "dateName");
                    String locdate = getElementText(itemElement, "locdate");

                    holidayInfo.put("dateName", dateName);
                    holidayInfo.put("locdate", locdate);

                    holidaylist.add(holidayInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<LinkedHashMap<String, String>> getFestivalList() {
        return festivalList;
    }

    public static List<LinkedHashMap<String, String>> getHolidayList() {
        return holidaylist;
    }

    private static String getElementText(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}