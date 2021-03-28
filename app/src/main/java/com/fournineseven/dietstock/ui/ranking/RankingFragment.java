package com.fournineseven.dietstock.ui.ranking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fournineseven.dietstock.R;

public class RankingFragment extends Fragment {
    private RankingViewModel rankingViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rankingViewModel = new ViewModelProvider(this).get(RankingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ranking, container, false);
        rankingViewModel.getValue1().observe(getViewLifecycleOwner(), value1 ->{
            //update UI
        });
        return root;
        //notificationsViewModel =
        //                ViewModelProvider(this).get(NotificationsViewModel::class.java)
        //        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        //        val textView: TextView = root.findViewById(R.id.text_notifications)
        //        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
        //            textView.text = it
        //        })
        //        return root
    }
}
