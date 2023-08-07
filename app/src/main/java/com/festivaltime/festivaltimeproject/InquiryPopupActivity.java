package com.festivaltime.festivaltimeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class InquiryPopupActivity extends Dialog {

   public Button Back_Btn;

    public InquiryPopupActivity(@NonNull Context context) {
        super(context);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_popup);

        Back_Btn=findViewById(R.id.inquiry_popup_close_btn);

        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EditText editText = findViewById(R.id.editText);
                editText.setText("");
                dismiss();
            }
        });
    }

    //팝업 밖 선택 시  닫힘 방지
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

}



