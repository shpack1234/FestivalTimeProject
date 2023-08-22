package com.festivaltime.festivaltimeproject.inquirydatabasepackage;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inquiries")
public class InquiryEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String userInputText;

    public InquiryEntity(String userInputText) {
        this.userInputText = userInputText;
    }

    // Getters and setters
    public String getUserInputText() {
        return userInputText;
    }

    public void setUserInputText(String userInputText) {
        this.userInputText = userInputText;
    }
}
