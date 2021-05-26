package com.fournineseven.dietstock.ui.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fournineseven.dietstock.R;

public class avoidFood_check extends AppCompatActivity {

    private Button btn_move;
    private CheckBox checkBox_poultry,checkBox_buckwheat,checkBox_peach,checkBox_tomato,checkBox_walnut;
    private CheckBox checkBox_peanut,checkBox_chicken,checkBox_beef,checkBox_oyster,checkBox_wheat,checkBox_crab,checkBox_mackerel;
    private CheckBox checkBox_pork,checkBox_shrimp,checkBox_squid,checkBox_shellfish,checkBox_abalone,checkBox_bean,checkBox_mussel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avoidfood_check);

        checkBox_poultry = findViewById(R.id.checkBox_poultry);
        checkBox_buckwheat = findViewById(R.id.checkBox_buckwheat);
        checkBox_peach = findViewById(R.id.checkBox_peach);
        checkBox_tomato = findViewById(R.id.checkBox_tomato);
        checkBox_walnut = findViewById(R.id.checkBox_walnut);
        checkBox_peanut = findViewById(R.id.checkBox_peanut);
        checkBox_chicken = findViewById(R.id.checkBox_chicken);
        checkBox_beef = findViewById(R.id.checkBox_beef);
        checkBox_oyster = findViewById(R.id.checkBox_oyster);
        checkBox_wheat = findViewById(R.id.checkBox_wheat);
        checkBox_crab = findViewById(R.id.checkBox_crab);
        checkBox_mackerel = findViewById(R.id.checkBox_mackerel);
        checkBox_pork = findViewById(R.id.checkBox_pork);
        checkBox_shrimp = findViewById(R.id.checkBox_shrimp);
        checkBox_shellfish = findViewById(R.id.checkBox_shellfish);
        checkBox_abalone = findViewById(R.id.checkBox_abalone);
        checkBox_bean = findViewById(R.id.checkBox_bean);
        checkBox_mussel = findViewById(R.id.checkBox_mussel);
        checkBox_squid = findViewById(R.id.checkBox_squid);

        btn_move = findViewById(R.id.btn_move);
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String result = "";

                if (checkBox_poultry.isChecked()) {
                    result += checkBox_poultry.getText().toString() + ",";
                }
                if (checkBox_buckwheat.isChecked()) {
                    result += checkBox_buckwheat.getText().toString() + ",";
                }
                if (checkBox_peach.isChecked()) {
                    result += checkBox_peach.getText().toString() + ",";
                }
                if (checkBox_tomato.isChecked()) {
                    result += checkBox_tomato.getText().toString() + ",";
                }
                if (checkBox_walnut.isChecked()) {
                    result += checkBox_walnut.getText().toString() + ",";
                }
                if (checkBox_peanut.isChecked()) {
                    result += checkBox_peanut.getText().toString() + ",";
                }
                if (checkBox_chicken.isChecked()) {
                    result += checkBox_chicken.getText().toString() + ",";
                }
                if (checkBox_beef.isChecked()) {
                    result += checkBox_beef.getText().toString() + ",";
                }
                if (checkBox_oyster.isChecked()) {
                    result += checkBox_oyster.getText().toString() + ",";
                }
                if (checkBox_wheat.isChecked()) {
                    result += checkBox_wheat.getText().toString() + ",";
                }
                if (checkBox_crab.isChecked()) {
                    result += checkBox_crab.getText().toString() + ",";
                }
                if (checkBox_mackerel.isChecked()) {
                    result += checkBox_mackerel.getText().toString() + ",";
                }
                if (checkBox_pork.isChecked()) {
                    result += checkBox_pork.getText().toString() + ",";
                }
                if (checkBox_shrimp.isChecked()) {
                    result += checkBox_shrimp.getText().toString() + ",";
                }
                if (checkBox_shellfish.isChecked()) {
                    result += checkBox_shellfish.getText().toString() + ",";
                }
                if (checkBox_abalone.isChecked()) {
                    result += checkBox_abalone.getText().toString() + ",";
                }
                if (checkBox_bean.isChecked()) {
                    result += checkBox_bean.getText().toString() + ",";
                }
                if (checkBox_squid.isChecked()) {
                    result += checkBox_squid.getText().toString() + ",";
                }
                if (checkBox_mussel.isChecked()) {
                    result += checkBox_mussel.getText().toString() + ",";
                }
                Bundle bundle = new Bundle();
                String sendstr = result;
                bundle.putString("send", sendstr );
                Fragment fragment = new FeedBackFragment();
                fragment.setArguments(bundle);

                finish();
            }
        });
    }
}
