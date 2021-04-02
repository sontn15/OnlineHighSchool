package com.sh.onlinehighschool.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.adapter.CommentAdapter;
import com.sh.onlinehighschool.model.Comment;
import com.sh.onlinehighschool.model.User;
import com.sh.onlinehighschool.model.YoutubeVideo;
import com.sh.onlinehighschool.utils.Pref;
import com.sh.onlinehighschool.utils.StringFormatUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewVideoActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog progressDialog;
    private EditText edtComment;
    private Button btnComment;
    private CardView cvViewVideo;
    private Toolbar toolbar;

    @BindView(R.id.textViewTitle)
    TextView textWaveTitle;
    @BindView(R.id.btnPlay)
    ImageView playButton;
    @BindView(R.id.imageViewItem)
    ImageView imageViewItems;
    @BindView(R.id.youtube_view)
    YouTubePlayerView youTubePlayerView;

    private RecyclerView rcvComment;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;

    private User userModel;
    private YoutubeVideo youtubeVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);
        initData();
        initViews();
        initToolbar();
        initAdapter();
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Xem video bài giảng");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    private void initData() {
        Pref pref = new Pref(this);
        userModel = pref.getUser(Pref.USER);

        Intent mIntent = getIntent();
        Bundle mBundle = mIntent.getExtras();
        youtubeVideo = mBundle.getParcelable("VIDEO_DETAIL");
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        edtComment = this.findViewById(R.id.edtComment);
        btnComment = this.findViewById(R.id.btnComment);
        rcvComment = this.findViewById(R.id.rcvCommentViewVideo);
        cvViewVideo = this.findViewById(R.id.cvViewVideo);

        LinearLayoutManager layoutManagerHot = new LinearLayoutManager(this);
        layoutManagerHot.setOrientation(RecyclerView.VERTICAL);
        rcvComment.setLayoutManager(layoutManagerHot);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Vui lòng đợi...");
        progressDialog.setCanceledOnTouchOutside(false);

        btnComment.setOnClickListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ButterKnife.bind(this, cvViewVideo);
        ((Activity) cvViewVideo.getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        if (youtubeVideo.getTitle() != null) {
            textWaveTitle.setText("[" + youtubeVideo.getSubject() + "] - " + youtubeVideo.getTitle());
        }
        if (youtubeVideo.getImageUrl() != null) {
            Glide.with(cvViewVideo.getContext())
                    .load(youtubeVideo.getImageUrl()).
                    apply(new RequestOptions().override(width - 36, 200))
                    .into(imageViewItems);
        }
        imageViewItems.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);
        youTubePlayerView.setVisibility(View.GONE);
        playButton.setOnClickListener(view -> {
            imageViewItems.setVisibility(View.GONE);
            youTubePlayerView.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.GONE);
            youTubePlayerView.initialize(
                    initializedYouTubePlayer -> initializedYouTubePlayer.addListener(
                            new AbstractYouTubePlayerListener() {
                                @Override
                                public void onReady() {
                                    initializedYouTubePlayer.loadVideo(youtubeVideo.getVideoId(), 0);
                                }
                            }), true);
        });
    }

    private void initAdapter() {
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        rcvComment.setAdapter(commentAdapter);
        buildAllComment();
    }

    private void buildAllComment() {
        showProgressDialog();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference drinks = database.getReference().child("Comment");
        drinks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    commentList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Comment model = ds.getValue(Comment.class);
                        if (model != null && model.getYoutubeVideo().getVideoId().equalsIgnoreCase(youtubeVideo.getVideoId())) {
                            commentList.add(model);
                        }
                    }
                    commentAdapter.notifyDataSetChanged();
                    hiddenProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hiddenProgressDialog();
            }
        });
        hiddenProgressDialog();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnComment: {
                String content = edtComment.getText().toString();
                if (content.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    Comment model = new Comment();
                    model.setUser(userModel);
                    model.setContent(content.trim());
                    model.setYoutubeVideo(youtubeVideo);
                    model.setCreatedDate(StringFormatUtils.getCurrentDateStrFull());

                    FirebaseDatabase.getInstance().getReference().child("Comment")
                            .push().setValue(model);
                    buildAllComment();
                    clearData();
                    Toast.makeText(this, "Bình luận thành công", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void clearData() {
        edtComment.setText("");
    }


    private void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hiddenProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
