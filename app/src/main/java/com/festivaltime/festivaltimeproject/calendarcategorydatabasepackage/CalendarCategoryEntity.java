package com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "calendarcategoryentity")
public class CalendarCategoryEntity {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "category_name")
    public String name;

    @ColumnInfo(name = "category_color")
    public String color;

    // 생성자
    public CalendarCategoryEntity() {
        // Room에 필요한 빈 생성자
    }

    public CalendarCategoryEntity(String color, String name) {
        this.color = color;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

}