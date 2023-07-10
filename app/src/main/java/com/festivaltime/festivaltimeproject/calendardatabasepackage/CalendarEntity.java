package com.festivaltime.festivaltimeproject.calendardatabasepackage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    // 추가적인 필드들 여기 추가

    // 생성자, getter, setter 필요 따라 추가
}
