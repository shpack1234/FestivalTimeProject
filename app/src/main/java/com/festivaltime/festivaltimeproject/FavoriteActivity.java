package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class FavoriteActivity extends AppCompatActivity {

    private ImageButton imgbtn;
    private ImageButton delbtn;

    private ImageButton addbtn;
    private RelativeLayout relative1;
    private LinearLayout lila;
    private ViewGroup parentView1;
    private ViewGroup parentView2;
    private FestivalDataBase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

         db= Room.databaseBuilder(getApplicationContext(), FestivalDataBase.class, "FestivalDatabase.db")
                .fallbackToDestructiveMigration()
                 .setQueryExecutor(AsyncTask.THREAD_POOL_EXECUTOR) // 백그라운드 스레드에서 작업을 실행하도록 설정
                .build();

        addbtn = findViewById(R.id.addButton);
        imgbtn = findViewById(R.id.imagebutton);


        lila = findViewById(R.id.linearLayout);
        parentView1 = findViewById(R.id.rela1);
        parentView2 = findViewById(R.id.rela2);


        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteActivity.this, MainActivity.class); //실행해보려고 잠깐 Main으로 이동하게 해놓음
                startActivity(intent);

            }
        });


/**
        delbtn.setOnClickListener(view -> { // 찜 삭제
            RelativeLayout rela1 = findViewById(R.id.rela1);
            rela1.setVisibility(View.GONE);
        });
**/

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);  //하단 바 navigate 처리
        bottomNavigationView.setSelectedItemId(R.id.action_favorite);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(FavoriteActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(FavoriteActivity.this);
                return false;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(FavoriteActivity.this);
                return false;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(FavoriteActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(FavoriteActivity.this);
                return true;
            }
        });



        saveIDToDatabase("1234");

        deleteIDFromDatabase("1234");

    }

    private void saveIDToDatabase(String id) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                FestivalDao festivalDao = db.festivalDao();
                if (festivalDao.getEntityById(strings[0]) != null) {
                    return null; // 중복 저장 방지
                }
                FestivalEntity entity = new FestivalEntity();
                entity.setId(strings[0]);
                festivalDao.insert(entity);
                return null;
            }
        }.execute(id);
    }

    private void deleteIDFromDatabase(String id) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                FestivalDao festivalDao = db.festivalDao();
                FestivalEntity entityToDelete = festivalDao.getEntityById(strings[0]);
                if (entityToDelete != null) {
                    festivalDao.delete(entityToDelete);
                }
                return null;
            }
        }.execute(id);
    }

    // relativeLayout 삭제 메소드
    private void removeRelativeLayout(ViewGroup parentView) {
        parentView.removeAllViews();
    }
}


