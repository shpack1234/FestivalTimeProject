package com.festivaltime.festivaltimeproject;

import android.os.StrictMode;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.ArrayList;

@Entity
public class UserEntity {
    @PrimaryKey
    private boolean isUserIdAssigned;
    private String userId;
    private String userName;
    private String userBirth;
    private String userGender;
    private ArrayList<String> userFavoriteFestival = new ArrayList<>();

    public boolean isUserIdAssigned() {
        return isUserIdAssigned;
    }

    public void setUserIdAssigned(boolean userIdAssigned) {
        isUserIdAssigned = userIdAssigned;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserBirth() {
        return userBirth;
    }

    public void setUserBirth(String userBirth) {
        this.userBirth = userBirth;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public ArrayList<String> getUserFavoriteFestival() {
        return userFavoriteFestival;
    }

    public void setUserFavoriteFestival(ArrayList<String> userFavoriteFestival) {
        this.userFavoriteFestival = userFavoriteFestival;
    }

}
