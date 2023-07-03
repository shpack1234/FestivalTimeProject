package com.festivaltime.festivaltimeproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PrivacyActivity extends AppCompatActivity {

    private String name;
    private String birth;
    private String gender;
    private UserDataBase db;
    private UserDao userDao;
    private Executor executor = Executors.newSingleThreadExecutor();

    private UserEntity loadedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        db = Room.databaseBuilder(getApplicationContext(), UserDataBase.class, "User_Database").fallbackToDestructiveMigration().build();
        userDao = db.userDao();

        String userId = getIntent().getStringExtra("userId");

        TextView privacy_userid = findViewById(R.id.privacy_userid);
        if (userId == null) {
            userId="null";
            privacy_userid.setText("no Id");
        } else {
            privacy_userid.setText(userId);
        }

        EditText userName = findViewById(R.id.privacy_username);
        DatePicker userBirthDatePicker = findViewById(R.id.privacy_userbirth);
        RadioGroup userGender = findViewById(R.id.privacy_usergender);
        Button saveButton = findViewById(R.id.privacy_savebutton);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfo();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadedUser!=null) {
                            RadioButton radioButtonMale = findViewById(R.id.radioButtonMale);
                            RadioButton radioButtonFemale = findViewById(R.id.radioButtonFemale);

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

        String finalUserId = userId;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                UserEntity userEntity = new UserEntity();
                if (loadedUser==null) {
                    userEntity.setUserIdAssigned(true);
                    userEntity.setUserId(finalUserId);
                }
                userEntity.setUserName(name);
                userEntity.setUserBirth(birth);
                userEntity.setUserGender(gender);

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        userDao.insert(userEntity);
                    }
                });

                finish();
            }
        });
    }
}