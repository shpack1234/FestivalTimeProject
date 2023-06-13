package com.festivaltime.festivaltimeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class MainActivity extends AppCompatActivity {
    private SearchView searchView;
    private String query;

    SliderView sliderView;
    int[] hot_festival_images={R.drawable.image01, R.drawable.image02, R.drawable.image03};
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.search_view);
        searchView.setOnTouchListener((v, event) -> {
            searchView.setIconified(false);
            searchView.performClick();
            return true;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                query = s;
                performSearch(query);
                navigateToSearchActivity();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        sliderView=findViewById(R.id.image_slider);

        SliderAdapter sliderAdapter=new SliderAdapter(hot_festival_images);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.startAutoCycle();
    }
    private void performSearch(String query) {
        System.out.println("검색어: " + query);
    }

    private void navigateToSearchActivity() {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }


}
