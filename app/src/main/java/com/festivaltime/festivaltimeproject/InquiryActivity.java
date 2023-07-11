package com.festivaltime.festivaltimeproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InquiryActivity extends AppCompatActivity {

    private TextView inquiryText;
    private TextView moreButton;
    private LinearLayout inquiryLayout;
    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry);

        inquiryText = findViewById(R.id.inquiry1_text);
        moreButton = findViewById(R.id.moreButton);
        inquiryLayout = findViewById(R.id.inquiry1_layout);

        // 초기에는 접혀진 상태로 시작
        collapseLayout();

        // Layout 클릭 시 확장/축소 동작
        inquiryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    collapseLayout();
                } else {
                    expandLayout();
                }
            }
        });

        // ...더보기 버튼 클릭 시 확장 동작
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandLayout();
            }
        });
    }

    private void expandLayout() {
        inquiryText.setMaxLines(Integer.MAX_VALUE);
        moreButton.setVisibility(View.GONE);
        isExpanded = true;
    }

    private void collapseLayout() {
        inquiryText.setMaxLines(2);
        moreButton.setVisibility(View.VISIBLE);
        isExpanded = false;
    }
}

