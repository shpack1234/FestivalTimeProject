package com.festivaltime.festivaltimeproject.inquirydatabasepackage;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.festivaltime.festivaltimeproject.R;

import com.festivaltime.festivaltimeproject.InquiryPopupActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InquiryActivity extends AppCompatActivity {
    public Button write_btn;
    public ImageButton Back_Btn;

    private LinearLayout layout;
    private InquiryPopupActivity Popup_btn;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry);

        write_btn = findViewById(R.id.write_submitBtn);
        Back_Btn=findViewById(R.id.before_btn);
        layout = findViewById(R.id.inquiry_container);

        write_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Popup_btn = new InquiryPopupActivity(InquiryActivity.this);

                // 팝업창이 화면 왼쪽에서 10dp, 오른쪽에서 10dp 떨어지도록 위치 설정
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(Popup_btn.getWindow().getAttributes());

                int marginPx = (int) getResources().getDisplayMetrics().density * 10; // 10dp를 px로 변환
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.horizontalMargin = marginPx;


                InquiryPopupActivity popup = new InquiryPopupActivity(InquiryActivity.this);
                popup.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String userInputText = Popup_btn.getUserInputText();

                        // inquiry_info_box.xml의 레이아웃을 inflate하여 TextView에 텍스트 설정
                        ConstraintLayout parentLayout = findViewById(R.id.inquiry_container); // inquiry_layout에 해당하는 ID 사용
                        View inquiryInfoBox = getLayoutInflater().inflate(R.layout.inquiry_info_box, parentLayout, false);
                        parentLayout.addView(inquiryInfoBox);

                        // Parent layout에 추가하여 화면에 표시
                        layout.addView(inquiryInfoBox);
                    }
                });

                Popup_btn.show();
                Popup_btn.getWindow().setAttributes(layoutParams);
            }
        });

        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   onBackPressed(); }
        });

        LinearLayout parentLayout = findViewById(R.id.inquiry_container); // inquiry_layout에 해당하는 ID 사용
        View inquiryInfoBox = getLayoutInflater().inflate(R.layout.inquiry_info_box, parentLayout, false);
        parentLayout.addView(inquiryInfoBox);





        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);//하단 바 navigate 처리
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    navigateToMainActivity(InquiryActivity.this);
                    return true;
                case R.id.action_map:
                    navigateToMapActivity(InquiryActivity.this);
                    return true;
                case R.id.action_calendar:
                    navigateToCalendarActivity(InquiryActivity.this);
                    return true;
                case R.id.action_favorite:
                    return true;
                case R.id.action_profile:
                    navigateToMyPageActivity(InquiryActivity.this);
                    return true;
            }
            return false;
        });
    }
}
