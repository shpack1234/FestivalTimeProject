package com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface CalendarCategoryDao {
    @Query("SELECT * FROM calendarcategoryentity")
    List<CalendarCategoryEntity> getAllCategoryEntity();

    @Insert
    void InsertCategory(CalendarCategoryEntity category);

    @Delete
    void DeleteCategory(CalendarCategoryEntity category);

    @Update
    void UpdateCategory(CalendarCategoryEntity category);
}

