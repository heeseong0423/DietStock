package com.fournineseven.dietstock.ui.feedback;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fournineseven.dietstock.R;
import com.fournineseven.dietstock.config.TaskServer;
import com.fournineseven.dietstock.ui.ranking.RankingAdapter;
import com.fournineseven.dietstock.ui.ranking.RankingItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class feedbackAdapter extends RecyclerView.Adapter<feedbackAdapter.CustomViewHolder> {

    private ArrayList<feedback_data> items =new ArrayList<>();
    private Context context;

    public feedbackAdapter(Context context) {this.context = context;}

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.feedback_list, parent, false);
        return new feedbackAdapter.CustomViewHolder(itemView,context);
    }

    @Override
    public void onBindViewHolder(@NonNull feedbackAdapter.CustomViewHolder holder, int position) {
        feedback_data item = items.get(position);
        holder.setItem(item);
    }

    public void addItem(feedback_data item){items.add(item);}
    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setEmpty(){items.clear();}


    static class CustomViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearlayout_ranking_item;
        ImageView food_image;
        EditText time;
        EditText foodname;
        Context context;

        public CustomViewHolder(@NonNull @NotNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            food_image = (ImageView) itemView.findViewById(R.id.food_image);
            time = (EditText)itemView.findViewById(R.id.time);
            foodname = (EditText)itemView.findViewById(R.id.foodname);
            linearlayout_ranking_item = (LinearLayout) itemView.findViewById(R.id.linearlayout_dailyFood);
        }

        public void setItem(feedback_data item){
            //food_image.setImageResource(Integer.parseInt(item.getFood_image()));
            Glide.with(context).load(TaskServer.base_url+item.getFood_image()).error(R.drawable.food_icon)
                    .placeholder(R.drawable.food_icon).into(food_image);
            foodname.setText(item.getFoodname());
        }
    }
}
