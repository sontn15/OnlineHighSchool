package com.sh.onlinehighschool.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.callback.OnRecyclerViewListener;
import com.sh.onlinehighschool.model.Question;

import java.util.ArrayList;


public class NumberQuestionAdapter extends RecyclerView.Adapter<NumberQuestionAdapter.ItemHolder> {

    private int status;
    private ArrayList<Question> questions;
    private OnRecyclerViewListener listener;

    public NumberQuestionAdapter(int status, ArrayList<Question> questions, OnRecyclerViewListener listener) {
        this.status = status;
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_number_question, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Question question = questions.get(position);
        holder.tvNumber.setText(String.valueOf(position + 1));
        if (question.getChoice() == null) {
            //Nếu chưa có câu trả lời -> cho giao diện mặc định
            holder.tvNumber.setBackgroundResource(R.drawable.bg_circle_normal);
            holder.tvNumber.setTextColor(Color.parseColor("#000000"));
        } else {
            holder.tvNumber.setTextColor(Color.parseColor("#FFFFFF"));
            if (status == 2) {
                //Nếu đang xem kết quả
                if (question.getChoice().equals(question.getResult().toUpperCase())) {
                    //Trả lời đúng -> cho màu xanh
                    holder.tvNumber.setBackgroundResource(R.drawable.bg_circle_blue);
                } else {
                    //Trả lời sai -> cho màu đỏ
                    holder.tvNumber.setBackgroundResource(R.drawable.bg_circle_red);
                }
            } else {
                holder.tvNumber.setBackgroundResource(R.drawable.bg_circle_blue);
            }
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tvNumber;

        ItemHolder(View view) {
            super(view);
            tvNumber = view.findViewById(R.id.tv_number);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemChange(view, getAdapterPosition());
                }
            });
        }
    }
}
