package com.sh.onlinehighschool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.callback.OnSubjectListener;
import com.sh.onlinehighschool.dialog.SubjectDialog;
import com.sh.onlinehighschool.model.Exam;
import com.sh.onlinehighschool.model.Subject;
import com.sh.onlinehighschool.model.User;
import com.sh.onlinehighschool.utils.DBAssetHelper;
import com.sh.onlinehighschool.utils.InputHelper;
import com.sh.onlinehighschool.utils.Pref;


public class ExamActivity extends AppCompatActivity implements OnSubjectListener {

    private boolean isLoading;
    private String idGV;
    private int subjectID;
    private int examID;
    private String mamon;
    private String gmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        try {
            docDaThi();
        } catch (Exception e) {
            Log.d("Loi", "Đăng nhập lần đầu!");
        }
        initWidgets();
        initToolbar();
        setupFirebaseAuth();

        if (savedInstanceState == null) {
            isLoading = false;
            subjectID = examID = 0;
            idGV = null;  //hải code
            mamon = null;
            gmail = null;
        } else {
            isLoading = savedInstanceState.getBoolean("loading");
            subjectID = savedInstanceState.getInt("subject");
            idGV = savedInstanceState.getString("idgv");
            examID = savedInstanceState.getInt("exam");
            mamon = savedInstanceState.getString("exam");
            gmail = savedInstanceState.getString("gmail");
        }
        initInputExamID();
        initInputSubjectID();
        initInputTeacherID();

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initNext();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("loading", isLoading);
        outState.putInt("subject", subjectID);
        outState.putInt("exam", examID);
        outState.putString("exam", mamon);
        outState.putString("idgv", idGV);
        outState.putString("gmail", gmail);
    }

    private Toolbar toolbar;
    private RelativeLayout layoutMain;
    private TextInputLayout inputSubjectID;
    private TextInputLayout inputExamID;
    private TextInputLayout inputGmail;
    private TextView tvExamStatus;
    private ImageButton btNext;
    private ProgressBar progressBarNext;
    private RelativeLayout layoutStatus;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private TextView tvAction;

    private TextInputLayout inputTeacherID;

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        layoutMain = findViewById(R.id.layout_main);
        inputSubjectID = findViewById(R.id.input_subject_id);
        inputExamID = findViewById(R.id.input_exam_id);

        inputTeacherID = findViewById(R.id.input_exam_magv);
        //inputTeacherID.getEditText().setFilters(new InputFilter[] { filter });
        tvExamStatus = findViewById(R.id.tv_exam_status);
        progressBarNext = findViewById(R.id.progressbar_next);
        btNext = findViewById(R.id.bt_next);
        layoutStatus = findViewById(R.id.layout_status);
        progressBar = findViewById(R.id.progressbar);
        tvStatus = findViewById(R.id.tv_status);
        tvAction = findViewById(R.id.tv_action);
    }

    //chặn nhập  ký tự đặc biệt trong Mã GV
    private String blockCharacterSet = ".~#^|$%&*!";
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    //hàm Hùng sửa
    private void initInputTeacherID() {
        //inputTeacherID.getEditText().setText(idGV);
        if (idGV != null) {
            inputTeacherID.getEditText().setText(String.valueOf(idGV));
        }
        inputTeacherID.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputTeacherID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    idGV = editable.toString();
                } catch (Exception e) {
                    idGV = null;
                }
            }
        });
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

    @Override
    public void onSubjectChange(Subject subject) {
        inputSubjectID.getEditText().setText(String.valueOf(subject.getId()));
    }

    public void onSubjectChange(User user) {
        inputGmail.getEditText().setText(user.getEmail());
    }

    private void initInputExamID() {
        if (examID != 0) {
            inputExamID.getEditText().setText(String.valueOf(examID));
        }
        inputExamID.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputExamID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    examID = Integer.parseInt(editable.toString());
                } catch (Exception e) {
                    examID = 0;
                }
            }
        });
    }


    private void initNext() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("exams");
        String emptyExamID = InputHelper.emptyData(examID, "mã đề thi");
        String emptyidGV = InputHelper.emptyData(idGV, "mã giáo viên"); //Hùng thêm
        setError(inputSubjectID, errSubjectID());
        setError(inputExamID, emptyExamID);
        setError(inputTeacherID, emptyidGV); //Hùng thêm
        if (errSubjectID() == null) {
            onLoading();
            myRef.child(idGV).child(String.valueOf(subjectID)).child(String.valueOf(examID))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                //Nếu tồn tại đề thi
                                try {
                                    boolean checkThi = false;
                                    Log.d("CheckDaThi", "Đề đã thi trên next: " + dedathi);
                                    String[] arrDathi = dedathi.split("\\s");
                                    for (int i = 0; i < arrDathi.length; i++) {
                                        String arrdedathi = inputSubjectID.getEditText().getText() + "*" + inputExamID.getEditText().getText() + "*" + inputTeacherID.getEditText().getText();
                                        Log.d("CheckDaThi", arrdedathi + " và Mảng: " + arrDathi[i]);
                                        if (arrdedathi.equals(arrDathi[i]) == true) {
                                            Toast.makeText(ExamActivity.this, "Bạn đã làm bài kiểm tra này!", Toast.LENGTH_SHORT).show();
                                            errorLoading("Bạn đã làm bài kiểm tra này!");
                                            checkThi = true;
                                            break;
                                        }
                                    }
                                    if (checkThi == false) {
                                        ghiDethi();
                                        Exam exam = dataSnapshot.getValue(Exam.class);
                                        exam.setId(examID);
                                        exam.setSubjectID(subjectID);
                                        String gvID = idGV;
                                        Bundle bundle = new Bundle();
                                        bundle.putString(QuizActivity.TYPE, "exam");
                                        bundle.putString(QuizActivity.TYPE2, "TYPE2");
                                        bundle.putString(QuizActivity.IDGV, gvID);
                                        bundle.putParcelable(QuizActivity.EXAM, exam);

                                        Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.scale_in, R.anim.no_animation);
                                        outLoading();
                                    }
                                    outLoading();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    errorLoading("Thông tin đề thi không phù hợp");
                                }

                            } else {
                                //Không tồn tại đề thi
                                errorLoading("Không tìm thấy đề thi");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            outLoading();
                        }
                    });
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


    private void onLoading() {
        isLoading = true;
        visibleViews(progressBarNext);
        goneViews(tvExamStatus, btNext);
        disableViews(btNext, inputSubjectID, inputExamID);
    }

    private void outLoading() {
        isLoading = false;
        enableViews(btNext, inputSubjectID, inputExamID);
        visibleViews(btNext);
        goneViews(progressBarNext);
    }

    private void errorLoading(String err) {
        isLoading = false;
        enableViews(btNext, inputSubjectID, inputExamID);
        visibleViews(btNext, tvExamStatus);
        tvExamStatus.setText(err);
        goneViews(progressBarNext);
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Thi Online");
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
                    if (!MainActivity.ID_GV.equals("MEO")) {
                        visibleViews(layoutStatus, tvAction, tvStatus);
                        goneViews(progressBar, layoutMain);
                        tvAction.setText("");
                        tvStatus.setText("Bạn đang sử dụng tài khoản Giáo viên!");
                    } else {
                        goneViews(layoutStatus);
                        visibleViews(layoutMain);
                    }
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


    String dedathi = null;

    public void docDaThi() {
        Pref pref;
        pref = new Pref(this);
        String s = pref.getData(Pref.UID);
        Log.d("CheckDaThi", "uID: " + s);
        //Toast.makeText(this, "uID: "+s, Toast.LENGTH_SHORT).show();
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("dsdethi").child(s);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("CheckDaThi", "Đề đã thi: " + value);
                //Toast.makeText(ExamActivity.this,"Đề đã thi: " + value, Toast.LENGTH_SHORT).show();
                if (value == null) {
                    dedathi = "de1*ma1*gv1 de2*ma2*gv2";
                    Log.d("CheckDaThi", "dedathi null");
                } else {
                    dedathi = value;
                    Log.d("CheckDaThi", "De da thi: " + dedathi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ExamActivity.this, ":Lỗi truy vấn! ", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private DatabaseReference myRef;

    private void ghiDethi() {
        Pref pref;
        pref = new Pref(this);
        final String s = pref.getData(Pref.UID);
        myRef = FirebaseDatabase.getInstance().getReference("dsdethi");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myRef.child(s).setValue(dedathi + " " + inputSubjectID.getEditText().getText() + "*" + inputExamID.getEditText().getText() + "*" + inputTeacherID.getEditText().getText());
                Log.d("CheckDaThi", "ghiDethi: " + dedathi + " " + inputSubjectID.getEditText().getText() + "*" + inputExamID.getEditText().getText() + "*" + inputTeacherID.getEditText().getText());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
