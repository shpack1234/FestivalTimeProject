package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class SearchActivity extends AppCompatActivity {

    private TextView queryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        queryTextView = findViewById(R.id.search_text_View);
        String query = getIntent().getStringExtra("query");
        if (query != null) {
            queryTextView.setText(query);
        }

        String filePath = "festivaldb01.xls";

        List<Cell[]> rowsWithKeyword = XlsReader.extractRowsWithKeyword(this, filePath, query);
        List<String> columnAValues = new ArrayList<>();

        if (rowsWithKeyword.isEmpty()) {
            Log.d("SearchActivity", "검색 결과가 없습니다.");
        } else {
            // 추출된 행에서 A열 값 추출
            for (Cell[] row : rowsWithKeyword) {
                if (row.length > 0) {
                    String cellContent = row[0].getContents(); // A열 값
                    columnAValues.add(cellContent);
                }
            }
        }

        for (String value : columnAValues) {
            Log.d("SearchActivity", value);
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);  //하단 바 navigate 처리
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(SearchActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(SearchActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(SearchActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(SearchActivity.this);
                return true;
            } else return item.getItemId() == R.id.action_profile;
        });
    }
}
