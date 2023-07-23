package com.example.mazdoorapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mazdoorapp.R;

import java.util.ArrayList;

public class ProviderTypesAdapter extends RecyclerView.Adapter<ProviderTypesAdapter.MyviewHolder> {
    ArrayList<ModelProviderType> list;
    Context context;

    public ProviderTypesAdapter(ArrayList<ModelProviderType> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_providers, parent, false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {

        ModelProviderType data = list.get(position);

        holder.providerImage.setImageResource(data.getImage());
        holder.providerType.setText(data.getType());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        ImageView providerImage;
        TextView providerType;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            providerImage = itemView.findViewById(R.id.imgProvider);
            providerType = itemView.findViewById(R.id.txtProvider);
        }
    }
}
