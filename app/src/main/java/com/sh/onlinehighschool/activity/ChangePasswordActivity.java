package com.sh.onlinehighschool.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.utils.InputHelper;
import com.sh.onlinehighschool.utils.Pref;
import com.sh.onlinehighschool.utils.UniversalImageLoader;

import java.io.IOException;


public class ChangePasswordActivity extends AppCompatActivity {

    private Pref pref;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Toolbar toolbar;
    private ImageView ivAvatar;
    private Button tvChangePass;
    private Button tvReset;
    TextInputLayout currentpass, newpass, cofirmpass;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass_word);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();
        pref = new Pref(this);
        auth = FirebaseAuth.getInstance();
        initWidgets();
        initToolbar();
        initAvatar();
        anhXa();
        DoiMatKhau();
        NhapLai();
    }

    public void anhXa() {
        currentpass = findViewById(R.id.edt_currentpass);
        newpass = findViewById(R.id.edt_newpass);
        cofirmpass = findViewById(R.id.edt_confirmpass);
    }

    private void DoiMatKhau() {
        tvChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (currentpass.getEditText().getText().toString().isEmpty() && newpass.getEditText().getText().toString().isEmpty() && cofirmpass.getEditText().getText().toString().isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Bạn hãy nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else {

                    if (newpass.getEditText().getText().toString().equals(cofirmpass.getEditText().getText().toString())) {
                        if (user != null && user.getEmail() != null) {
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(user.getEmail(), currentpass.getEditText().getText().toString());

                            // Prompt the user to re-provide their sign-in credentials
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChangePasswordActivity.this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();
                                                user.updatePassword(cofirmpass.getEditText().getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(ChangePasswordActivity.this, "Thay đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                                                    auth.signOut();
                                                                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                        }
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void NhapLai() {
        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentpass.getEditText().setText("");
                newpass.getEditText().setText("");
                cofirmpass.getEditText().setText("");
            }
        });
    }


    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        ivAvatar = findViewById(R.id.iv_avatar);
        tvChangePass = findViewById(R.id.btn_luuthaydoi);
        tvReset = findViewById(R.id.btn_nhaplai);
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Đổi mật khẩu");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    private void initAvatar() {
        if (pref.getData(Pref.AVATAR) != null && !pref.getData(Pref.AVATAR).equals("")) {
            UniversalImageLoader.setImage(pref.getData(Pref.AVATAR), ivAvatar, null);
        } else {
            String s = InputHelper.getCharAvatar(pref.getData(Pref.NAME));
            TextDrawable drawable = TextDrawable.builder().buildRound(s, Color.GREEN);
            ivAvatar.setImageDrawable(drawable);
        }
    }


    private Uri filePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivAvatar.setImageBitmap(bitmap);
                toolbar.getMenu().findItem(R.id.item_save).setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        menu.findItem(R.id.item_save).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out_right);
    }
}
