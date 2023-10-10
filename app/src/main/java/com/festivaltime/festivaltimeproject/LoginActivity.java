package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private UserDataBase db;
    private UserDao userDao;

    private UserEntity loadedUser;
    private String userId;
    private Executor executor = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

        Button loginButton = findViewById(R.id.login_button);
        Button signupButton = findViewById(R.id.login_sign_up);
        EditText userName = findViewById(R.id.login_nickname);
        EditText userPassword = findViewById(R.id.login_password);
        Button findNickname=findViewById(R.id.login_find_nickname);
        Button findPassword=findViewById(R.id.login_find_password);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        signupButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (loadedUser == null) {
                                    navigateToSomeActivity.navigateToPrivacyActivity(LoginActivity.this, null);
                                } else {
                                    Toast.makeText(getApplicationContext(), "이미 회원 가입한 기기 입니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        loginButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (userName.getText().toString().equals("admin") && userPassword.getText().toString().equals("admin00")) {
                                    Toast.makeText(getApplicationContext(), "마스터 아이디로 로그인 합니다.", Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("userId", "000000");
                                    editor.commit();
                                    UserEntity userEntity = new UserEntity();
                                    userEntity.setUserId("000000");
                                    userEntity.setUserFavoriteFestival(new ArrayList<>());
                                    userEntity.setPassword(userPassword.getText().toString());
                                    userEntity.setIsLogin(true);
                                    userEntity.setUserName("admin");
                                    userEntity.setUserBirth("2023-10-16");
                                    userEntity.setUserGender("남성");
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            userDao.insertOrUpdate(userEntity);
                                        }
                                    });
                                    finish();
                                    navigateToSomeActivity.navigateToMainActivity(LoginActivity.this);
                                }

                                if (loadedUser == null) {
                                    Toast.makeText(getApplicationContext(), "회원 가입 후 진행해 주세요.", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (loadedUser.getUserName().equals(userName.getText().toString()) && loadedUser.getPassword().equals(userPassword.getText().toString())) {
                                        loadedUser.setIsLogin(true);
                                        executor.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                userDao.insertOrUpdate(loadedUser);
                                            }
                                        });
                                        finish();
                                        navigateToSomeActivity.navigateToMainActivity(LoginActivity.this);
                                    } else if (loadedUser.getUserName().equals(userName.getText().toString())) {
                                        userName.setError("잘못된 닉네임 입니다.");
                                    } else {
                                        userPassword.setError("잘못된 비밀번호 입니다.");
                                    }
                                }
                            }
                        });

                        findNickname.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(loadedUser==null) {
                                    Toast.makeText(getApplicationContext(), "회원 가입 후 진행해 주세요.", Toast.LENGTH_SHORT).show();
                                } else {
                                    navigateToSomeActivity.navigateToFindNickNameActivity(LoginActivity.this);
                                }
                            }
                        });

                        findPassword.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(loadedUser==null) {
                                    Toast.makeText(getApplicationContext(), "회원 가입 후 진행해 주세요.", Toast.LENGTH_SHORT).show();
                                } else {
                                    navigateToSomeActivity.navigateToFindPasswordActivity(LoginActivity.this);
                                }
                            }
                        });
                    }
                });
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(LoginActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(LoginActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(LoginActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(LoginActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(LoginActivity.this);
                return true;
            }
        });
    }

    public void customOnClick(View view) {
        finish();
    }
}