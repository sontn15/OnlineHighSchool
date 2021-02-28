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


public class YearAdapter extends RecyclerView.Adapter<YearAdapter.ItemHolder> {

    private String currentYear;
    private ArrayList<String> years;

    public YearAdapter(String currentYear, ArrayList<String> years) {
        this.currentYear = currentYear;
        this.years = years;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_year, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        final String item = years.get(position);
        holder.tvYear.setText(item);
        if (item.equals(currentYear)){
            holder.layoutItem.setBackgroundResource(R.drawable.bg_border_all_corner_20_blue);
            holder.tvYear.setTextColor(Color.WHITE);
        } else {
            holder.layoutItem.setBackgroundResource(R.drawable.bg_border_all_corner_20_transparent);
            holder.tvYear.setTextColor(Color.BLACK);
        }
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(getItemPosition(currentYear));
                currentYear = item;
                notifyItemChanged(getItemPosition(item));
            }
        });
    }

    @Override
    public int getItemCount() {
        return years.size();
    }

    private int getItemPosition(String year){
        int position = 0;
        for (int i = 0; i < years.size(); i++){
            if (years.get(i).equals(year)){
                position = i;
            }
        }
        return position;
    }

    public String getCurrentYear(){
        return currentYear;
    }

    class ItemHolder extends RecyclerView.ViewHolder{

        private RelativeLayout layoutItem;
        private TextView tvYear;
        ItemHolder(View view) {
            super(view);
            layoutItem = view.findViewById(R.id.layout_item);
            tvYear = view.findViewById(R.id.tv_year);
        }
    }
}
