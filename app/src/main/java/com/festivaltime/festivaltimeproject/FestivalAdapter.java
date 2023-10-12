package com.festivaltime.festivaltimeproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDao;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarDatabase;
import com.festivaltime.festivaltimeproject.calendardatabasepackage.CalendarEntity;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDao;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBase;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserDataBaseSingleton;
import com.festivaltime.festivaltimeproject.userdatabasepackage.UserEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class FestivalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private MainActivity activity;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();
    private boolean isLoading = false;
    private ApiReader apiReader;
    private Context context; // context 변수를 어댑터 클래스 레벨에서 유지
    private TextView areaName;
    private UserEntity loadedUser;
    private String userId;
    private UserDataBase db;
    private UserDao userDao;
    private CalendarDao calendarDao;
    private CalendarEntity calendarEntity;
    private CalendarDatabase calendarDatabase;
    private List<String> areaNames = new ArrayList<>();

    // 클릭 리스너 인터페이스 정의
    interface OnItemClickListener {
        void onItemClick(String contentId);
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public FestivalAdapter(Context context, MainActivity activity, String userId) {
        this.context = context;
        this.activity = activity;
        this.userId = userId; // userId 설정 추가
        db = UserDataBaseSingleton.getInstance(context);
        userDao = db.userDao();
    }


    public void setData(List<HashMap<String, String>> data) {
        festivalList.clear();
        festivalList.addAll(data);
        notifyDataSetChanged();
    }

    private static final int VIEW_TYPE_AREA = 1;

    @Override
    public int getItemViewType(int position) {
        // 5개마다 VIEW_TYPE_AREA를 반환하도록 설정
        if (position % 5 == 0) {
            return VIEW_TYPE_AREA;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return festivalList.size();
    }


    public void addData(List<HashMap<String, String>> data, String areaName) {
        festivalList.addAll(data);

        areaNames.add(areaName);

        notifyDataSetChanged();
    }

    public class AreaViewHolder extends RecyclerView.ViewHolder {
        TextView areaNameTextView;

        public AreaViewHolder(View itemView) {
            super(itemView);
            areaNameTextView = itemView.findViewById(R.id.main_festival_area_title);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_ITEM) {
            // 항목 뷰 홀더 생성
            View itemView = inflater.inflate(R.layout.festival_info_box, parent, false);
            return new ViewHolder(itemView); // 커스텀 뷰 홀더 클래스 사용
        } else if (viewType == VIEW_TYPE_AREA) {
            // areaName 뷰 홀더 생성
            View areaView = inflater.inflate(R.layout.main_festival_container, parent, false);
            return new AreaViewHolder(areaView);
        }

        return null;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        apiReader = new ApiReader();
        String apiKey = context.getString(R.string.api_key);

        if (holder instanceof ViewHolder) {
            HashMap<String, String> festivalInfo = festivalList.get(position);
            ViewHolder itemHolder = (ViewHolder) holder;

            ImageButton calendaraddButton = itemHolder.itemView.findViewById(R.id.calendar_addButton);
            ImageButton favoriteaddButton = itemHolder.itemView.findViewById(R.id.favorite_addButton);

            // 각 아이템을 화면에 표시하는 코드 작성
            TextView titleTextView = itemHolder.itemView.findViewById(R.id.festival_title);
            TextView locationTextView = itemHolder.itemView.findViewById(R.id.festival_location);
            TextView overviewTextView = itemHolder.itemView.findViewById(R.id.festival_overview);
            ImageButton searchImageButton = itemHolder.itemView.findViewById(R.id.festival_rep_image);
            TextView stateTextView = itemHolder.itemView.findViewById(R.id.festival_state);

            String title = festivalInfo.get("title");
            String id = festivalInfo.get("contentid");
            String address1 = festivalInfo.get("address1");
            //문자열길이 일정수 넘어가면 ...형태로 표시
            if (address1 != null && address1.length() > 15) {
                address1 = address1.substring(0, 15) + "...";
            }
            String repImage = festivalInfo.get("img");
            String startDateStr = festivalInfo.get("eventstartdate");
            String endDateStr = festivalInfo.get("eventenddate");

            // 날짜 문자열을 Date 객체로 변환
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

            Date startDate = null;
            Date endDate = null;
            Date currentDate = new Date(); // 현재 날짜 가져오기

            try {
                startDate = dateFormat.parse(startDateStr);
                endDate = dateFormat.parse(endDateStr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            titleTextView.setText(title);
            locationTextView.setText(address1);

            // 축제 상태 설정
            setFestivalStatus(startDate, endDate, currentDate, stateTextView);

            // 축제 이미지 설정
            setFestivalImage(repImage, searchImageButton);

            // API에서 추가 정보 가져오기
            loadAdditionalFestivalInfo(apiKey, id, overviewTextView);

            // 아이템 클릭 이벤트 설정
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String contentId = id;
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(contentId);
                    }
                    navigateToDetailFestivalActivity(activity, id);
                }
            });

            // 즐겨찾기 버튼 클릭 이벤트 설정
            favoriteaddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleFavoriteButtonClick(id);
                }
            });

            // 캘린더 추가 버튼 클릭 이벤트 설정
            calendaraddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCalendarAddButtonClick(startDateStr, endDateStr, id, title);
                }
            });
        } else if (holder instanceof AreaViewHolder) {
            // 지역 이름을 표시하는 코드 작성
            AreaViewHolder areaHolder = (AreaViewHolder) holder;
            String areaNameText = areaNames.get(position / 5); // 5개마다 지역 이름을 변경
            areaHolder.areaNameTextView.setText(areaNameText);

            TextView areaNameTextView = areaHolder.itemView.findViewById(R.id.main_festival_area_title);
        }


    }

    // 축제 상태 설정
    private void setFestivalStatus(Date startDate, Date endDate, Date currentDate, TextView stateTextView) {
        if (startDate != null && endDate != null) {
            if (currentDate.before(startDate)) {
                // 현재 날짜가 시작일 이전인 경우
                stateTextView.setText("진행예정");
                stateTextView.setTextColor(context.getColor(android.R.color.holo_red_light)); // 주황색으로 설정
            } else if (currentDate.after(endDate)) {
                // 현재 날짜가 종료일 이후인 경우
                // 필요한 처리 추가
            } else {
                // 현재 날짜가 시작일과 종료일 사이에 있는 경우
                stateTextView.setText("진행중");
                stateTextView.setTextColor(context.getColor(android.R.color.holo_green_light)); // 초록색으로 설정
            }
        }
    }

    // 축제 이미지 설정
    private void setFestivalImage(String repImage, ImageButton searchImageButton) {
        if (repImage == null || repImage.isEmpty()) {
            searchImageButton.setImageResource(R.drawable.ic_image);
        } else {
            Glide.with(context).load(repImage).transform(new CenterCrop(), new RoundedCorners(30)).placeholder(R.drawable.ic_image).into(searchImageButton);
        }
    }

    // API에서 추가 정보 가져오기
    private void loadAdditionalFestivalInfo(String apiKey, String id, TextView overviewTextView) {
        apiReader.FestivalInfo2(apiKey, id, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                ParsingApiData.parseXmlDataFromDetailInfo2(response);
                //Log.d("festivalinfo response: ", response);
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                LinkedHashMap<String, String> firstMap = null;

                try {
                    firstMap = parsedFestivalList.get(0);
                    String detailInfo = firstMap.get("infotext");

                    // 문자열 길이 일정 수를 넘어가면 ... 형태로 표시
                    if (detailInfo != null && detailInfo.length() > 30) {
                        detailInfo = detailInfo.substring(0, 30) + "...";
                    }

                    // HTML 형태 변환하여 setText
                    if (detailInfo != null) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            overviewTextView.setText(Html.fromHtml(detailInfo, Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            overviewTextView.setText(Html.fromHtml(detailInfo));
                        }
                    } else {
                        // detailInfo가 null인 경우에 대한 처리 추가
                    }
                } catch (IndexOutOfBoundsException e) {
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });
    }

    private void handleFavoriteButtonClick(String id) {
        // 백그라운드 스레드에서 데이터베이스 작업을 수행
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadedUser = userDao.getUserInfoById(userId);
                if (loadedUser != null) {
                    if (loadedUser.getIsLogin()) {
                        // 데이터베이스 작업을 백그라운드 스레드에서 수행한 후 결과를 처리
                        boolean result = performDatabaseOperation(id);
                        handleDatabaseOperationResult(result);
                    } else {
                        // UI 스레드에서 Toast를 표시
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Log.e("No UserInfo", "You should get your information in MyPage");
                    // UI 스레드에서 Toast를 표시
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "로그인 후에 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private boolean performDatabaseOperation(String id) {
        String contentId = id;

        if (loadedUser.getUserFavoriteFestival().contains(contentId)) {
            // 이미 추가된 축제인 경우
            return false;
        } else {
            // 새로운 축제를 추가
            loadedUser.addUserFavoriteFestival(contentId);
            userDao.insertOrUpdate(loadedUser); // 사용자 정보 업데이트
            return true;
        }
    }

    private void handleDatabaseOperationResult(boolean result) {
        if (result) {
            // 즐겨찾기가 추가된 경우
            // UI 스레드에서 Toast를 표시
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "축제가 즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 이미 추가된 축제인 경우
            // UI 스레드에서 Toast를 표시
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "이미 추가된 축제입니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 캘린더 추가 버튼 클릭 처리
    private void handleCalendarAddButtonClick(String startDateStr, String endDateStr, String id, String title) {
        new CalendarAddTask(startDateStr, endDateStr, id, title).execute();
    }
    private class CalendarAddTask extends AsyncTask<Void, Void, Boolean> {
        private final String startDateStr;
        private final String endDateStr;
        private final String id;
        private final String title;

        CalendarAddTask(String startDateStr, String endDateStr, String id, String title) {
            this.startDateStr = startDateStr;
            this.endDateStr = endDateStr;
            this.id = id;
            this.title = title;
        }

        private void showAddFTDialog(final String title, final String id, final String formattedStartDate, final String formattedEndDate) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // UI 관련 작업을 여기서 수행
                    AddFT dialog = new AddFT(activity, title, id, formattedStartDate, formattedEndDate);
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            // 팝업이 닫힐 때 실행할 코드 작성 부분
                        }
                    });
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy.M.d", Locale.getDefault());
                Date currentDate = new Date();
                String currentDateStr = sdf.format(currentDate);

                String CompareStartDate = startDateStr;
                String CompareEndDate = endDateStr;

                Log.d("startdate: ", "Start Date: " + CompareStartDate);
                Log.d("enddate: ", "End Date: " + CompareEndDate);

                String startTime = "";
                String endTime = "";

                loadedUser = userDao.getUserInfoById(userId);
                calendarDatabase = CalendarDatabase.getInstance(context);
                calendarDao = calendarDatabase.calendarDao();

                if (loadedUser != null) {
                    if (loadedUser.getIsLogin()) {
                        if (calendarDao.getAllContentIds().contains(id)) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "이미 추가된 축제이거나 지난 축제입니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Date compareDate = sdf.parse(CompareEndDate);
                            Date current = sdf.parse(currentDateStr);

                            // 날짜 없으면 추가되지 않도록 설정
                            if (CompareStartDate != null && CompareEndDate != null) {
                                if (current.compareTo(compareDate) <= 0) {
                                    Log.d("Button Action", "Add about Festival to Calendar");

                                    Date originalStartDate = sdf.parse(CompareStartDate);
                                    String formattedStartDate = targetDateFormat.format(originalStartDate);
                                    Date originalEndDate = sdf.parse(CompareEndDate);
                                    String formattedEndDate = targetDateFormat.format(originalEndDate);

                                    // 일수 계산 위해 밀리초로 날짜 변환
                                    long startDateMillis = originalStartDate.getTime();
                                    long endDateMillis = originalEndDate.getTime();

                                    // 두 날짜 사이의 일 수 계산
                                    long daysBetween = (endDateMillis - startDateMillis) / (1000 * 60 * 60 * 24);

                                    // 14일(2주) 이상인 경우 팝업
                                    if (daysBetween >= 14) {
                                        // 팝업을 표시
                                        showAddFTDialog(title, id, formattedStartDate, formattedEndDate);

                                    } else {
                                        Log.d("formattedStartDate: ", "Formatted Start Date: " + formattedStartDate);
                                        Log.d("formattedEndDate: ", "Formatted End Date: " + formattedEndDate);

                                        // CalendarEntity 생성
                                        CalendarEntity event = new CalendarEntity();
                                        event.title = title;
                                        event.startDate = formattedStartDate;
                                        event.endDate = formattedEndDate;
                                        event.startTime = startTime;
                                        event.endTime = endTime;
                                        event.category = "#ed5c55";
                                        event.contentid = id;

                                        // CalendarEntityDao를 사용하여 데이터베이스에 이벤트 추가
                                        calendarDao.InsertSchedule(event);

                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "축제가 캘린더에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "이미 추가된 축제이거나 지난 축제입니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "이미 추가된 축제이거나 지난 축제입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "로그인 후 이용가능 합니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null; // 작업 실패
        }
    }


    // ViewHolder 클래스를 정의합니다.
    private class ViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder 내에서 필요한 View들을 선언합니다.
        TextView titleTextView;
        TextView locationTextView;
        TextView overviewTextView;
        ImageButton searchImageButton;
        ImageButton calendaraddButton;
        ImageButton favoriteaddButton;
        TextView stateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // 필요한 View들을 초기화합니다.
            titleTextView = itemView.findViewById(R.id.festival_title);
            locationTextView = itemView.findViewById(R.id.festival_location);
            overviewTextView = itemView.findViewById(R.id.festival_overview);
            searchImageButton = itemView.findViewById(R.id.festival_rep_image);
            calendaraddButton = itemView.findViewById(R.id.calendar_addButton);
            favoriteaddButton = itemView.findViewById(R.id.favorite_addButton);
            stateTextView = itemView.findViewById(R.id.festival_state);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataSetChanged();
    }
}
