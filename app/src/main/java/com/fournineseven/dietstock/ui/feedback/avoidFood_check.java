package com.fournineseven.dietstock.ui.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private String avoidFood_str ="";

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
                    result += "poultry,";
                }
                if (checkBox_buckwheat.isChecked()) {
                    result += "buckwheat,";
                }
                if (checkBox_peach.isChecked()) {
                    result += "peach,";
                }
                if (checkBox_tomato.isChecked()) {
                    result += "tomato,";
                }
                if (checkBox_walnut.isChecked()) {
                    result += "walnut,";
                }
                if (checkBox_peanut.isChecked()) {
                    result += "peanut,";
                }
                if (checkBox_chicken.isChecked()) {
                    result += "chicken,";
                }
                if (checkBox_beef.isChecked()) {
                    result += "beef,";
                }
                if (checkBox_oyster.isChecked()) {
                    result += "oyster,";
                }
                if (checkBox_wheat.isChecked()) {
                    result += "wheat,";
                }
                if (checkBox_crab.isChecked()) {
                    result += "crab,";
                }
                if (checkBox_mackerel.isChecked()) {
                    result += "mackerel,";
                }
                if (checkBox_pork.isChecked()) {
                    result += "pork,";
                }
                if (checkBox_shrimp.isChecked()) {
                    result += "shrimp,";
                }
                if (checkBox_shellfish.isChecked()) {
                    result += "shellfish,";
                }
                if (checkBox_abalone.isChecked()) {
                    result += "abalone,";
                }
                if (checkBox_bean.isChecked()) {
                    result += "bean,";
                }
                if (checkBox_squid.isChecked()) {
                    result += "squid,";
                }
                if (checkBox_mussel.isChecked()) {
                    result += "mussel,";
                }
                avoidFood_str = result;

                if(result.length() != 0){
                Intent intent = new Intent();
                intent.putExtra("result", avoidFood_str);
                setResult(RESULT_OK, intent);
                }

                finish();
            }
        });
    }
}
