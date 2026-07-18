package com.example.smartroad;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MyReportAdapter extends RecyclerView.Adapter<MyReportAdapter.ViewHolder> {

    private List<Report> reportList;

    public MyReportAdapter(List<Report> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = reportList.get(position);

        holder.txtMyReportType.setText(report.hazardType);
        holder.txtMyReportDate.setText(report.date);

        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), HazardDetailsActivity.class);
            intent.putExtra("HAZARD_ID", report.hazardId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMyReportType;
        TextView txtMyReportDate;
        MaterialButton btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMyReportType = itemView.findViewById(R.id.txtMyReportType);
            txtMyReportDate = itemView.findViewById(R.id.txtMyReportDate);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
