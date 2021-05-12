package com.fournineseven.dietstock.ui.feedback;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fournineseven.dietstock.R;

public class Feedback extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        SharedPreferences sharedPreferences = getSharedPreferences("shared_avoid",0);
        String user_avoid = sharedPreferences.getString("user_avoidFood","");


    }

}
