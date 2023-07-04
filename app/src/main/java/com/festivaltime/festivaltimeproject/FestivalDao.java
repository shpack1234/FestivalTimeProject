package com.festivaltime.festivaltimeproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


@Dao
public interface FestivalDao {
    @Insert
    void insert(FestivalEntity entity);

    @Delete
    void delete(FestivalEntity entity); // 삭제 메소드

    @Query("SELECT * FROM FestivalEntity WHERE id = :id")
    FestivalEntity getEntityById(String id);
}

