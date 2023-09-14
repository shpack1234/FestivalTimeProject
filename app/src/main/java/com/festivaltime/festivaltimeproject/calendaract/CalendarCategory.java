package com.festivaltime.festivaltimeproject.calendaract;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.festivaltime.festivaltimeproject.R;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryDataBase;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryEntity;

//캘린더 카테고리 관리 class
public class CalendarCategory extends Dialog {
    private int selectedColor = Color.parseColor("#ed5c55"); // 기본 색상 값
    protected Context mContext;
    private CalendarCategoryDataBase categoryDatabase;
    private EditText txtText; //카테고리명
    private Button close_btn, add_btn; //닫기, 추가하기 btn
    public ImageButton select_color, color1, color2, color3, color4, color5; //설정 색상 5가지 제한 > 수정 필요시 추후에 수정

    public CalendarCategory(@NonNull Context context){
        super(context);
        setContentView(R.layout.activity_calendar_category);
        this.mContext = context;

        // 팝업 테두리 바깥 영역을 흰색 반투명으로 설정
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams. MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80FFFFFF"))); // 흰색 반투명 설정
        }

        // 바깥 화면을 터치해도 팝업 창이 닫히지 않도록 설정
        setCanceledOnTouchOutside(false);

        txtText = findViewById(R.id.calendar_category_new_category);
        close_btn = findViewById(R.id.calendar_category_close_btn);
        add_btn = findViewById(R.id.calendar_category_add_finish_btn);
        select_color = findViewById(R.id.calendar_category_select_color);
        color1 = findViewById(R.id.calendar_category_select_color1);
        color2 = findViewById(R.id.calendar_category_select_color2);
        color3 = findViewById(R.id.calendar_category_select_color3);
        color4 = findViewById(R.id.calendar_category_select_color4);
        color5 = findViewById(R.id.calendar_category_select_color5);

        categoryDatabase = CalendarCategoryDataBase.getInstance(context);

        //close시 팝업나가기
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        // 선택한 색상 값 변경하는 클릭 이벤트 핸들러
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.calendar_category_select_color1:
                        selectedColor = Color.parseColor("#ed5c55");
                        break;
                    case R.id.calendar_category_select_color2:
                        selectedColor = Color.parseColor("#ed8a54");
                        break;
                    case R.id.calendar_category_select_color3:
                        selectedColor = Color.parseColor("#edc652");
                        break;
                    case R.id.calendar_category_select_color4:
                        selectedColor = Color.parseColor("#a7ed54");
                        break;
                    case R.id.calendar_category_select_color5:
                        selectedColor = Color.parseColor("#52c8ed");
                        break;
                }

                select_color.setColorFilter(selectedColor); // 선택한 색상을 시각적으로 표현
            }
        };
        color1.setOnClickListener(onClickListener);
        color2.setOnClickListener(onClickListener);
        color3.setOnClickListener(onClickListener);
        color4.setOnClickListener(onClickListener);
        color5.setOnClickListener(onClickListener);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txtText.getText().toString();
                String color = String.format("#%06X", (0xFFFFFF & selectedColor));

                if (name.isEmpty()) {
                    Toast.makeText(mContext, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    CalendarCategoryEntity newCategory = new CalendarCategoryEntity();
                    newCategory.name = name;
                    newCategory.color = color;

                    CategoryLoader loader = new CategoryLoader(getContext(), newCategory, categoryDatabase.categoryDao());
                    loader.forceLoad();

                    dismiss();
                }
            }
        });

    }

}
