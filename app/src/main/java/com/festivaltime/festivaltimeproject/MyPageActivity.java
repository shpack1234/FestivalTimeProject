package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToAppSettingActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToChangePasswordActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToBadgeActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToInquiryActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToLoginActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToPrivacyActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.festivaltime.festivaltimeproject.badgedatabase.BadgeDao;
import com.festivaltime.festivaltimeproject.badgedatabase.BadgeDatabase;
import com.festivaltime.festivaltimeproject.badgedatabase.BadgeEntity;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MyPageActivity extends AppCompatActivity {

    private UserDataBase db;
    private UserDao userDao;
    private String userId;

    private UserEntity loadedUser;

    private boolean isLogin;

    boolean userExist = false;

    private String imagePath;
    private ImageView card1ImageView;
    private ImageView card2ImageView;
    private ImageView card3ImageView;

    private BadgeDatabase badgeDatabase;
    private BadgeDao badgeDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        getSupportActionBar().hide();
        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);//하단 바 navigate 처리
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    navigateToMainActivity(MyPageActivity.this);
                    return true;
                case R.id.action_map:
                    navigateToMapActivity(MyPageActivity.this, null);
                    return true;
                case R.id.action_calendar:
                    navigateToCalendarActivity(MyPageActivity.this);
                    return true;
                case R.id.action_favorite:
                    navigateToFavoriteActivity(MyPageActivity.this);
                    return true;
                case R.id.action_profile:
                    navigateToMyPageActivity(MyPageActivity.this);
                    return true;
            }
            return false;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();
        badgeDatabase = BadgeDatabase.getInstance(this);
        badgeDao = badgeDatabase.badgeDao();

        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);
        CardView card3 = findViewById(R.id.card3);
        card1ImageView = findViewById(R.id.card1).findViewById(R.id.img1);
        card2ImageView = findViewById(R.id.card2).findViewById(R.id.img2);
        card3ImageView = findViewById(R.id.card3).findViewById(R.id.img3);

        // 뱃지 데이터베이스에서 이미지 정보 가져오기
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<BadgeEntity> badgeList = badgeDao.getAllBadges();

                // 뱃지 이미지를 저장할 리스트 생성
                List<Bitmap> badgeImages = new ArrayList<>();

                // 뱃지 데이터베이스에서 이미지 정보를 가져와서 리스트에 추가
                for (BadgeEntity badgeEntity : badgeList) {
                    String imagePath = badgeEntity.imagePath;
                    Bitmap bitmap = loadImageFromPath(imagePath);
                    if (bitmap != null) {
                        badgeImages.add(bitmap);
                    }
                }

                // 뱃지 이미지를 적절한 위치에 저장 또는 표시
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (badgeImages.size() >= 3) {
                            // 뱃지 이미지를 UI의 ImageView에 설정
                            card1ImageView.setImageBitmap(badgeImages.get(0));
                            card2ImageView.setImageBitmap(badgeImages.get(1));
                            card3ImageView.setImageBitmap(badgeImages.get(2));

                            // 뱃지 이미지가 있을 때, TextView를 숨깁니다.
                            TextView badgeMessage = findViewById(R.id.badge_message);
                            badgeMessage.setVisibility(View.GONE);
                        } else {
                            // 뱃지 이미지가 3개 미만이거나 모두 null인 경우, TextView에 메시지를 설정하고 표시합니다.
                            TextView badgeMessage = findViewById(R.id.badge_message);
                            badgeMessage.setVisibility(View.VISIBLE);

                            // 뱃지 이미지가 없을 때, ImageView를 초기화합니다.
                            card1ImageView.setImageResource(android.R.color.transparent);
                            card1.setVisibility(View.GONE);
                            card2ImageView.setImageResource(android.R.color.transparent);
                            card2.setVisibility(View.GONE);
                            card3ImageView.setImageResource(android.R.color.transparent);
                            card3.setVisibility(View.GONE);
                        }
                    }
                });


            }
        });

        ImageView profileImage = findViewById(R.id.user_profile_image);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);

                if (loadedUser != null) {
                    if (loadedUser.getIsLogin()) {
                        userId = loadedUser.getUserId();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View blurLayout = findViewById(R.id.blur_layout);
                                blurLayout.setVisibility(View.GONE);

                                TextView userNickname = findViewById(R.id.user_nickname);
                                TextView userIdText = findViewById(R.id.user_id);

                                userNickname.setText(loadedUser.getUserName());
                                userIdText.setText("#" + userId);

                                imagePath=loadedUser.getProfileImage();

                                if(imagePath!=null) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                                    profileImage.setImageBitmap(bitmap);
                                } else {
                                    profileImage.setImageResource(R.drawable.ic_user);
                                }


                                userExist = true;
                                isLogin = true;
                            }
                        });
                    } else {
                        userExist = true;
                        isLogin = false;
                    }
                }
            }
        });

    }

    private Bitmap loadImageFromPath(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            // 이미지 경로에서 비트맵 로드
            try {
                FileInputStream fileInputStream = new FileInputStream(imagePath);
                return BitmapFactory.decodeStream(fileInputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }


    private void loadAndSetImage(ImageView imageView, int drawableResId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableResId);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
        }
    }

    public void customOnClick(View v) {
        if (userExist) {
            navigateToBadgeActivity(MyPageActivity.this);
        }
    }

    public void InquiryOnClick(View v) {
        Intent urlintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://open.kakao.com/o/sEEKjlJf"));
        startActivity(urlintent);
        //navigateToInquiryActivity(MyPageActivity.this);
    }

    public void LoginOnClick(View v) {
        Log.d("st", String.valueOf(isLogin) + String.valueOf(userExist));


        if (userExist) {
            if (!isLogin) {
                navigateToLoginActivity(MyPageActivity.this, userId);
            } else {
                navigateToPrivacyActivity(MyPageActivity.this, null);
            }
        } else {
            navigateToLoginActivity(MyPageActivity.this, null);
        }
    }

    public void settingOnClick(View v) {
        navigateToAppSettingActivity(MyPageActivity.this);
    }

    public void changePWOnClick(View v) {
        if (userExist) {
            if (!isLogin) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                navigateToChangePasswordActivity(MyPageActivity.this);
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
