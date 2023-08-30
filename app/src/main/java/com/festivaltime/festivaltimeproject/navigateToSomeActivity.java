package com.festivaltime.festivaltimeproject;

import android.app.Activity;
import android.content.Intent;

import com.festivaltime.festivaltimeproject.calendaract.CalendarActivity;
import com.festivaltime.festivaltimeproject.map.MapActivity;

public class navigateToSomeActivity {
    public static void navigateToSearchActivity(Activity thisActivity, String query) {
        Intent intent = new Intent(thisActivity, SearchScreenActivity.class);
        intent.putExtra("query", query);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }

    public static void navigateToSearchActivity(Activity thisActivity, String query, Intent intent) {
        intent.putExtra("query", query);
        thisActivity.startActivity(intent);
    }

    public static void navigateToMainActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, MainActivity.class);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }

    public static void navigateToMapActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, MapActivity.class);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }

    public static void navigateToCalendarActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, CalendarActivity.class);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0, 0);
    }

    public static void navigateToFavoriteActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, FavoriteActivity.class);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }

    public static void navigateToMyPageActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, MyPageActivity.class);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }

    public static void navigateToBadgeActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, BadgeActivity.class);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }

    public static void navigateToInquiryActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, InquiryActivity.class);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }


    public static void navigateToAppSettingActivity(Activity thisActivity){
        Intent intent = new Intent(thisActivity, AppSettingActivity.class);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }

    public static void navigateToChangePasswordActivity(Activity thisActivity){
        Intent intent = new Intent(thisActivity, ChangePasswordActivity.class);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }

    public static void navigateToDetailFestivalActivity(Activity thisActivity, String contentId) {
        Intent intent = new Intent(thisActivity, EntireViewActivity.class);
        intent.putExtra("contentid", contentId);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }

    public static void navigateToDetailFestivalActivity(Activity thisActivity, String contentId,String type) {
        Intent intent = new Intent(thisActivity, EntireViewActivity.class);
        intent.putExtra("contentid", contentId);
        intent.putExtra("type",type);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }


    public static void navigateToPrivacyActivity(Activity thisActivity, String userId) {
        Intent intent = new Intent(thisActivity, PrivacyActivity.class);
        intent.putExtra("userId", userId);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }

    public static void navigateToSearchDetailActivity(Activity thisActivity, String query, String type) {
        Intent intent = new Intent(thisActivity, SearchDetailActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("query", query);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }

    public static void navigateToEntireViewActivity(Activity thisActivity, String query, String type) {
        Intent intent = new Intent(thisActivity, EntireViewActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("query", query);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }
}
