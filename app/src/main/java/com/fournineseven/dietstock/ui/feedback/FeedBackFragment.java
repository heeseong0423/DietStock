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
import com.fournineseven.dietstock.R;
import com.fournineseven.dietstock.api.RetrofitService;
import com.fournineseven.dietstock.model.getDailyFood.DailyFoodResult;
import com.fournineseven.dietstock.model.getDailyFood.GetDailyFoodRequest;
import com.fournineseven.dietstock.model.getDailyFood.GetDailyFoodResponse;
import com.fournineseven.dietstock.model.getRequestFood.GetRequestFoodRequest;
import com.fournineseven.dietstock.model.getRequestFood.GetRequestFoodResponse;
import com.fournineseven.dietstock.model.getRequestFood.RequestFoodResult;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fournineseven.dietstock.LoginState.SHARED_PREFS;

public class FeedBackFragment extends Fragment {

    private Button btn_gocheck;
    private String user_avoid;
    private int user_no;
    long now = System.currentTimeMillis();
    private String avoid_food;
    private String nutriention;
    private float gram;
    private float carbs=0,protein=0,fat=0;

    ArrayList<feedback_data> arrayList;
    feedbackAdapter feedbackadapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.feedback_main,container,false);
        btn_gocheck = rootView.findViewById(R.id.btn_gocheck);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        //linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        feedbackadapter = new feedbackAdapter(arrayList);
        recyclerView.setAdapter(feedbackadapter);

        Bundle bundle = getArguments();
        user_avoid = bundle.getString("메시지"); //user_avoid = 유저가 체크한 기피식품

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(user_avoid,0);
        user_avoid = sharedPreferences.getString("user_avoid","");


        SharedPreferences sharedPreferences_user_no = getActivity().getSharedPreferences(SHARED_PREFS,0);
        user_no = sharedPreferences.getInt("shared_prefs",0);
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
                    feedbackadapter.setEmpty();
                    for(int i=0; i<dailyFoodResults.size(); i++){
                        DailyFoodResult dailyfood = dailyFoodResults.get(i);
                        feedback_data data = new feedback_data(dailyfood.getFood_image(),dailyfood.getKcal(),dailyfood.getCarbs(),dailyfood.getProtein(),dailyfood.getFat(),
                                        dailyfood.getUpdated_dt(),dailyfood.getFood_name());
                        feedbackadapter.addItem(data);
                        carbs += data.getCarbs();
                        protein += data.getProtein();
                        fat += data.getFat();
                    }
                    recyclerView.setAdapter(feedbackadapter);
                    feedbackadapter.notifyDataSetChanged();
                    //ArrayList<DailyFoodResult> dailyFoodResult = getDailyFoodResponse.getResult();
                }
            }

            @Override
            public void onFailure(Call<GetDailyFoodResponse> call, Throwable t) {

                Log.d("debug", "onFailure"+t.toString());
            }
        });

        avoid_food = user_avoid;
        //nutriention 이랑 gram 설정해야 함

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
