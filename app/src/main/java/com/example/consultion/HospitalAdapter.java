package com.example.consultion;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class HospitalAdapter extends  RecyclerView.Adapter<HospitalAdapter.ViewHolder> {
    public Context context;
    public ArrayList<Hospital> hospitals;

    public HospitalAdapter(Context context, ArrayList<Hospital> hospitals) {
        this.context = context;
        this.hospitals = hospitals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.hospitals,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hospital hospital = hospitals.get(position);
        holder.hospitalName.setText(hospital.name);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("https://maps.google.com/?q="+hospitals.get(position).getLatitude()+","+hospitals.get(position).getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hospitals.size();
    }

    public static class ViewHolder  extends  RecyclerView.ViewHolder {
        TextView hospitalName;
        CardView cardView ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            cardView = itemView.findViewById(R.id.hospitalCardView);
        }
    }
}
