package com.fournineseven.dietstock.ui.feedback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fournineseven.dietstock.R;


public class avoidFood_result extends AppCompatActivity {

    private Button page2_btn_finish;
    private Button page2_btn_prevmove;
    public TextView avoidFood_result;
    public String shared_avoid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avoidfood_result);

        avoidFood_result = findViewById(R.id.avoidFood_result);

        Intent secondIntent = getIntent();
        String message = secondIntent.getStringExtra("메시지");

        avoidFood_result.setText(message);

        page2_btn_prevmove = findViewById(R.id.page2_btn_prevmove);
        page2_btn_prevmove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(avoidFood_result.this,avoidFood_check.class); //이동->이동할 곳(class)
                startActivity(intent); //액티비티 이동
            }
        });

        page2_btn_finish = findViewById(R.id.page2_btn_finish);
        page2_btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(avoidFood_result.this,FeedBackFragment.class); //이동->이동할 곳(class)

                Bundle bundle = new Bundle();
                bundle.putString("메시지",message);

                startActivity(intent); //액티비티 이동
            }
        });
    }
}
