package com.sh.onlinehighschool.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.activity.LoginActivity;
import com.sh.onlinehighschool.activity.MainActivity;
import com.sh.onlinehighschool.adapter.ExamImportedAdapter;
import com.sh.onlinehighschool.callback.OnDeleteListener;
import com.sh.onlinehighschool.callback.OnRecyclerViewListener;
import com.sh.onlinehighschool.dialog.DeleteDialog;
import com.sh.onlinehighschool.model.Exam;
import com.sh.onlinehighschool.utils.DBAssetHelper;
import com.sh.onlinehighschool.views.AutoRecyclerView;


public class ImportedgvFregment extends Fragment implements OnDeleteListener {

    public static ImportedgvFregment newInstance(int subjectID) {
        ImportedgvFregment fragment = new ImportedgvFregment();
        Bundle args = new Bundle();
        args.putInt("SUBJECT", subjectID);
        fragment.setArguments(args);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_importedgv_fregment, container, false);
        initWidgets(view);
        return view;
    }

    private AutoRecyclerView recyclerView;
    private RelativeLayout layoutStatus;
    private TextView tvStatus;
    private TextView tvAction;
    private ProgressBar progressBar;

    private void initWidgets(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        layoutStatus = view.findViewById(R.id.layout_status);
        tvStatus = view.findViewById(R.id.tv_status);
        tvStatus.setTextColor(Color.WHITE);
        tvAction = view.findViewById(R.id.tv_action);
        progressBar = view.findViewById(R.id.progressbar);
    }

    private DBAssetHelper dbAssetHelper;
    private int subjectID;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbAssetHelper = new DBAssetHelper(getActivity());
        subjectID = getArguments().getInt("SUBJECT");
        Log.d("ID", subjectID + "");
        setupFirebaseAuth();
    }

    private ExamImportedAdapter adapter;

    private void initRecyclerView() {
        adapter = new ExamImportedAdapter(getActivity(), isOneSubject(), new OnRecyclerViewListener() {
            @Override
            public void onItemChange(View view, int position) {
                Exam exam = adapter.getItem(position);
                if (view.getId() == R.id.bt_delete) {
                    //Hiển thị dialog xóa
                    DeleteDialog dialog = DeleteDialog.newInstance(exam);
                    dialog.setTargetFragment(ImportedgvFregment.this, 1);
                    dialog.show(getFragmentManager(), dialog.getTag());
                } else {
                    //Bắt đầu thi
//                    Bundle bundle = new Bundle();
//                    bundle.putString(QuizActivity.TYPE, "exams");
//                    bundle.putParcelable(QuizActivity.EXAM, exam);
//                    Intent intent = new Intent(getActivity(), QuizActivity.class);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.scale_in, R.anim.no_animation);

                    //Không cho thi
                    Toast.makeText(getActivity(), "Bạn đang sử dụng tài khoản cho Giáo viên", Toast.LENGTH_LONG).show();
                    //Toast.makeText(getActivity(), "s" + mAuth.getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        initDatabase();
    }


    @Override
    public void onDelete(Exam exam) {
        adapter.remove(exam);
        //String[] words= exam.getName().split("\\s");
        //Toast.makeText(this, "Chuoi cat: "+words[0]+"|"+words[1]+"|"+words[2], Toast.LENGTH_LONG).show();
        String path = MainActivity.ID_GV + "/" + exam.getSubjectID() + "/" + exam.getId();
        Log.d("pathHuuHai", path);
        //Xóa dữ liệu trên Firebase Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("exams");
        databaseReference.child(path).setValue(null);

        // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("onluyen");
        //databaseReference.child(path).setValue(null);
        //Xóa dữ liệu trên Firebase Storage
        String path2 = exam.getSubjectID() + "/" + MainActivity.ID_GV + "/" + exam.getId();
        Log.d("pathHuuHai2", path2);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("exams");
        storageReference.child(path + ".json").delete();

        //StorageReference storageReference = FirebaseStorage.getInstance().getReference("onluyen");
        //storageReference.child(path + ".json").delete();


        adapter.remove(exam);
        String path1 = MainActivity.ID_GV + "/" + exam.getSubjectID() + "/" + exam.getId();
        Log.d("pathHuuHai", path1);
        //Xóa dữ liệu trên Firebase Database
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("onluyen");
        databaseReference1.child(path).setValue(null);
        //Xóa dữ liệu trên Firebase Storage
        String path3 = exam.getSubjectID() + "/" + MainActivity.ID_GV + "/" + exam.getId();
        Log.d("pathHuuHai2", path3);
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference("onluyen");
        storageReference1.child(path + ".json").delete();
    }

    private boolean isOneSubject() {
        return subjectID != 0;
    }

    private void initDatabase() {
        visibleViews(layoutStatus, progressBar);
        goneViews(tvStatus, tvAction, recyclerView);
        if (subjectID == 0) {
            loadAllDatabase();
            loadAllDatabase1a();
            Log.d("Hiii", "Mèo mẹ GV");
        } else {
            queryDatabase();
            Log.d("Hiii", "Mèo con GV");
        }
    }

    int dem = 0;

    private void loadAllDatabase() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("exams");
        if (mAuth.getCurrentUser() != null) {
            for (int i = 101; i <= 126; i++) {
                myRef.child(MainActivity.ID_GV).child(i + "").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        retireDatabase(dataSnapshot, "Không có đề thi nào được tải lên");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void loadAllDatabase1a() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("onluyen");
        if (mAuth.getCurrentUser() != null) {
            for (int i = 101; i <= 126; i++) {
                myRef.child(MainActivity.ID_GV).child(i + "").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        retireDatabase(dataSnapshot, "Không có đề thi nào được tải lên");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void queryDatabase() {
        dem = 0;
        Log.d("Hiii", "Vào lọc");
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        if (mAuth.getCurrentUser() != null) {
            Query query = myRef.child("exams").child(MainActivity.ID_GV)
                    .orderByChild("subjectID").equalTo(subjectID);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    retireDatabase(dataSnapshot, "Môn: " + dbAssetHelper.subject(subjectID).getName() +
                            "\nKhông có đề thi nào được tải lên");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void retireDatabase(DataSnapshot dataSnapshot, String empty) {
        if (dataSnapshot.exists()) {
            dem++;
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                try {
                    Exam exam = ds.getValue(Exam.class);
                    exam.setId(Long.parseLong(ds.getKey()));
                    visibleViews(recyclerView);
                    goneViews(layoutStatus);
                    adapter.add(exam);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("Trangthai", "Môn này không có đề");
            if (dem == 0) {
                visibleViews(layoutStatus, tvStatus);
                goneViews(progressBar, tvAction, recyclerView);
                tvStatus.setText(empty);
            }
        }
    }

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private void setupFirebaseAuth() {
        visibleViews(layoutStatus, progressBar);
        goneViews(tvStatus, tvAction, recyclerView);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    visibleViews(layoutStatus, tvAction, tvStatus);
                    goneViews(progressBar, recyclerView);
                    tvStatus.setText(R.string.no_login);
                    tvAction.setText(Html.fromHtml("<u>Đăng nhập ngay</u>"));
                    tvAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.no_animation);
                        }
                    });
                } else {
                    if (adapter != null && adapter.getItemCount() > 0) {
                        goneViews(layoutStatus);
                        visibleViews(recyclerView);
                    } else {
                        initRecyclerView();
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
}
