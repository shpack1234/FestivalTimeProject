package com.festivaltime.festivaltimeproject.inquirydatabasepackage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InquiryDao {

    @Insert
    void insertInquiry(InquiryEntity inquiryEntity);

    @Query("SELECT * FROM inquiries")
    List<InquiryEntity> getAllInquiries();
}
