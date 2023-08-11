package com.festivaltime.festivaltimeproject;

import android.content.Context;
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

public class ImageBannerAdapter extends RecyclerView.Adapter<ImageBannerAdapter.ImageViewHolder> {

    private Context context;
    private int[] images; // 이미지 리소스 배열
    private ViewPager2 viewPager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable slideRunnable;

    public ImageBannerAdapter(Context context, int[] images, ViewPager2 viewPager) {
        this.context = context;
        this.images = images;
        this.viewPager = viewPager;
        setupAutoSlide();
    }

    private void setupAutoSlide() {
        slideRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < images.length - 1) {
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
        holder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_banner);

            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Log.d("Touch", "dwon");
                            stopAutoSlide(); // 손을 올릴 때 자동 스크롤 중지
                            break;
                        case MotionEvent.ACTION_UP:
                            startAutoSlide(); // 손을 내릴 때 자동 스크롤 시작
                            break;
                    }
                    return false;
                }
            });
        }
    }
}
