package com.festivaltime.festivaltimeproject.map;

import com.google.gson.Gson;

import java.util.List;

public class ApiResponseModel {
    private List<PoiItem> documents;

    public ApiResponseModel(List<PoiItem> places) {
        this.documents = documents;
    }

    public List<PoiItem> getDocuments() {
        return documents;
    }

    public String getJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
