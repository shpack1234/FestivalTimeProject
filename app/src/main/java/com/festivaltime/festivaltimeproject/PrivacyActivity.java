package com.festivaltime.festivaltimeproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.festivaltime.festivaltimeproject.userdatabasepackage.*;

public class PrivacyActivity extends AppCompatActivity {

    private String name;
    private String birth;
    private String gender;
    private UserDataBase db;
    private UserDao userDao;
    private Executor executor = Executors.newSingleThreadExecutor();

    private UserEntity loadedUser;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

        TextView privacy_userid = findViewById(R.id.privacy_userid);
        EditText userName = findViewById(R.id.privacy_username);
        DatePicker userBirthDatePicker = findViewById(R.id.privacy_userbirth);
        RadioGroup userGender = findViewById(R.id.privacy_usergender);
        Button saveButton = findViewById(R.id.privacy_savebutton);
        EditText password=findViewById(R.id.privacy_password);
        EditText passwordConfirm=findViewById(R.id.privacy_password_confirm);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadedUser!=null) {
                            RadioButton radioButtonMale = findViewById(R.id.radioButtonMale);
                            RadioButton radioButtonFemale = findViewById(R.id.radioButtonFemale);

                            LinearLayout passwordBox=findViewById(R.id.privacy_password_box);
                            passwordBox.setVisibility(View.GONE);

                            privacy_userid.setText(userId);
                            userName.setText(loadedUser.getUserName());
                            String userBirth = loadedUser.getUserBirth();
                            String[] birthParts = userBirth.split("-");
                            int year = Integer.parseInt(birthParts[0]);
                            int month = Integer.parseInt(birthParts[1]) - 1;
                            int day = Integer.parseInt(birthParts[2]);
                            userBirthDatePicker.updateDate(year, month, day);

                            String gender = loadedUser.getUserGender();
                            if (gender.equals("남성")) {
                                radioButtonMale.setChecked(true);
                            } else if (gender.equals("여성")) {
                                radioButtonFemale.setChecked(true);
                            }

                        }
                    }
                });
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordText=password.getText().toString();
                String passwordConfirmText=passwordConfirm.getText().toString();

                if(!passwordText.equals(passwordConfirmText)) {
                    passwordConfirm.setError("비밀번호가 일치하지 않습니다.");
                } else {
                    name = userName.getText().toString();

                    // 생년월일 가져오기
                    int year = userBirthDatePicker.getYear();
                    int month = userBirthDatePicker.getMonth();
                    int day = userBirthDatePicker.getDayOfMonth();
                    birth = year + "-" + (month + 1) + "-" + day;

                    // 성별 가져오기
                    int selectedRadioButtonId = userGender.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                    gender = selectedRadioButton.getText().toString();

                    UserEntity userEntity;
                    if (loadedUser == null) {
                        Log.d("loadUser", "null");
                        // 신규 사용자
                        userEntity = new UserEntity();
                        userEntity.setUserId(userId);
                        userEntity.setUserFavoriteFestival(new ArrayList<>());
                        userEntity.setPassword(passwordText);
                    } else {
                        // 기존 사용자
                        userEntity = loadedUser;
                        userEntity.setUserId(loadedUser.getUserId());
                    }
                    userEntity.setUserName(name);
                    userEntity.setUserBirth(birth);
                    userEntity.setUserGender(gender);

                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            userDao.insertOrUpdate(userEntity);
                        }
                    });

                    finish();
                    navigateToSomeActivity.navigateToMainActivity(PrivacyActivity.this);
                }
            }
        });
    }
}