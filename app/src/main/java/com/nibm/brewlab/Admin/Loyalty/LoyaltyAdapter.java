package com.nibm.brewlab.Admin.Loyalty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.brewlab.R;

import java.util.ArrayList;


public class LoyaltyAdapter extends RecyclerView.Adapter<LoyaltyAdapter.ViewHolder> {


    private Context context;
    private ArrayList<Loyalty> loyaltyList;
    private FirebaseFirestore db;


    public LoyaltyAdapter(Context context, ArrayList<Loyalty> loyaltyList) {

        this.context = context;
        this.loyaltyList = loyaltyList;
        db = FirebaseFirestore.getInstance();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_loyalty, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        Loyalty loyalty = loyaltyList.get(position);


        holder.txtName.setText(loyalty.getName());
        holder.txtEmail.setText(loyalty.getEmail());

        holder.txtPoints.setText("Points : " + loyalty.getPoints());

        holder.txtLevel.setText("Level : " + loyalty.getLevel());


        holder.btnUpdatePoints.setOnClickListener(v -> {

            View dialogView = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_update_points, null);

            EditText edtPoints = dialogView.findViewById(R.id.edtPoints);
            Button btnMinus = dialogView.findViewById(R.id.btnMinus);
            Button btnPlus = dialogView.findViewById(R.id.btnPlus);

            edtPoints.setText(String.valueOf(loyalty.getPoints()));

            btnPlus.setOnClickListener(view -> {
                int value = Integer.parseInt(edtPoints.getText().toString());
                value += 2;
                edtPoints.setText(String.valueOf(value));
            });

            btnMinus.setOnClickListener(view -> {
                int value = Integer.parseInt(edtPoints.getText().toString());

                if (value >= 2) {
                    value -= 2;
                } else {
                    value = 0;
                }

                edtPoints.setText(String.valueOf(value));
            });

            new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .setPositiveButton("Update", (dialog, which) -> {

                        int newPoints;

                        try {
                            newPoints = Integer.parseInt(
                                    edtPoints.getText().toString().trim()
                            );
                        } catch (NumberFormatException e) {
                            Toast.makeText(context,
                                    "Please enter valid points",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String newLevel = Loyalty.calculateLevel(newPoints);

                        db.collection("Loyalty")
                                .document(loyalty.getId())
                                .update(
                                        "points", newPoints,
                                        "level", newLevel
                                )
                                .addOnSuccessListener(unused -> {

                                    loyalty.setPoints(newPoints);
                                    loyalty.setLevel(newLevel);

                                    notifyItemChanged(holder.getAdapterPosition());

                                    Toast.makeText(context,
                                            "Points Updated",
                                            Toast.LENGTH_SHORT).show();
                                });

                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        });


    }


    @Override
    public int getItemCount() {

        return loyaltyList.size();

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView txtName;
        TextView txtEmail;
        TextView txtPoints;
        TextView txtLevel;

        Button btnUpdatePoints;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);


            txtName = itemView.findViewById(R.id.txtName);

            txtEmail = itemView.findViewById(R.id.txtEmail);

            txtPoints = itemView.findViewById(R.id.txtPoints);

            txtLevel = itemView.findViewById(R.id.txtLevel);


            btnUpdatePoints = itemView.findViewById(R.id.btnUpdatePoints);

        }
    }
}