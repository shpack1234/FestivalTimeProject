package com.festivaltime.festivaltimeproject.calendaract;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;

import com.festivaltime.festivaltimeproject.R;


//캘린더 카테고리 관리 class
public class CalendarCategory extends Dialog {

    private EditText txtText; //카테고리명
    private Button close_btn, add_btn; //닫기, 추가하기 btn
    public ImageButton select_color, color1, color2, color3, color4, color5; //설정 색상 5가지 제한 > 수정 필요시 추후에 수정

    public CalendarCategory(@NonNull Context context){
        super(context);
        setContentView(R.layout.activity_calendar_category);

        txtText = findViewById(R.id.calendar_category_new_category);
        close_btn = findViewById(R.id.calendar_category_close_btn);
        add_btn = findViewById(R.id.calendar_category_add_finish_btn);
        select_color = findViewById(R.id.calendar_category_select_color);
        color1 = findViewById(R.id.calendar_category_select_color1);
        color2 = findViewById(R.id.calendar_category_select_color2);
        color3 = findViewById(R.id.calendar_category_select_color3);
        color4 = findViewById(R.id.calendar_category_select_color4);
        color5 = findViewById(R.id.calendar_category_select_color5);

        //close시 팝업나가기
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //add시 데이터 전달 (미구현)
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //select_color.getColorFilter();
                dismiss();
            }
        });

        //카테고리 색 버튼 클릭시 색 visible
        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color1.setVisibility(View.VISIBLE); color2.setVisibility(View.VISIBLE);
                color3.setVisibility(View.VISIBLE); color4.setVisibility(View.VISIBLE);
                color5.setVisibility(View.VISIBLE);
            }
        });

        // 카테고리 색상 선택시 현재 선택한 색상 표시
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.calendar_category_select_color1:
                        select_color.setColorFilter(Color.parseColor("#ed5c55"));
                        break;
                    case R.id.calendar_category_select_color2:
                        select_color.setColorFilter(Color.parseColor("#ed8a54"));
                        break;
                    case R.id.calendar_category_select_color3:
                        select_color.setColorFilter(Color.parseColor("#edc652"));
                        break;
                    case R.id.calendar_category_select_color4:
                        select_color.setColorFilter(Color.parseColor("#a7ed54"));
                        break;
                    case R.id.calendar_category_select_color5:
                        select_color.setColorFilter(Color.parseColor("#52c8ed"));
                        break;
                }
            }
        };
        color1.setOnClickListener(onClickListener);
        color2.setOnClickListener(onClickListener);
        color3.setOnClickListener(onClickListener);
        color4.setOnClickListener(onClickListener);
        color5.setOnClickListener(onClickListener);

    }

}
