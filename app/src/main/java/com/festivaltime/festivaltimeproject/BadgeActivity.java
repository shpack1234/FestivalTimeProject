package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToBadgeActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.festivaltime.festivaltimeproject.calendaract.CalendarPopupActivity;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryDataBase;
import com.festivaltime.festivaltimeproject.calendarcategorydatabasepackage.CalendarCategoryEntity;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class BadgeActivity extends AppCompatActivity {

    ImageButton back_Btn;
    private ImageView upload_img;
    private Button select_ft;
    private TextView ft_name;
    int count;
    protected Context mContext;

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
        mContext = this;

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_badge, null);
        ft_name = dialogView.findViewById(R.id.badge_name);
        select_ft = dialogView.findViewById(R.id.select_ft);

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

    public void showPopupDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.custom_popup);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_badge, null);
        Button confirmButton = dialogView.findViewById(R.id.dialog_popup_add_btn);

        upload_img = dialogView.findViewById(R.id.badge_image);
        //TextView ft_name = dialogView.findViewById(R.id.badge_name);

        Button select_img = dialogView.findViewById(R.id.upload_image);
        //Button select_ft = dialogView.findViewById(R.id.select_ft);

        dialogBuilder.setView(dialogView); // 다이얼로그에 뷰 추가

        Dialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
                new LoadCategoryTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        new LoadCategoryTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        dialog.show();
    }

    private class LoadCategoryTask extends AsyncTask<Void, Void, List<CalendarEntity>> {
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
            setupFestivalMenu(festivals);
        }
    }
    private void setupFestivalMenu(List<CalendarEntity> festivals) {
        // 팝업 메뉴를 생성하고 위치 지정
        PopupMenu popupMenu = new PopupMenu(this, ft_name);

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


