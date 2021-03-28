package com.fournineseven.dietstock.ui.rolemodel;

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

public class RoleModelFragment extends Fragment {
    private RoleModelViewModel roleModelViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        roleModelViewModel = new ViewModelProvider(this).get(RoleModelViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ranking, container, false);
        roleModelViewModel.getValue1().observe(getViewLifecycleOwner(), value1 ->{
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
