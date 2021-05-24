package com.fournineseven.dietstock.ui.feedback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fournineseven.dietstock.App;
import com.fournineseven.dietstock.LoginState;
import com.fournineseven.dietstock.R;
import com.fournineseven.dietstock.api.RetrofitService;
import com.fournineseven.dietstock.model.getDailyFood.DailyFoodResult;
import com.fournineseven.dietstock.model.getDailyFood.GetDailyFoodRequest;
import com.fournineseven.dietstock.model.getDailyFood.GetDailyFoodResponse;
import com.fournineseven.dietstock.model.getRequestFood.GetRequestFoodRequest;
import com.fournineseven.dietstock.model.getRequestFood.GetRequestFoodResponse;
import com.fournineseven.dietstock.model.getRequestFood.RequestFoodResult;
import com.fournineseven.dietstock.ui.ranking.RankingAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
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

    private EditText et_real_kcal;
    private EditText et_real_carbs;
    private EditText et_real_protein;
    private EditText et_real_fat;
    private EditText et_recommend_kcal;
    private EditText et_recommend_carbs;
    private EditText et_recommend_protein;
    private EditText et_recommend_fat;


    private Button btn_gocheck;
    private String user_avoid = "";
    private int user_no;
    long now = System.currentTimeMillis();
    private String avoid_food ="";
    private String nutriention ="";
    private float gram;
    private float carbs=0,protein=0,fat=0;
    private int kcal=0;
    private float recommend_carbs=0,recommend_protein=0,recommend_fat=0;
    private float compare[] = new float[3];
    private float dummy[] = new float[3];

    private float height=0;
    private int gender=0;
    private int activity = 0;
    private float standardweight=0;
    private float required_kcal=0;
    private RecyclerView recyclerView;

    ArrayList<feedback_data> arrayList;
    feedbackAdapter feedbackadapter;

    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.feedback_main,container,false);
        btn_gocheck = rootView.findViewById(R.id.btn_gocheck);
        et_real_kcal = rootView.findViewById(R.id.et_real_kcal);
        et_real_carbs = rootView.findViewById(R.id.et_real_carbs);
        et_real_protein = rootView.findViewById(R.id.et_real_protein);
        et_real_fat = rootView.findViewById(R.id.et_real_fat);
        et_recommend_kcal = rootView.findViewById(R.id.et_recommend_kcal);
        et_recommend_carbs = rootView.findViewById(R.id.et_recommend_carbs);
        et_recommend_protein = rootView.findViewById(R.id.et_recommend_protein);
        et_recommend_fat = rootView.findViewById(R.id.et_recommend_fat);


        arrayList = new ArrayList<>();

        //Bundle bundle = getArguments();
        //user_avoid = bundle.getString("메시지"); //user_avoid = 유저가 체크한 기피식품

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS,0);
        user_avoid = sharedPreferences.getString("user_avoid","");


        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences(SHARED_PREFS,0);
        int sharedPreferences_user_no = Integer.valueOf(sharedPreferences.getString(LoginState.USER_NUMBER,null));
        user_no = sharedPreferences_user_no;
        Log.d("user_no"," " + user_no);
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
                GetDailyFoodResponse getDailyFoodResponse = (GetDailyFoodResponse)response.body();
                ArrayList<DailyFoodResult> dailyFoodResults = (getDailyFoodResponse).getResult();
                if(getDailyFoodResponse.isSuccess()) { //리스트로 result를 받아옴(먹은 식단)
                    kcal = 0;
                    carbs = 0;
                    protein = 0;
                    fat = 0;
                    feedbackadapter.setEmpty();
                    for(int i=0; i<dailyFoodResults.size(); i++){
                        DailyFoodResult dailyfood = dailyFoodResults.get(i);
                        feedback_data data = new feedback_data(dailyfood.getFood_image(), dailyfood.getServing(),dailyfood.getKcal(), dailyfood.getCarbs(),dailyfood.getProtein(),dailyfood.getFat(),
                                        dailyfood.getUpdated_dt(),dailyfood.getFood_name());
                        feedbackadapter.addItem(data);
                        kcal += data.getKcal() * data.getServing();
                        carbs += data.getCarbs() * data.getServing(); //먹은 음식의 탄수화물 총합
                        protein += data.getProtein() * data.getServing(); //먹은 음식의 단백질 총합
                        fat += data.getFat() * data.getServing(); //먹은 음식의 지방 총합
                        Log.d("info idx = " + String.valueOf(i), String.valueOf(kcal) + " " + String.valueOf(carbs) + " " + String.valueOf(protein) + " " + String.valueOf(fat) + " Serving = " + String.valueOf(data.getServing()));

                        Log.e("error", String.valueOf(kcal));
                    }
                    recyclerView.setAdapter(feedbackadapter);
                    feedbackadapter.notifyDataSetChanged();
                    et_real_kcal.setText(String.valueOf(Math.round((kcal*100)/100.0 )+"kcal"));
                    et_real_carbs.setText(String.valueOf(Math.round((carbs*100)/100.0 )+"g"));
                    et_real_protein.setText(String.valueOf(Math.round((protein*100)/100.0 )+"g"));
                    et_real_fat.setText(String.valueOf(Math.round((fat*100)/100.0 ))+"g");
                    //ArrayList<DailyFoodResult> dailyFoodResult = getDailyFoodResponse.getResult();
                }
            }



            @Override
                public void onFailure(Call<GetDailyFoodResponse> call, Throwable t) {
                    Log.d("debug", "onFailure"+t.toString());
            }
        });
        recyclerView =(RecyclerView)rootView.findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        feedbackadapter = new feedbackAdapter(getActivity());

        int sharedPreferences_sex = sharedPreferences.getInt(GENDER_KEY,0);
        gender = sharedPreferences.getInt(GENDER_KEY,0);
        float sharedPreferences_height = sharedPreferences.getFloat(HEIGHT_KEY,0);
        height = sharedPreferences.getFloat(HEIGHT_KEY,0);
        int sharedPreferences_activity = sharedPreferences.getInt(ACTIVITY_KEY,0);
        activity = sharedPreferences.getInt(ACTIVITY_KEY,0);

        Log.d("gender"," " + gender);
        Log.d("height"," " + height);
        Log.d("activity"," " + activity);

        if(gender==1){
            standardweight = (height/100) * (height/100) * 21;
            Log.d("standardweight"," " + standardweight);
        } else{
            standardweight = (height/100) * (height/100) * 22;
            Log.d("standardweight"," " + standardweight);
        }

        switch(activity){
            case 0 :  required_kcal = (float)(standardweight * 27.5);
            case 1 :  required_kcal = (float) (standardweight * 32.5);
            case 2 :  required_kcal = (float) (standardweight * 37.5);
        }

        required_kcal -= 300;

        //탄 단 지 = 3 : 4 : 3
        recommend_carbs = (float) ((required_kcal * 0.3) / 4);
        recommend_protein = (float) ((required_kcal * 0.4) / 4);
        recommend_fat = (float) ((required_kcal * 0.3) / 9);

        et_recommend_kcal.setText(String.valueOf(Math.round((required_kcal*100)/100.0 ))+"kcal");
        et_recommend_carbs.setText(String.valueOf(Math.round((recommend_carbs*100)/100.0 ))+"g");
        et_recommend_protein.setText(String.valueOf(Math.round((recommend_protein*100)/100.0 ))+"g");
        et_recommend_fat.setText(String.valueOf(Math.round((recommend_fat*100)/100.0 ))+"g");

        compare[0] = carbs - recommend_carbs;
        compare[1] = protein - recommend_protein;
        compare[2] = fat - recommend_fat;
        for(int i=0;i<3;i++){
            dummy[i] = Math.abs(compare[i]);
        }
        float max = dummy[0];
        int max_index=0;
        for(int i=1;i<3;i++){
            if(max < dummy[i]){
                max = dummy[i];
                max_index=i;
            }
        }
        switch(max_index){
            case 0 : nutriention = "carbs"; gram = compare[0]; break;
            case 1 : nutriention = "protein"; gram = compare[1]; break;
            case 2 : nutriention = "fat"; gram = compare[2]; break;
        }

        //gram = 오늘 먹은 음식의 총량 중 가장 차이나는 탄 단 지 중 하나

        //ex) 탄수화물 -100 -> 탄수화물이 100보다 작은 음식 넘어옴
        //ex) 단백질 +50 -> 단백질이 50보다 큰 음식 넘어옴

        String getstr = getArguments().getString("send");
        avoid_food = getstr;
        Log.d("avoid_food"," " + getstr);

        RetrofitService getRequestFoodService = App.retrofit.create(RetrofitService.class);
        Call<GetRequestFoodResponse> callGetRequestFood =
                getRequestFoodService.getRequestFood(new GetRequestFoodRequest(avoid_food, nutriention, gram));

        callGetRequestFood.enqueue(new Callback<GetRequestFoodResponse>() {
            @Override
            public void onResponse(Call<GetRequestFoodResponse> call, Response<GetRequestFoodResponse> response) {
                Log.d("debug", response.body().toString());
                GetRequestFoodResponse GetRequestFoodResponse = (GetRequestFoodResponse)response.body();
                ArrayList<RequestFoodResult> requestFoodResultsResults = (GetRequestFoodResponse).getResult();
                if(GetRequestFoodResponse.isSuccess()){
                    for(int i=0; i<requestFoodResultsResults.size(); i++) {
                        /*private String food_name;
                        private float kcal;
                        private float carbs;
                        private float protein;
                        private float fat;
                        private String food_image;*/




                    }
                }
            }

            @Override
            public void onFailure(Call<GetRequestFoodResponse> call, Throwable t) {
                Log.d("debug", "onFailure"+t.toString());
            }
        });



        btn_gocheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(),avoidFood_check.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(user_avoid,0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String value = user_avoid;
        editor.putString("user_avoid",value);
        editor.commit();
    }
}
