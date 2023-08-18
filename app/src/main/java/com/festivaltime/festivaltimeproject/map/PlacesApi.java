package com.festivaltime.festivaltimeproject.map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface PlacesApi {
    @GET("/v2/local/search/keyword.json")
    Call<ApiResponseModel> searchPlaces(
            @Query("query") String query,
            @Query("x") double longitude,
            @Query("y") double latitude,
            @Query("radius") int radius,
            @Query("apikey") String apiKey
    );
}