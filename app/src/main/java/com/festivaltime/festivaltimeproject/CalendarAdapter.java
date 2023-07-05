package com.festivaltime.festivaltimeproject;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    ArrayList<Date> dayList;
    boolean showOtherMonths;
    private RecyclerView recyclerView;
    private int previousSelectedPosition = -1;

    public CalendarAdapter(ArrayList<Date> dayList, boolean showOtherMonths, RecyclerView recyclerView) {
        this.dayList = dayList;
        this.showOtherMonths = showOtherMonths;
        this.recyclerView = recyclerView;
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
                int adapterPosition = holder.getAbsoluteAdapterPosition(); // getAdapterPosition()를 사용하여 현재 위치를 가져옵니다.
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Date selectedDate = dayList.get(adapterPosition);

                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.setTime(selectedDate);

                    int selectedMonth = selectedCalendar.get(Calendar.MONTH) + 1;
                    int selectedYear = selectedCalendar.get(Calendar.YEAR);

                    int currentMonth = CalendarUtil.selectedDate.get(Calendar.MONTH) + 1;
                    int currentYear = CalendarUtil.selectedDate.get(Calendar.YEAR);

                    if ((showOtherMonths && selectedMonth == currentMonth) || (selectedYear == currentYear && selectedMonth == currentMonth)) {
                        // 이전에 클릭한 날짜의 텍스트 색상과 배경 리소스를 초기화
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
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        //초기화
        TextView dayText;
        View parentView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            parentView = itemView.findViewById(R.id.parentView);
        }
    }

}
