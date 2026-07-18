package com.example.smartroad;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NearbyHazardAdapter
        extends RecyclerView.Adapter<NearbyHazardAdapter.ViewHolder> {

    private List<NearbyHazard> hazardList;

    public NearbyHazardAdapter(List<NearbyHazard> hazardList) {
        this.hazardList = hazardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_nearby_hazard,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        NearbyHazard hazard = hazardList.get(position);

        holder.txtHazard.setText(
                hazard.getHazardType());

        holder.txtDistance.setText(
                hazard.getDistance());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), HazardDetailsActivity.class);
            intent.putExtra("HAZARD_ID", hazard.getHazardId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hazardList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtHazard;
        TextView txtDistance;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtHazard =
                    itemView.findViewById(R.id.txtHazard);

            txtDistance =
                    itemView.findViewById(R.id.txtDistance);
        }
    }
}