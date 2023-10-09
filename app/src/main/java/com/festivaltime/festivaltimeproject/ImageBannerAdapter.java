package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageBannerAdapter extends RecyclerView.Adapter<ImageBannerAdapter.ImageViewHolder> {
    private Activity activity;
    private Context context;
    private String[] imageUrls; // 이미지 URL 배열
    private String[] imageIds; // 각 이미지에 대한 ID 배열 (문자열)
    private ViewPager2 viewPager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable slideRunnable;

    public ImageBannerAdapter(Context context, String[] imageUrls, String[] imageIds, ViewPager2 viewPager, Activity activity) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.imageIds = imageIds;
        this.viewPager = viewPager;
        this.activity = activity;
        setupAutoSlide();
    }

    private void setupAutoSlide() {
        slideRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < imageUrls.length - 1) {
                    viewPager.setCurrentItem(currentItem + 1);
                } else {
                    viewPager.setCurrentItem(0);
                }
                handler.postDelayed(this, 2000); // 2초마다 자동 스크롤
            }
        };
    }

    public void startAutoSlide() {
        handler.postDelayed(slideRunnable, 2000);
    }

    public void stopAutoSlide() {
        handler.removeCallbacks(slideRunnable);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.festival_banner, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls[position];
        String imageId = imageIds[position];
        new DownloadImageTask(holder.imageView, imageId).execute(imageUrl);
    }

    @Override
    public int getItemCount() {
        return imageUrls.length;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_banner);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String contentId = imageIds[getAdapterPosition()];
                    navigateToDetailFestivalActivity(activity, contentId);
                }
            });


            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Log.d("Touch", "down");
                            stopAutoSlide(); // 손을 올릴 때 자동 스크롤 중지
                            break;
                        case MotionEvent.ACTION_UP:
                            Log.d("Touch", "up");
                            startAutoSlide(); // 손을 내릴 때 자동 스크롤 시작
                            break;
                    }
                    return false;
                }
            });
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        private String imageId;

        public DownloadImageTask(ImageView imageView, String imageId) {
            this.imageView = imageView;
            this.imageId = imageId;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(urls[0]).openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
                viewPager.requestLayout();
            }
        }
    }
}
