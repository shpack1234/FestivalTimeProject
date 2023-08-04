package com.festivaltime.festivaltimeproject.calendaract;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryDao;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryEntity;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;

public class CategoryLoader extends AsyncTaskLoader<Void> {
    private CalendarCategoryEntity newCategory;
    private CalendarCategoryDao categoryDao;

    public CategoryLoader(Context context, CalendarCategoryEntity category, CalendarCategoryDao dao) {
        super(context);
        this.newCategory = category;
        this.categoryDao = dao;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Void loadInBackground() {
        categoryDao.InsertCategory(newCategory);
        return null;
    }
}