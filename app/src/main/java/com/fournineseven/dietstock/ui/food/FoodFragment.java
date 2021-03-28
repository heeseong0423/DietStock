package com.fournineseven.dietstock.ui.food;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fournineseven.dietstock.R;
import com.fournineseven.dietstock.ui.ranking.RankingViewModel;

public class FoodFragment extends Fragment {
    private FoodViewModel foodViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        foodViewModel = new ViewModelProvider(this).get(FoodViewModel.class);
        View root = inflater.inflate(R.layout.fragment_food, container, false);
        foodViewModel.getValue1().observe(getViewLifecycleOwner(), value1 ->{
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
