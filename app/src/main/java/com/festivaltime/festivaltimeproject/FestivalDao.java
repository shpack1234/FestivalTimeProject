package com.festivaltime.festivaltimeproject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


@Dao
public interface FestivalDao {
    @Insert
    void insert(FestivalEntity entity);

    @Query("SELECT * FROM FestivalEntity WHERE id = :id")
    FestivalEntity getEntityById(String id);
}

