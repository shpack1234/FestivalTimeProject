package com.festivaltime.festivaltimeproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FestivalCalendarAdapter extends RecyclerView.Adapter<FestivalCalendarAdapter.FestivalCalendarViewHolder> {
    ArrayList<Date> dayList;

    public FestivalCalendarAdapter(ArrayList<Date> dayList) {
        this.dayList = dayList;
    }

    @NonNull
    @Override
    public FestivalCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.festivalcalendar_cell, parent, false);
        return new FestivalCalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FestivalCalendarViewHolder holder, int position) {
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
        } else {
            holder.parentView.setVisibility(View.INVISIBLE);
        }

        int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        holder.dayText.setText(String.valueOf(dayNo));

        //날짜 클릭 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class FestivalCalendarViewHolder extends RecyclerView.ViewHolder {
        //초기화
        TextView dayText;
        View parentView;

        public FestivalCalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            parentView = itemView.findViewById(R.id.parentView);
        }
    }

}
