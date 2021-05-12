package com.fournineseven.dietstock.ui.feedback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fournineseven.dietstock.R;

public class FeedBackFragment extends Fragment {

    private Button btn_gocheck;
    private Button btn_feedback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_feedback,container,false);
        btn_gocheck = rootView.findViewById(R.id.btn_gocheck);
        btn_feedback = rootView.findViewById(R.id.btn_feedback);

        btn_gocheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(),avoidFood_check.class);
                startActivity(intent);
            }
        });

        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(),Feedback.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

}
