package com.sh.onlinehighschool.adapter;

import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.model.Score;
import com.sh.onlinehighschool.utils.QuizHelper;

import java.util.ArrayList;
import java.util.Locale;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ItemHolder> {

    private ArrayList<Score> scores;

    public ScoreAdapter(ArrayList<Score> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_score, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Score score = scores.get(position);
        holder.tvSubject.setText(String.format(Locale.getDefault(), "%d- %s",
                score.getSubjectID(), score.getSubjectName()));
        holder.tvViews.setText(String.valueOf(score.getViews()));
        String rowScore = "<font color=\"red\">" + QuizHelper.getStringScore(score.getAvgScore()) + "</font>" +
                " (" + QuizHelper.getStringScore(score.getMinScore()) + " - " +
                QuizHelper.getStringScore(score.getMaxScore()) + ")";
        holder.tvScore.setText(Html.fromHtml(rowScore));
        if (position %2 == 0){
            //Nếu dòng chẵn (VD: 0, 2,...) -> Cho màu nền trắng
            holder.layoutItem.setBackgroundColor(Color.WHITE);
        } else {
            //Nếu là các dòng lẻ (VD: 1, 3...) -> Cho màu xẫm hơn
            holder.layoutItem.setBackgroundColor(Color.parseColor("#DCDCDC"));
        }
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder{

        private LinearLayout layoutItem;
        private TextView tvSubject;
        private TextView tvViews;
        private TextView tvScore;
        ItemHolder(View view) {
            super(view);
            layoutItem = view.findViewById(R.id.layout_item);
            tvSubject = view.findViewById(R.id.tv_subject);
            tvViews = view.findViewById(R.id.tv_views);
            tvScore = view.findViewById(R.id.tv_score);
        }
    }
}
