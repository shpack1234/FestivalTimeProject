package com.festivaltime.festivaltimeproject;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.festivaltime.festivaltimeproject.inquirydatabasepackage.DatabaseHolder;

public class InquiryPopupActivity extends Dialog {

    private Button Submit_Btn;
    public EditText Edit_Text;
    public Button Back_Btn;
    public InquiryDao inquiryDao;
    private String userInputText;

    public String getUserInputText() {
        return userInputText;
    }

    public InquiryPopupActivity(@NonNull Context context) {
        super(context);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_popup);

        Back_Btn=findViewById(R.id.inquiry_popup_close_btn);
        Submit_Btn=findViewById(R.id.submit_Btn);
        Edit_Text = findViewById(R.id.editText);

        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Edit_Text.setText("");
                dismiss();
            }
        });

        Submit_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.editText);
                userInputText = editText.getText().toString();


                // 데이터베이스에 저장
                InquiryDatabase inquiryDatabase = DatabaseHolder.getAppDatabase(getContext());
                InquiryDao inquiryDao = inquiryDatabase.inquiryDao();

                InquiryEntity entity = new InquiryEntity(userInputText);
                inquiryDao.insertInquiry(entity);

                // TextView에 띄워주기
                TextView inquiryText = findViewById(R.id.inquiry_text);
                inquiryText.setText(userInputText);


                // 팝업 닫기
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



