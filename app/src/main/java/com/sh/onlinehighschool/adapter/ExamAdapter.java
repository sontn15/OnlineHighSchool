package com.sh.onlinehighschool.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.callback.OnRecyclerViewListener;
import com.sh.onlinehighschool.model.Exam;
import com.sh.onlinehighschool.utils.QuizHelper;

import java.util.ArrayList;
import java.util.Locale;


public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ItemHolder> {

    private ArrayList<Exam> exams;
    private OnRecyclerViewListener listener;

    public ExamAdapter(OnRecyclerViewListener listener) {
        this.exams = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_exam, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Exam exam = exams.get(position);
        holder.tvNumber.setText(String.valueOf(position + 1));
        holder.tvName.setText(exam.getName());
        holder.tvQuestions.setText(String.format(Locale.getDefault(), "%d câu", exam.getQues()));
        holder.tvTime.setText(String.format(Locale.getDefault(), "%d phút", exam.getTime()));
        if (exam.getLastHistory() != null && exam.getLastHistory().getId() != 0) {
            holder.tvHistory.setVisibility(View.VISIBLE);
            holder.tvHistory.setText(Html.fromHtml(
                    "Lần luyện tập gần nhất: " +
                            QuizHelper.unixTimeToDate(exam.getLastHistory().getSubmitted()) +
                            "<br>Điểm cao nhất: <font color=\"red\">" +
                            QuizHelper.getStringScore(exam.getHighScoreHistory().getScore()) +
                            "</font>"
            ));
        } else {
            holder.tvHistory.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return exams == null ? 0 : exams.size();
    }

    public void notifyItem(Exam exam) {
        for (int i = 0; i < exams.size(); i++) {
            if (exams.get(i).getId() == exam.getId()) {
                exams.set(i, exam);
                notifyItemChanged(i);
            }
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tvNumber;
        private TextView tvName;
        private TextView tvQuestions;
        private TextView tvTime;
        private TextView tvHistory;

        ItemHolder(View view) {
            super(view);
            tvNumber = view.findViewById(R.id.tv_number);
            tvName = view.findViewById(R.id.tv_name);
            tvQuestions = view.findViewById(R.id.tv_question);
            tvTime = view.findViewById(R.id.tv_time);
            tvHistory = view.findViewById(R.id.tv_history);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemChange(v, getAdapterPosition());
                }
            });
        }
    }

    public void add(Exam exam) {
        exams.add(exam);
        notifyItemInserted(exams.size() - 1);
    }

    public void addAll(ArrayList<Exam> exams) {
        for (Exam exam : exams) {
            add(exam);
        }
    }

    public Exam getItem(int position) {
        return exams.get(position);
    }
}
