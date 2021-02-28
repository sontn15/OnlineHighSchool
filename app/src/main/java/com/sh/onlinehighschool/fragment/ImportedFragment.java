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
import com.sh.onlinehighschool.activity.QuizActivity;
import com.sh.onlinehighschool.adapter.ExamImportedAdapter;
import com.sh.onlinehighschool.callback.OnDeleteListener;
import com.sh.onlinehighschool.callback.OnRecyclerViewListener;
import com.sh.onlinehighschool.dialog.DeleteDialog;
import com.sh.onlinehighschool.model.Exam;
import com.sh.onlinehighschool.utils.DBAssetHelper;
import com.sh.onlinehighschool.utils.Pref;
import com.sh.onlinehighschool.views.AutoRecyclerView;


public class ImportedFragment extends Fragment implements OnDeleteListener {

    public static ImportedFragment newInstance(int subjectID) {
        ImportedFragment fragment = new ImportedFragment();
        Bundle args = new Bundle();
        args.putInt("SUBJECT", subjectID);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imported, container, false);
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
                    dialog.setTargetFragment(ImportedFragment.this, 1);
                    dialog.show(getFragmentManager(), dialog.getTag());
                } else {
                    //Bắt đầu thi
                    Bundle bundle = new Bundle();
                    bundle.putString(QuizActivity.TYPE, "upload");
                    bundle.putParcelable(QuizActivity.EXAM, exam);
                    Intent intent = new Intent(getActivity(), QuizActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.scale_in, R.anim.no_animation);
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
        Pref pref = new Pref(getActivity());
        String path = pref.getData(Pref.UID) + "/" + exam.getId();
        //Xóa dữ liệu trên Firebase Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("upload");
        databaseReference.child(path).setValue(null);
        //Xóa dữ liệu trên Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("upload");
        storageReference.child(path + ".json").delete();
    }

    private boolean isOneSubject() {
        return subjectID != 0;
    }

    private void initDatabase() {
        visibleViews(layoutStatus, progressBar);
        goneViews(tvStatus, tvAction, recyclerView);
        if (subjectID == 0) {
            loadAllDatabase();
            Log.d("Hiii", "Mèo mẹ");
        } else {
            queryDatabase();
            Log.d("Hiii", "Mèo con");
        }
    }

    private void loadAllDatabase() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("upload");
        if (mAuth.getCurrentUser() != null) {
            myRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void queryDatabase() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        if (mAuth.getCurrentUser() != null) {
            Query query = myRef.child("upload").child(mAuth.getCurrentUser().getUid())
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
            visibleViews(layoutStatus, tvStatus);
            goneViews(progressBar, tvAction, recyclerView);
            tvStatus.setText(empty);
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
