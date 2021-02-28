package com.sh.onlinehighschool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.model.User;
import com.sh.onlinehighschool.utils.InputHelper;
import com.sh.onlinehighschool.utils.Pref;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    private Pref pref;
    private String userEmail;
    private String userPassword;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate");
        setContentView(R.layout.activity_login);
        pref = new Pref(this);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("users");
        if (savedInstanceState == null) {
            userEmail = pref.getData(Pref.EMAIL);
            userPassword = pref.getData(Pref.PASSWORD);
            isLoading = false;
        } else {
            userEmail = savedInstanceState.getString("USER_EMAIL");
            userEmail = savedInstanceState.getString("USER_PASSWORD");
            isLoading = savedInstanceState.getBoolean("LOADING");
        }
        initWidgets();
        initToolbar();
        initEmail();
        initPassword();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("USER_EMAIL", userEmail);
        outState.putString("USER_PASSWORD", userPassword);
        outState.putBoolean("LOADING", isLoading);
    }

    private Toolbar toolbar;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    private ProgressBar progressBar;
    private TextView tvRegistration;
    private ImageButton btLogin;

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.progressbar);
        tvRegistration = findViewById(R.id.tv_registration);
        tvRegistration.setOnClickListener(this);
        btLogin = findViewById(R.id.bt_login);
        btLogin.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Đăng nhập");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_registration:
                showRegistration();
                break;
            case R.id.bt_login:
                login();
                break;
        }
    }

    private void showRegistration() {
        Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
        finish();
    }

    private void login() {
        String validEmail = InputHelper.validEmail(userEmail);
        String validPassword = InputHelper.validPassword(userPassword);
        setError(inputEmail, validEmail);
        setError(inputPassword, validPassword);


        if (validPassword == null & validEmail == null) {
            onLoading();
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            loadUserDatabase(uid);
                        } else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            outLoading();
                        }
                    });
        }
    }

    private void loadUserDatabase(final String uid) {
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                pref.saveData(Pref.UID, uid);
                pref.saveData(Pref.EMAIL, user.getEmail());
                pref.saveData(Pref.PASSWORD, userPassword);
                pref.saveData(Pref.NAME, user.getName());
                if (user.getAvatar() != null) {
                    pref.saveData(Pref.AVATAR, user.getAvatar());
                }
                kiemtraGV();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void kiemtraGV() {
        pref = new Pref(this);
        String str = pref.getData(Pref.EMAIL);
        try {
            if (str.contains("@uneti.edu.vn")) {
                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(pref.getData(Pref.UID)).child("idgv");

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        MainActivity.ID_GV = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("idGV", "Lỗi truy vấn!");
                    }
                });
            } else {
                MainActivity.ID_GV = "MEO";
            }
        } catch (Exception e) {
            Log.d("idGV", "Lỗi truy vấn");
        }

    }


    private void onLoading() {
        isLoading = true;
        visibleViews(progressBar);
        goneViews(btLogin);
        disableViews(inputEmail, inputPassword, tvRegistration);
    }

    private void outLoading() {
        isLoading = false;
        enableViews(inputEmail, inputPassword, tvRegistration);
        visibleViews(btLogin);
        goneViews(progressBar);
    }

    private void initEmail() {
        if (inputEmail.getEditText() != null) {
            inputEmail.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            inputEmail.getEditText().setText(userEmail);
            inputEmail.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    userEmail = editable.toString();
                    inputEmail.setErrorEnabled(false);
                }
            });
        }
    }

    private void initPassword() {
        if (inputPassword.getEditText() != null) {
            inputPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            inputPassword.getEditText().setText(userPassword);
            inputPassword.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    userPassword = editable.toString();
                    inputPassword.setErrorEnabled(false);
                }
            });
        }
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

    private void setError(TextInputLayout input, String error) {
        if (error != null) {
            input.setErrorEnabled(true);
            input.setError(error);
        } else {
            input.setErrorEnabled(false);
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
