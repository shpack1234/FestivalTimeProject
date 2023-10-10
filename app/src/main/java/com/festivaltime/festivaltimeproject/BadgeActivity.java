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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BadgeActivity extends AppCompatActivity {
    private Executor executor = Executors.newSingleThreadExecutor();
    ImageButton back_Btn;
    private ImageView upload_img;
    int count;
    Boolean check_img = false;
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

        getSupportActionBar().hide();
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

        for (int i = 0; i < badges.size(); i++) {
            BadgeEntity badge = badges.get(i);

            // 뱃지 레이아웃을 동적으로 생성
            View badgeLayout = getLayoutInflater().inflate(R.layout.badge_items, null);

            ImageView badgeImage = badgeLayout.findViewById(R.id.badge_image);
            TextView badgeName = badgeLayout.findViewById(R.id.badge_name);

            // 뱃지 이미지와 이름 설정
            badgeImage.setImageBitmap(BitmapFactory.decodeFile(badge.imagePath));
            badgeName.setText(badge.badgeName);

            // 각 뱃지 레이아웃에 고유한 ID 설정
            badgeLayout.setId(View.generateViewId());

            // 뱃지 레이아웃을 badgeContainer에 추가
            badgeContainer.addView(badgeLayout);
        }
    }

    // 뱃지 UI 업데이트
    private void updateBadgeUI() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // 백그라운드 스레드에서 뱃지 데이터베이스를 조회하고 UI를 업데이트
                List<BadgeEntity> badges = badgeDao.getAllBadges();

                // UI 업데이트 메서드 호출
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GridLayout badgeContainer = findViewById(R.id.badge_container);
                        badgeContainer.removeAllViews(); // 기존 뱃지를 모두 제거
                        for (int i = 0; i < badges.size(); i++) {
                            BadgeEntity badge = badges.get(i);

                            // 뱃지 레이아웃을 동적으로 생성
                            View badgeLayout = getLayoutInflater().inflate(R.layout.badge_items, null);

                            ImageView badgeImage = badgeLayout.findViewById(R.id.badge_image);
                            TextView badgeName = badgeLayout.findViewById(R.id.badge_name);

                            // 뱃지 이미지와 이름 설정
                            badgeImage.setImageBitmap(BitmapFactory.decodeFile(badge.imagePath));
                            badgeName.setText(badge.badgeName);

                            // 각 뱃지 레이아웃에 고유한 ID 설정
                            badgeLayout.setId(View.generateViewId());

                            // 뱃지 레이아웃을 badgeContainer에 추가
                            badgeContainer.addView(badgeLayout);
                        }
                    }
                });
            }
        });
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
                // select_img와 select_ft에서 내용 가져오기
                String badgeNameText = ft_name.getText().toString();

                if (upload_img.getDrawable() != null && check_img && !badgeNameText.equals("(축제를 선택해주세요.)")) {
                    // 업로드된 이미지 Drawable 가져오기
                    Drawable drawable = upload_img.getDrawable();

                    // 뱃지 이미지 Bitmap 초기화
                    final Bitmap badgeBitmap;

                    if (drawable instanceof BitmapDrawable) {
                        // BitmapDrawable인 경우 직접 Bitmap을 가져옵니다.
                        badgeBitmap = ((BitmapDrawable) drawable).getBitmap();

                        // 데이터베이스 작업을 Executor를 사용하여 백그라운드 스레드에서 실행
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                // 뱃지 이미지를 사용하여 작업을 수행합니다.
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
                                            updateBadgeUI();
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

                            }
                        });
                    } else {
                        // 선택된 이미지가 BitmapDrawable이 아닌 경우에 대한 처리를 추가할 수 있습니다.
                        // 예를 들어, Toast 메시지를 표시하거나 다른 작업을 수행할 수 있습니다.
                        Toast.makeText(BadgeActivity.this, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BadgeActivity.this, "뱃지 정보를 확인하세요.", Toast.LENGTH_SHORT).show();
                }
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
                    check_img = true;
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "profile_image_" + timeStamp + ".jpg";
        File imageFile = new File(filesDir, imageFileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 파일 저장 실패 시 null 반환
        }
        return imageFile.getAbsolutePath();
    }

}


