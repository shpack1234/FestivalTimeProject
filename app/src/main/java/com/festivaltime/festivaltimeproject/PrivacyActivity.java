package com.festivaltime.festivaltimeproject;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.festivaltime.festivaltimeproject.userdatabasepackage.*;
import android.Manifest;

public class PrivacyActivity extends AppCompatActivity {

    private String name;
    private String birth;
    private String gender;
    private Executor executor = Executors.newSingleThreadExecutor();
    private UserDataBase db;
    private UserDao userDao;
    private UserEntity loadedUser;
    private String userId;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        profileImageView=findViewById(R.id.privacy_userimage);

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
        Button logoutButton=findViewById(R.id.login_logout);

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

                            logoutButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadedUser.setIsLogin(false);
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            userDao.insertOrUpdate(loadedUser);
                                        }
                                    });
                                    finish();
                                    navigateToSomeActivity.navigateToMainActivity(PrivacyActivity.this);
                                }
                            });
                        } else {
                            logoutButton.setVisibility(View.GONE);
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

                    Log.d("name", name);

                    if(name.length()<2) {
                        userName.setError("닉네임은 최소 2자 이상 6자 이하로 이루어져 있어야 합니다.");
                    } else if(userId.equals("000000") && !name.equals("admin")) {
                        userName.setError("admin 계정의 닉네임은 변결할 수 없습니다.");
                    } else if(gender==null) {
                        Toast.makeText(getApplicationContext(), "성별을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        UserEntity userEntity;
                        if (loadedUser == null) {
                            // 신규 사용자
                            userEntity = new UserEntity();
                            userEntity.setUserId(userId);
                            userEntity.setUserFavoriteFestival(new ArrayList<>());
                            userEntity.setPassword(passwordText);
                            userEntity.setIsLogin(true);
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
            }
        });
    }

    public void customOnClick(View view) {
        finish();
    }

    public void profileImageOnClick(View view) {
        Log.d("click", "click");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            openGallery();
        }
    }

    private ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        // 이미지를 선택한 후의 작업을 여기에 수행합니다.
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            profileImageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void openGallery() {
        imagePickerLauncher.launch("image/*");
    }

}