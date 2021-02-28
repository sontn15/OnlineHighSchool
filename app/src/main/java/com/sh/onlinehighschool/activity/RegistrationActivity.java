package com.sh.onlinehighschool.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
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

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext = RegistrationActivity.this;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    private Pref pref;
    private String name;
    private String email;
    private String password;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = new Pref(mContext);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        if (savedInstanceState == null) {
            name = email = password = null;
            isLoading = false;
        } else {
            name = savedInstanceState.getString("name");
            email = savedInstanceState.getString("email");
            password = savedInstanceState.getString("password");
            isLoading = savedInstanceState.getBoolean("loading");
        }
        setContentView(R.layout.activity_registation);
        initWidgets();
        initToolbar();
        initName();
        initEmail();
        initPassword();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", name);
        outState.putString("email", email);
        outState.putString("password", password);
        outState.putBoolean("loading", isLoading);
    }

    private Toolbar toolbar;
    private TextInputLayout inputName;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    private ProgressBar progressBar;
    private TextView tvLogin;
    private ImageButton btRegistration;

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.progressbar);
        tvLogin = findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(this);
        btRegistration = findViewById(R.id.bt_registration);
        btRegistration.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Tạo tài khoản");
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

    private void initName() {
        inputName.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        inputName.getEditText().setText(name);
        inputName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                name = editable.toString();
                inputName.setErrorEnabled(false);
            }
        });
    }

    private void initEmail() {
        inputEmail.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputEmail.getEditText().setText(email);
        inputEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                email = editable.toString();
                inputEmail.setErrorEnabled(false);
            }
        });
    }

    private void initPassword() {
        inputPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputPassword.getEditText().setText(password);
        inputPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                password = editable.toString();
                inputPassword.setErrorEnabled(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
                showLogin();
                break;
            case R.id.bt_registration:
                registration();
                break;
        }
    }

    private void showLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
        finish();
    }

    private void registration() {
        String validName = InputHelper.validName(name);
        String validEmail = InputHelper.validEmail(email);
        String validPassword = InputHelper.validPassword(password);
        setError(inputName, validName);
        setError(inputEmail, validEmail);
        setError(inputPassword, validPassword);
        if (validName == null & validPassword == null & validEmail == null) {
            onLoading();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            createUserDatabase();
                        } else {
                            showSortToast("Tạo tài khoản thất bại");
                            outLoading();
                        }
                    });
        }
    }

    private void createUserDatabase() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //add new user
                String userID = mAuth.getCurrentUser().getUid();
                User user = new User(userID, name, email, null, null);
                pref.saveData(Pref.UID, userID);
                pref.saveData(Pref.EMAIL, email);
                pref.saveData(Pref.PASSWORD, password);
                pref.saveData(Pref.NAME, name);
                myRef.child("users").child(userID).setValue(user);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                outLoading();
                showSortToast("Lỗi: " + databaseError.getMessage());
            }
        });
    }

    private void onLoading() {
        visibleViews(progressBar);
        goneViews(btRegistration);
        disableViews(inputName, inputEmail, inputPassword, tvLogin);
    }

    private void outLoading() {
        enableViews(inputName, inputEmail, inputPassword, tvLogin);
        visibleViews(btRegistration);
        goneViews(progressBar);
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

    protected void enableViews(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setEnabled(true);
                    view.setClickable(true);
                }
            }
        }
    }

    protected void disableViews(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setEnabled(false);
                    view.setClickable(false);
                }
            }
        }
    }

    protected void goneViews(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void visibleViews(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showSortToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }
}
