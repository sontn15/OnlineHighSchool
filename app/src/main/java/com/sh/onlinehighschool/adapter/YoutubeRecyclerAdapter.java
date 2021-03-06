package com.sh.onlinehighschool.adapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.callback.OnClickVideoListener;
import com.sh.onlinehighschool.model.YoutubeVideo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YoutubeRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    public static final int VIEW_TYPE_NORMAL = 1;
    private List<YoutubeVideo> mYoutubeVideos;
    private final OnClickVideoListener listener;

    private DisplayMetrics displayMetrics = new DisplayMetrics();


    public YoutubeRecyclerAdapter(List<YoutubeVideo> youtubeVideos, OnClickVideoListener listener) {
        mYoutubeVideos = youtubeVideos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_youtube_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (mYoutubeVideos != null && mYoutubeVideos.size() > 0) {
            return mYoutubeVideos.size();
        } else {
            return 1;
        }
    }

    public void setItems(List<YoutubeVideo> youtubeVideos) {
        mYoutubeVideos = youtubeVideos;
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.textViewTitle)
        TextView textWaveTitle;
        @BindView(R.id.imageViewItem)
        ImageView imageViewItems;
        @BindView(R.id.youtube_view)
        YouTubePlayerView youTubePlayerView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
        }

        public void onBind(int position) {
            super.onBind(position);
            if (mYoutubeVideos.size() != 0) {
                final YoutubeVideo mYoutubeVideo = mYoutubeVideos.get(position);
                ((Activity) itemView.getContext()).getWindowManager()
                        .getDefaultDisplay()
                        .getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                if (mYoutubeVideo.getTitle() != null) {
                    textWaveTitle.setText("[" + mYoutubeVideo.getSubject() + "] - " + mYoutubeVideo.getTitle());
                }
                if (mYoutubeVideo.getImageUrl() != null) {
                    Glide.with(itemView.getContext())
                            .load(mYoutubeVideo.getImageUrl()).
                            apply(new RequestOptions().override(width - 36, 200))
                            .into(imageViewItems);
                }
                imageViewItems.setVisibility(View.VISIBLE);
                youTubePlayerView.setVisibility(View.GONE);

                itemView.setOnClickListener(v -> listener.onClickView(mYoutubeVideos.get(position)));
            }
        }
    }


}