package com.festivaltime.festivaltimeproject;

import android.util.Log;

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



    public void searchKeyword(String serviceKey, String keyword, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchKeyword1")
                    .addQueryParameter("numOfRows", "11")
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
    public void searchKeyword(String serviceKey, String keyword, String cat, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchKeyword1")
                    .addQueryParameter("numOfRows", "11")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("keyword", keyword)
                    .addQueryParameter("contentTypeId", "15")
                    .addQueryParameter("serviceKey", serviceKey)
                    .addQueryParameter("cat3", cat);

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


    public void searchKeyword(String serviceKey, String keyword, String cat, int page, final ApiResponseListener listener) {
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchKeyword1")
                    .addQueryParameter("numOfRows", "11")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("keyword", keyword)
                    .addQueryParameter("contentTypeId", "15")
                    .addQueryParameter("serviceKey", serviceKey)
                    .addQueryParameter("cat3", cat)
                    .addQueryParameter("pageNo", String.valueOf(page)); // 추가된 부분

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

    public void searchKeyword2(String serviceKey, String keyword, String cat2, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchKeyword1")
                    .addQueryParameter("numOfRows", "11")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("keyword", keyword)
                    .addQueryParameter("contentTypeId", "15")
                    .addQueryParameter("serviceKey", serviceKey)
                    .addQueryParameter("cat2", cat2);

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

    public void FestivalN(String serviceKey, String selectDate, final ApiResponseListener listener) {
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchFestival")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("listYN", "N")
                    .addQueryParameter("eventStartDate", selectDate)
                    .addQueryParameter("eventEndDate", selectDate)
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

    public void categoryCode1(String serviceKey, String cat, final ApiResponseListener listener) {
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("categoryCode1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("cat2", cat)
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

    public void areaBasedSync(String serviceKey, String areaNum, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("areaBasedSyncList1")
                    .addQueryParameter("numOfRows", "1000")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("contentTypeId", "15")
                    .addQueryParameter("serviceKey", serviceKey)
                    .addQueryParameter("areaCode", areaNum);

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
}
