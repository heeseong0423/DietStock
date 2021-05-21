package com.fournineseven.dietstock.ui.feedback;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fournineseven.dietstock.R;
import com.fournineseven.dietstock.ui.ranking.RankingItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class feedbackAdapter extends RecyclerView.Adapter<feedbackAdapter.CustomViewHolder> {

    private ArrayList<feedback_data> arrayList;

    public feedbackAdapter(ArrayList<feedback_data> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @NotNull
    @Override
    public feedbackAdapter.CustomViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_list,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull feedbackAdapter.CustomViewHolder holder, int position) {
        feedback_data item = arrayList.get(position);
    }

    public void addItem(feedback_data item){arrayList.add(item);}
    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public void setEmpty(){arrayList.clear();}


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView food_image;
        protected EditText carbs;
        protected EditText protein;
        protected EditText fat;
        protected EditText time;
        protected EditText foodname;

        public CustomViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.food_image = (ImageView) itemView.findViewById(R.id.food_image);
            this.carbs = (EditText) itemView.findViewById(R.id.carbs);
            this.protein = (EditText) itemView.findViewById(R.id.protein);
            this.fat = (EditText) itemView.findViewById(R.id.fat);
            this.time = (EditText) itemView.findViewById(R.id.time);
            this.foodname = (EditText) itemView.findViewById(R.id.foodname);



        }
    }
}
