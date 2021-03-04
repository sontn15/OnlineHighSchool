package com.sh.onlinehighschool.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.adapter.YoutubeRecyclerAdapter;
import com.sh.onlinehighschool.callback.OnUploadVideoDialogListener;
import com.sh.onlinehighschool.dialog.UploadVideoDialog;
import com.sh.onlinehighschool.model.YoutubeVideo;
import com.sh.onlinehighschool.utils.Pref;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadVideoActivity extends AppCompatActivity implements OnUploadVideoDialogListener {

    @BindView(R.id.recyclerUploadVideo)
    RecyclerView recyclerViewFeed;
    YoutubeRecyclerAdapter mRecyclerAdapter;

    private Toolbar toolbar;
    private FloatingActionButton fabUploadVideo;

    private Pref pref;
    private List<YoutubeVideo> youtubeVideos;

    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);
        initWidgets();
        initToolbar();
        initAdapter();
        buildAllYoutubes();
    }

    private void initAdapter() {
        ButterKnife.bind(this);
        // prepare data for list
        youtubeVideos = new ArrayList<>();
        mRecyclerAdapter = new YoutubeRecyclerAdapter(youtubeVideos);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(UploadVideoActivity.this);
        recyclerViewFeed.setLayoutManager(mLayoutManager);
        recyclerViewFeed.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFeed.setAdapter(mRecyclerAdapter);
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        fabUploadVideo = findViewById(R.id.fabUploadVideo);

        fabUploadVideo.setOnClickListener(v -> {
            //Hiển thị dialog xóa
            UploadVideoDialog dialog = UploadVideoDialog.newInstance("TAG", "Upload video bài giảng mới", "MESSAGE");
            dialog.show(getSupportFragmentManager(), dialog.getTag());
        });
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Upload video bài giảng");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    private void buildAllYoutubes() {
        pref = new Pref(this);
        userId = pref.getData(Pref.UID);

        ArrayList<YoutubeVideo> videoArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("videos");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<YoutubeVideo> response = new ArrayList<>();
                for (DataSnapshot dataSnap : snapshot.getChildren()) {
                    YoutubeVideo video = dataSnap.getValue(YoutubeVideo.class);
                    if (video != null) {
                        if (userId.equalsIgnoreCase(video.getUserId()) && pref.getDataInt(Pref.KHOI) == video.getKhoi()) {
                            response.add(video);
                        }
                    }
                }
                if (response.size() > 0) {
                    videoArrayList.clear();
                    videoArrayList.addAll(response);
                    mRecyclerAdapter.setItems(videoArrayList);
                    mRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    public void onClickConfirmUploadVideo(YoutubeVideo model) {
        model.setUserId(userId);
        model.setId((youtubeVideos.size() + 1));
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("videos");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDatabase.push().setValue(model);
                buildAllYoutubes();
                Toast.makeText(UploadVideoActivity.this, "Thêm mới thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UploadVideoActivity.this, "Thêm mới thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClickCancelUploadVideo(YoutubeVideo model) {

    }
}
