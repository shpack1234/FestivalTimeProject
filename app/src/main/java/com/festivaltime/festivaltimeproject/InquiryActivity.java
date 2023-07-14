package com.festivaltime.festivaltimeproject;

import androidx.appcompat.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.festivaltime.festivaltimeproject.calendaract.CalendarActivity;
import com.festivaltime.festivaltimeproject.calendaract.CalendarPopupActivity;

import org.w3c.dom.Text;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.festivaltime.festivaltimeproject.InquiryPopupActivity;

public class InquiryActivity extends AppCompatActivity {
    Button writebtn;
    TextView inquirytext;
    private InquiryPopupActivity Popup_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry);

        inquirytext = findViewById(R.id.inquiry_text);
        writebtn = findViewById(R.id.write_submitBtn);

        // 2줄 이상이면 더보기
        inquirytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (textView.getMaxLines() == 2) {
                    textView.setMaxLines(Integer.MAX_VALUE);
                    textView.setEllipsize(null);
                } else {
                    textView.setMaxLines(2);
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });

        writebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Popup_btn = new InquiryPopupActivity(InquiryActivity.this);
                Popup_btn.show();
            }
        });
    }
}
