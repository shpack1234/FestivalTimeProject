package com.festivaltime.festivaltimeproject.map;

import java.util.List;

public class ApiResponseModel {
    private List<PoiItem> places;

    public ApiResponseModel(List<PoiItem> places) {
        this.places = places;
    }

    public List<PoiItem> getPlaces() {
        return this.places;
    }
}
