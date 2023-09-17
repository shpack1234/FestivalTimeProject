package com.festivaltime.festivaltimeproject.calendardatabasepackage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Calendar;
import java.util.List;

@Dao
public interface CalendarDao {
    @Query("SELECT * FROM calendarentity")
    List<CalendarEntity> getAllCalendarEntity();

    @Query("SELECT festival_id FROM calendarentity")
    List<String> getAllContentIds();

    @Query("SELECT * FROM calendarentity WHERE schedule_category = :category")
    List<CalendarEntity> getCalendarEntitiesByCategory(String category);

    @Insert
    void InsertSchedule(CalendarEntity schedule);

    @Delete
    void DeleteSchedule(CalendarEntity schedule);

    @Update
    void UpdateSchedule(CalendarEntity schedule);
}
