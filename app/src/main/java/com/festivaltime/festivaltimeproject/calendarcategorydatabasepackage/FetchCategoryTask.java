package com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage;

import android.content.Context;


import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FetchCategoryTask {
    private Context mContext;
    private CalendarCategoryDao categoryDao;

    public FetchCategoryTask(Context context, CalendarCategoryDao categoryDao) {
        this.mContext = context;
        this.categoryDao = categoryDao;
    }

    public void fetchCategories(FetchCategoryTaskListener listener) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // 데이터베이스에서 일정 데이터를 가져오기 위해 DAO를 사용하여 쿼리 수행
                List<CalendarCategoryEntity> categories = categoryDao.getAllCategoryEntity();

                // 가져온 일정 데이터를 listener를 통해 CalendarActivity로 전달
                listener.onFetchCompleted(categories);
            }
        });
    }

    // FetchScheduleTask에서 일정 데이터를 가져온 후, CalendarActivity에 전달하는 리스너 인터페이스
    public interface FetchCategoryTaskListener {
        void onFetchCompleted(List<CalendarCategoryEntity> categorylist);
    }
}
