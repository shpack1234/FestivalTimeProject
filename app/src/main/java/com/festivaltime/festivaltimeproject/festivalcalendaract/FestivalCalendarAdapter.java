package com.festivaltime.festivaltimeproject.festivalcalendaract;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.festivaltime.festivaltimeproject.ApiReader;
import com.festivaltime.festivaltimeproject.ParsingApiData;
import com.festivaltime.festivaltimeproject.calendaract.CalendarUtil;
import com.festivaltime.festivaltimeproject.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMDD");
        String todayDate = dateFormat.format(dateCalendar.getTime());

        /*apiReader.FestivalN(apiKey, todayDate, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("FestivalCount response: ", response);
                Pattern pattern = Pattern.compile("<body>(.*?)</body>", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(response);

                if(matcher.find()){
                    String bodyCount = matcher.group(1);
                    Log.d("-About body: ", bodyCount);

                    holder.festival_num.setText(bodyCount);
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error: " + error);
            }
        });*/

        if (displayMonth == currentMonth && displayYear == currentYear) {
        } else {
            holder.dayText.setVisibility(View.INVISIBLE);
            holder.festival_num.setVisibility(View.INVISIBLE);
        }

        int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        holder.dayText.setText(String.valueOf(dayNo));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //클릭 기능 구현
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayList.size();
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
