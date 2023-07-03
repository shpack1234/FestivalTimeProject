package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.content.*;

import java.util.ArrayList;
import java.util.List;

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
                .build();

        saveIDToDatabase("1234");
        
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

        // 삭제 버튼 1, 2
        ImageButton delbtn1 = findViewById(R.id.deleteButton);
        delbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRelativeLayout(parentView1);
            }
        });

        ImageButton delbtn2 = findViewById(R.id.deleteButton2);
        delbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRelativeLayout(parentView2);
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

    }

    private void saveIDToDatabase(String id) {
        FestivalDao festivalDao = db.festivalDao();
        FestivalEntity entity = new FestivalEntity();
        entity.setId(id);
        festivalDao.insert(entity);
    }

    // relativeLayout 삭제 메소드
    private void removeRelativeLayout(ViewGroup parentView) {
        parentView.removeAllViews();
    }
}


