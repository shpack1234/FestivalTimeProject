package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToSearchActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private SearchView searchView;
    private String query;

    SliderView sliderView;
    int[] hot_festival_images={R.drawable.image01, R.drawable.image02, R.drawable.image03};

    private final int numberOfLayouts = 3;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HashGetter.getHashKey(getApplicationContext());

        searchView = findViewById(R.id.search_view);
        searchView.setOnTouchListener((v, event) -> {
            searchView.setIconified(false);
            searchView.performClick();
            return true;
        });
        ImageButton searchoptionbutton = findViewById(R.id.search_option);
        searchoptionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupDialog();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {       //검색 시 검색 내용 SearchActivity 로 전달
                query = s;
                performSearch(query);
                navigateToSearchActivity(MainActivity.this, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        LinearLayout mainFestivalContainer=findViewById(R.id.main_festival_container);

        for (int i = 0; i < numberOfLayouts; i++) {
            LinearLayout customFestivalLayout = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.main_festival_container, mainFestivalContainer, false);

            // Add your logic to populate data into the custom_festival_layout views here

            mainFestivalContainer.addView(customFestivalLayout);
        }



        sliderView=findViewById(R.id.image_slider);           //Hot Festival 배너 이미지 전환
        SliderAdapter sliderAdapter=new SliderAdapter(hot_festival_images);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.startAutoCycle();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(MainActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(MainActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(MainActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(MainActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(MainActivity.this);
                return true;
            }
        });

    }

    private void showPopupDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_popup, null);
        Button cancelButton = dialogView.findViewById(R.id.dialog_popup_close_btn);
        Button confirmButton = dialogView.findViewById(R.id.dialog_popup_add_btn);
        // 시작 날짜 선택 버튼
        Button startdateClick = dialogView.findViewById(R.id.dialog_popup_start_date);
        Button starttimeClick = dialogView.findViewById(R.id.dialog_popup_start_time);
        Button enddateClick = dialogView.findViewById(R.id.dialog_popup_end_date);
        Button endtimeClick = dialogView.findViewById(R.id.dialog_popup_end_time);

        Button bigcategory = dialogView.findViewById(R.id.dialog_popup_category01);
        Button smallcategory = dialogView.findViewById(R.id.dialog_popup_category02);
        Button location = dialogView.findViewById(R.id.dialog_popup_location);

        DatePicker StartDatePicker = dialogView.findViewById(R.id.dialog_popup_StartDatePicker);
        TimePicker StartTimePicker = dialogView.findViewById(R.id.dialog_popup_StartTimePicker);
        DatePicker EndDatePicker = dialogView.findViewById(R.id.dialog_popup_EndDatePicker);
        TimePicker EndTimePicker = dialogView.findViewById(R.id.dialog_popup_EndTimePicker);

        // 대분류 카테고리 버튼
        bigcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);

                getMenuInflater().inflate(R.menu.dialog_bigcategory, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String selectedBigCategory = item.getTitle().toString();
                        bigcategory.setText(selectedBigCategory);

                        // bigcategory를 변경한 후, smallcategory의 텍스트를 확인하여 조건에 따라 "모든 카테고리"로 변경합니다.
                        String currentSmallCategory = smallcategory.getText().toString();
                        if (!selectedBigCategory.equals(currentSmallCategory)) {
                            smallcategory.setText("모든 카테고리");
                        }

                        return false;
                    }
                });
                popup.show();
            }
        });


        //소분류 카테고리
        smallcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);

                // 대분류 카테고리에 따라 다른 메뉴를 보여줍니다.
                String bigCategoryText = bigcategory.getText().toString();
                if (bigCategoryText.equals("축제")) {
                    getMenuInflater().inflate(R.menu.dialog_smallcategory01, popup.getMenu());
                } else if (bigCategoryText.equals("공연/행사")) {
                    getMenuInflater().inflate(R.menu.dialog_smallcategory02, popup.getMenu());
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // 선택한 항목의 title을 가져와서 smallcategory의 텍스트로 설정합니다.
                        smallcategory.setText(item.getTitle().toString());
                        return false;
                    }
                });
                popup.show();
            }
        });



        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);

                getMenuInflater().inflate(R.menu.dialog_region, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.every_region:
                                location.setText("모든 지역");
                                break;
                            case R.id.seoul:
                                location.setText("서울");
                                break;
                            case R.id.gyeonggi:
                                location.setText("경기");
                                break;
                            case R.id.incheon:
                                location.setText("인천");
                                break;
                            case R.id.gangwon:
                                location.setText("강원");
                                break;
                            case R.id.jeju:
                                location.setText("제주");
                                break;
                            case R.id.daejeon:
                                location.setText("대전");
                                break;
                            case R.id.chungbuk:
                                location.setText("충북");
                                break;
                            case R.id.chungnam_sejong:
                                location.setText("충남/세종");
                                break;
                            case R.id.busan:
                                location.setText("부산");
                                break;
                            case R.id.ulsan:
                                location.setText("울산");
                                break;
                            case R.id.gyeongnam:
                                location.setText("경남");
                                break;
                            case R.id.daegu:
                                location.setText("대구");
                                break;
                            case R.id.gyeongbuk:
                                location.setText("경북");
                                break;
                            case R.id.gwangju:
                                location.setText("광주");
                                break;
                            case R.id.jeonnam:
                                location.setText("전남");
                                break;
                            case R.id.jeonju_jeonbuk:
                                location.setText("전주/전북");
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        startdateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // StartDatePicker의 가시성을 토글
                switch (StartDatePicker.getVisibility()) {
                    case View.VISIBLE:
                        StartDatePicker.setVisibility(View.GONE);
                        break;
                    case View.GONE:
                    default:
                        StartDatePicker.setVisibility(View.VISIBLE);
                        StartTimePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        break;
                }
            }
        });

        starttimeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // StartTimePicker의 가시성을 토글
                switch (StartTimePicker.getVisibility()) {
                    case View.VISIBLE:
                        StartTimePicker.setVisibility(View.GONE);
                        break;
                    case View.GONE:
                    default:
                        StartTimePicker.setVisibility(View.VISIBLE);
                        StartDatePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        break;
                }
            }
        });

        enddateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EndDatePicker의 가시성을 토글
                switch (EndDatePicker.getVisibility()) {
                    case View.VISIBLE:
                        EndDatePicker.setVisibility(View.GONE);
                        break;
                    case View.GONE:
                    default:
                        EndDatePicker.setVisibility(View.VISIBLE);
                        StartDatePicker.setVisibility(View.GONE);
                        StartTimePicker.setVisibility(View.GONE);
                        EndTimePicker.setVisibility(View.GONE);
                        break;
                }
            }
        });

        endtimeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EndTimePicker의 가시성을 토글
                switch (EndTimePicker.getVisibility()) {
                    case View.VISIBLE:
                        EndTimePicker.setVisibility(View.GONE);
                        break;
                    case View.GONE:
                    default:
                        EndTimePicker.setVisibility(View.VISIBLE);
                        StartDatePicker.setVisibility(View.GONE);
                        StartTimePicker.setVisibility(View.GONE);
                        EndDatePicker.setVisibility(View.GONE);
                        break;
                }
            }
        });

        //시작 시간-날짜 변화시
        StartDatePicker.init(StartDatePicker.getYear(), StartDatePicker.getMonth(), StartDatePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        startdateClick.setText(String.format("%d.%d.%d", year, month + 1, day));
                    }
                });


        //시작 시간-시간 변화시
        StartTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay < 10 && minute < 10) {
                    starttimeClick.setText(String.format("0%d:0%d", hourOfDay, minute));
                } else if (minute < 10) {
                    starttimeClick.setText(String.format("%d:0%d", hourOfDay, minute));
                } else if (hourOfDay < 10) {
                    starttimeClick.setText(String.format("0%d:%d", hourOfDay, minute));
                } else {
                    starttimeClick.setText(String.format("%d:%d", hourOfDay, minute));
                }
            }
        });

        //end 시간-날짜 변화시
        EndDatePicker.init(EndDatePicker.getYear(), EndDatePicker.getMonth(), EndDatePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int month, int day) {
                        enddateClick.setText(String.format("%d.%d.%d", year, month + 1, day));
                    }
                });

        //end 시간-시간 변화시
        EndTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay < 10 && minute < 10) {
                    endtimeClick.setText(String.format("0%d:0%d", hourOfDay, minute));
                } else if (minute < 10) {
                    endtimeClick.setText(String.format("%d:0%d", hourOfDay, minute));
                } else if (hourOfDay < 10) {
                    endtimeClick.setText(String.format("0%d:%d", hourOfDay, minute));
                } else {
                    endtimeClick.setText(String.format("%d:%d", hourOfDay, minute));
                }
            }
        });
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 취소 버튼을 눌렀을 때 실행할 코드 작성
                alertDialog.dismiss(); // 팝업 대화상자 닫기
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startdate = startdateClick.getText().toString();
                String starttime = starttimeClick.getText().toString();
                String enddate = enddateClick.getText().toString();
                String endtime = endtimeClick.getText().toString();

                // 시작 및 종료 날짜-시간 문자열을 적절한 형식으로 변환
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                try {
                    Date startDate = sdf.parse(startdate + " " + starttime);
                    Date endDate = sdf.parse(enddate + " " + endtime);

                    if (endDate.after(startDate)) {
                        // 종료 날짜-시간이 시작 날짜-시간보다 나중일 경우
                        // 여기에 추가 동작 수행

                        // 대화 상자 닫기
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "기간을 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.show();
    }

    private void performSearch(String query) {
        System.out.println("검색어: " + query);
    }

}
