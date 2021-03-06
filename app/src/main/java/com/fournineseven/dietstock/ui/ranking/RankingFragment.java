package com.fournineseven.dietstock.ui.ranking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fournineseven.dietstock.App;
import com.fournineseven.dietstock.LoginState;
import com.fournineseven.dietstock.R;
import com.fournineseven.dietstock.api.DialogService;
import com.fournineseven.dietstock.api.RetrofitService;
import com.fournineseven.dietstock.model.getRanking.GetRankingResponse;
import com.fournineseven.dietstock.model.getRanking.RankingResult;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fournineseven.dietstock.LoginState.SHARED_PREFS;

public class RankingFragment extends Fragment {
    private RankingViewModel rankingViewModel;
    private Button btn_test;
    private RecyclerView recyclerView;
    private RadioGroup radioGroup_ranking_category;
    private RadioButton radioButton_ranking_day,radioButton_ranking_week,radioButton_ranking_month;

    /*IndexTask indexTask;*/
    View root;
    Handler mHandler = null;
    RankingAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rankingViewModel = new ViewModelProvider(this).get(RankingViewModel.class);
        root = inflater.inflate(R.layout.fragment_ranking, container, false);
        rankingViewModel.getValue1().observe(getViewLifecycleOwner(), value1 ->{
            //update UI
        });
        init();
        return root;
    }

    public void init(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, 0);

        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences(SHARED_PREFS, 0);
        int sharedPreferences_user_no = Integer.valueOf(sharedPreferences.getString(LoginState.USER_NUMBER, null));
        int user_no = sharedPreferences_user_no;
        mHandler = new Handler(Looper.getMainLooper());
        radioGroup_ranking_category = (RadioGroup)root.findViewById(R.id.radiogroup_ranking_category);
        RetrofitService getRankingService = App.retrofit.create(RetrofitService.class);
        Call<GetRankingResponse> call = getRankingService.getRanking("week", user_no);
        call.enqueue(new Callback<GetRankingResponse>() {
            @Override
            public void onResponse(Call<GetRankingResponse> call, Response<GetRankingResponse> response) {
                GetRankingResponse getRankingResponse = (GetRankingResponse)response.body();
                ArrayList<RankingResult> rankingResultArray = getRankingResponse.getResult();
                if(getRankingResponse.isSuccess()){
                    adapter.setEmpty();
                    int cnt=1;
                    for(int i=0; i<rankingResultArray.size(); i++){
                        RankingResult rankingItem = rankingResultArray.get(i);
                        if(rankingItem.getKcal()!=null) {
                            adapter.addItem(new RankingItem(cnt, rankingItem.getName(),
                                    rankingItem.getKcal(), user_no == rankingItem.getUser_no()));
                            cnt++;
                        }
                    }
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else{
                    DialogService.showDialog(getActivity(),"??????", "???????????? ????????? ???????????? ??????");
                }
            }

            @Override
            public void onFailure(Call<GetRankingResponse> call, Throwable t) {
                DialogService.showDialog(getActivity(),"??????", "?????? ?????? ??????");
            }
        });

        radioButton_ranking_week = (RadioButton)root.findViewById(R.id.radiobutton_ranking_week);
        radioButton_ranking_month = (RadioButton)root.findViewById(R.id.radiobutton_ranking_month);
        radioGroup_ranking_category.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                RetrofitService getRankingService = App.retrofit.create(RetrofitService.class);

                String division ="week";
                switch (id){

                    case R.id.radiobutton_ranking_week:
                        division = "week";
                        break;
                    case R.id.radiobutton_ranking_month:
                        division = "month";
                        break;
                }
                Call<GetRankingResponse> call = getRankingService.getRanking(division, user_no);
                call.enqueue(new Callback<GetRankingResponse>() {
                    @Override
                    public void onResponse(Call<GetRankingResponse> call, Response<GetRankingResponse> response) {
                        GetRankingResponse getRankingResponse = (GetRankingResponse)response.body();
                        ArrayList<RankingResult> rankingResultArray = getRankingResponse.getResult();
                        if(getRankingResponse.isSuccess()){
                            adapter.setEmpty();
                            int cnt=1;
                            for(int i=0; i<rankingResultArray.size(); i++){
                                RankingResult rankingItem = rankingResultArray.get(i);
                                if(rankingItem.getKcal()!=null) {
                                    adapter.addItem(new RankingItem(cnt, rankingItem.getName(),
                                            rankingItem.getKcal(), user_no == rankingItem.getUser_no()));
                                    cnt++;
                                }
                            }
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }else{
                            DialogService.showDialog(getActivity(), "??????","???????????? ????????? ???????????? ??????");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetRankingResponse> call, Throwable t) {
                        DialogService.showDialog(getActivity(),"??????", "?????? ?????? ??????");
                    }
                });
            }
        });
        recyclerView = (RecyclerView)root.findViewById(R.id.recyclerview_ranking);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RankingAdapter(getActivity());
    }

}
