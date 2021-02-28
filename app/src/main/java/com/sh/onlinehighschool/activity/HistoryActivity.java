package com.sh.onlinehighschool.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.fragment.HistoryFragment;
import com.sh.onlinehighschool.model.Subject;


public class HistoryActivity extends AppCompatActivity {

    private Bundle bundle;
    private Subject subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        setContentView(R.layout.activity_history);
        initWidgets();
        if (bundle != null) {
            subject = bundle.getParcelable("SUBJECT");
        }
        initToolbar();
        initFragment();
    }

    private void initFragment() {
        HistoryFragment fragment = HistoryFragment.newInstance(subject.getId());
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_history, fragment).commit();
    }

    private Toolbar toolbar;

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        String title = "Lịch sử thi";
        if (subject != null) {
            title = title + ": " + subject.getName();
        }
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out_right);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bundle != null) {
            bundle.clear();
        }
    }
}
