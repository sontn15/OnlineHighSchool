package com.sh.onlinehighschool.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.adapter.TimeTableStudentAdapter;
import com.sh.onlinehighschool.model.TimeTable;
import com.sh.onlinehighschool.utils.FileUtils;
import com.sh.onlinehighschool.utils.IOHelper;
import com.sh.onlinehighschool.utils.Pref;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TimeTableTeacherActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView tvAttachFile;
    private ImageButton btNext;
    private ProgressBar progressBarNext;

    private RecyclerView rcvTimeTable;
    private TimeTableStudentAdapter adapter;
    private List<TimeTable> listTimeTables;

    private TextView tvStatus;
    private TextView tvAction;

    private Pref pref;
    private int khoi;
    private boolean isLoading = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_teacher);

        pref = new Pref(this);
        khoi = pref.getDataInt(Pref.KHOI);

        initView();
        initToolbar();
        setupFirebaseAuth();
        initAdapter();
    }

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                visibleViews(tvAction, tvStatus);
                Toast.makeText(this, getResources().getString(R.string.no_login), Toast.LENGTH_SHORT).show();
                tvAction.setOnClickListener(v -> {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.no_animation);
                });
            }
        };
    }

    private void initAdapter() {
        listTimeTables = new ArrayList<>();
        adapter = new TimeTableStudentAdapter(this, listTimeTables);
        rcvTimeTable.setAdapter(adapter);
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Quản lý thời khóa biểu khối " + khoi);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    private void initView() {
        progressBarNext = findViewById(R.id.progressbar_next);
        tvAttachFile = findViewById(R.id.tv_attach_file);
        btNext = findViewById(R.id.bt_next);

        rcvTimeTable = this.findViewById(R.id.rcvTimeTableTeacher);
        toolbar = this.findViewById(R.id.toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rcvTimeTable.setLayoutManager(layoutManager);

        tvAction = findViewById(R.id.tv_action);
        tvStatus = findViewById(R.id.tv_status);

        btNext.setOnClickListener(this);
        tvAttachFile.setOnClickListener(this);
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
            case R.id.bt_next:
                initNext();
                break;
            case R.id.tv_attach_file:
                attachFile();
                break;
        }
    }

    private void attachFile() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new BaseMultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        super.onPermissionsChecked(report);
                        Intent intent = new Intent();
                        intent.setType("*/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Chọn file"), 2);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        super.onPermissionRationaleShouldBeShown(permissions, token);
                    }
                }).check();
    }

    private Uri uriPath;
    private String filePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            assert data != null;
            if (data.getData() != null) {
                uriPath = data.getData();
                if (uriPath != null) {
                    filePath = FileUtils.getPath(TimeTableTeacherActivity.this, uriPath);
                    tvAttachFile.setText(filePath);
                }
            }
        }
    }

    private void initNext() {
        if (uriPath != null) {
            if (filePath != null && filePath.endsWith(".json")) {
                readJsonFile(filePath);
            } else {
                Toast.makeText(this, "File đính kèm không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bạn chưa đính kèm file thời khóa biểu", Toast.LENGTH_SHORT).show();
        }
    }

    private void readJsonFile(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            String jString = Charset.defaultCharset().decode(bb).toString();
            listTimeTables = IOHelper.timeTables(jString);
            if (listTimeTables.size() > 0) {
                onLoading();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("upload");
                //Tên file tải lên
                final String fileName = String.valueOf(System.currentTimeMillis());
                String pathChild = mAuth.getUid() + "/" + fileName + ".json";
                storageRef.child(pathChild).putFile(uriPath)
                        .addOnSuccessListener(taskSnapshot -> {
                            Toast.makeText(this, "Upload file successfully!", Toast.LENGTH_SHORT).show();
                            createDatabase();
                            onUploadComplete();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Upload file lỗi", Toast.LENGTH_SHORT).show();
                            onUploadFalse();
                        });
            } else {
                Toast.makeText(this, "File đính kèm không hợp lệ.", Toast.LENGTH_SHORT).show();
                onUploadFalse();
            }
        } catch (Exception e) {
            Toast.makeText(this, "File đính kèm không hợp lệ.", Toast.LENGTH_SHORT).show();
            onUploadFalse();
        }
    }

    private DatabaseReference myRef;

    private void createDatabase() {
        myRef = FirebaseDatabase.getInstance().getReference("thoikhoabieu").child(String.valueOf(khoi));
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < listTimeTables.size(); i++) {
                    TimeTable timeTable = listTimeTables.get(i);
                    myRef.child(String.valueOf(timeTable.getId())).setValue(timeTable);
                }
                buildAllTimeTable();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        if (!isLoading) {
            super.onBackPressed();
        }
    }

    private void onUploadFalse() {
        isLoading = false;
        enableViews(btNext, tvAttachFile);
        visibleViews(btNext);
        goneViews(progressBarNext);
    }

    private void onLoading() {
        isLoading = true;
        visibleViews(progressBarNext);
        goneViews(btNext);
        disableViews(btNext, tvAttachFile);
    }

    private void onUploadComplete() {
        isLoading = false;
        visibleViews(btNext);
        goneViews(progressBarNext);
    }

    private void goneViews(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    private void visibleViews(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void enableViews(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setEnabled(true);
                    view.setClickable(true);
                }
            }
        }
    }

    private void disableViews(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setEnabled(false);
                    view.setClickable(false);
                }
            }
        }
    }

    private void setError(TextInputLayout input, String error) {
        if (error != null) {
            input.setErrorEnabled(true);
            input.setError(error);
        } else {
            input.setErrorEnabled(false);
        }
    }

}
