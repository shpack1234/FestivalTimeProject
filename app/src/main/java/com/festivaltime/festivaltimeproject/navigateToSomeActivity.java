package com.festivaltime.festivaltimeproject;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;

public class navigateToSomeActivity {
    public static void navigateToSearchActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, SearchActivity.class);
        thisActivity.startActivity(intent);
    }

    public static void navigateToMainActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, MainActivity.class);
        thisActivity.startActivity(intent);
    }

    public static void navigateToMapActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, MapActivity.class);
        thisActivity.startActivity(intent);
    }

    public static void navigateToCalendarActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, CalendarActivity.class);
        thisActivity.startActivity(intent);
    }
    /*public static void navigateToMyPageActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, MyPageActivity.class);
        thisActivity.startActivity(intent);
    } */

    /*public static void navigateToFavoriteActivity(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, Favoritectivity.class);
        thisActivity.startActivity(intent);
    } */
}
