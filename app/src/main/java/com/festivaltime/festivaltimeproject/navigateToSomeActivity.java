package com.festivaltime.festivaltimeproject;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;

public class navigateToSomeActivity {
    public static void navigateToSearchActivity(Activity thisActivity, String query) {
        Intent intent = new Intent(thisActivity, SearchActivity.class);
        intent.putExtra("query", query);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
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

    public static void navigateToDetailFestivalActivity(Activity thisActivity, String contentId) {
        Intent intent = new Intent(thisActivity, DetailFestivalActivity.class);
        intent.putExtra("contentid", contentId);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }

    public static void navigateToPrivacyActivity(Activity thisActivity, String userId) {
        Intent intent = new Intent(thisActivity, PrivacyActivity.class);
        intent.putExtra("userId", userId);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(0,0);
    }
}
