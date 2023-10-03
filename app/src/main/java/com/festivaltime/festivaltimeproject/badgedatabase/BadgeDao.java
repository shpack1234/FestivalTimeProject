package com.festivaltime.festivaltimeproject.badgedatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BadgeDao {
    @Insert
    void insertBadge(BadgeEntity badge);

    @Query("SELECT * FROM badges")
    List<BadgeEntity> getAllBadges();
}
