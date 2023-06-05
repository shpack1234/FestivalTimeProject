package com.festivaltime.festivaltimeproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    private TextView queryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        queryTextView = findViewById(R.id.search_text_View);
        String query = getIntent().getStringExtra("query");
        queryTextView.setText(query);
    }
}
