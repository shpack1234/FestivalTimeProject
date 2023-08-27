package com.festivaltime.festivaltimeproject.map;

import com.google.gson.annotations.SerializedName;

public class PoiItem {

    @SerializedName("address_name")
    private String addressName;

    @SerializedName("place_name")
    private String placeName;

    @SerializedName("place_url")
    private String placeUrl;
    @SerializedName("id")
    private String id;

    @SerializedName("category_group_name")
    private String categoryGroupName;
    private String x; // 경도
    private String y; // 위도

    public String getAddressName() {
        return addressName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceUrl() {
        return placeUrl;
    }


    public String getId() {
        return id;
    }

    public String getCategoryGroupName() {
        return categoryGroupName;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }
}