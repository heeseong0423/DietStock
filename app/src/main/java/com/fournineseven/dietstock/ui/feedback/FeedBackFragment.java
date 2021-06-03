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
    private EditText requestFood_name;


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
    private float standardweight = 0;
    private float required_kcal = 0;
    private RecyclerView recyclerView;

    ArrayList<feedback_data> arrayList;
    feedbackAdapter feedbackadapter;

    LinearLayoutManager linearLayoutManager;


    ViewGroup rootView;

    @Override
    public void onResume () {
        super.onResume();
        avoid_food = avoidFood_check.avoidFood_str;

        if (avoidFood_check.avoidFood_str.length() != 0) {
            avoid_food = avoidFood_check.avoidFood_str.substring(0, avoidFood_check.avoidFood_str.length() - 1);
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, 0);
        avoidFood_str = avoid_food;
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

            requestFood();

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case 1:{
                    requestFood();
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


        arrayList = new ArrayList<>();

        avoid_food = avoidFood_check.avoidFood_str;
        if (avoidFood_check.avoidFood_str.length() != 0) {
            avoid_food = avoidFood_check.avoidFood_str.substring(0, avoidFood_check.avoidFood_str.length() - 1);
        }

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
        requestFood();
        return rootView;
    }

    private void requestFood(){

        RetrofitService getRequestFoodService = App.retrofit.create(RetrofitService.class);
        Call<GetRequestFoodResponse> callGetRequestFood =
                getRequestFoodService.getRequestFood(new GetRequestFoodRequest(avoid_food, nutriention, gram));

        Log.d("avoid_food = ", avoid_food);

        callGetRequestFood.enqueue(new Callback<GetRequestFoodResponse>() {
            @Override
            public void onResponse(Call<GetRequestFoodResponse> call, Response<GetRequestFoodResponse> response) {
                Log.d("debug", response.body().toString());
                GetRequestFoodResponse GetRequestFoodResponse = (GetRequestFoodResponse) response.body();
                ArrayList<RequestFoodResult> requestFoodResultsResults = (GetRequestFoodResponse).getResult();
                if (GetRequestFoodResponse.isSuccess()) {
                    Log.d("debug", "-----------------------------------------------------------------------------------------------------2");
                    kcal = 0;
                    carbs = 0;
                    protein = 0;
                    fat = 0;
                    int flag = 0;
                    int ratio = 5;
                    ArrayList<RequestFoodResult> correct_requestFood = new ArrayList<>();

                    for (int j = 0; j < dailyFood_all.size(); j++) { //오늘 먹은 음식들 중 최악을 뺀 음식들의 영양소 합
                        if (j != index) {
                            kcal += dailyFood_all.get(j).getKcal() * dailyFood_all.get(j).getServing();
                            carbs += dailyFood_all.get(j).getCarbs() * dailyFood_all.get(j).getServing();
                            protein += dailyFood_all.get(j).getProtein() * dailyFood_all.get(j).getServing();
                            fat += dailyFood_all.get(j).getFat() * dailyFood_all.get(j).getServing();
                        }
                    }


                    Glide.with(rootView).load(TaskServer.base_url + dailyFood_all.get(index).getFood_image()).error(R.drawable.food_icon)
                            .placeholder(R.drawable.food_icon).into(iv_bad_food);


                    //iv_bad_food.setImageResource(Integer.parseInt(dailyFood_all.get(index).getFood_image()));

                    Log.d("못들어간다","못들어간다");
                    while(true) {
                        Log.d("들어는 간다","간다");
                        ratio += 5;
                        Log.d("request", String.valueOf(requestFoodResultsResults.size()));
                        for (int i = 0; i < requestFoodResultsResults.size(); i++) { //

                            flag = 0;

                            float virtual_Carbs = carbs + requestFoodResultsResults.get(i).getCarbs();
                            Log.d("virtual_Carbs",String.valueOf(virtual_Carbs));
                            Log.d("recommend_carbs",String.valueOf(recommend_carbs));
                            float virtual_Protein = protein + requestFoodResultsResults.get(i).getProtein();
                            Log.d("virtual_Protein",String.valueOf(virtual_Protein));
                            Log.d("recommend_protein",String.valueOf(recommend_protein));
                            float virtual_Fat = fat + requestFoodResultsResults.get(i).getFat();
                            Log.d("virtual_Fat",String.valueOf(virtual_Fat));
                            Log.d("recommend_fat",String.valueOf(recommend_fat));
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
                            }
                        }
                        if(correct_requestFood.size() != 0 )break;
                    }

                    float min_carbs = correct_requestFood.get(0).getCarbs();
                    int min_carbs_index = 0;

                    for (int i = 1; i < correct_requestFood.size(); i++) {
                        if (min_carbs > correct_requestFood.get(i).getCarbs()) {
                            min_carbs = correct_requestFood.get(i).getCarbs();
                            min_carbs_index = i;
                        }
                    }
                    Log.d("debug", "-----------------------------------------------------------------------------------------------------");
                    Log.e("e", correct_requestFood.get(min_carbs_index).food_image());
                    requestFood_name.setText(correct_requestFood.get(min_carbs_index).getFood_name());
                    Glide.with(rootView).load(TaskServer.base_url + "requestfood/" + correct_requestFood.get(min_carbs_index).food_image()).error(R.drawable.food_icon)
                            .placeholder(R.drawable.food_icon).into(iv_good_food);
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

    private void calculate_recommend () {

        if (gender == 1) {
            standardweight = (height / 100) * (height / 100) * 21;
            Log.d("standardweight", " " + standardweight);
        } else {
            standardweight = (height / 100) * (height / 100) * 22;
            Log.d("standardweight", " " + standardweight);
        }

        switch (activity) {
            case 0:
                required_kcal = (float) (standardweight * 27.5);
            case 1:
                required_kcal = (float) (standardweight * 32.5);
            case 2:
                required_kcal = (float) (standardweight * 37.5);
        }

        required_kcal -= 300;

        //탄 단 지 = 3 : 4 : 3
        recommend_carbs = (float) ((required_kcal * 0.3) / 4);
        recommend_protein = (float) ((required_kcal * 0.4) / 4);
        recommend_fat = (float) ((required_kcal * 0.3) / 9);

        et_recommend_kcal.setText(String.valueOf(Math.round((required_kcal * 100) / 100.0)) + "kcal");
        et_recommend_carbs.setText(String.valueOf(Math.round((recommend_carbs * 100) / 100.0)) + "g");
        et_recommend_protein.setText(String.valueOf(Math.round((recommend_protein * 100) / 100.0)) + "g");
        et_recommend_fat.setText(String.valueOf(Math.round((recommend_fat * 100) / 100.0)) + "g");
    }
}
