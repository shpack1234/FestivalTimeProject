package com.festivaltime.festivaltimeproject.calendardatabasepackage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "calendarentity")
public class CalendarEntity {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "schedule_title")
    public String title;

    @ColumnInfo(name = "schedule_startdate")
    public String startDate;

    @ColumnInfo(name = "schedule_enddate")
    public String endDate;

    @ColumnInfo(name = "schedule_starttime")
    public String startTime;

    @ColumnInfo(name = "schedule_endtime")
    public String endTime;

    @ColumnInfo(name = "schedule_category")
    public String category;

    //축제에서 받아온 일정이면 id추가
    @ColumnInfo(name = "festival_id")
    public String contentid;

    // 생성자
    public CalendarEntity() {
        // Room에 필요한 빈 생성자
    }


    public CalendarEntity(String title, String startDate, String endDate, String startTime, String endTime, String category, String contentid) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.contentid = contentid;
    }

    // Getter와 Setter (필요한 경우)
}