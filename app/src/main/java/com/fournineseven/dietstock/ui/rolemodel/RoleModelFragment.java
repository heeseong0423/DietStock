package com.fournineseven.dietstock.ui.rolemodel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.fournineseven.dietstock.App;
import com.fournineseven.dietstock.R;
import com.fournineseven.dietstock.api.DialogService;
import com.fournineseven.dietstock.api.RetrofitService;
import com.fournineseven.dietstock.config.TaskServer;
import com.fournineseven.dietstock.model.getRanking.GetRankingResponse;
import com.fournineseven.dietstock.model.getRanking.RankingResult;
import com.fournineseven.dietstock.model.getRolemodel.GetRolemodelResponse;
import com.fournineseven.dietstock.model.getRolemodel.RolemodelResult;
import com.fournineseven.dietstock.ui.ranking.RankingItem;
import com.fournineseven.dietstock.ui.ranking.RankingViewModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoleModelFragment extends Fragment {
    private RoleModelViewModel roleModelViewModel;
    private LinearLayout linearlayout_rolemodel1, linearlayout_rolemodel2, linearlayout_rolemodel3;
    private TextView textView_rolemodel_name1, textView_rolemodel_name2, textView_rolemodel_name3;
    private TextView textView_rolemodel_result1,textView_rolemodel_result2,textView_rolemodel_result3;
    private ImageView imageview_rolemodel_image_before1, imageview_rolemodel_image_before2, imageview_rolemodel_image_before3;
    private ImageView imageview_rolemodel_image_after1, imageview_rolemodel_image_after2, imageview_rolemodel_image_after3;


    View root;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        roleModelViewModel = new ViewModelProvider(this).get(RoleModelViewModel.class);
        root = inflater.inflate(R.layout.fragment_rolemodel, container, false);
        roleModelViewModel.getValue1().observe(getViewLifecycleOwner(), value1 ->{
            //update UI
        });
        init();
        return root;
    }

    public void init(){
        textView_rolemodel_name1 = (TextView)root.findViewById(R.id.textview_rolemodel_name1);
        textView_rolemodel_name2 = (TextView)root.findViewById(R.id.textview_rolemodel_name2);
        textView_rolemodel_name3 = (TextView)root.findViewById(R.id.textview_rolemodel_name3);
        textView_rolemodel_result1 = (TextView)root.findViewById(R.id.textview_rolemodel_result1);
        textView_rolemodel_result2 = (TextView)root.findViewById(R.id.textview_rolemodel_result2);
        textView_rolemodel_result3 = (TextView)root.findViewById(R.id.textview_rolemodel_result3);
        imageview_rolemodel_image_after1=(ImageView)root.findViewById(R.id.imageview_rolemodel_image_after1);
        imageview_rolemodel_image_after2=(ImageView)root.findViewById(R.id.imageview_rolemodel_image_after2);
        imageview_rolemodel_image_after3=(ImageView)root.findViewById(R.id.imageview_rolemodel_image_after3);
        imageview_rolemodel_image_before1=(ImageView)root.findViewById(R.id.imageview_rolemodel_image_before1);
        imageview_rolemodel_image_before2=(ImageView)root.findViewById(R.id.imageview_rolemodel_image_before2);
        imageview_rolemodel_image_before3=(ImageView)root.findViewById(R.id.imageview_rolemodel_image_before3);
        RetrofitService getRolemodelService = App.retrofit.create(RetrofitService.class);
        Call<GetRolemodelResponse> call = getRolemodelService.getRolemodel();
        call.enqueue(new Callback<GetRolemodelResponse>() {
            @Override
            public void onResponse(Call<GetRolemodelResponse> call, Response<GetRolemodelResponse> response) {
                Log.d("debug", response.body().toString());
                GetRolemodelResponse getRankingResponse = (GetRolemodelResponse)response.body();
                ArrayList<RolemodelResult> rankingResultArray = getRankingResponse.getResult();
                if(getRankingResponse.isSuccess()){
                    textView_rolemodel_name1.setText(rankingResultArray.get(0).getName());
                    textView_rolemodel_result1.setText(String.valueOf(rankingResultArray.get(0).getWeight_gap()));
                    Glide.with(root).load(TaskServer.base_url+rankingResultArray.get(0).getAfterimage()).error(R.drawable.hindoongi)
                    .placeholder(R.drawable.hindoongi).override(90, 120).into(imageview_rolemodel_image_after1);

                    Glide.with(root).load(TaskServer.base_url+rankingResultArray.get(0).getBeforeimage()).error(R.drawable.hindoongi)
                            .placeholder(R.drawable.hindoongi).override(90, 120).into(imageview_rolemodel_image_before1);

                    linearlayout_rolemodel1 = (LinearLayout)root.findViewById(R.id.linearlayout_rolemodel1);
                    linearlayout_rolemodel1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), RoleModelDetailActivity.class);
                            intent.putExtra("user_no", rankingResultArray.get(0).getUser_no());
                            intent.putExtra("name", rankingResultArray.get(0).getName());
                            intent.putExtra("weight_gap", rankingResultArray.get(0).getWeight_gap());
                            intent.putExtra("after_image", rankingResultArray.get(0).getAfterimage());
                            startActivity(intent);
                        }
                    });

                    textView_rolemodel_name2.setText(rankingResultArray.get(1).getName());
                    textView_rolemodel_result2.setText(String.valueOf(rankingResultArray.get(1).getWeight_gap()));

                    Glide.with(root).load(TaskServer.base_url+rankingResultArray.get(1).getBeforeimage()).error(R.drawable.hindoongi)
                            .placeholder(R.drawable.hindoongi).override(90, 120).into(imageview_rolemodel_image_before2);

                    Glide.with(root).load(TaskServer.base_url+rankingResultArray.get(1).getAfterimage()).error(R.drawable.hindoongi)
                            .placeholder(R.drawable.hindoongi).override(90, 120).into(imageview_rolemodel_image_after2);
                    linearlayout_rolemodel2 = (LinearLayout)root.findViewById(R.id.linearlayout_rolemodel2);
                    linearlayout_rolemodel2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), RoleModelDetailActivity.class);
                            intent.putExtra("user_no", rankingResultArray.get(1).getUser_no());
                            intent.putExtra("name", rankingResultArray.get(1).getName());
                            intent.putExtra("weight_gap", rankingResultArray.get(1).getWeight_gap());
                            intent.putExtra("after_image", rankingResultArray.get(1).getAfterimage());
                            startActivity(intent);
                        }
                    });

                    textView_rolemodel_name3.setText(rankingResultArray.get(2).getName());
                    textView_rolemodel_result3.setText(String.valueOf(rankingResultArray.get(2).getWeight_gap()));

                    Glide.with(root).load(TaskServer.base_url+rankingResultArray.get(2).getBeforeimage()).error(R.drawable.hindoongi)
                            .placeholder(R.drawable.hindoongi).override(90, 120).into(imageview_rolemodel_image_before3);

                    Glide.with(root).load(TaskServer.base_url+rankingResultArray.get(2).getAfterimage()).error(R.drawable.hindoongi)
                            .placeholder(R.drawable.hindoongi).override(90, 120).into(imageview_rolemodel_image_after3);
                    linearlayout_rolemodel3 = (LinearLayout)root.findViewById(R.id.linearlayout_rolemodel3);
                    linearlayout_rolemodel3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), RoleModelDetailActivity.class);
                            intent.putExtra("user_no", rankingResultArray.get(2).getUser_no());
                            intent.putExtra("name", rankingResultArray.get(2).getName());
                            intent.putExtra("weight_gap", rankingResultArray.get(2).getWeight_gap());
                            intent.putExtra("after_image", rankingResultArray.get(2).getAfterimage());
                            startActivity(intent);
                        }
                    });
                }else{
                    DialogService.showDialog(getActivity(), "오류", "서버에서 데이터 가져오기 실패");
                }
            }

            @Override
            public void onFailure(Call<GetRolemodelResponse> call, Throwable t) {
                Log.d("debug", "onFailure: "+t.getMessage());
                DialogService.showDialog(getActivity(), "오류", "서버 접속 실패");
            }
        });

    }
}
