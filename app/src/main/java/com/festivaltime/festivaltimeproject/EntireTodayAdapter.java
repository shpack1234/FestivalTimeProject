package com.festivaltime.festivaltimeproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;
import java.util.LinkedHashMap;

public class EntireTodayAdapter extends RecyclerView.Adapter<EntireTodayAdapter.ViewHolder> {
    private List<LinkedHashMap<String, String>> data;
    private EntireViewActivity activity;

    public EntireTodayAdapter(EntireViewActivity activity, List<LinkedHashMap<String, String>> data) {
        this.data = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LinkedHashMap<String, String> samedayInfo = data.get(position);

        String img = samedayInfo.get("img");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (img == null || img.isEmpty()) {
                    holder.imageButton.setImageResource(R.mipmap.empty_image);
                } else {
                    Glide.with(holder.itemView.getContext())
                            .load(img)
                            .fitCenter()
                            .transform(new CenterCrop(), new RoundedCorners(30))
                            .placeholder(R.mipmap.empty_image)
                            .into(holder.imageButton);
                }

                String startdate = samedayInfo.get("eventstartdate");
                String enddate = samedayInfo.get("eventenddate");
                String minMonth = "";
                String minDay = "";
                String maxMonth = "";
                String maxDay = "";

                if (startdate != null && startdate.length() >= 8) {
                    minMonth = String.valueOf(Integer.parseInt(startdate.substring(4, 6)));
                    minDay = String.valueOf(Integer.parseInt(startdate.substring(6, 8)));
                }

                if (enddate != null && enddate.length() >= 8) {
                    maxMonth = String.valueOf(Integer.parseInt(enddate.substring(4, 6)));
                    maxDay = String.valueOf(Integer.parseInt(enddate.substring(6, 8)));
                }

                String entitydate = minMonth + "/" + minDay + "~" + maxMonth + "/" + maxDay;

                holder.datetext.setText(entitydate);

                holder.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String contentId = samedayInfo.get("contentid");
                        navigateToSomeActivity.navigateToDetailFestivalActivity(activity, contentId);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(data.size(), 5);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        TextView datetext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.image_button);
            datetext = itemView.findViewById(R.id.date_text);
        }
    }
}
