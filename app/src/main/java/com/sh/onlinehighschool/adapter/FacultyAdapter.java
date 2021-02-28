package com.sh.onlinehighschool.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.onlinehighschool.R;

import java.util.ArrayList;


public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.ItemHolder> {

    private String currentFaculty;
    private ArrayList<String> faculties;

    public FacultyAdapter(String currentFaculty, ArrayList<String> faculties) {
        this.currentFaculty = currentFaculty;
        this.faculties = faculties;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_faculty, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        final String item = faculties.get(position);
        holder.tvFaculty.setText(item);
        if (item.equals(currentFaculty)){
//            holder.tvFaculty.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
////            holder.tvFaculty.setTextColor(Color.parseColor("#FA7268"));
            holder.layoutItem.setBackgroundResource(R.drawable.bg_border_all_corner_20_blue);
            holder.tvFaculty.setTextColor(Color.WHITE);
        } else {
//            holder.tvFaculty.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
//            holder.tvFaculty.setTextColor(Color.parseColor("#303030"));
            holder.layoutItem.setBackgroundResource(R.drawable.bg_border_all_corner_20_transparent);
            holder.tvFaculty.setTextColor(Color.BLACK);
        }
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(getItemPosition(currentFaculty));
                currentFaculty = item;
                notifyItemChanged(getItemPosition(item));
            }
        });
    }

    @Override
    public int getItemCount() {
        return faculties.size();
    }

    private int getItemPosition(String year){
        int position = 0;
        for (int i = 0; i < faculties.size(); i++){
            if (faculties.get(i).equals(year)){
                position = i;
            }
        }
        return position;
    }

    public String getCurrentFaculty(){
        return currentFaculty;
    }

    class ItemHolder extends RecyclerView.ViewHolder{

        private RelativeLayout layoutItem;
        private TextView tvFaculty;
        ItemHolder(View view) {
            super(view);
            layoutItem = view.findViewById(R.id.layout_item);
            tvFaculty = view.findViewById(R.id.tv_faculty);
        }
    }
}
