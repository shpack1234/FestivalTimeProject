package com.festivaltime.festivaltimeproject;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
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
    public void searchKeyword(String serviceKey, String keyword, String cat, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchKeyword1")
                    .addQueryParameter("numOfRows", "100000")
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

    public Single<String> searchKeyword(String serviceKey, String keyword, String cat) {
        return Single.create(emitter -> {
            try {
                HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .addPathSegment("B551011")
                        .addPathSegment("KorService1")
                        .addPathSegment("searchKeyword1")
                        .addQueryParameter("numOfRows", "100000")
                        .addQueryParameter("MobileOS", "AND")
                        .addQueryParameter("MobileApp", "FestivalTime")
                        .addQueryParameter("keyword", keyword)
                        .addQueryParameter("contentTypeId", "15")
                        .addQueryParameter("serviceKey", serviceKey)
                        .addQueryParameter("cat3", cat);

                String url = urlBuilder.build().toString();
                Log.d(TAG, url);
                Request request = new Request.Builder().url(url).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        if (!emitter.isDisposed()) {
                            emitter.onError(e);
                        }
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            emitter.onError(new IOException(response.message()));
                        } else {
                            String responseData = response.body().string();
                            emitter.onSuccess(responseData);
                        }
                    }
                });
            } catch (Exception e) {
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
            }
        });
    }



    public void searchKeyword(String serviceKey, String keyword, String cat, int page, final ApiResponseListener listener) {
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
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

    public void searchKeyword2(String serviceKey, String keyword, String cat, int page, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
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
                    .addQueryParameter("serviceKey", serviceKey)
                    .addQueryParameter("cat2", cat)
                    .addQueryParameter("pageNo", String.valueOf(page)); // 추가된 부분;


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

    public void searchKeyword2(String serviceKey, String keyword, String cat, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchKeyword1")
                    .addQueryParameter("numOfRows", "100000")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("keyword", keyword)
                    .addQueryParameter("contentTypeId", "15")
                    .addQueryParameter("serviceKey", serviceKey)
                    .addQueryParameter("cat2", cat);

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

    public Single<String> searchKeyword2(String serviceKey, String keyword, String cat){
        return Single.create(emitter -> {
            try {
                HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .addPathSegment("B551011")
                        .addPathSegment("KorService1")
                        .addPathSegment("searchKeyword1")
                        .addQueryParameter("numOfRows", "100000")
                        .addQueryParameter("MobileOS", "AND")
                        .addQueryParameter("MobileApp", "FestivalTime")
                        .addQueryParameter("keyword", keyword)
                        .addQueryParameter("contentTypeId", "15")
                        .addQueryParameter("serviceKey", serviceKey)
                        .addQueryParameter("cat2", cat);

                String url = urlBuilder.build().toString();
                Log.d(TAG, "URL: " + url);
                Request request = new Request.Builder().url(url).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        if (!emitter.isDisposed()) {
                            emitter.onError(e);
                        }
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            if (!emitter.isDisposed()) {
                                emitter.onSuccess(responseData);
                            }
                        } else {
                            if (!emitter.isDisposed()) {
                                emitter.onError(new IOException(response.message()));
                            }
                        }
                    }
                });
            } catch (Exception e) {
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
            }
        });
    }




    public void searchKeyword2(String serviceKey, String areacode, String keyword, String cat, final ApiResponseListener listener){
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
                    .addQueryParameter("serviceKey", serviceKey)
                    .addQueryParameter("areaCode", areacode)
                    .addQueryParameter("cat2", cat);

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

    public Observable<String> searchKeyword2(String serviceKey, String areacode, String keyword, String cat) {
        return Observable.create(emitter -> {
            try {
                HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .addPathSegment("B551011")
                        .addPathSegment("KorService1")
                        .addPathSegment("searchKeyword1")
                        .addQueryParameter("numOfRows", "100000")
                        .addQueryParameter("MobileOS", "AND")
                        .addQueryParameter("MobileApp", "FestivalTime")
                        .addQueryParameter("keyword", keyword)
                        .addQueryParameter("contentTypeId", "15")
                        .addQueryParameter("serviceKey", serviceKey)
                        .addQueryParameter("areaCode", areacode)
                        .addQueryParameter("cat2", cat);

                String url = urlBuilder.build().toString();
                Log.d(TAG, "url: " + url);

                Request request = new Request.Builder().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        emitter.onError(e); // 네트워크 오류를 전달합니다.
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            emitter.onNext(responseData); // 응답 데이터를 전달합니다.
                            emitter.onComplete(); // 완료 이벤트를 전달합니다.
                        } else {
                            emitter.onError(new IOException(
                                    "Server Error: " + response.code()));  // 서버 오류를 전달합니다.
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);  // URL 인코딩 오류 등의 예외를 전달합니다.
            }
        });
    }

    public Observable<String> searchKeyword3(String serviceKey, String areacode, String keyword, String cat) {
        return Observable.create(emitter -> {
            try {
                HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .addPathSegment("B551011")
                        .addPathSegment("KorService1")
                        .addPathSegment("searchKeyword1")
                        .addQueryParameter("numOfRows", "100000")
                        .addQueryParameter("MobileOS", "AND")
                        .addQueryParameter("MobileApp", "FestivalTime")
                        .addQueryParameter("keyword", keyword)
                        .addQueryParameter("contentTypeId", "15")
                        .addQueryParameter("serviceKey", serviceKey)
                        .addQueryParameter("areaCode", areacode)
                        .addQueryParameter("cat3", cat);

                String url = urlBuilder.build().toString();
                Log.d(TAG, "url: " + url);

                Request request = new Request.Builder().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        emitter.onError(e); // 네트워크 오류를 전달합니다.
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            emitter.onNext(responseData); // 응답 데이터를 전달합니다.
                            emitter.onComplete(); // 완료 이벤트를 전달합니다.
                        } else {
                            emitter.onError(new IOException(
                                    "Server Error: " + response.code()));  // 서버 오류를 전달합니다.
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);  // URL 인코딩 오류 등의 예외를 전달합니다.
            }
        });
    }

    //지역기반서치 목록별
    public void searchLocation(String serviceKey, String areacode, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("areaBasedList1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("listYN", "Y")
                    .addQueryParameter("numOfRows", "100000")
                    .addQueryParameter("contentTypeId", "15")
                    .addQueryParameter("areaCode", areacode)
                    .addQueryParameter("serviceKey", serviceKey);
            String url = urlBuilder.build().toString();
            Log.d("Location search url: ", url);
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

    //상세정보(이용시간 등) 검색
    public void FestivalInfo(String serviceKey, String contentID, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("detailIntro1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("contentId", contentID)
                    .addQueryParameter("contentTypeId", "15")
                    .addQueryParameter("serviceKey", serviceKey);
            String url = urlBuilder.build().toString();
            Log.d("Location search url: ", url);
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

    public Observable<String> FestivalInfo(String serviceKey, String contentID) {
        return Observable.create(emitter -> {
            try {
                HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .addPathSegment("B551011")
                        .addPathSegment("KorService1")
                        .addPathSegment("detailIntro1")
                        .addQueryParameter("MobileOS", "AND")
                        .addQueryParameter("MobileApp", "FestivalTime")
                        .addQueryParameter("contentId", contentID)
                        .addQueryParameter("contentTypeId", "15")
                        .addQueryParameter("serviceKey", serviceKey);

                String url = urlBuilder.build().toString();
                Log.d(TAG, "Location search url: " + url);

                Request request = new Request.Builder().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        emitter.onError(e); // 네트워크 오류를 전달합니다.
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            emitter.onNext(responseData); // 응답 데이터를 전달합니다.
                            emitter.onComplete(); // 완료 이벤트를 전달합니다.
                        } else {
                            emitter.onError(new IOException(
                                    "Server Error: " + response.code()));  // 서버 오류를 전달합니다.
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);  // URL 인코딩 오류 등의 예외를 전달합니다.
            }
        });
    }


    //상세정보 검색
    public void FestivalInfo2(String serviceKey, String contentID, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("detailInfo1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("contentId", contentID)
                    .addQueryParameter("contentTypeId", "15")
                    .addQueryParameter("serviceKey", serviceKey);
            String url = urlBuilder.build().toString();
            Log.d("Location search url: ", url);
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

    //행사정보조회 목록별(지역기반)
    public void FestivallitLoc(String serviceKey, String selectDate, String areacode, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchFestival1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("listYN", "Y")
                    .addQueryParameter("arrange", "O")
                    .addQueryParameter("areaCode", areacode)
                    .addQueryParameter("numOfRows", "10000000")
                    .addQueryParameter("eventStartDate", selectDate)
                    .addQueryParameter("eventEndDate", selectDate)
                    .addQueryParameter("serviceKey", serviceKey);
            String url = urlBuilder.build().toString();
            Log.d("FestivalN url: ", url);
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

    public void FestivallitLoc(String serviceKey, String startDate, String endDate, String areacode, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchFestival1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("listYN", "Y")
                    .addQueryParameter("arrange", "O")
                    .addQueryParameter("areaCode", areacode)
                    .addQueryParameter("numOfRows", "10000000")
                    .addQueryParameter("eventStartDate", startDate)
                    .addQueryParameter("eventEndDate", endDate)
                    .addQueryParameter("serviceKey", serviceKey);
            String url = urlBuilder.build().toString();
            Log.d("FestivalN url: ", url);
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

    public void Festivallit2(String serviceKey, String startdate, String enddate, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchFestival1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("listYN", "Y")
                    .addQueryParameter("numOfRows", "100000")
                    .addQueryParameter("eventStartDate", startdate)
                    .addQueryParameter("eventEndDate", enddate)
                    .addQueryParameter("serviceKey", serviceKey);
            String url = urlBuilder.build().toString();
            Log.d("FestivalN url: ", url);
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

    //행사정보조회 목록별
    public void Festivallit(String serviceKey, String selectDate, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchFestival1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("listYN", "Y")
                    .addQueryParameter("numOfRows", "100000")
                    .addQueryParameter("eventStartDate", selectDate)
                    .addQueryParameter("eventEndDate", selectDate)
                    .addQueryParameter("serviceKey", serviceKey);
            String url = urlBuilder.build().toString();
            Log.d("FestivalN url: ", url);
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

    public Observable<String> Festivallit(String serviceKey, String selectDate) {
        return Observable.create(emitter -> {
            try {
                HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .addPathSegment("B551011")
                        .addPathSegment("KorService1")
                        .addPathSegment("searchFestival1")
                        .addQueryParameter("MobileOS", "AND")
                        .addQueryParameter("MobileApp", "FestivalTime")
                        .addQueryParameter("listYN", "Y")
                        .addQueryParameter("numOfRows", "100000")
                        .addQueryParameter("eventStartDate", selectDate)
                        .addQueryParameter("eventEndDate", selectDate)
                        .addQueryParameter("serviceKey", serviceKey);
                String url = urlBuilder.build().toString();
                Log.d(TAG, "FestivalN url: "+url);
                Request request = new Request.Builder().url(url).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        emitter.onError(e); // emit error
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            emitter.onNext(responseData); // emit the data received from the API
                            emitter.onComplete(); // complete the emission of data
                        } else {
                            emitter.onError(new Exception ("Server Error: "+response.code()));
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e); // emit error
            }
        });
    }


    //행사정보조회 개수별
    public void FestivalN(String serviceKey, String selectDate, final ApiResponseListener listener) {
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchFestival1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("_type", "json")
                    .addQueryParameter("listYN", "N")
                    .addQueryParameter("eventStartDate", selectDate)
                    .addQueryParameter("eventEndDate", selectDate)
                    .addQueryParameter("serviceKey", serviceKey);
            String url = urlBuilder.build().toString();
            Log.d("FestivalN url: ", url);
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

                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            JSONObject responseObj = jsonObject.getJSONObject("response");
                            JSONObject bodyObj = responseObj.getJSONObject("body");
                            JSONObject itemsObj = bodyObj.getJSONObject("items");
                            JSONArray itemArray = itemsObj.getJSONArray("item");

                            if (itemArray.length() > 0) {
                                JSONObject itemObj = itemArray.getJSONObject(0);
                                int totalCnt = itemObj.getInt("totalCnt");
                                listener.onSuccess(String.valueOf(totalCnt));
                            } else {
                                listener.onSuccess("");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError("JSON Parsing Error");
                        }

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

    public void searchFestival(String serviceKey, String areacode, String startdate, String enddate, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("searchFestival1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("areaCode", areacode)
                    .addQueryParameter("numOfRows", "10")
                    .addQueryParameter("eventStartDate", startdate)
                    .addQueryParameter("eventEndDate", enddate)
                    .addQueryParameter("serviceKey", serviceKey);
            String url = urlBuilder.build().toString();
            Log.d("FestivalN url: ", url);
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
                    .addQueryParameter("mapinfoYN", "Y")
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

    public void detailIntro(String serviceKey, String contentId, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("detailIntro1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("contentId", contentId)
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

    public void searchImg(String serviceKey, String contentId, final ApiResponseListener listener){
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder() // 수정된 부분
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B551011")
                    .addPathSegment("KorService1")
                    .addPathSegment("detailImage1")
                    .addQueryParameter("MobileOS", "AND")
                    .addQueryParameter("MobileApp", "FestivalTime")
                    .addQueryParameter("contentId", contentId)
                    .addQueryParameter("imageYN", "Y")
                    .addQueryParameter("subImageYN", "Y")
                    .addQueryParameter("numOfRows", "100000")
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

    //휴가 한국천문연구원API
    public void holiday(String serviceKey, String Solyear, String SolMonth, final ApiResponseListener listener) {
        try {
            HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .addPathSegment("B090041")
                    .addPathSegment("openapi")
                    .addPathSegment("service")
                    .addPathSegment("SpcdeInfoService")
                    .addPathSegment("getHoliDeInfo")
                    .addQueryParameter("serviceKey", serviceKey)
                    .addQueryParameter("solYear", Solyear)
                    .addQueryParameter("solMonth", SolMonth);
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
