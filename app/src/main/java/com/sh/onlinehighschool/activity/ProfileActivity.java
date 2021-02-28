package com.sh.onlinehighschool.activity;

import android.Manifest;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.utils.InputHelper;
import com.sh.onlinehighschool.utils.Pref;
import com.sh.onlinehighschool.utils.Units;
import com.sh.onlinehighschool.utils.UniversalImageLoader;

import java.io.IOException;
import java.util.List;


public class ProfileActivity extends AppCompatActivity {


    private Pref pref;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();
        pref = new Pref(this);
        initWidgets();
        initToolbar();
        initAvatar();
        changeAvatar();
        initlogin();

        tvchangepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChangePassWord.class);
                startActivity(intent);
            }
        });

    }

    private Toolbar toolbar;
    private ImageView ivAvatar;
    private TextView tvChangeAvatar;
    private TextView tt;
    private TextView tvchangepass;

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        ivAvatar = findViewById(R.id.iv_avatar);
        tvChangeAvatar = findViewById(R.id.tv_change_avatar);
        tvchangepass = findViewById(R.id.tv_changepass);
        tt = findViewById(R.id.tv_thongtin);
    }

    private void initToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Trang cá nhân");
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

    private void initlogin() {
        //tt.setId(TV_NAME_ID);
        tt.setText("Họ và tên: " + pref.getData(Pref.NAME) + "\n" + "Email: " + pref.getData(Pref.EMAIL));
    }

    private void changeAvatar() {
        tvChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
    }

    private void checkPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new BaseMultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        super.onPermissionsChecked(report);
                        choseFiles();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        super.onPermissionRationaleShouldBeShown(permissions, token);
                    }
                }).check();
    }

    private void choseFiles() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 2);
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
            case R.id.item_save:
                saveAvatar();
                break;
        }
        return true;
    }

    private void saveAvatar() {
        final String UID = pref.getData(Pref.UID);
        final StorageReference fileUpload = storageReference.child("avatar").child(UID);
        final MenuItem item = toolbar.getMenu().findItem(R.id.item_save);
        item.setEnabled(false);
        ProgressBar progressBar = new ProgressBar(this);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(
                Units.dpToPx(32), Units.dpToPx(32)
        );
        progressBar.setLayoutParams(params);
        item.setActionView(progressBar);
        fileUpload.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                databaseReference.child(UID).child("avatar").setValue(url);
                                pref.saveData(Pref.AVATAR, url);
                                item.setActionView(null);
                                toolbar.getMenu().findItem(R.id.item_save).setVisible(false);
                                Toast.makeText(ProfileActivity.this, "Đổi ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out_right);
    }
}
