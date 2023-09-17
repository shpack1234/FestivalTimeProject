package com.festivaltime.festivaltimeproject.calendaract;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.festivaltime.festivaltimeproject.calendardatabasepackage.FetchScheduleTask;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//캘린더 설정 dialog class 카테고리 관리, 카테고리별/날짜별 표시 기능
public class CalendarSetting extends Dialog implements FetchCategoryTask.FetchCategoryTaskListener {
    private Activity mActivity;
    private CalendarCategoryDao categoryDao;
    private List<CalendarCategoryEntity> categorylist;
    final Switch othermonth_switch;
    final boolean showOtherMonths;
    private boolean deleteVisible = false;
    protected Context mContext;
    public Button finish_btn, del_categories, add_category, close_btn;
    private Executor executor;

    public CalendarSetting(@NonNull Context context, boolean showOtherMonths, Activity activity) {
        super(context);
        //팝업 애니메이션 위한 윈도우
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.showOtherMonths = showOtherMonths;
        setContentView(R.layout.activity_calendar_setting);
        this.mContext = context;
        mActivity = activity;

        finish_btn = findViewById(R.id.add_finish_btn);
        del_categories = findViewById(R.id.del_category);
        add_category = findViewById(R.id.add_category);
        close_btn = findViewById(R.id.close_del);
        othermonth_switch = findViewById(R.id.othermonth_switch);

        executor = Executors.newSingleThreadExecutor();

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
                dismiss();
            }
        });

        //삭제버튼
        del_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteVisible = true;
                close_btn.setVisibility(View.VISIBLE);
                updateUI();
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteVisible = false;
                close_btn.setVisibility(View.INVISIBLE);
                updateUI();
            }
        });

        //카테고리 추가 이중 다이얼로그 구현 예정
        add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarCategory dialog = new CalendarCategory(mContext);
                dialog.show();

                dialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        FetchCategoryTask fetchCategoryTask = new FetchCategoryTask(mContext, categoryDao);
                        fetchCategoryTask.fetchCategories(new FetchCategoryTask.FetchCategoryTaskListener() {
                            @Override
                            public void onFetchCompleted(List<CalendarCategoryEntity> categoryList) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 카테고리를 가져온 후 UI 업데이트 코드를 실행
                                        updateUI();
                                    }
                                });
                            }
                        });

                    }
                });
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
        this.categorylist = categorylist;
        updateUI(); // 매개변수를 전달하여 호출합니다.
    }

    // 화면을 업데이트하는 메서드
    private void updateUI() {
        LinearLayout categoryContainer = findViewById(R.id.category_container);
        categoryContainer.removeAllViews();

        for (CalendarCategoryEntity category : categorylist) {
            View categoryBox = getLayoutInflater().inflate(R.layout.category_box, null);
            TextView titleTextView = categoryBox.findViewById(R.id.category_box_text);
            ImageView categoryColorView = categoryBox.findViewById(R.id.category_box_category);
            ImageButton deleteButton = categoryBox.findViewById(R.id.category_deleteButton);

            if (deleteVisible == true) {
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                deleteButton.setVisibility(View.GONE);
            }

            String title = category.name;
            String color = category.color;

            titleTextView.setText(title);
            categoryColorView.setColorFilter(Color.parseColor(color));

            categoryContainer.addView(categoryBox);

            // 삭제 버튼 클릭 리스너 등록
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭된 카테고리 데이터를 데이터베이스에서 삭제
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            categoryDao.DeleteCategory(category);
                            // 삭제된 카테고리가 포함되지 않은 목록을 가져옴
                            categorylist = categoryDao.getAllCategoryEntity();

                            // UI 업데이트를 메인 스레드에서 실행
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI();
                                }
                            });
                        }
                    });
                }
            });

        }
    }

    public boolean getShowOtherMonths() {
        return othermonth_switch.isChecked();
    }

}
