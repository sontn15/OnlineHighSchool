package com.sh.onlinehighschool.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.adapter.TimeTableStudentAdapter;
import com.sh.onlinehighschool.model.TimeTable;
import com.sh.onlinehighschool.utils.Pref;

import java.util.ArrayList;
import java.util.List;

public class TimeTableActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rcvTimeTables;
    private TimeTableStudentAdapter adapter;
    private List<TimeTable> listTimeTables;

    private Pref pref;
    private int khoi;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_student);

        pref = new Pref(this);
        khoi = pref.getDataInt(Pref.KHOI);

        initWidgets();
        initToolbar();
        initAdapter();
        buildAllTimeTable();
    }

    private void buildAllTimeTable() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("thoikhoabieu").child(String.valueOf(khoi));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TimeTable> response = new ArrayList<>();
                for (DataSnapshot dataSnap : snapshot.getChildren()) {
                    TimeTable timeTable = dataSnap.getValue(TimeTable.class);
                    if (timeTable != null) {
                        response.add(timeTable);
                    }
                }
                if (response.size() > 0) {
                    listTimeTables.clear();
                    listTimeTables.addAll(response);
                    adapter.setItems(listTimeTables);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initAdapter() {
        listTimeTables = new ArrayList<>();
        adapter = new TimeTableStudentAdapter(this, listTimeTables);
        rcvTimeTables.setAdapter(adapter);
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Thời khóa biểu khối " + khoi);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    private void initWidgets() {
        rcvTimeTables = this.findViewById(R.id.rcvTimeTable);
        toolbar = this.findViewById(R.id.toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rcvTimeTables.setLayoutManager(layoutManager);
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
