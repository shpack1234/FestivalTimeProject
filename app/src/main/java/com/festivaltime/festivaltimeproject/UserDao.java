package com.festivaltime.festivaltimeproject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM userentity")
    List<UserEntity> getAll();

    @Insert
    void insert(UserEntity entity);

    @Query("SELECT isUserIdAssigned FROM userentity LIMIT 1")
    boolean isUserIdAssigned();

    @Query("SELECT userId FROM userentity WHERE isUserIdAssigned = 1 LIMIT 1")
    String getUserId();

    @Query("SELECT * FROM userentity LIMIT 1")
    UserEntity getUserInfo();

}
