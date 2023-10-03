package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.festivaltime.festivaltimeproject.badgedatabase.BadgeDao;
import com.festivaltime.festivaltimeproject.badgedatabase.BadgeDatabase;
import com.festivaltime.festivaltimeproject.badgedatabase.BadgeEntity;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kakao.vectormap.label.Badge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BadgeActivity extends AppCompatActivity {
    private Executor executor = Executors.newSingleThreadExecutor();
    ImageButton back_Btn;
    private ImageView upload_img;
    int count;
    protected Context mContext;

    private UserDataBase db;
    private UserDao userDao;
    private String userId;

    private UserEntity loadedUser;
    private BadgeDao badgeDao;
    private BadgeEntity badgeEntity;
    private BadgeDatabase badgeDatabase;
    private boolean isLogin;

    private static final int GALLERY_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);
        mContext = this;

        badgeDatabase = BadgeDatabase.getInstance(getApplicationContext());
        badgeDao = badgeDatabase.badgeDao();

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
                // 백그라운드 스레드에서 뱃지 데이터베이스를 조회하고 UI를 업데이트
                List<BadgeEntity> badges = badgeDao.getAllBadges();

                // UI 업데이트 메서드 호출
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateBadgeUI(badges);
                        Log.d("BadgeActivity", "Badge UI updated.");
                    }
                });
            }

        });


        Button uploadButton = findViewById(R.id.badge_upload_button);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupDialog();

                //Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
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

    private void updateBadgeUI(List<BadgeEntity> badges) {
        GridLayout badgeContainer = findViewById(R.id.badge_container);
        badgeContainer.removeAllViews(); // 기존 뱃지를 모두 제거

        int maxColumnCount = 3;

        for (int i = 0; i < badges.size(); i++) {
            BadgeEntity badge = badges.get(i);

            // 뱃지를 동적으로 생성하고 UI에 추가하기 전에 로그 추가
            Log.d("BadgeActivity", "Adding Badge: " + badge.badgeName);

            // 뱃지를 동적으로 생성하고 UI에 추가
            LayoutInflater inflater = LayoutInflater.from(BadgeActivity.this);
            View badgeItemView = inflater.inflate(R.layout.badge_items, null);

            ImageView badgeImage = badgeItemView.findViewById(R.id.badge_image);
            TextView badgeName = badgeItemView.findViewById(R.id.badge_name);

            Bitmap bitmap = BitmapFactory.decodeFile(badge.imagePath);
            badgeImage.setImageBitmap(bitmap);
            badgeName.setText(badge.badgeName); // 뱃지 이름 설정

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = 0; // 너비를 0으로 설정하여 가중치를 적용
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT; // 높이는 WRAP_CONTENT로 설정
            int rowIndex = i / maxColumnCount; // 현재 행 번호 계산
            int columnIndex = i % maxColumnCount; // 현재 열 번호 계산
            layoutParams.rowSpec = GridLayout.spec(rowIndex);
            layoutParams.columnSpec = GridLayout.spec(columnIndex, 1f); // 가중치 적용

            badgeItemView.setLayoutParams(layoutParams);
            badgeContainer.addView(badgeItemView);
        }
    }


    public void showPopupDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.custom_popup);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_badge, null);
        Button confirmButton = dialogView.findViewById(R.id.dialog_popup_add_btn);

        upload_img = dialogView.findViewById(R.id.badge_image);
        TextView ft_name = dialogView.findViewById(R.id.badge_name);

        Button select_img = dialogView.findViewById(R.id.upload_image);
        Button select_ft = dialogView.findViewById(R.id.select_ft);

        dialogBuilder.setView(dialogView); // 다이얼로그에 뷰 추가

        Dialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 데이터베이스 작업을 Executor를 사용하여 백그라운드 스레드에서 실행
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // select_img와 select_ft에서 내용 가져오기
                        String badgeNameText = ft_name.getText().toString();

                        // upload_img의 Drawable을 Bitmap으로 변환
                        Bitmap badgeBitmap = ((BitmapDrawable) upload_img.getDrawable()).getBitmap();
                        badgeDatabase = BadgeDatabase.getInstance(getApplicationContext());
                        badgeDao = badgeDatabase.badgeDao();

                        if (badgeBitmap != null) {
                            String badgeImagePath = saveImageToFile(badgeBitmap); // 이미지 파일 경로를 얻음
                            Log.d("BadgeActivity", "Badge Image Path: " + badgeImagePath); // 로그 추가

                            if (badgeImagePath != null) {
                                BadgeEntity badge = new BadgeEntity();

                                badge.imagePath = badgeImagePath; // 이미지 파일 경로를 저장
                                badge.badgeName = badgeNameText;

                                badgeDao.insertBadge(badge);
                                Log.d("BadgeActivity", "Badge inserted into database: " + badge.badgeName);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(BadgeActivity.this, "이미지를 저장하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BadgeActivity.this, "뱃지 이미지를 선택하세요.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });

        select_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 갤러리에서 이미지를 선택하기 위한 Intent 생성
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        select_ft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadFTTask(select_ft, ft_name).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        new LoadFTTask(select_ft, ft_name).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        dialog.show();
    }

    private class LoadFTTask extends AsyncTask<Void, Void, List<CalendarEntity>> {
        private Button selectFt;
        private TextView ftName;

        public LoadFTTask(Button selectFt, TextView ftName) {
            this.selectFt = selectFt;
            this.ftName = ftName;
        }

        @Override
        protected List<CalendarEntity> doInBackground(Void... voids) {
            // 데이터베이스 인스턴스 생성
            CalendarDatabase festivalDataBase = CalendarDatabase.getInstance(mContext);

            // 데이터베이스에서 카테고리 목록을 가져옴
            return festivalDataBase.calendarDao().getCalendarEntitiesByCategory("#ed5c55");
        }

        @Override
        protected void onPostExecute(List<CalendarEntity> festivals) {
            // 데이터베이스에서 가져온 카테고리 목록을 메뉴에 추가하는 코드
            setupFestivalMenu(festivals, ftName, selectFt);
        }
    }

    private void setupFestivalMenu(List<CalendarEntity> festivals, TextView ft_name, Button
            select_ft) {
        // 팝업 메뉴를 생성하고 위치 지정
        PopupMenu popupMenu = new PopupMenu(this, select_ft);

        // 메뉴 인플레이션
        popupMenu.getMenuInflater().inflate(R.menu.dialog_badge, popupMenu.getMenu());

        // 동적으로 추가할 그룹 ID
        int groupId = R.id.dynamic_items_group;

        // 데이터베이스에서 가져온 카테고리 목록을 메뉴에 추가
        for (CalendarEntity fesitval : festivals) {
            popupMenu.getMenu().add(groupId, Menu.NONE, Menu.NONE, fesitval.getTitle());
        }

        // 팝업 메뉴 아이템 클릭 리스너 설정
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 선택한 메뉴 아이템의 이름을 가져와서 처리
                String selectedCategory = item.getTitle().toString();

                // 선택한 카테고리 이름을 설정
                ft_name.setText(selectedCategory);
                return true;
            }
        });

        // 팝업 메뉴 보이기
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                if (upload_img != null) {
                    upload_img.setImageBitmap(bitmap);
                }
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


