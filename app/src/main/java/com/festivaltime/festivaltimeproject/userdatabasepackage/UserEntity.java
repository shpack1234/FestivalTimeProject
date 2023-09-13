package com.festivaltime.festivaltimeproject.userdatabasepackage;

import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.ArrayList;

@Entity
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId;
    private String userName;
    private String userBirth;
    private String userGender;
    private String password;
    private ArrayList<String> userFavoriteFestival;
    private boolean isLogin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setPassword(String password) {
        this.password=password;
    }

    public String getPassword() {
        return password;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin=isLogin;
    }

    public boolean getIsLogin() {
        return isLogin;
    }

    public void setUserFavoriteFestival(ArrayList<String> userFavoriteFestival) {
        this.userFavoriteFestival=userFavoriteFestival;
    }

    public void addUserFavoriteFestival(String FestivalId) {
        this.userFavoriteFestival.add(FestivalId);
    }

    public void deleteUserFavoriteFestival(String FestivalId) {
        this.userFavoriteFestival.remove(FestivalId);
    }

}
