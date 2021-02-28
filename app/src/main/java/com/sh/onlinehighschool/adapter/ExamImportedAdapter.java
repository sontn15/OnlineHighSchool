package com.sh.onlinehighschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.callback.OnRecyclerViewListener;
import com.sh.onlinehighschool.model.Exam;
import com.sh.onlinehighschool.utils.DBAssetHelper;

import java.util.ArrayList;
import java.util.Locale;


public class ExamImportedAdapter extends RecyclerView.Adapter<ExamImportedAdapter.ItemHolder> {

    private ArrayList<Exam> exams;
    private DBAssetHelper dbAssetHelper;
    private OnRecyclerViewListener listener;
    private boolean isOneSubject;

    public ExamImportedAdapter(Context context, boolean isOneSubject, OnRecyclerViewListener listener) {
        this.exams = new ArrayList<>();
        dbAssetHelper = new DBAssetHelper(context);
        this.isOneSubject = isOneSubject;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_exam_imported, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Exam exam = exams.get(position);
        String name = exam.getName();
        if (!isOneSubject){
            name = name + "\n" + dbAssetHelper.subject(exam.getSubjectID()).getName();
        }
        holder.tvName.setText(name);
        holder.tvNumber.setText(String.valueOf(position + 1));
        holder.tvQuestions.setText(String.format(Locale.getDefault(), "%d câu", exam.getQues()));
        holder.tvTime.setText(String.format(Locale.getDefault(), "%d phút", exam.getTime()));
    }

    @Override
    public int getItemCount() {
        return exams == null ? 0 : exams.size();
    }

    public void notifyItem(Exam exam){
        for (int i = 0; i < exams.size(); i++){
            if (exams.get(i).getId() == exam.getId()){
                exams.set(i, exam);
                notifyItemChanged(i);
            }
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvNumber;
        private TextView tvName;
        private TextView tvQuestions;
        private TextView tvTime;

        ItemHolder(View view) {
            super(view);
            tvNumber = view.findViewById(R.id.tv_number);
            tvName = view.findViewById(R.id.tv_name);
            tvQuestions = view.findViewById(R.id.tv_question);
            tvTime = view.findViewById(R.id.tv_time);
            ImageButton btDelete = view.findViewById(R.id.bt_delete);
            btDelete.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemChange(v, getAdapterPosition());
        }
    }

    public void add(Exam exam){
        exams.add(exam);
        notifyItemInserted(exams.size() - 1);
    }

    public void addAll(ArrayList<Exam> exams){
        for (Exam exam: exams){
            add(exam);
        }
    }

    public void remove(Exam exam){
        int position = exams.indexOf(exam);
        if (position > -1){
            exams.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Exam getItem(int position){
        return exams.get(position);
    }
}
