package com.sh.onlinehighschool.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.callback.OnRecyclerViewListener;
import com.sh.onlinehighschool.model.History;
import com.sh.onlinehighschool.utils.DBAssetHelper;
import com.sh.onlinehighschool.utils.QuizHelper;

import java.util.ArrayList;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemHolder> {

    private boolean isOneSubject;
    private DBAssetHelper dbAssetHelper;
    private ArrayList<History> histories;
    private OnRecyclerViewListener listener;

    public HistoryAdapter(Context context, boolean isOneSubject, OnRecyclerViewListener listener) {
        dbAssetHelper = new DBAssetHelper(context);
        this.isOneSubject = isOneSubject;
        this.histories = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_history, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        History history = histories.get(position);
        holder.tvNumber.setText(String.valueOf(position + 1));
        String name =  history.getName();
        if (!isOneSubject){
            name = dbAssetHelper.subject(history.getSubjectID()).getName() + "\n" + name;
        }
        holder.tvName.setText(name);
        holder.tvSubmitted.setText(String.format("Ngày thi: %s", QuizHelper.unixTimeToDate(history.getSubmitted())));
        String textScore = "Điểm thi: <font color=\"red\">" + QuizHelper.getStringScore(history.getScore()) + "</font>";
        holder.tvScore.setText(Html.fromHtml(textScore));
    }


    @Override
    public int getItemCount() {
        return histories == null ? 0 : histories.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder{

        private TextView tvNumber;
        private TextView tvName;
        private TextView tvScore;
        private TextView tvSubmitted;
        ItemHolder(View view) {
            super(view);
            tvNumber = view.findViewById(R.id.tv_number);
            tvName = view.findViewById(R.id.tv_name);
            tvScore = view.findViewById(R.id.tv_score);
            tvSubmitted = view.findViewById(R.id.tv_submitted);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemChange(v, getAdapterPosition());
                }
            });
        }
    }

    private void add(History history){
        histories.add(history);
        notifyItemInserted(histories.size() - 1);
    }

    public void addAll(ArrayList<History> histories){
        for (History history: histories){
            add(history);
        }
    }

    public History getItem(int position){
        return histories.get(position);
    }
}
