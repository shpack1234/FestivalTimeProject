package com.festivaltime.festivaltimeproject.map;

public class PoiItem {

    private String place_name;
    private String x; // 경도
    private String y; // 위도

    public String getName() {
        return place_name;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }
}