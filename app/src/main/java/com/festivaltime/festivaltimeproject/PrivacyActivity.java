package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

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
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.festivaltime.festivaltimeproject.userdatabasepackage.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

    private static final int GALLERY_REQUEST_CODE = 123;

    private ImageView privacyUserImage;
    private String imagePath = null;

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
        EditText password = findViewById(R.id.privacy_password);
        EditText passwordConfirm = findViewById(R.id.privacy_password_confirm);
        Button logoutButton = findViewById(R.id.login_logout);
        Button deleteButton = findViewById(R.id.login_Delete);
        privacyUserImage = findViewById(R.id.privacy_userimage);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadedUser != null) {
                            RadioButton radioButtonMale = findViewById(R.id.radioButtonMale);
                            RadioButton radioButtonFemale = findViewById(R.id.radioButtonFemale);

                            if (imagePath != null) {
                                // 이미지 파일이 있는 경우
                                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                                privacyUserImage.setImageBitmap(bitmap);
                            } else {
                                // imagePath가 null인 경우 기본 이미지 설정
                                privacyUserImage.setImageResource(R.mipmap.image);
                            }


                            LinearLayout passwordBox = findViewById(R.id.privacy_password_box);
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

                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (loadedUser.getUserId().equals("000000")) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "admin 계정은 탈퇴할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                userDao.delete(loadedUser);
                                                finish();
                                                navigateToSomeActivity.navigateToMainActivity(PrivacyActivity.this);
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            logoutButton.setVisibility(View.GONE);
                            deleteButton.setVisibility(View.GONE);

                        }
                    }
                });
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordText = password.getText().toString();
                String passwordConfirmText = passwordConfirm.getText().toString();

                if (!passwordText.equals(passwordConfirmText)) {
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
                    if (selectedRadioButton == null) {
                        Toast.makeText(getApplicationContext(), "성별을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                        gender = null;
                    } else {
                        gender = selectedRadioButton.getText().toString();
                    }

                    if (name.length() < 2) {
                        userName.setError("닉네임은 최소 2자 이상 6자 이하로 이루어져 있어야 합니다.");
                    } else if (userId.equals("000000") && !name.equals("admin")) {
                        userName.setError("admin 계정의 닉네임은 변결할 수 없습니다.");
                    } else if (gender == null) {
                        Toast.makeText(getApplicationContext(), "성별을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        UserEntity userEntity;
                        if (loadedUser == null) {
                            // 신규 사용자
                            userEntity = new UserEntity();
                            userEntity.setUserId(userId);
                            userEntity.setUserFavoriteFestival(new ArrayList<>());
                            //userEntity.setUserBadge(new ArrayList<>());
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
                        userEntity.setProfileImage(imagePath);

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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(PrivacyActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(PrivacyActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(PrivacyActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(PrivacyActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(PrivacyActivity.this);
                return true;
            }
        });
    }

    public void customOnClick(View view) {
        finish();
    }

    public void profileImageOnClick(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                privacyUserImage.setImageBitmap(bitmap);
                imagePath = saveImageToFile(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToFile(Bitmap bitmap) {
        File filesDir = getFilesDir();
        File imageFile = new File(filesDir, "profile_image.jpg");

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile.getAbsolutePath();
    }
}