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

    // 필요에 따라 추가적인 필드와 메서드를 여기에 추가할 수 있습니다.

    // 생성자
    public CalendarEntity() {
        // Room에 필요한 빈 생성자
    }

    public CalendarEntity(String title, String startDate, String endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getter와 Setter (필요한 경우)
}