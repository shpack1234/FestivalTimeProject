package com.festivaltime.festivaltimeproject.festivalcalendaract;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.festivaltime.festivaltimeproject.ApiReader;
import com.festivaltime.festivaltimeproject.FestivalDataTask;
import com.festivaltime.festivaltimeproject.R;
import com.festivaltime.festivaltimeproject.calendaract.CalendarUtil;
import com.festivaltime.festivaltimeproject.navigateToSomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//축제 캘린더 calendar recyclerview class
public class FestivalCalendarAdapter extends RecyclerView.Adapter<FestivalCalendarAdapter.FestivalCalendarViewHolder> {
    ArrayList<Date> dayList;
    private ApiReader apiReader;
    private Context context;

    public FestivalCalendarAdapter(Context context, ArrayList<Date> dayList) {
        this.context = context;
        this.dayList = dayList;
    }

    @NonNull
    @Override
    public FestivalCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.festivalcalendar_cell, parent, false);
        return new FestivalCalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FestivalCalendarViewHolder holder, int position) {
        Date monthDate = dayList.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);

        String apiKey = context.getResources().getString(R.string.api_key);
        apiReader = new ApiReader();

        int currentMonth = CalendarUtil.selectedDate.get(Calendar.MONTH) + 1;
        int currentYear = CalendarUtil.selectedDate.get(Calendar.YEAR);

        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String todayDate = dateFormat.format(dateCalendar.getTime());

        new FestivalDataTask(apiKey, todayDate, holder.festival_num).execute();


        if (displayMonth == currentMonth && displayYear == currentYear) {
        } else {
            holder.dayText.setTextColor(Color.parseColor("#D6D6D6"));
            holder.festival_num.setVisibility(View.INVISIBLE);
        }

        int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        holder.dayText.setText(String.valueOf(dayNo));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSomeActivity.navigateToSearchActivity((Activity) context, todayDate);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    // FestivalCalendarAdapter 클래스에 추가 메서드
    public void updateData(ArrayList<Date> newDayList) {
        this.dayList = newDayList;
    }


    class FestivalCalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        TextView festival_num;

        public FestivalCalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.festival_cell_dayText);
            festival_num = itemView.findViewById(R.id.festival_cell_num);
        }
    }


}
