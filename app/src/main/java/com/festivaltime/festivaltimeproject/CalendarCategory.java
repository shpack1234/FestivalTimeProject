package com.festivaltime.festivaltimeproject;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;


public class CalendarCategory extends Dialog {

    private EditText txtText;
    private Button close_btn, add_btn;
    public ImageButton select_color;

    public CalendarCategory(@NonNull Context context){
        super(context);
        setContentView(R.layout.activity_calendar_category);

        txtText = findViewById(R.id.new_category);
        close_btn = findViewById(R.id.close_btn);
        add_btn = findViewById(R.id.add_finish_btn);
        select_color = findViewById(R.id.select_color);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }
}
