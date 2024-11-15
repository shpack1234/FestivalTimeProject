package com.festivaltime.festivaltimeproject.userdatabasepackage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM userentity")
    List<UserEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(UserEntity entity);

    @Query("SELECT * FROM userentity WHERE userId = :userId LIMIT 1")
    UserEntity getUserInfoById(String userId);

    @Delete
    void delete(UserEntity entity);
}
