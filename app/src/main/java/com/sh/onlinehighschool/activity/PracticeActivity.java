package com.sh.onlinehighschool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.adapter.ExamAdapter;
import com.sh.onlinehighschool.callback.OnRecyclerViewListener;
import com.sh.onlinehighschool.model.Exam;
import com.sh.onlinehighschool.model.Subject;
import com.sh.onlinehighschool.utils.DBAssetHelper;
import com.sh.onlinehighschool.utils.DBHelper;
import com.sh.onlinehighschool.views.AutoRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


public class PracticeActivity extends AppCompatActivity {

    private Bundle bundle;
    private Subject subject;

    private DBAssetHelper dbAssetHelper;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        dbAssetHelper = new DBAssetHelper(this);
        dbHelper = new DBHelper(this);
        if (bundle != null) {
            subject = bundle.getParcelable("SUBJECT");
        }
        setContentView(R.layout.activity_practice);
        initWidgets();
        initToolbar();
        initData();
    }

    private Toolbar toolbar;
    private AutoRecyclerView recyclerView;
    private TextView tvStatus;

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        tvStatus = findViewById(R.id.tv_status);
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle(subject.getName());
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    private ExamAdapter adapter;

    private void initData() {
        final ArrayList<Exam> exams = dbAssetHelper.exams(subject.getId());
        if (exams.size() == 0) {
            tvStatus.setVisibility(View.VISIBLE);
        } else {
            tvStatus.setVisibility(View.GONE);
        }
        adapter = new ExamAdapter(new OnRecyclerViewListener() {
            @Override
            public void onItemChange(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(QuizActivity.TYPE, "practice");
                bundle.putParcelable(QuizActivity.EXAM, exams.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.scale_in, R.anim.no_animation);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.addAll(exams);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_practice, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_histories:
                showHistories();
                break;
        }
        return true;
    }

    private void showHistories() {
        Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("SUBJECT", subject);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.no_animation);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out_right);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Exam exam) {
        exam.setLastHistory(dbHelper.lastHistory(exam.getId()));
        exam.setHighScoreHistory(dbHelper.highScoreHistory(exam.getId()));
        adapter.notifyItem(exam);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStop() {
        if (bundle != null) {
            bundle.clear();
        }
        super.onStop();
    }
}
