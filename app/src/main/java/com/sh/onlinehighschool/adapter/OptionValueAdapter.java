package com.sh.onlinehighschool.adapter;

import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.model.Question;
import com.sh.onlinehighschool.utils.QuizHelper;


public class OptionValueAdapter extends RecyclerView.Adapter<OptionValueAdapter.ItemHolder> {

    private int status;
    private Question question;

    OptionValueAdapter(int status, Question question) {
        this.status = status;
        this.question = question;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_option_value, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, final int position) {
        String value = "<b>" + QuizHelper.choice(position) + "</b>. " + question.getOptions().get(position);
        holder.tvOption.setText(Html.fromHtml(value));
        //TH cho hiển thị đáp án
        if (status == 2){
            //1. Tô màu đáp án đúng
            if (question.getResult().toUpperCase().equals(QuizHelper.choice(position))){
                holder.tvOption.setBackgroundResource(R.drawable.bg_option_value_blue);
                holder.tvOption.setTextColor(Color.WHITE);
            }

            //2. Tô màu phương án trả lời (nếu trả lời sai)
            if (question.getChoice() != null &&
                    !question.getChoice().equals(question.getResult().toUpperCase()) &&
                    question.getChoice().equals(QuizHelper.choice(position))){
                holder.tvOption.setBackgroundResource(R.drawable.bg_option_value_red);
                holder.tvOption.setTextColor(Color.WHITE);
            }
        } else {
            if (question.getChoice() != null && question.getChoice().equals(QuizHelper.choice(position))){
                //Nếu đã trả lời -> cho giao diện màu xanh da trời
                holder.tvOption.setBackgroundResource(R.drawable.bg_option_value_blue);
                holder.tvOption.setTextColor(Color.WHITE);
            } else {
                //Nếu chưa trả lời -> cho giao diện mặc định
                holder.tvOption.setBackgroundResource(R.drawable.bg_option_value_normal);
                holder.tvOption.setTextColor(Color.BLACK);
            }
            if (status == 0){
                //Nếu đang thi -> bắt sự kiện khi bấm vào lựa chọn
                holder.tvOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Cập nhật lại giao diện lựa chọn cũ (nếu có)
                        notifyItem(QuizHelper.optionPosition(question.getChoice()));
                        //Cập nhật lại giao diện cho lựa chọn mới
                        notifyItem(position);
                        //Cập nhật phương án trả lời
                        question.setChoice(QuizHelper.choice(position));
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return question.getOptions().size();
    }

    private void notifyItem(int position){
        if (position != -1){
            notifyItemChanged(position);
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder{

        private TextView tvOption;
        ItemHolder(View view) {
            super(view);
            tvOption = view.findViewById(R.id.tv_option);
        }
    }
}
