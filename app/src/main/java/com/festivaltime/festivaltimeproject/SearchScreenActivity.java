package com.festivaltime.festivaltimeproject;

import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToCalendarActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToDetailFestivalActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToFavoriteActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMainActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMapActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToMyPageActivity;
import static com.festivaltime.festivaltimeproject.navigateToSomeActivity.navigateToSearchActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SearchScreenActivity extends AppCompatActivity {

    private static final String TAG = "SearchScreenActivity";
    private Executor executor;
    private SearchView searchView;
    private String query;
    private List<HashMap<String, String>> festivalList = new ArrayList<>();
    private List<HashMap<String, String>> parsedFestivalList = new ArrayList<>();
    private ApiReader apiReader;
    private String type;
    private String cat2, cat3, cat4, cat5, cat6, cat7, cat8, cat9, cat10 , cat11, cat12= "";





    private Semaphore secondSemaphore = new Semaphore(0);
    private Semaphore thirdSemaphore = new Semaphore(0);
    private Semaphore fourthSemaphore = new Semaphore(0);
    private Semaphore fifthSemaphore = new Semaphore(0);
    private Semaphore sixthSemaphore = new Semaphore(0);
    private Semaphore seventhSemaphore = new Semaphore(0);
    private Semaphore eightSemaphore = new Semaphore(0);
    private Semaphore ninthSemaphore = new Semaphore(0);
    private Semaphore tenthSemaphore = new Semaphore(0);
    private Semaphore eleventhSemaphore = new Semaphore(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        HashGetter.getHashKey(getApplicationContext());

        searchView = findViewById(R.id.main_search_bar);
        searchView.setOnTouchListener((v, event) -> {
            searchView.setIconified(false);
            searchView.performClick();
            return true;
        });
        ImageButton searchoptionbutton = findViewById(R.id.detailButton);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {       //검색 시 검색 내용 SearchActivity 로 전달
                query = s;
                performSearch(query);
                navigateToSearchActivity(SearchScreenActivity.this, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        executor = Executors.newSingleThreadExecutor();

        String query = getIntent().getStringExtra("query");
        type = getIntent().getStringExtra("type");

        if (type == null) {
            type = ""; // type이 null일 경우 빈 문자열로 초기화
        }

        String apiKey = getResources().getString(R.string.api_key);


        cat2 = "A0207";

        apiReader = new ApiReader();
        apiReader.searchKeyword2(apiKey, query, cat2, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {

                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response); // 응답을 파싱하여 데이터를 저장
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear(); // 기존 데이터를 모두 제거
                        festivalList.addAll(parsedFestivalList);

                        // UI 갱신은 메인 스레드에서 실행
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // UI 갱신 코드
                                LinearLayout searchContainer = findViewById(R.id.search_container);
                                searchContainer.removeAllViews();

                                View searchContainerView = getLayoutInflater().inflate(R.layout.festivalsearch_container, null);
                                GridLayout festivalImageNText = searchContainerView.findViewById(R.id.festivalSearch_container3);
                                //GridLayout festivalImageNText = findViewById(R.id.festivalSearch_container3);
                                festivalImageNText.removeAllViews();


                                // 받아온 type 값에 따라 title_name TextView에 텍스트 설정
                                String textToShow = getTextToShow(cat2);
                                TextView titleTextView = searchContainerView.findViewById(R.id.title_name);
                                titleTextView.setText(textToShow);


                                int maxItems = Math.min(festivalList.size(), 6);


                                for (int i = 0; i < maxItems; i++) {
                                    HashMap<String, String> festivalInfo = festivalList.get(i);
                                    View festivalItemView = getLayoutInflater().inflate(R.layout.festival_search_imagentext, null);
                                    TextView searchTextView = festivalItemView.findViewById(R.id.search_text);
                                    ImageButton searchImageButton = festivalItemView.findViewById(R.id.search_image);

                                    String title = festivalInfo.get("title");
                                    String id = festivalInfo.get("contentid");
                                    String repImage = festivalInfo.get("img");

                                    searchTextView.setText(title);
                                    searchTextView.setMaxEms(8);

                                    Log.d(TAG, "Rep Image URL: " + repImage);
                                    if (repImage == null || repImage.isEmpty()) {
                                        searchImageButton.setImageResource(R.drawable.ic_image);
                                    } else {
                                        //Picasso.get().load(repImage).placeholder(R.drawable.ic_image).into(searchImageButton);
                                    }
                                    festivalImageNText.addView(festivalItemView);


                                    festivalItemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String contentId = id;
                                            // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                                            navigateToDetailFestivalActivity(SearchScreenActivity.this, contentId);
                                        }
                                    });


                                }


                                searchContainer.addView(searchContainerView);

                                Button detailSearchButton = searchContainerView.findViewById(R.id.detail_search_button);
                                detailSearchButton.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        navigateToSomeActivity.navigateToSearchDetailActivity(SearchScreenActivity.this, query, cat2);
                                    }
                                });

                                secondSemaphore.release();
                            }


                        });
                    }
                });


            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });

        cat3 = "A02080500";
        apiReader.searchKeyword(apiKey, query, cat3, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {


                try {
                    thirdSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat3); // 응답을 파싱하여 데이터를 저장
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(festivalList.size()>0) {

                                    loopUI(query, cat3, 3);
                                }
                                fourthSemaphore.release();
                            }

                        });

                    }
                });
            }


            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });


        cat4 = "A02080200";
        apiReader.searchKeyword(apiKey, query, cat4, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {


                try {
                    secondSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat4); // 응답을 파싱하여 데이터를 저장
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(festivalList.size()>0) {
                                    loopUI(query, cat4, 3);
                                }
                                thirdSemaphore.release();
                            }

                        });

                    }
                });
            }


            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });

        cat5 = "A02080100";
        apiReader.searchKeyword(apiKey, query, cat5, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {


                try {
                    fourthSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat5); // 응답을 파싱하여 데이터를 저장
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(festivalList.size()>0) {
                                    loopUI(query, cat5, 3);
                                }
                                fifthSemaphore.release();
                            }

                        });

                    }
                });
            }



            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });

        cat6 = "A02080300";
        apiReader.searchKeyword(apiKey, query, cat6, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {


                try {
                    fifthSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat6); // 응답을 파싱하여 데이터를 저장
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(festivalList.size()>0) {
                                    loopUI(query, cat6, 3);
                                }
                                sixthSemaphore.release();

                            }

                        });

                    }
                });
            }



            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });

        cat7 = "A02080400";
        apiReader.searchKeyword(apiKey, query, cat7, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {


                try {
                    sixthSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat7); // 응답을 파싱하여 데이터를 저장
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(festivalList.size()>0) {
                                    loopUI(query, cat7, 3);
                                }
                                seventhSemaphore.release();

                            }

                        });

                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });

        cat8 = "A02080600";
        apiReader.searchKeyword(apiKey, query, cat8, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {


                try {
                    seventhSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat8); // 응답을 파싱하여 데이터를 저장
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(festivalList.size()>0) {
                                    loopUI(query, cat8, 3);
                                }
                                eightSemaphore.release();

                            }

                        });

                    }
                });
            }



            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });

        cat9 = "A02080800";
        apiReader.searchKeyword(apiKey, query, cat9, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {


                try {
                    eightSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat9); // 응답을 파싱하여 데이터를 저장
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(festivalList.size()>0) {
                                    loopUI(query, cat9, 3);
                                }
                                ninthSemaphore.release();

                            }

                        });

                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });

        cat10 = "A02080900";
        apiReader.searchKeyword(apiKey, query, cat10, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {


                try {
                    ninthSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat10); // 응답을 파싱하여 데이터를 저장
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(festivalList.size()>0) {
                                    loopUI(query, cat10, 3);
                                }
                                tenthSemaphore.release();

                            }

                        });

                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });

        cat11 = "A02081000";
        apiReader.searchKeyword(apiKey, query, cat11, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {


                try {
                    tenthSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat11); // 응답을 파싱하여 데이터를 저장
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(festivalList.size()>0) {
                                    loopUI(query, cat11, 3);
                                }
                                eleventhSemaphore.release();

                            }

                        });

                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });

        cat12 = "A02081100";
        apiReader.searchKeyword(apiKey, query, cat12, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {


                try {
                    eleventhSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("response", response);
                ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat12); // 응답을 파싱하여 데이터를 저장
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(festivalList.size()>0) {
                                    loopUI(query, cat12, 3);
                                }

                            }

                        });

                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });









        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                navigateToMainActivity(SearchScreenActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_map) {
                navigateToMapActivity(SearchScreenActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_calendar) {
                navigateToCalendarActivity(SearchScreenActivity.this);
                return true;
            } else if (item.getItemId() == R.id.action_favorite) {
                navigateToFavoriteActivity(SearchScreenActivity.this);
                return true;
            } else {
                navigateToMyPageActivity(SearchScreenActivity.this);
                return true;
            }
        });
    }

    private void fetchAndDisplayData(String apiKey, String query, String cat, Semaphore semaphore) {
        apiReader.searchKeyword(apiKey, query, cat, new ApiReader.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ParsingApiData.parseXmlDataFromSearchKeyword(response, null, cat);
                List<LinkedHashMap<String, String>> parsedFestivalList = ParsingApiData.getFestivalList();

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        festivalList.clear();
                        festivalList.addAll(parsedFestivalList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loopUI(query, cat, 3);
                                semaphore.release();
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
            }
        });
    }


    private void loopUI(String query, String cat, int count) {
        LinearLayout searchContainer = findViewById(R.id.search_container);

        View searchContainerView = getLayoutInflater().inflate(R.layout.festivalsearch_container, null);
        GridLayout festivalImageNText = searchContainerView.findViewById(R.id.festivalSearch_container3);
        festivalImageNText.removeAllViews();

        // 받아온 type 값에 따라 title_name TextView에 텍스트 설정
        String textToShow = getTextToShow(cat);
        TextView titleTextView = searchContainerView.findViewById(R.id.title_name);
        titleTextView.setText(textToShow);


        int loopItems = Math.min(festivalList.size(), count);

        for (int i = 0; i < loopItems; i++) {
            HashMap<String, String> festivalInfo = festivalList.get(i);
            View festivalItemView = getLayoutInflater().inflate(R.layout.festival_search_imagentext, null);
            TextView searchTextView = festivalItemView.findViewById(R.id.search_text);
            ImageButton searchImageButton = festivalItemView.findViewById(R.id.search_image);

            String title = festivalInfo.get("title");
            String id = festivalInfo.get("contentid");
            String repImage = festivalInfo.get("img");

            searchTextView.setText(title);
            searchTextView.setMaxEms(8);

            Log.d(TAG, "Rep Image URL: " + repImage);
            if (repImage == null || repImage.isEmpty()) {
                searchImageButton.setImageResource(R.drawable.ic_image);
            } else {
                //Picasso.get().load(repImage).placeholder(R.drawable.ic_image).into(searchImageButton);
            }
            festivalImageNText.addView(festivalItemView);


            festivalItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String contentId = id;
                    // 가져온 contentid 값을 사용하여 원하는 작업을 수행
                    navigateToDetailFestivalActivity(SearchScreenActivity.this, contentId);
                }
            });

        }

        searchContainer.addView(searchContainerView);

        Button detailSearchButton = searchContainerView.findViewById(R.id.detail_search_button);
        detailSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                navigateToSomeActivity.navigateToSearchDetailActivity(SearchScreenActivity.this, query, cat);
            }
        });
    }

    private String getTextToShow(String type) {
        switch (type) {
            case "A02080100":
                return "전통공연";
            case "A02080200":
                return "연극";
            case "A02080300":
                return "뮤지컬";
            case "A02080400":
                return "오페라";
            case "A02080500":
                return "전시회";
            case "A02080600":
                return "박람회";
            case "A02080800":
                return "무용";
            case "A02080900":
                return "클래식음악회";
            case "A02081000":
                return "대중콘서트";
            case "A02081100":
                return "영화";
            default:
                if (!type.isEmpty() && type.startsWith("A0207")) {
                    return "축제";
                } else {
                    return "기타";
                }
        }
    }

    private void performSearch(String query) {
        System.out.println("검색어: " + query);
    }
}