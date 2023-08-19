package com.festivaltime.festivaltimeproject;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface InquiryDao {

    @Insert
    void insertInquiry(InquiryEntity inquiry);
}

