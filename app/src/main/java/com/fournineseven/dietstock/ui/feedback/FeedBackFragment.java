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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fournineseven.dietstock.App;
import com.fournineseven.dietstock.R;
import com.fournineseven.dietstock.api.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeedBackFragment extends Fragment {

    private Button btn_gocheck;
    private String user_avoid;
    private EditText breakfast_kcal;
    private EditText breakfast_carbs;
    private EditText breakfast_protein;
    private EditText breakfast_fat;
    private EditText breakfast_time;
    private EditText lunch_kcal;
    private EditText lunch_carbs;
    private EditText lunch_protein;
    private EditText lunch_fat;
    private EditText lunch_time;
    private EditText dinner_kcal;
    private EditText dinner_carbs;
    private EditText dinner_protein;
    private EditText dinner_fat;
    private EditText dinner_time;
    private EditText breakfast_foodname;
    private EditText lunch_foodname;
    private EditText dinner_foodname;
    getDailyFood today_food;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_feedback,container,false);
        btn_gocheck = rootView.findViewById(R.id.btn_gocheck);

        breakfast_kcal = rootView.findViewById(R.id.breakfast_kcal);
        breakfast_carbs = rootView.findViewById(R.id.breakfast_carbs);
        breakfast_protein = rootView.findViewById(R.id.breakfast_protein);
        breakfast_fat = rootView.findViewById(R.id.breakfast_fat);
        breakfast_time = rootView.findViewById(R.id.breakfast_time);

        lunch_kcal = rootView.findViewById(R.id.lunch_kcal);
        lunch_carbs = rootView.findViewById(R.id.lunch_carbs);
        lunch_protein = rootView.findViewById(R.id.lunch_protein);
        lunch_fat = rootView.findViewById(R.id.lunch_fat);
        lunch_time = rootView.findViewById(R.id.lunch_time);

        dinner_kcal = rootView.findViewById(R.id.dinner_kcal);
        dinner_carbs = rootView.findViewById(R.id.dinner_carbs);
        dinner_protein = rootView.findViewById(R.id.dinner_protein);
        dinner_fat = rootView.findViewById(R.id.dinner_fat);
        dinner_time = rootView.findViewById(R.id.dinner_time);

        breakfast_foodname = rootView.findViewById(R.id.dinner_kcal);
        lunch_foodname = rootView.findViewById(R.id.lunch_foodname);
        dinner_foodname = rootView.findViewById(R.id.dinner_foodname);


        Bundle bundle = getArguments();
        user_avoid = bundle.getString("메시지"); //user_avoid = 유저가 체크한 기피식품

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(user_avoid,0);
        user_avoid = sharedPreferences.getString("user_avoid","");


        RetrofitService getDailyFoodService = App.retrofit.create(RetrofitService.class);
        Call<List<getDailyFood>> call = getDailyFoodService.getDailyFood();
        call.enqueue(new Callback<List<getDailyFood>>(){

            @Override
            public void onResponse(Call<List<getDailyFood>> call, Response<List<getDailyFood>> response) {
                List<getDailyFood> today_food = response.body();






                Log.d("debug", today_food.toString());
            }

            @Override
            public void onFailure(Call<List<getDailyFood>> call, Throwable t) {
                Log.d("debug", "onFailure: "+t.getMessage());
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
