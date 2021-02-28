package com.sh.onlinehighschool.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.callback.OnRecyclerViewListener;
import com.sh.onlinehighschool.model.Subject;

import java.util.ArrayList;
import java.util.Locale;


public class SubjectRowAdapter extends RecyclerView.Adapter<SubjectRowAdapter.ItemHolder> {

    private OnRecyclerViewListener listener;
    private ArrayList<Subject> subjects;

    public SubjectRowAdapter(ArrayList<Subject> subjects, OnRecyclerViewListener listener) {
        this.listener = listener;
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_subject_row, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.tvSubject.setText(String.format(Locale.getDefault(), "%d. %s",
                subject.getId(), subject.getName()));
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tvSubject;

        ItemHolder(View view) {
            super(view);
            tvSubject = view.findViewById(R.id.tv_name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemChange(v, getAdapterPosition());
                }
            });
        }
    }
}
