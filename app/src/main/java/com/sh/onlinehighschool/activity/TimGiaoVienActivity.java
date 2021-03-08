package com.sh.onlinehighschool.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.sh.onlinehighschool.callback.OnSubjectListener;
import com.sh.onlinehighschool.dialog.SubjectDialog;
import com.sh.onlinehighschool.model.Question;
import com.sh.onlinehighschool.model.Subject;
import com.sh.onlinehighschool.utils.DBAssetHelper;
import com.sh.onlinehighschool.utils.FileUtil;
import com.sh.onlinehighschool.utils.IOHelper;
import com.sh.onlinehighschool.utils.InputHelper;
import com.sh.onlinehighschool.utils.Pref;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class TimGiaoVienActivity extends AppCompatActivity implements View.OnClickListener, OnSubjectListener {

    private boolean isLoading;
    private int subjectID;
    private int time;
    private int madethi;
    private String examName;
    private String idGV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timgiaovien);
        initWidgets();
        initToolbar();
        setupFirebaseAuth();
        if (savedInstanceState == null) {
            isLoading = false;
            subjectID = time = 0;
            madethi = 0;
            examName = idGV = null;
        } else {
            isLoading = savedInstanceState.getBoolean("loading");
            subjectID = savedInstanceState.getInt("subject");
            time = savedInstanceState.getInt("time");
            examName = savedInstanceState.getString("exam");
            madethi = savedInstanceState.getInt("madethi");
            idGV = savedInstanceState.getString("idgv");
        }
        initInputSubjectID();
        initInputTime();
        initInputExamID();
        inputmadt();
        docIDGV();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("loading", isLoading);
        outState.putInt("subject", subjectID);
        outState.putInt("time", time);
        outState.putString("exam", examName);
        outState.putInt("madethi", madethi);
        outState.putString("magiaovien", idGV);
    }

    private Toolbar toolbar;
    private RelativeLayout layoutMain;
    private TextInputLayout inputSubjectID;
    private TextInputLayout inputTime;
    private TextInputLayout inputExamName;
    private TextInputLayout magv;
    private TextInputLayout mamh;
    private ImageButton btNext;
    private ProgressBar progressBarNext;
    private TextView tvAttachFile;
    private TextView tvExamInfo;
    private RelativeLayout layoutStatus;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private TextView tvAction;

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        layoutMain = findViewById(R.id.layout_main);
        inputSubjectID = findViewById(R.id.input_subject_id);

        mamh = findViewById(R.id.input_exam_madt);
        //magv = findViewById(R.id.input_exam_magv);
        inputTime = findViewById(R.id.input_time);
        inputExamName = findViewById(R.id.input_exam_name);
        progressBarNext = findViewById(R.id.progressbar_next);
        tvAttachFile = findViewById(R.id.tv_attach_file);
        tvAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachFile();
            }
        });
        tvExamInfo = findViewById(R.id.tv_exam_info);
        btNext = findViewById(R.id.bt_next);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initNext();
            }
        });
        layoutStatus = findViewById(R.id.layout_status);
        progressBar = findViewById(R.id.progressbar);
        tvStatus = findViewById(R.id.tv_status);
        tvAction = findViewById(R.id.tv_action);
    }

    private void initInputSubjectID() {
        if (subjectID != 0) {
            inputSubjectID.getEditText().setText(String.valueOf(subjectID));
        }
        inputSubjectID.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubjectDialog dialog = new SubjectDialog();
                dialog.show(getSupportFragmentManager(), dialog.getTag());
            }
        });
        inputSubjectID.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputSubjectID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    subjectID = Integer.parseInt(editable.toString());
                } catch (Exception e) {
                    subjectID = 0;
                }
            }
        });
    }

    //@Override
    public void onSubjectChange(Subject subject) {
        inputSubjectID.getEditText().setText(String.valueOf(subject.getId()));
    }

    private void initInputTime() {
        if (time != 0) {
            inputTime.getEditText().setText(String.valueOf(time));
        }
        inputTime.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputTime.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    time = Integer.parseInt(editable.toString());
                } catch (Exception e) {
                    time = 0;
                }
            }
        });
    }

    private void initInputExamID() {
        inputExamName.getEditText().setText(examName);
        inputExamName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputExamName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                examName = editable.toString();
            }
        });
    }

    private void inputmadt() {
        if (madethi != 0) {
            mamh.getEditText().setText(String.valueOf(madethi));
        }
        //mamh.getEditText().setText(String.valueOf(madethi));
        mamh.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mamh.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    madethi = Integer.parseInt(s.toString());
                } catch (Exception e) {
                    madethi = 0;
                }

            }
        });
    }

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
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data.getData() != null) {
            uriPath = data.getData();
            if (uriPath != null) {
                if (Build.VERSION.SDK_INT >= 26) {
                    File file = new File(uriPath.getPath());
                    final String[] split = file.getPath().split(":");
                    filePath = split[1];
                } else {
                    filePath = FileUtil.getPath(this, uriPath);
                }
                tvAttachFile.setText(filePath);
            }
        }
    }

    private void initNext() {
        String emptyTime = InputHelper.emptyData(time, "thời gian thi");
        String emptyExamName = InputHelper.emptyData(examName, "tên đề thi");
        String madt = InputHelper.emptyData(madethi, "mã đề thi");
        setError(inputSubjectID, errSubjectID());
        setError(inputTime, emptyTime);
        setError(inputExamName, emptyExamName);
        setError(mamh, madt);
        if (errSubjectID() == null && emptyTime == null && emptyExamName == null) {
            uploadFile();
        }
    }

    private String errSubjectID() {
        if (InputHelper.emptyData(subjectID, "mã môn học") == null) {
            DBAssetHelper dbAssetHelper = new DBAssetHelper(this);
            if (dbAssetHelper.subject(subjectID).getName() == null) {
                return "Mã môn học không hợp lệ";
            }
            return null;
        }
        return InputHelper.emptyData(subjectID, "mã môn học");
    }


    private void uploadFile() {
        if (uriPath != null) {
            if (filePath != null && filePath.endsWith(".json")) {
                readJsonFile(filePath);
            } else {
                setInfo("File đính kèm không hợp lệ");
            }
        } else {
            setInfo("Bạn chưa đính kèm file câu hỏi");
        }
    }


    private void readJsonFile(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            String jString = Charset.defaultCharset().decode(bb).toString();
            final ArrayList<Question> questions = IOHelper.questions(jString);
            if (questions.size() > 0) {
                onLoading();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("exams");
                final String fileName = String.valueOf(madethi);
                String pathChild = subjectID + "/" + idGV + "/" + madethi + ".json";
                storageRef.child(pathChild).putFile(uriPath)
                        .addOnSuccessListener(taskSnapshot -> {
                            setInfo("Tải đề thi thành công:" +
                                    "<br>- Mã môn học: " + subjectID +
                                    "<br>- Tên đề thi: " + examName +
                                    "<br>- Mã đề thi: " + madethi +
                                    "<br>- Mã giáo viên: " + idGV +
                                    "<br>- Thời gian thi: " + time + " phút" +
                                    "<br>- Số câu hỏi: " + questions.size());
                            createDatabase(fileName, questions.size());
                            onUploadComplete();
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                setInfo("Không thể upload file.<br><b>Lỗi:</b> " + e.getMessage());
                                onUploadFalse();
                            }
                        });
            } else {
                setInfo("File đính kèm không hợp lệ.<br><b>Lỗi:</b> Định dạng câu hỏi không đúng");
                onUploadFalse();
            }
        } catch (Exception e) {
            setInfo("File đính kèm không hợp lệ.<br><b>Lỗi:</b> " + e.getMessage());
            onUploadFalse();
        }
    }

    private DatabaseReference myRef;

    private void createDatabase(final String fileName, final int ques) {
        myRef = FirebaseDatabase.getInstance().getReference("exams");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //String path = mAuth.getUid() + "/" + fileName;
                //Toast.makeText(Timgiaovien.this, idGV, Toast.LENGTH_SHORT).show();
                String path = String.valueOf(idGV) + "/" + String.valueOf(subjectID) + "/" + String.valueOf(madethi);
                myRef.child(path).child("name").setValue(idGV + " " + madethi + " - " + examName);
                myRef.child(path).child("time").setValue(time);
                myRef.child(path).child("subjectID").setValue(subjectID);
                myRef.child(path).child("ques").setValue(ques);
                myRef.child(path).child("madethi").setValue(madethi);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onLoading() {
        isLoading = true;
        visibleViews(progressBarNext);
        goneViews(tvExamInfo, btNext);
        disableViews(btNext, inputTime, inputSubjectID, inputExamName, tvAttachFile, mamh, magv);
    }

    private void onUploadComplete() {
        isLoading = false;
        visibleViews(btNext);
        goneViews(progressBarNext);
    }

    private void onUploadFalse() {
        isLoading = false;
        enableViews(btNext, inputSubjectID, inputTime, inputExamName, tvAttachFile, magv, mamh, magv);
        visibleViews(btNext);
        goneViews(progressBarNext);
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Import đề thi");
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private void setupFirebaseAuth() {
        visibleViews(layoutStatus, progressBar);
        goneViews(tvStatus, tvAction, layoutMain);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    visibleViews(layoutStatus, tvAction, tvStatus);
                    goneViews(progressBar, layoutMain);
                    tvStatus.setText(R.string.no_login);
                    tvAction.setText(Html.fromHtml("<u>Đăng nhập ngay</u>"));
                    tvAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.no_animation);
                        }
                    });
                } else {
                    goneViews(layoutStatus);
                    visibleViews(layoutMain);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        if (!isLoading) {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out_right);
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

    private void setInfo(String s) {
        visibleViews(tvExamInfo);
        tvExamInfo.setText(Html.fromHtml(s));
    }

    public void docIDGV() {
        Pref pref;
        pref = new Pref(this);
        String s = pref.getData(Pref.UID);
        //Toast.makeText(this, "s"+s, Toast.LENGTH_SHORT).show();
        DatabaseReference mDatabase;
        //mDatabase = FirebaseDatabase.getInstance().getReference().child("conmeo").child("meoden");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(s).child("idgv");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                //Toast.makeText(Timgiaovien.this,"IDGV: " + value, Toast.LENGTH_SHORT).show();
                idGV = value;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TimGiaoVienActivity.this, ":Lỗi truy vấn! ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
