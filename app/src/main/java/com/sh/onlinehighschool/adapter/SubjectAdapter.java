package com.sh.onlinehighschool.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.callback.OnRecyclerViewListener;
import com.sh.onlinehighschool.model.Subject;
import com.sh.onlinehighschool.utils.UniversalImageLoader;

import java.util.ArrayList;


public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ItemHolder> {

    private OnRecyclerViewListener listener;
    private ArrayList<Subject> subjects;

    public SubjectAdapter(ArrayList<Subject> subjects, OnRecyclerViewListener listener) {
        this.listener = listener;
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_subject, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.tvSubject.setText(subject.getName());
        String ID = "ID: <font color=\"red\"><u>" + subject.getId() + "</u></font>";
        holder.tvID.setText(Html.fromHtml(ID));
        UniversalImageLoader.setImage(subject.getIcon(), holder.ivIcon, null);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        private ImageView ivIcon;
        private TextView tvID;
        private TextView tvSubject;
        ItemHolder(View view) {
            super(view);
            tvSubject = view.findViewById(R.id.tv_name);
            tvID = view.findViewById(R.id.tv_id);
            ivIcon = view.findViewById(R.id.iv_icon);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemChange(v, getAdapterPosition());
                }
            });
        }
    }
}
