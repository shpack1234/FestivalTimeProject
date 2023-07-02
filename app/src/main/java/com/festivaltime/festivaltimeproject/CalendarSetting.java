package com.festivaltime.festivaltimeproject;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

public class CalendarSetting extends Dialog {
    protected Context mContext;
    public Button finish_btn, add_category;
    private CalendarCategory Add_category;

    public CalendarSetting(@NonNull Context context) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calendar_setting);
        this.mContext = context;

        finish_btn = findViewById(R.id.add_finish_btn);
        add_category = findViewById(R.id.add_category);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        Window window = getWindow();

        if(window!=null){
            //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams params = window.getAttributes();
            // 화면에 가득 차도록
            params.width         = WindowManager.LayoutParams.MATCH_PARENT;
            params.height        = WindowManager.LayoutParams.WRAP_CONTENT;

            // 열기&닫기 시 애니메이션 설정
            params.windowAnimations = R.style.AnimationPopupStyle;
            window.setAttributes( params );
            // UI 하단 정렬
            window.setGravity( Gravity.BOTTOM );
        }

        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //일단 dismiss로 처리하고 데이터 들어가고 변경예정
                dismiss();
            }
        });

        //카테고리 추가 이중 다이얼로그 구현 예정
        add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add_category = new CalendarCategory(null);
                //Add_category.show();
            }
        });
    }
}
