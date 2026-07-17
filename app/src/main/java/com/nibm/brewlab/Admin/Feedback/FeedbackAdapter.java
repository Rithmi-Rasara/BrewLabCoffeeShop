package com.nibm.brewlab.Admin.Feedback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nibm.brewlab.R;

import java.util.ArrayList;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    ArrayList<Feedback> list;

    public FeedbackAdapter(ArrayList<Feedback> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Feedback feedback = list.get(position);

        holder.txtName.setText(feedback.getCustomerName());
        holder.txtMessage.setText(feedback.getMessage());
        holder.ratingBar.setRating(feedback.getRating());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtName,txtMessage;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName=itemView.findViewById(R.id.txtCustomerName);
            txtMessage=itemView.findViewById(R.id.txtMessage);
            ratingBar=itemView.findViewById(R.id.ratingBar);
        }
    }
}