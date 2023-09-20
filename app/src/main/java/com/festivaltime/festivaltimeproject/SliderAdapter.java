package com.festivaltime.festivaltimeproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {
    int[] images;
    String[] dates;

    public SliderAdapter(int[] images, String[] dates) {
        this.images = images;
        this.dates = dates;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        viewHolder.imageView.setImageResource(images[position]);
        viewHolder.dateTextView.setText(dates[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    public class Holder extends SliderViewAdapter.ViewHolder {
        ImageView imageView;
        TextView dateTextView;

        public Holder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_button);
            dateTextView = itemView.findViewById(R.id.date_text);
        }

    }
}
