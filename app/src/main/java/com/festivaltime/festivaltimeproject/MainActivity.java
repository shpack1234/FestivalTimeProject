package com.festivaltime.festivaltimeproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


class Hello {
    Hello() {
        System.out.println("Hello World");
    }
}

class HelloTwice extends Hello {
    HelloTwice() {
        super();
        System.out.println("Hello World");
    }
    private void HelloPrint(int num) {
        for(int i=0; i<num; i++) {
            System.out.println("Hello World");
        }
    }
}

class Hellobabo extends Hello {
    Hellobabo() {
        super();
        System.out.println("Hello World");
    }
    private void HelloPrint(int num) {
        for(int i=0; i<num; i++) {
            System.out.println("Hello World");
        }
    }
}

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}