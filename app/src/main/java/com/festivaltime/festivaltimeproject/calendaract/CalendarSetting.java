package com.festivaltime.festivaltimeproject.calendaract;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.festivaltime.festivaltimeproject.R;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryDao;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryDataBase;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryEntity;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.FetchCategoryTask;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;

import java.util.List;

//캘린더 설정 dialog class 카테고리 관리, 카테고리별/날짜별 표시 기능
public class CalendarSetting extends Dialog implements FetchCategoryTask.FetchCategoryTaskListener{
    private CalendarCategoryDao categoryDao;
    private LinearLayout categoryContainer;
    final Switch othermonth_switch;
    final boolean showOtherMonths;
    protected Context mContext;
    public Button finish_btn, add_category;

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

        categoryDao = CalendarCategoryDataBase.getInstance(mContext).categoryDao();

        FetchCategoryTask fetchCategoryTask = new FetchCategoryTask(mContext, categoryDao);
        fetchCategoryTask.fetchCategories(this);

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

    @Override
    public void onFetchCompleted(List<CalendarCategoryEntity> categorylist) {
        updateUI(categorylist); // 매개변수를 전달하여 호출합니다.
    }

    // 화면을 업데이트하는 메서드
    private void updateUI(List<CalendarCategoryEntity> categorylist) {
        LinearLayout categoryContainer = findViewById(R.id.category_container);
        categoryContainer.removeAllViews();

        for (CalendarCategoryEntity category : categorylist) {
            View categoryBox = getLayoutInflater().inflate(R.layout.category_box, null);
            TextView titleTextView = categoryBox.findViewById(R.id.category_box_text);
            //ImageButton deleteButton = categoryBox.findViewById(R.id.schedule_deleteButton);

            String title = category.name;

            // 일정 데이터를 각 scheduleBox에 담는 작업
            titleTextView.setText(title);

            // 각 scheduleBox를 scheduleContainer에 추가
            categoryContainer.addView(categoryBox);
        }
    }

    public boolean getShowOtherMonths() {
        return othermonth_switch.isChecked();
    }

}


