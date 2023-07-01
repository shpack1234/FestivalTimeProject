package com.festivaltime.festivaltimeproject;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Queue;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiReader {
    public interface ApiResponseListener {
        void onSuccess(String response);
        void onError(String error);
    }
    private OkHttpClient client;

    public ApiReader() {
        client=new OkHttpClient();
    }

    public void searchKeyword(String serviceKey, String keyword, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchKeyword1")
                    .addQueryParameter("numOfRows", "10")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("keyword", keyword)
                    .addQueryParameter("contentTypeId", "15")
                    .addQueryParameter("serviceKey", serviceKey);

            String url = urlBuilder.build().toString();
            Log.d("url", url);
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    listener.onError("Network Error");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        listener.onSuccess(responseData);
                    } else {
                        listener.onError("Server Error: " + response.code());
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            listener.onError("URL Encoding Error");
        }

    }
    public void detailCommon(String serviceKey, String contentId, final ApiResponseListener listener) {
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("detailCommon1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("contentId", contentId)
                    .addQueryParameter("defaultYN", "Y")
                    .addQueryParameter("firstImageYN", "Y")
                    .addQueryParameter("addrinfoYN", "Y")
                    .addQueryParameter("overviewYN", "Y")
                    .addQueryParameter("serviceKey", serviceKey);

            String url = urlBuilder.build().toString();
            Log.d("url", url);
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    listener.onError("Network Error");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        listener.onSuccess(responseData);
                    } else {
                        listener.onError("Server Error: " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onError("URL Encoding Error");
        }

    }
}
