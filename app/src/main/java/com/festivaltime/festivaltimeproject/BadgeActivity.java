package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToBadgeActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BadgeActivity extends AppCompatActivity {

    ImageButton back_Btn;
    int count;

    private UserDataBase db;
    private UserDao userDao;
    private String userId;

    private UserEntity loadedUser;

    private boolean isLogin;

    private static final int GALLERY_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);

        //상태바 아이콘 어둡게
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 상태 바 배경 투명 색상 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        back_Btn = findViewById(R.id.before_btn);
        back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db = UserDataBaseSingleton.getInstance(getApplicationContext());
        userDao = db.userDao();

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
                                LayoutInflater inflater = LayoutInflater.from(BadgeActivity.this);
                                View badgeItemView = inflater.inflate(R.layout.badge_items, null);

                                ImageView badgeImage = badgeItemView.findViewById(R.id.badge_image);
                                TextView badgeName = badgeItemView.findViewById(R.id.badge_name);

                                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

                                int rowIndex = 0; // 원하는 행 번호
                                int columnIndex = 0; // 원하는 열 번호
                                layoutParams.rowSpec = GridLayout.spec(rowIndex);
                                layoutParams.columnSpec = GridLayout.spec(columnIndex);



                                GridLayout badgeContainer = findViewById(R.id.badge_container);
                                badgeItemView.setLayoutParams(layoutParams);
                                badgeContainer.addView(badgeItemView);

                            }
                        });
                    }
                }
            }
        });

        Button uploadButton=findViewById(R.id.badge_upload_button);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);//하단 바 navigate 처리
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnItemSelectedListener(item ->

        {
            switch (item.getItemId()) {
                case R.id.action_home:
                    navigateToMainActivity(BadgeActivity.this);
                    return true;
                case R.id.action_map:
                    navigateToMapActivity(BadgeActivity.this);
                    return true;
                case R.id.action_calendar:
                    navigateToCalendarActivity(BadgeActivity.this);
                    return true;
                case R.id.action_favorite:
                    navigateToFavoriteActivity(BadgeActivity.this);
                    return true;
                case R.id.action_profile:
                    navigateToMyPageActivity(BadgeActivity.this);
                    return true;
            }
            return false;
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                //privacyUserImage.setImageBitmap(bitmap);
                //imagePath = saveImageToFile(bitmap);

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


