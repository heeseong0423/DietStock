package com.fournineseven.dietstock.ui.ranking;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fournineseven.dietstock.R;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder>{
    Context context;
    ArrayList<RankingItem> items = new ArrayList<RankingItem>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearlayout_ranking_item;
        TextView textview_ranking_no;
        TextView textview_ranking_name;
        TextView textview_ranking_kcal;
        ImageView imageview_ranking_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textview_ranking_no = (TextView)itemView.findViewById(R.id.textview_ranking_no);
            textview_ranking_name = (TextView)itemView.findViewById(R.id.textview_ranking_name);
            textview_ranking_kcal = (TextView)itemView.findViewById(R.id.textview_ranking_kcal);
            imageview_ranking_image = (ImageView) itemView.findViewById(R.id.imageview_ranking_image);
            linearlayout_ranking_item = (LinearLayout) itemView.findViewById(R.id.linearlayout_ranking_item);
        }

        public void setItem(RankingItem item){
            textview_ranking_no.setText(String.valueOf(item.getNo()));
            textview_ranking_name.setText(item.getName());
            textview_ranking_kcal.setText(item.getKcal());
            imageview_ranking_image.setImageResource(R.drawable.ranking_up);
        }
    }
}
