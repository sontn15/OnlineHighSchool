package com.sh.onlinehighschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.model.TimeTable;

import java.util.List;

public class TimeTableStudentAdapter extends RecyclerView.Adapter<TimeTableStudentAdapter.TripViewHolder> {
    private final Context mContext;
    public List<TimeTable> listTimeTable;

    public TimeTableStudentAdapter(Context mContext, List<TimeTable> listTimeTable) {
        this.mContext = mContext;
        this.listTimeTable = listTimeTable;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_timetable, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        TimeTable timeTable = listTimeTable.get(position);

        holder.tvThu.setText(timeTable.getThu());

        holder.tvTiet1Sang.setText("Tiết 1: " + timeTable.getTiet1Sang());
        holder.tvTiet2Sang.setText("Tiết 2: " + timeTable.getTiet2Sang());
        holder.tvTiet3Sang.setText("Tiết 3: " + timeTable.getTiet3Sang());
        holder.tvTiet4Sang.setText("Tiết 4: " + timeTable.getTiet4Sang());
        holder.tvTiet5Sang.setText("Tiết 5: " + timeTable.getTiet5Sang());

        if (timeTable.getHocChieuKhong() == 1) {
            if (timeTable.getTiet1Chieu() != null && !timeTable.getTiet1Chieu().isEmpty()) {
                holder.tvTiet1Chieu.setText("Tiết 1: " + timeTable.getTiet1Chieu());
            } else {
                holder.tvTiet1Chieu.setVisibility(View.GONE);
            }

            if (timeTable.getTiet2Chieu() != null && !timeTable.getTiet2Chieu().isEmpty()) {
                holder.tvTiet2Chieu.setText("Tiết 2: " + timeTable.getTiet2Chieu());
            } else {
                holder.tvTiet2Chieu.setVisibility(View.GONE);
            }

            if (timeTable.getTiet3Chieu() != null && !timeTable.getTiet3Chieu().isEmpty()) {
                holder.tvTiet3Chieu.setText("Tiết 3: " + timeTable.getTiet3Chieu());
            } else {
                holder.tvTiet3Chieu.setVisibility(View.GONE);
            }

            if (timeTable.getTiet4Chieu() != null && !timeTable.getTiet4Chieu().isEmpty()) {
                holder.tvTiet4Chieu.setText("Tiết 4: " + timeTable.getTiet4Chieu());
            } else {
                holder.tvTiet4Chieu.setVisibility(View.GONE);
            }

            if (timeTable.getTiet5Chieu() != null && !timeTable.getTiet5Chieu().isEmpty()) {
                holder.tvTiet5Chieu.setText("Tiết 5: " + timeTable.getTiet5Chieu());
            } else {
                holder.tvTiet5Chieu.setVisibility(View.GONE);
            }

            holder.llChieu.setVisibility(View.VISIBLE);
        } else {
            holder.llChieu.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (listTimeTable != null) {
            return listTimeTable.size();
        } else {
            return 0;
        }
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvThu, tvTiet1Sang, tvTiet2Sang, tvTiet3Sang, tvTiet4Sang, tvTiet5Sang;
        protected TextView tvTiet1Chieu, tvTiet2Chieu, tvTiet3Chieu, tvTiet4Chieu, tvTiet5Chieu;
        protected TextView tvSang, tvChieu;
        protected LinearLayout llChieu;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvThu = itemView.findViewById(R.id.tvThuTimeTableItem);
            tvTiet1Sang = itemView.findViewById(R.id.tvTiet1SangTimeTableItem);
            tvTiet2Sang = itemView.findViewById(R.id.tvTiet2SangTimeTableItem);
            tvTiet3Sang = itemView.findViewById(R.id.tvTiet3SangTimeTableItem);
            tvTiet4Sang = itemView.findViewById(R.id.tvTiet4SangTimeTableItem);
            tvTiet5Sang = itemView.findViewById(R.id.tvTiet5SangTimeTableItem);

            tvTiet1Chieu = itemView.findViewById(R.id.tvTiet1ChieuTimeTableItem);
            tvTiet2Chieu = itemView.findViewById(R.id.tvTiet2ChieuTimeTableItem);
            tvTiet3Chieu = itemView.findViewById(R.id.tvTiet3ChieuTimeTableItem);
            tvTiet4Chieu = itemView.findViewById(R.id.tvTiet4ChieuTimeTableItem);
            tvTiet5Chieu = itemView.findViewById(R.id.tvTiet5ChieuTimeTableItem);

            tvSang = itemView.findViewById(R.id.tvSangTimeTableItem);
            tvChieu = itemView.findViewById(R.id.tvChieuTimeTableItem);

            llChieu = itemView.findViewById(R.id.llChieuTimeTableItem);
        }
    }

    public void setItems(List<TimeTable> timeTables) {
        listTimeTable = timeTables;
        notifyDataSetChanged();
    }
}
