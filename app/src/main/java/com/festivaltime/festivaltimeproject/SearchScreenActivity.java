package com.festivaltime.festivaltimeproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SearchScreenActivity extends AppCompatActivity {

    String type;

    private ApiReader apiReader;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();
    private Executor executor;
    private LinearLayout festivalContainer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        executor = Executors.newSingleThreadExecutor();

        type="A02080200";

        String query = getIntent().getStringExtra("query");

        Button entireButton = findViewById(R.id.detail_search_button);
        entireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSomeActivity.navigateToSearchDetailActivity(SearchScreenActivity.this, query, type);
            }
        });


    }
}
