package com.festivaltime.festivaltimeproject.calendaract;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.festivaltime.festivaltimeproject.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//개인 캘린더 calendar recyclerview class
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    ArrayList<Date> dayList;
    boolean showOtherMonths;
    private TextView scheduleText;
    private RecyclerView recyclerView;
    private int previousSelectedPosition = -1;
    private TextView selectDateView;

    //recyclerview 정렬한 arraylist, 다른달 표시 여부, calendar recyclerview, 선택 날짜, 일정 view
    public CalendarAdapter(ArrayList<Date> dayList, boolean showOtherMonths, RecyclerView recyclerView, TextView selectDateView, TextView scheduleText) {
        this.dayList = dayList;
        this.showOtherMonths = showOtherMonths;
        this.recyclerView = recyclerView;
        this.selectDateView = selectDateView;
        this.scheduleText = scheduleText;
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
                                previousHolder.dayText.setTextColor(Color.parseColor("#737373"));
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
                        scheduleText.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return dayList.size();
    } //calendar size 반환

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        //초기화
        TextView dayText;
        View parentView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.calendar_cell_dayText);
            parentView = itemView.findViewById(R.id.calendar_cell_parentView);
        }
    }

}
