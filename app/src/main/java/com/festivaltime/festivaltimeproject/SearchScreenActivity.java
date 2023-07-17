package com.festivaltime.festivaltimeproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SearchScreenActivity extends AppCompatActivity {

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        type="A02080200";

        String query = getIntent().getStringExtra("query");

        Button entireButton=findViewById(R.id.detail_search_button);
        entireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSomeActivity.navigateToSearchDetailActivity(SearchScreenActivity.this, query, type);
            }
        });
    }

}