package com.festivaltime.festivaltimeproject.map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface PlacesApi {
    @Headers("Authorization: KakaoAK 117e462c5fa40e0dc33af107ca087840")
    @GET("/v2/local/search/keyword.json")
    Call<ApiResponseModel> searchPlaces(
            @Query("query") String query,
            @Query("x") double longitude,
            @Query("y") double latitude,
            @Query("radius") int radius
    );
}