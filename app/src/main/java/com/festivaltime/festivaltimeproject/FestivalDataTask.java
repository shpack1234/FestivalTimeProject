package com.festivaltime.festivaltimeproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class FestivalDataTask extends AsyncTask<Void, Void, String> {
    private TextView festivalNumTextView;
    private String apiKey;
    private String todayDate;

    public FestivalDataTask(String apiKey, String todayDate, TextView festivalNumTextView) {
        this.apiKey = apiKey;
        this.todayDate = todayDate;
        this.festivalNumTextView = festivalNumTextView;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = "";

        try {
            // ApiReader 클래스의 인스턴스 생성
            ApiReader apiReader = new ApiReader();

            // 데이터를 가져오는 작업 수행
            apiReader.FestivalN(apiKey, todayDate, new ApiReader.ApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    // 가져온 데이터를 메인 스레드에서 UI에 설정
                    festivalNumTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            festivalNumTextView.setText(response);
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error: " + error);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        // 결과 처리 부분
    }
}
