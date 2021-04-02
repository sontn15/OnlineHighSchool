package com.sh.onlinehighschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.model.Comment;

import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ItemCommentViewHolder> {
    private final Context mContext;
    private final List<Comment> commentModels;

    public CommentAdapter(Context mContext, List<Comment> commentModels) {
        this.mContext = mContext;
        this.commentModels = commentModels;
    }

    @NonNull
    @Override
    public ItemCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        return new ItemCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCommentViewHolder holder, int position) {
        Comment model = commentModels.get(position);
        holder.tvName.setText(model.getUser().getName());
        holder.tvCreatedDate.setText(model.getCreatedDate());
        holder.tvContent.setText(model.getContent());
    }

    @Override
    public int getItemCount() {
        if (commentModels != null) {
            return commentModels.size();
        } else {
            return 0;
        }
    }

    public static class ItemCommentViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvName, tvContent, tvCreatedDate;

        public ItemCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameCustomerCommentItem);
            tvContent = itemView.findViewById(R.id.tvContentCommentItem);
            tvCreatedDate = itemView.findViewById(R.id.tvTimeCommentItem);
        }
    }
}