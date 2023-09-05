package com.festivaltime.festivaltimeproject.calendaract;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.festivaltime.festivaltimeproject.R;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.FetchScheduleTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//개인 캘린더 calendar recyclerview class
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private CalendarDao calendarDao;
    ArrayList<Date> dayList;
    boolean showOtherMonths;
    private RecyclerView recyclerView;
    private OnDateClickListener onDateClickListener;
    private int previousSelectedPosition = -1;
    private TextView selectDateView;
    private ScrollView schedules;

    //recyclerview 정렬한 arraylist, 다른달 표시 여부, calendar recyclerview, 선택 날짜, 일정 view
    public CalendarAdapter(ArrayList<Date> dayList, boolean showOtherMonths, RecyclerView recyclerView, TextView selectDateView, ScrollView schedules) {
        this.dayList = dayList;
        this.showOtherMonths = showOtherMonths;
        this.recyclerView = recyclerView;
        this.selectDateView = selectDateView;
        this.schedules = schedules;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        return new CalendarViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        Date monthDate = dayList.get(position); //날짜 변수 적용
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);

        int currentDay = CalendarUtil.selectedDate.get(Calendar.DAY_OF_MONTH);
        int currentMonth = CalendarUtil.selectedDate.get(Calendar.MONTH) + 1;
        int currentYear = CalendarUtil.selectedDate.get(Calendar.YEAR);

        int displayDay = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);

        //선택한 달에 해당하는 날짜만 visible하도록 설정
        if (displayMonth == currentMonth && displayYear == currentYear) {
            holder.parentView.setVisibility(View.VISIBLE);
        } else {
            holder.dayText.setTextColor(Color.parseColor("#D6D6D6"));
            if (showOtherMonths) {
                holder.parentView.setVisibility(View.VISIBLE);
            } else {
                holder.parentView.setVisibility(View.INVISIBLE);
            }
        }

        int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        holder.dayText.setText(String.valueOf(dayNo));

        // 날짜가 현재 날짜일 경우 텍스트 색상 변경
        if (displayYear == currentYear && displayMonth == currentMonth && displayDay == currentDay) {
            holder.dayText.setTextColor(Color.parseColor("#5E9DF1"));
            holder.dayText.setBackgroundResource(R.drawable.ic_cal_select);
        }

        calendarDao = CalendarDatabase.getInstance(holder.itemView.getContext()).calendarDao();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean innerHasSchedule = checkForSchedule(monthDate, holder.itemView.getContext(), calendarDao);
                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (innerHasSchedule) {
                            holder.scheduleView.setVisibility(View.VISIBLE);
                        } else {
                            holder.scheduleView.setVisibility(View.INVISIBLE);
                            //selectDateView.setTextColor(Color.TRANSPARENT);
                        }
                    }
                });
            }
        }).start();



        // 날짜 클릭 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAbsoluteAdapterPosition(); // getAdapterPosition()를 사용하여 현재 위치를 가져옴.
                if (adapterPosition != RecyclerView.NO_POSITION) { //해당 날짜 위치 있을시에만 작동
                    Date selectedDate = dayList.get(adapterPosition); //선택한 날짜 position 재설정

                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.setTime(selectedDate);

                    int selectedMonth = selectedCalendar.get(Calendar.MONTH) + 1;
                    int selectedYear = selectedCalendar.get(Calendar.YEAR);

                    int currentMonth = CalendarUtil.selectedDate.get(Calendar.MONTH) + 1;
                    int currentYear = CalendarUtil.selectedDate.get(Calendar.YEAR);

                    //이전에 클릭한 날짜 텍스트 색상, 배경 리소스 초기화 위한 if문 (이중 선택한것처럼 보이는 효과 피하도록)
                    if ((showOtherMonths && selectedMonth == currentMonth) || (selectedYear == currentYear && selectedMonth == currentMonth)) {
                        if (previousSelectedPosition != -1) {
                            CalendarViewHolder previousHolder = (CalendarViewHolder) recyclerView.findViewHolderForAdapterPosition(previousSelectedPosition);
                            if (previousHolder != null) {
                                if (CalendarUtil.isSameDate(dayList.get(previousSelectedPosition), Calendar.getInstance().getTime())) {
                                } else {
                                    previousHolder.dayText.setTextColor(Color.parseColor("#737373"));
                                }
                                previousHolder.dayText.setBackgroundResource(0);
                            }
                        }

                        // 현재 클릭한 날짜의 텍스트 색상과 배경 리소스를 설정
                        holder.dayText.setTextColor(Color.parseColor("#5E9DF1"));
                        holder.dayText.setBackgroundResource(R.drawable.ic_cal_select);

                        // 이전에 클릭한 날짜의 위치 업데이트
                        previousSelectedPosition = adapterPosition;

                        // 선택한 날짜에 따라 SelectDateView, textView, del_Btn을 표시
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                        String selectedDateString = dateFormat.format(selectedDate);
                        selectDateView.setText(selectedDateString);
                        selectDateView.setVisibility(View.VISIBLE);
                        schedules.setVisibility(View.VISIBLE);

                        // 오늘 날짜가 아닌 다른 날짜를 선택한 경우, 오늘 날짜의 색과 배경을 초기화
                        if (selectedYear != currentYear || selectedMonth != currentMonth || selectedCalendar.get(Calendar.DAY_OF_MONTH) != currentDay) {
                            CalendarViewHolder todayHolder = (CalendarViewHolder) recyclerView.findViewHolderForAdapterPosition(dayList.indexOf(CalendarUtil.selectedDate.getTime()));
                            if (todayHolder != null) {
                                todayHolder.dayText.setBackgroundResource(0);
                            }
                        }

                        if (onDateClickListener != null) {
                            onDateClickListener.onDateClick(selectedDate);
                        }
                    }
                }
            }
        });

    }

    // displayDate가 일정에 속하는지 확인하는 메소드
    // checkForSchedule 메서드 내에서 calendarDao가 null인 경우 처리 추가
    private boolean checkForSchedule(Date displayDate, Context context, CalendarDao calendarDao) {
        if (calendarDao == null) {
            return false; // 혹은 적절한 값으로 처리
        }

        FetchScheduleTask fetchScheduleTask = new FetchScheduleTask(context, calendarDao);
        List<CalendarEntity> schedules = fetchScheduleTask.fetchSchedulesForDate(displayDate);

        return !schedules.isEmpty();
    }


    public interface OnDateClickListener {
        void onDateClick(Date selectedDate);
    }

    public void setOnDateClickListener(OnDateClickListener listener) {
        this.onDateClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    } //calendar size 반환

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        //초기화
        TextView dayText;
        View parentView;
        ImageView scheduleView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.calendar_cell_dayText);
            parentView = itemView.findViewById(R.id.calendar_cell_parentView);
            scheduleView = itemView.findViewById(R.id.calendar_cell_schedule);
        }
    }

}
