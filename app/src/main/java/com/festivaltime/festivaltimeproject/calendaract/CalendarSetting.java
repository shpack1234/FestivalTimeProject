package com.festivaltime.festivaltimeproject.calendaract;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.festivaltime.festivaltimeproject.R;

//캘린더 설정 dialog class 카테고리 관리, 카테고리별/날짜별 표시 기능
public class CalendarSetting extends Dialog {
    final Switch othermonth_switch;
    final  boolean showOtherMonths;
    protected Context mContext;
    public Button finish_btn, add_category;
    private CalendarCategory Add_category;

    public CalendarSetting(@NonNull Context context, boolean showOtherMonths) {
        super(context);
        //팝업 애니메이션 위한 윈도우
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.showOtherMonths = showOtherMonths;
        setContentView(R.layout.activity_calendar_setting);
        this.mContext = context;

        finish_btn = findViewById(R.id.add_finish_btn);
        add_category = findViewById(R.id.add_category);
        othermonth_switch = findViewById(R.id.othermonth_switch);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        Window window = getWindow();

        if (window != null) {
            //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams params = window.getAttributes();
            // 화면에 가득 차도록
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            // 열기&닫기 시 애니메이션 설정
            params.windowAnimations = R.style.AnimationPopupStyle;
            window.setAttributes(params);
            // UI 하단 정렬
            window.setGravity(Gravity.BOTTOM);
        }

        //완료 버튼
        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.putExtra("showOtherMonths", lastmonth_switch.isChecked());*/
                dismiss();
            }
        });

        //카테고리 추가 이중 다이얼로그 구현 예정
        add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarCategory dialog = new CalendarCategory(mContext);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // 카테고리 팝업이 닫힐 때 수행할 작업
                    }
                });
                dialog.show();
            }
        });

        //다른달 날짜 visible
        if (showOtherMonths) {
            othermonth_switch.setChecked(true);
        } else {
            othermonth_switch.setChecked(false);
        }

    }
    public boolean getShowOtherMonths() {
        return othermonth_switch.isChecked();
    }

}


