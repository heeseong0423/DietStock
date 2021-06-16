package com.fournineseven.dietstock.ui.feedback;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fournineseven.dietstock.App;
import com.fournineseven.dietstock.LoginState;
import com.fournineseven.dietstock.R;
import com.fournineseven.dietstock.api.RetrofitService;
import com.fournineseven.dietstock.config.TaskServer;
import com.fournineseven.dietstock.model.getDailyFood.DailyFoodResult;
import com.fournineseven.dietstock.model.getDailyFood.GetDailyFoodRequest;
import com.fournineseven.dietstock.model.getDailyFood.GetDailyFoodResponse;
import com.fournineseven.dietstock.model.getRequestFood.GetRequestFoodRequest;
import com.fournineseven.dietstock.model.getRequestFood.GetRequestFoodResponse;
import com.fournineseven.dietstock.model.getRequestFood.RequestFoodResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fournineseven.dietstock.LoginState.ACTIVITY_KEY;
import static com.fournineseven.dietstock.LoginState.GENDER_KEY;
import static com.fournineseven.dietstock.LoginState.HEIGHT_KEY;
import static com.fournineseven.dietstock.LoginState.SHARED_PREFS;

public class FeedBackFragment extends Fragment {
    private static int RequestCode = 1;

    private EditText et_real_kcal;
    private EditText et_real_carbs;
    private EditText et_real_protein;
    private EditText et_real_fat;
    private EditText et_recommend_kcal;
    private EditText et_recommend_carbs;
    private EditText et_recommend_protein;
    private EditText et_recommend_fat;
    private ImageView iv_bad_food;
    private ImageView iv_good_food;
    private ImageView food_image;
    private TextView requestFood_name;
    private TextView arrow;
    private TextView one_meal_ment;


    private Button btn_gocheck;
    private String avoidFood_str = "";
    private String getstr = "";
    private int user_no;
    long now = System.currentTimeMillis();
    private String avoid_food = "";
    private String nutriention = "";
    private float gram;
    private float carbs = 0, protein = 0, fat = 0;
    private int kcal = 0;
    private float recommend_carbs = 0, recommend_protein = 0, recommend_fat = 0;
    private float compare[] = new float[3];
    private float dummy[] = new float[3];

    float[][] dailyFood_info = null;
    private int meal_count = 0;
    ArrayList<feedback_data> dailyFood_all = new ArrayList<>();

    private int index = 0;
    private int max_index = 0;

    private float height = 0;
    private int gender = 0;
    private int activity = 0;
    private float standardweight = 0.0f;
    private float required_kcal = 0;
    private RecyclerView recyclerView;

    ArrayList<feedback_data> arrayList;
    feedbackAdapter feedbackadapter;

    LinearLayoutManager linearLayoutManager;


    ViewGroup rootView;

    @Override
    public void onResume () {
        super.onResume();

        //Bundle bundle = getArguments();
        //user_avoid = bundle.getString("메시지"); //user_avoid = 유저가 체크한 기피식품

        // getArgument로 avoidFood_check에서 문자열을 받아오려고 시도
        btn_gocheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), avoidFood_check.class);
                startActivityForResult(intent,RequestCode);
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, 0);

        Log.d("user_avoid", " " + avoidFood_str);

        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences(SHARED_PREFS, 0);
        int sharedPreferences_user_no = Integer.valueOf(sharedPreferences.getString(LoginState.USER_NUMBER, null));
        user_no = sharedPreferences_user_no;

        Log.d("user_no", " " + user_no);
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String updated_dt = simpleDate.format(mDate);

        RetrofitService getDailyFoodService = App.retrofit.create(RetrofitService.class);
        Call<GetDailyFoodResponse> callGetDailyFood =
                getDailyFoodService.getDailyFood(new GetDailyFoodRequest(user_no, updated_dt));

        callGetDailyFood.enqueue(new Callback<GetDailyFoodResponse>() {
            @Override
            public void onResponse(Call<GetDailyFoodResponse> call, Response<GetDailyFoodResponse> response) {
                Log.d("debug", response.body().toString());
                GetDailyFoodResponse getDailyFoodResponse = (GetDailyFoodResponse) response.body();
                ArrayList<DailyFoodResult> dailyFoodResults = (getDailyFoodResponse).getResult();

                if (getDailyFoodResponse.isSuccess()) { //리스트로 result를 받아옴(먹은 식단)

                    kcal = 0;
                    carbs = 0;
                    protein = 0;
                    fat = 0;
                    feedbackadapter.setEmpty();
                    dailyFood_info = new float[dailyFoodResults.size()][3];
                    meal_count = dailyFoodResults.size();

                    dailyFood_all.clear();

                    for (int i = 0; i < dailyFoodResults.size(); i++) {
                        DailyFoodResult dailyfood = dailyFoodResults.get(i);
                        feedback_data data = new feedback_data(dailyfood.getFood_image(), dailyfood.getServing(), dailyfood.getKcal(), dailyfood.getCarbs(), dailyfood.getProtein(), dailyfood.getFat(),
                                dailyfood.getUpdated_dt(), dailyfood.getFood_name());
                        feedbackadapter.addItem(data);
                        kcal += data.getKcal() * data.getServing();
                        carbs += data.getCarbs() * data.getServing(); //먹은 음식의 탄수화물 총합
                        protein += data.getProtein() * data.getServing(); //먹은 음식의 단백질 총합
                        fat += data.getFat() * data.getServing(); //먹은 음식의 지방 총합
                        /*try{
                            Log.d("test", data.getFood_image());
                            food_image.setImageResource(Integer.parseInt(data.getFood_image()));
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }*/

                        //food_image.setImageResource(Integer.parseInt((data.getFood_image())));

                        dailyFood_info[i][0] = carbs;
                        dailyFood_info[i][1] = protein;
                        dailyFood_info[i][2] = fat;

                        dailyFood_all.add(data);

                        Log.d("info idx = " + String.valueOf(i), String.valueOf(kcal) + " " + String.valueOf(carbs) + " " + String.valueOf(protein) + " " + String.valueOf(fat) + " Serving = " + String.valueOf(data.getServing()));
                        Log.e("error", String.valueOf(kcal));
                    }
                    recyclerView.setAdapter(feedbackadapter);
                    feedbackadapter.notifyDataSetChanged();
                    et_real_kcal.setText(String.valueOf(Math.round((kcal * 100) / 100.0) + "kcal"));
                    et_real_carbs.setText(String.valueOf(Math.round((carbs * 100) / 100.0) + "g"));
                    et_real_protein.setText(String.valueOf(Math.round((protein * 100) / 100.0) + "g"));
                    et_real_fat.setText(String.valueOf(Math.round((fat * 100) / 100.0)) + "g");
                    //ArrayList<DailyFoodResult> dailyFoodResult = getDailyFoodResponse.getResult();

                    nutriention_gram_calculate();

                    if(meal_count >= 2 ) {
                        requestFood();
                        requestFood_name.setVisibility(View.VISIBLE);
                        one_meal_ment.setVisibility(View.GONE);
                        arrow.setVisibility(View.VISIBLE);
                        iv_bad_food.setVisibility(View.VISIBLE);
                        iv_good_food.setVisibility(View.VISIBLE);

                    }
                    else{
                        one_meal_ment.setVisibility(View.VISIBLE);
                        requestFood_name.setVisibility(View.GONE);
                        arrow.setVisibility(View.GONE);
                        iv_bad_food.setVisibility(View.GONE);
                        iv_good_food.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetDailyFoodResponse> call, Throwable t) {
                Log.d("debug", "onFailure" + t.toString());
            }

        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        feedbackadapter = new feedbackAdapter(getActivity());


        int sharedPreferences_sex = sharedPreferences.getInt(GENDER_KEY, 0);
        gender = sharedPreferences.getInt(GENDER_KEY, 0);
        float sharedPreferences_height = sharedPreferences.getFloat(HEIGHT_KEY, 0);
        height = sharedPreferences.getFloat(HEIGHT_KEY, 0);
        int sharedPreferences_activity = sharedPreferences.getInt(ACTIVITY_KEY, -1);
        Log.d("activity12213", String.valueOf(sharedPreferences_activity));
        activity = sharedPreferences.getInt(ACTIVITY_KEY, 0);

        Log.d("gender", " " + gender);
        Log.d("height", " " + height);
        Log.d("activity", " " + activity);

        }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case 1:{
                    avoid_food = data.getStringExtra("result");
                    if (avoid_food.charAt(avoid_food.length()-1) == ',') {
                        avoid_food = avoid_food.substring(0,avoid_food.length() - 1);
                    }
                    Log.d("onActivityResult avoid_food",avoid_food);
                    Log.d("meal_count meal_count",String.valueOf(meal_count));
                    if(meal_count >= 2) {
                        requestFood();
                    }
                    break;
                }
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.feedback_main, container, false);

        btn_gocheck = rootView.findViewById(R.id.btn_gocheck);
        food_image = rootView.findViewById(R.id.food_image);
        et_real_kcal = rootView.findViewById(R.id.et_real_kcal);
        et_real_carbs = rootView.findViewById(R.id.et_real_carbs);
        et_real_protein = rootView.findViewById(R.id.et_real_protein);
        et_real_fat = rootView.findViewById(R.id.et_real_fat);
        et_recommend_kcal = rootView.findViewById(R.id.et_recommend_kcal);
        et_recommend_carbs = rootView.findViewById(R.id.et_recommend_carbs);
        et_recommend_protein = rootView.findViewById(R.id.et_recommend_protein);
        et_recommend_fat = rootView.findViewById(R.id.et_recommend_fat);
        iv_bad_food = rootView.findViewById(R.id.iv_bad_food);
        iv_good_food = rootView.findViewById(R.id.iv_good_food);
        requestFood_name = rootView.findViewById(R.id.requestFood_name);
        arrow = rootView.findViewById(R.id.arrow);
        one_meal_ment = rootView.findViewById(R.id.one_meal_ment);

        arrayList = new ArrayList<>();

        //Bundle bundle = getArguments();
        //user_avoid = bundle.getString("메시지"); //user_avoid = 유저가 체크한 기피식품

        // getArgument로 avoidFood_check에서 문자열을 받아오려고 시도
        btn_gocheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), avoidFood_check.class);
                startActivityForResult(intent,RequestCode);
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, 0);

        Log.d("user_avoid", " " + avoidFood_str);

        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences(SHARED_PREFS, 0);
        int sharedPreferences_user_no = Integer.valueOf(sharedPreferences.getString(LoginState.USER_NUMBER, null));
        user_no = sharedPreferences_user_no;

        Log.d("user_no", " " + user_no);
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String updated_dt = simpleDate.format(mDate);

        RetrofitService getDailyFoodService = App.retrofit.create(RetrofitService.class);
        Call<GetDailyFoodResponse> callGetDailyFood =
                getDailyFoodService.getDailyFood(new GetDailyFoodRequest(user_no, updated_dt));

        callGetDailyFood.enqueue(new Callback<GetDailyFoodResponse>() {
            @Override
            public void onResponse(Call<GetDailyFoodResponse> call, Response<GetDailyFoodResponse> response) {
                Log.d("debug", response.body().toString());
                GetDailyFoodResponse getDailyFoodResponse = (GetDailyFoodResponse) response.body();
                ArrayList<DailyFoodResult> dailyFoodResults = (getDailyFoodResponse).getResult();

                if (getDailyFoodResponse.isSuccess()) { //리스트로 result를 받아옴(먹은 식단)

                    kcal = 0;
                    carbs = 0;
                    protein = 0;
                    fat = 0;
                    feedbackadapter.setEmpty();
                    dailyFood_info = new float[dailyFoodResults.size()][3];
                    meal_count = dailyFoodResults.size();

                    dailyFood_all.clear();

                    Log.d("dailyFoodResults", String.valueOf(dailyFoodResults.size()));
                    for (int i = 0; i < dailyFoodResults.size(); i++) {
                        if(dailyFoodResults.size() == 0) break;
                        DailyFoodResult dailyfood = dailyFoodResults.get(i);
                        feedback_data data = new feedback_data(dailyfood.getFood_image(), dailyfood.getServing(), dailyfood.getKcal(), dailyfood.getCarbs(), dailyfood.getProtein(), dailyfood.getFat(),
                                dailyfood.getUpdated_dt(), dailyfood.getFood_name());
                        feedbackadapter.addItem(data);
                        kcal += data.getKcal() * data.getServing();
                        carbs += data.getCarbs() * data.getServing(); //먹은 음식의 탄수화물 총합
                        protein += data.getProtein() * data.getServing(); //먹은 음식의 단백질 총합
                        fat += data.getFat() * data.getServing(); //먹은 음식의 지방 총합
                        /*try{
                            Log.d("test", data.getFood_image());
                            food_image.setImageResource(Integer.parseInt(data.getFood_image()));
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }*/

                        //food_image.setImageResource(Integer.parseInt((data.getFood_image())));

                        dailyFood_info[i][0] = carbs;
                        dailyFood_info[i][1] = protein;
                        dailyFood_info[i][2] = fat;

                        dailyFood_all.add(data);
                        Log.d("dailyFood_all.name", dailyFood_all.get(i).getFoodname());

                        Log.d("info idx = " + String.valueOf(i), String.valueOf(kcal) + " " + String.valueOf(carbs) + " " + String.valueOf(protein) + " " + String.valueOf(fat) + " Serving = " + String.valueOf(data.getServing()));
                        Log.e("error", String.valueOf(kcal));
                    }
                    recyclerView.setAdapter(feedbackadapter);
                    feedbackadapter.notifyDataSetChanged();
                    et_real_kcal.setText(String.valueOf(Math.round((kcal * 100) / 100.0) + "kcal"));
                    et_real_carbs.setText(String.valueOf(Math.round((carbs * 100) / 100.0) + "g"));
                    et_real_protein.setText(String.valueOf(Math.round((protein * 100) / 100.0) + "g"));
                    et_real_fat.setText(String.valueOf(Math.round((fat * 100) / 100.0)) + "g");
                    //ArrayList<DailyFoodResult> dailyFoodResult = getDailyFoodResponse.getResult();

                    nutriention_gram_calculate();

                    Log.d("meal_count.size()",String.valueOf(meal_count));
                    if(meal_count >= 2 ) {
                        requestFood();
                        requestFood_name.setVisibility(View.VISIBLE);
                        one_meal_ment.setVisibility(View.GONE);
                        arrow.setVisibility(View.VISIBLE);
                        iv_bad_food.setVisibility(View.VISIBLE);
                        iv_good_food.setVisibility(View.VISIBLE);

                    }
                    else{
                        one_meal_ment.setVisibility(View.VISIBLE);
                        requestFood_name.setVisibility(View.GONE);
                        arrow.setVisibility(View.GONE);
                        iv_bad_food.setVisibility(View.GONE);
                        iv_good_food.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetDailyFoodResponse> call, Throwable t) {
                Log.d("debug", "onFailure" + t.toString());
            }

        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        feedbackadapter = new feedbackAdapter(getActivity());


        int sharedPreferences_sex = sharedPreferences.getInt(GENDER_KEY, 0);
        gender = sharedPreferences.getInt(GENDER_KEY, 0);
        float sharedPreferences_height = sharedPreferences.getFloat(HEIGHT_KEY, 0);
        height = sharedPreferences.getFloat(HEIGHT_KEY, 0);
        int sharedPreferences_activity = sharedPreferences.getInt(ACTIVITY_KEY, 0);
        activity = sharedPreferences.getInt(ACTIVITY_KEY, 0);

        Log.d("gender", " " + gender);
        Log.d("height", " " + height);
        Log.d("activity", " " + activity);

        calculate_recommend();

        // 섭취 - 권장 ( -:덜 먹은거 / +: 많이 먹은 거)


        return rootView;
    }

    private void nutriention_gram_calculate() {
        compare[0] = carbs - recommend_carbs;
        compare[1] = protein - recommend_protein;
        compare[2] = fat - recommend_fat;

        for (int i = 0; i < 3; i++) {
            dummy[i] = Math.abs(compare[i]);
        }
        float max = dummy[0];

        for (int i = 1; i < 3; i++) {
            if (max < dummy[i]) {
                max = dummy[i];
                max_index = i;
            }
        } //가장 많이 차이나는 영양소 구분

        if (meal_count != 0) {
            switch (max_index) { //gram = 최고 차이나는 영양소량
                case 0:
                    nutriention = "carbs";
                    gram = compare[0];
                    find_worstfood();
                    break;

                case 1:
                    nutriention = "protein";
                    gram = compare[1];
                    find_worstfood();
                    break;

                case 2:
                    nutriention = "fat";
                    gram = compare[2];
                    find_worstfood();
                    break;

            }
            //nutriention = 오늘 먹은 음식의 총량 중 가장 차이나는 탄 단 지 중 하나
            //gram = nutriention의 차이

            //ex) 탄수화물 -100 -> 탄수화물이 100보다 작은 음식 넘어옴
            //ex) 단백질 +50 -> 단백질이 50보다 큰 음식 넘어옴
        }
    }

    private void requestFood(){
        Log.e("request start", "마하반야바라밀다심경");
        Log.e("line 466",avoid_food);
        RetrofitService getRequestFoodService = App.retrofit.create(RetrofitService.class);
        Call<GetRequestFoodResponse> callGetRequestFood =
                getRequestFoodService.getRequestFood(new GetRequestFoodRequest(avoid_food, nutriention, gram));



        callGetRequestFood.enqueue(new Callback<GetRequestFoodResponse>() {
            @Override
            public void onResponse(Call<GetRequestFoodResponse> call, Response<GetRequestFoodResponse> response) {
                Log.d("debug", response.body().toString());
                GetRequestFoodResponse GetRequestFoodResponse = (GetRequestFoodResponse) response.body();
                ArrayList<RequestFoodResult> requestFoodResultsResults = (GetRequestFoodResponse).getResult();
                Log.e("before success", String.valueOf(GetRequestFoodResponse.isSuccess()));
                if (GetRequestFoodResponse.isSuccess()) {
//                    Log.e("image url------------", "--------------------------------------------------");
//                    Log.e("image url------------", dailyFood_all.get(index).getFood_image());
//                    Log.d("GetRequestFoodResponse avoid_food",avoid_food);
                    kcal = 0;
                    carbs = 0;
                    protein = 0;
                    fat = 0;
                    int flag = 0;
                    double ratio = 0.2d;
                    ArrayList<RequestFoodResult> correct_requestFood = new ArrayList<>();

                    for (int j = 0; j < dailyFood_all.size(); j++) { //오늘 먹은 음식들 중 최악을 뺀 음식들의 영양소 합
                        Log.d("dailyFood_all.name",dailyFood_all.get(j).getFoodname());
                        if (j != index) {
                            kcal += dailyFood_all.get(j).getKcal() * dailyFood_all.get(j).getServing();
                            carbs += dailyFood_all.get(j).getCarbs() * dailyFood_all.get(j).getServing();
                            protein += dailyFood_all.get(j).getProtein() * dailyFood_all.get(j).getServing();
                            fat += dailyFood_all.get(j).getFat() * dailyFood_all.get(j).getServing();
                        }
                    }


                    Glide.with(rootView).load(TaskServer.base_url + dailyFood_all.get(index).getFood_image()).error(R.drawable.food_icon)
                    .placeholder(R.drawable.food_icon).into(iv_bad_food);

//                    Log.d("dailyFood_all.size()",String.valueOf(dailyFood_all.size()));
                    if(dailyFood_all.size() >= 2) {
                        Log.e("dailyFood_all.size()",String.valueOf(dailyFood_all.size()));
                        while (true) {
//                            Log.d("debug", String.valueOf(requestFoodResultsResults.size()));
                            if (ratio > 1) break;
//                            Log.d("ratio", String.valueOf(ratio));
                            for (int i = 0; i < requestFoodResultsResults.size(); i++) { //

                                flag = 0;

                                double carbs_by_ratio = Math.abs(required_kcal - kcal) / (requestFoodResultsResults.get(i).getKcal());
//                                Log.e("carbs_by_ratio", String.valueOf(carbs_by_ratio));
                                double virtual_Carbs = carbs + requestFoodResultsResults.get(i).getCarbs() * carbs_by_ratio;

                                double virtual_Protein = protein + requestFoodResultsResults.get(i).getProtein() * carbs_by_ratio;

                                double virtual_Fat = fat + requestFoodResultsResults.get(i).getFat() * carbs_by_ratio;
//                                Log.e("ddddddddd", String.valueOf(requestFoodResultsResults.get(i).getCarbs()) + "," + String.valueOf(requestFoodResultsResults.get(i).getProtein()) + ","
//                                        + String.valueOf(requestFoodResultsResults.get(i).getFat()) + ",");
//                                Log.e("eeeeeeeeeeee", String.valueOf(virtual_Carbs) + "," + String.valueOf(virtual_Protein) + "," + String.valueOf(virtual_Fat) + "," + String.valueOf(recommend_fat));
                                if (!(virtual_Carbs >= recommend_carbs * (1 - ratio) && virtual_Carbs <= recommend_carbs * (1 + ratio))) {
                                    flag = 1;
                                }
                                if (!(virtual_Protein >= recommend_protein * (1 - ratio) && virtual_Protein <= recommend_protein * (1 + ratio))) {
                                    flag = 1;
                                }
                                if (!(virtual_Fat >= recommend_fat * (1 - ratio) && virtual_Fat <= recommend_fat * (1 + ratio))) {
                                    flag = 1;
                                }

                                if (flag == 0) {
                                    correct_requestFood.add(requestFoodResultsResults.get(i));

                                   Log.d("어떤 음식이 들어갔나요 : ", requestFoodResultsResults.get(i).getFood_name());
                                }
                            }
                            ratio += 0.1;
                            /*Log.d("correct_requestFood.size()", String.valueOf(correct_requestFood.size()));*/
                            if (correct_requestFood.size() != 0) break;
                        }
                    }
//                    Log.d("correct_requestFood.size()2", String.valueOf(correct_requestFood.size()));
                    int min_carbs_index = 0;



                    if(correct_requestFood.size() == 0){
                        Log.d("change","k11111111232323231" + String.valueOf(min_carbs_index));
                        Glide.with(rootView).load(TaskServer.base_url + dailyFood_all.get(index).getFood_image()).error(R.drawable.food_icon)
                                .placeholder(R.drawable.food_icon).into(iv_bad_food);
                        requestFood_name.setText("섭취량이 너무 많아 피드백이 불가합니다");
                    }
                    else {
                        Log.d("change","k11111111111111" + String.valueOf(min_carbs_index));
                        if(correct_requestFood.size() == 1){
                            min_carbs_index = 0;
                            Log.d("change","kkkkkkkkkkkkkkkkkk" + String.valueOf(min_carbs_index));
                        }

                        else {
                            SimpleDateFormat simpleDate = new SimpleDateFormat("dd");
                            Date mDate = new Date(now);
                            String getTime = simpleDate.format(mDate);
                            Log.e("correct_requestFood.size", String.valueOf(correct_requestFood.size()));
                            Log.e("get Time", getTime);
                            min_carbs_index = Integer.valueOf(getTime) % correct_requestFood.size();
                        }
                        Log.d("change","hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh" + String.valueOf(min_carbs_index));
                        requestFood_name.setText(correct_requestFood.get(min_carbs_index).getFood_name());
                        Glide.with(rootView).load(TaskServer.base_url + "requestfood/" + correct_requestFood.get(min_carbs_index).food_image()).error(R.drawable.food_icon)
                                .placeholder(R.drawable.food_icon).into(iv_good_food);
                    }
                    //iv_good_food.setImageResource(Integer.parseInt(correct_requestFood.get(min_carbs_index).food_image()));
                } else {
                    Log.d("debug", "-----------------------------------------------------------------------------------------------------1");
                }
            }

            @Override
            public void onFailure(Call<GetRequestFoodResponse> call, Throwable t) {
                Log.d("debug", "onFailure" + t.toString());
            }
        });
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(avoidFood_str, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String value = avoidFood_str;
        editor.putString("user_avoid", value);
        editor.commit();
    }

    private void find_worstfood () {
        index = 0;
        float max = dailyFood_info[index][max_index];
        float min = dailyFood_info[index][max_index];


        if (gram > 0) { //초과 섭취
            for (int i = 1; i < meal_count; i++) {
                if (max < dailyFood_info[i][max_index]) {
                    max = dailyFood_info[i][max_index];
                    index = i;
                }
            }
            feedback_data dt = dailyFood_all.get(index);
            switch (max_index) { //초과 섭취했으므로 가장 많은 영양소를 함유한 음식보다 적은 음식을 가져와서 구분해야함
                case 0:
                    gram = dt.getCarbs() * (-1);
                    break;
                case 1:
                    gram = dt.getProtein() * (-1);
                    break;
                case 2:
                    gram = dt.getFat() * (-1);
                    break;
            }
        } else { //부족 섭취
            for (int i = 1; i < meal_count; i++) {
                if (min > dailyFood_info[i][max_index]) {
                    min = dailyFood_info[i][max_index];
                    index = i;
                }
            }
            feedback_data dt = dailyFood_all.get(index);
            /*switch(max_index){
                case 0 : gram = dt.getCarbs() ; break;
                case 1 : gram = dt.getProtein(); break;
                case 2 : gram = dt.getFat(); break;
            }*/
            gram = 0;
        }
    }

    public void calculate_recommend () {

        if (gender == 1) {
            standardweight = (float)((height / 100.0) * (height  / 100.0) * 21);
            Log.d("gender 여자냐 : ",String.valueOf(gender));
        } else {
            standardweight = (float)((height / 100.0) * (height  / 100.0) * 22);
            Log.d("gender 남자냐 : ",String.valueOf(gender));
        }
        Log.d("required_kcal", String.valueOf(standardweight));
        Log.d("required_kcal", String.valueOf(activity));
        switch (activity) {
            case 1:
                Log.d("required_kcal", "1");
                required_kcal = (float) (standardweight * 27.5);
                break;
            case 2:
                Log.d("required_kcal", "2");
                required_kcal = (float) (standardweight * 32.5);
                break;
            case 3:
                Log.d("required_kcal", "3");
                required_kcal = (float) (standardweight * 37.5);
                break;
        }
        Log.d("required_kcal", String.valueOf(required_kcal));
        required_kcal -= 300;

        //탄 단 지 = 3 : 4 : 3
        recommend_carbs = (float) ((required_kcal * 0.3) / 4);
        recommend_protein = (float) ((required_kcal * 0.4) / 4);
        recommend_fat = (float) ((required_kcal * 0.3) / 9);
        Log.d("required_kcal", String.valueOf(required_kcal));
        Log.d("recommand_carbs", String.valueOf(recommend_carbs));
        Log.d("recommend_protein", String.valueOf(recommend_protein));
        Log.d("recommend_fat", String.valueOf(recommend_fat));

        et_recommend_kcal.setText(String.valueOf(Math.round((required_kcal * 100) / 100.0)) + "kcal");
        et_recommend_carbs.setText(String.valueOf(Math.round((recommend_carbs * 100) / 100.0)) + "g");
        et_recommend_protein.setText(String.valueOf(Math.round((recommend_protein * 100) / 100.0)) + "g");
        et_recommend_fat.setText(String.valueOf(Math.round((recommend_fat * 100) / 100.0)) + "g");
    }
}
