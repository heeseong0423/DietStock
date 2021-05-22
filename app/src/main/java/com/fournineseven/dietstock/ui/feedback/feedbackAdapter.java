package com.fournineseven.dietstock.ui.feedback;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

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

    public feedbackAdapter(Context context) {this.context = context;
    }

    @NonNull

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.feedback_list, parent, false);
        return new feedbackAdapter.CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull feedbackAdapter.CustomViewHolder holder, int position) {
        feedback_data item = items.get(position);
        holder.setItem(item);
    }

    public void addItem(feedback_data item){items.add(item);}
    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }

    public void setEmpty(){items.clear();}


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
            this.time = (EditText) itemView.findViewById(R.id.time);
            this.foodname = (EditText) itemView.findViewById(R.id.foodname);



        }

        public void setItem(feedback_data item){
            Glide.with(context).load(TaskServer.base_url+item.getFood_image()).error(R.drawable.hindoongi)
                    .placeholder(R.drawable.hindoongi).override(40, 30).into(food_image);
            time.setText(item.getTime());
            foodname.setText(item.getFoodname());
        }
    }
}
