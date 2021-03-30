package com.fournineseven.dietstock.ui.ranking;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fournineseven.dietstock.R;
import com.fournineseven.dietstock.Utils.HttpConnectUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RankingFragment extends Fragment {
    private RankingViewModel rankingViewModel;
    private Button btn_test;
    private RecyclerView recyclerView;

    /*IndexTask indexTask;*/
    View root;

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
        recyclerView = (RecyclerView)root.findViewById(R.id.recyclerview_ranking);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RankingAdapter(getActivity());

        /*btn_test = (Button)root.findViewById(R.id.btn_test);

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indexTask = new IndexTask();
                indexTask.execute("http://497.iptime.org/");
            }
        });*/
    }

    /*class IndexTask extends AsyncTask<String, Void, Boolean> {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("로딩 중");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    IndexTask.this.cancel(true);
                }
            });
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                HashMap<String, String> result = HttpConnectUtil.sendGetData(params[0]);
                if(!result.containsKey("error")) {
                    Log.d("debug", result.get("result"));
                    JSONObject resultJsonObject = new JSONObject(result.get("result"));
                    Log.i("info", resultJsonObject.toString());
                    return true;
                }else{

                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            super.onPostExecute(aBoolean);
        }
    }*/

}
