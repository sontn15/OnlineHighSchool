package com.sh.onlinehighschool.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.adapter.YoutubeRecyclerAdapter;
import com.sh.onlinehighschool.model.Subject;
import com.sh.onlinehighschool.model.YoutubeVideo;
import com.sh.onlinehighschool.utils.Pref;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity {

    @BindView(R.id.recyclerViewFeed)
    RecyclerView recyclerViewFeed;
    YoutubeRecyclerAdapter mRecyclerAdapter;

    private Toolbar toolbar;

    private Pref pref;
    private String userId;

    private Bundle bundle;
    private Subject subject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            subject = bundle.getParcelable("SUBJECT_SELECTED");
        }
        initWidgets();
        initToolbar();
        initAdapter();
        buildAllYoutubes();
    }

    private void initAdapter() {
        ButterKnife.bind(this);
        // prepare data for list
        List<YoutubeVideo> youtubeVideos = new ArrayList<>();
        mRecyclerAdapter = new YoutubeRecyclerAdapter(youtubeVideos);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewFeed.setLayoutManager(mLayoutManager);
        recyclerViewFeed.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFeed.setAdapter(mRecyclerAdapter);
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
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
                        if (subject.getName().equalsIgnoreCase(video.getSubject())
                                && pref.getDataInt(Pref.KHOI) == video.getKhoi()) {
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
}
