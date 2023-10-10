package com.festivaltime.festivaltimeproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;

import android.content.Context;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class holidayFestivalAdapter extends RecyclerView.Adapter<holidayFestivalAdapter.ViewHolder> {
    private Context context;
    private MainActivity activity;
    private List<HashMap<String, String>> holidaylist;
    private ApiReader apiReader;

    public holidayFestivalAdapter(Context context, List<HashMap<String, String>> holidaylist, MainActivity activity) {
        this.context = context;
        this.holidaylist = holidaylist;
        this.activity = activity;
        apiReader = new ApiReader();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < 5) { // 최대 5개의 아이템만 처리
            holder.bind(position);
        }
    }

    @Override
    public int getItemCount() {
        if (holidaylist == null || holidaylist.isEmpty()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView holidayText = activity.findViewById(R.id.holiday_text);
                    if (holidayText != null) {
                        holidayText.setVisibility(View.VISIBLE);
                    }
                }
            });
            return 0;
        } else {
            int itemCount = Math.min(holidaylist.size(), 5); // 최대 5개 아이템만 표시
            if (itemCount < 5) {
                // holidaylist가 5개 미만이면 추가 아이템을 가져오기
                int itemsToAdd = 5 - itemCount;
                for (int i = 0; i < itemsToAdd; i++) {
                    // holidaylist를 순환하여 추가 아이템을 가져옴
                    int indexToReuse = i % holidaylist.size(); // holidaylist를 순환하면서 아이템을 가져옴
                    HashMap<String, String> holidayInfoToReuse = holidaylist.get(indexToReuse);
                    holidaylist.add(holidayInfoToReuse);
                    itemCount++;
                }
            }
            return itemCount;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        TextView dateText;

        public ViewHolder(View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.image_button);
            dateText = itemView.findViewById(R.id.date_text);
        }

        public void bind(int position) {
            HashMap<String, String> holidayInfo = holidaylist.get(position);
            String apiKey = context.getString(R.string.api_key);

            String name = holidayInfo.get("dateName");
            String date = holidayInfo.get("locdate");
            String startholidate = holidayInfo.get("startdate");
            String endholidate = holidayInfo.get("enddate");

            apiReader.Festivallit2(apiKey, startholidate, endholidate, new ApiReader.ApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    ParsingApiData.parseXmlDataFromFestival(response);
                    List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                    List<HashMap<String, String>> festivalList = new ArrayList<>();
                    festivalList.clear();
                    festivalList.addAll(parsedFestivalList);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (festivalList != null && festivalList.size() > 0) {
                                HashMap<String, String> festivalInfo = null;
                                if (position >= 0 && position < festivalList.size()) {
                                    festivalInfo = festivalList.get(position);

                                    if (festivalInfo != null) {
                                        String id = festivalInfo.get("contentid");
                                        String repImage = festivalInfo.get("img");

                                        if (repImage == null || repImage.isEmpty()) {
                                            imageButton.setImageResource(R.mipmap.empty_image);
                                        } else {
                                            Glide.with(context)
                                                    .load(repImage)
                                                    .fitCenter()
                                                    .transform(new CenterCrop(), new RoundedCorners(30))
                                                    .placeholder(R.mipmap.empty_image)
                                                    .into(imageButton);
                                        }

                                        //휴가가 하루일경우 해당 날짜만 settext
                                        if (startholidate.equals(endholidate)) {
                                            String monthtext = String.valueOf(Integer.parseInt(startholidate.substring(4, 6)));
                                            String datext = String.valueOf(Integer.parseInt(startholidate.substring(6, 8)));
                                            dateText.setText(monthtext + "/" + datext);
                                        } else {
                                            dateText.setText(date);
                                        }

                                        imageButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String contentId = id;
                                                navigateToDetailFestivalActivity(activity, contentId);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "API Error: " + error);
                }
            });
        }
    }
}
