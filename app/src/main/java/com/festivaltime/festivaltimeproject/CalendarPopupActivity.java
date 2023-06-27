package com.festivaltime.festivaltimeproject;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CalendarPopupActivity extends Dialog {
    private TextView date_txt;
    private Button shutdownClick;

    public CalendarPopupActivity(@NonNull Context context, String contents) {
        super(context);
        setContentView(R.layout.activity_calendar_popup);

        date_txt = findViewById(R.id.date_txt);
        date_txt.setText(contents);
        shutdownClick = findViewById(R.id.close_btn);
        shutdownClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}