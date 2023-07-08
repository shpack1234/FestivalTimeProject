package com.festivaltime.festivaltimeproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CalendarScheduleAdapter extends RecyclerView.Adapter<CalendarScheduleAdapter.ViewHolder> {
    private ArrayList<CalendarSchedule> mData = null;

    public class ViewHolder extends RecyclerView.ViewHolder{
        protected TextView scheduleText;
        protected ImageButton delte_schedule;

        public ViewHolder(View schedule){
            super(schedule);

            this.scheduleText = schedule.findViewById(R.id.scheduleText);
            this.delte_schedule = schedule.findViewById(R.id.delte_schedule);

            delte_schedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition(); //현재 어뎁터 리스트 포지션 가져옴

                    if(position != RecyclerView.NO_POSITION){ //삭제된 포지션 아닐경우
                        mData.remove(position); //해당 포지션 아이템 제거
                        notifyDataSetChanged(); //데이터셋 변경 알림
                    }
                }
            });
        }
    }

    CalendarScheduleAdapter(ArrayList<CalendarSchedule> list){
        mData = list;
    }

    @Override
    public CalendarScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.schedule_cell, parent, false);
        CalendarScheduleAdapter.ViewHolder vh = new CalendarScheduleAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(CalendarScheduleAdapter.ViewHolder holder, int position){
        holder.scheduleText.setText(mData.get(position).getSchedule());
    }

    @Override
    public int getItemCount(){
        return mData.size();
    }
}
