package com.festivaltime.festivaltimeproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

class Hello {
    Hello() {

    }
    private void HelloFunction() {
        System.out.println("Hello World");
    }
}

class NewHello extends  Hello {
    int a;
    NewHello() {
        this.a=a;
    }
}

public class MainActivity extends AppCompatActivity {
    Button b1;
    int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = findViewById(R.id.page1);
        b1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this,CalendarActivity.class);
                        startActivity(i);
                    }
                }
        );
    }
}