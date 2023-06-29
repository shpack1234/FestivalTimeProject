package com.festivaltime.festivaltimeproject;

import androidx.annotation.NonNull;

import java.io.IOException;

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

    public void searchKeword(String serviceKey, String keyword, final ApiResponseListener listener) {
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                .scheme("https")
                .host("apis.data.go.kr")
                .addPathSegment("B551973")
                .addPathSegment("KorService1")
                .addPathSegment("searchFestival1")
                .addQueryParameter("ServiceKey", serviceKey)
                .addQueryParameter("eventStartDate", "20210101")
                .addQueryParameter("eventEndDate", "20251231")
                .addQueryParameter("arrange", "P")
                .addQueryParameter("listYN", "Y")
                .addQueryParameter("MobileOS", "AND")
                .addQueryParameter("MobileApp", "AppTest")
                .addQueryParameter("keyword", keyword)
                .addQueryParameter("_type", "json");
        String url = urlBuilder.build().toString();

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
}
