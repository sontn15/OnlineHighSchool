package com.sh.onlinehighschool.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.callback.OnSubjectListener;
import com.sh.onlinehighschool.dialog.SubjectDialog;
import com.sh.onlinehighschool.dialog.ToggleDialog;
import com.sh.onlinehighschool.fragment.HistoryFragment;
import com.sh.onlinehighschool.fragment.HomeFragment;
import com.sh.onlinehighschool.fragment.ImportedFragment;
import com.sh.onlinehighschool.fragment.ImportedgvFregment;
import com.sh.onlinehighschool.fragment.ScoreboardFragment;
import com.sh.onlinehighschool.model.Subject;
import com.sh.onlinehighschool.utils.InputHelper;
import com.sh.onlinehighschool.utils.Pref;
import com.sh.onlinehighschool.utils.Units;
import com.sh.onlinehighschool.utils.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements OnSubjectListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String NAV_ITEM_SELECTED = "ITEM_SELECTED";
    public static String ID_GV = "MEO";
    private Pref pref;
    private int navItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = new Pref(this);
        setContentView(R.layout.activity_main);
        initImageLoader();
        initWidgets();
        if (savedInstanceState == null) {
            navItem = R.id.nav_home;
        } else {
            navItem = savedInstanceState.getInt(NAV_ITEM_SELECTED);
        }
        kiemtraGV();
        setupFirebaseAuth();
        initToolbar();
        initDrawer();
        initFragment();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_SELECTED, navItem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        }
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getApplicationContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RelativeLayout layoutHeader;

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        layoutHeader = navigationView.getHeaderView(0).findViewById(R.id.layout_header);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initFragment() {
        if (navItem == R.id.nav_home) {
            replaceFragment(new HomeFragment(), getString(R.string.app_name));
            toolbar.getMenu().findItem(R.id.item_filter).setVisible(true);
        } else if (navItem == R.id.nav_imported) {
            //sửa chỗ này
            try {
                String str = pref.getData(Pref.EMAIL);
                if (str.contains("@uneti.edu.vn")) {
                    replaceFragment(ImportedgvFregment.newInstance(0), "Đề thi đã tải lên");
                    toolbar.getMenu().findItem(R.id.item_filter).setVisible(false);
                } else {
                    replaceFragment(ImportedFragment.newInstance(0), "Đề thi đã tải lên");
                    toolbar.getMenu().findItem(R.id.item_filter).setVisible(true);
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Bạn cần phải đăng nhập!", Toast.LENGTH_SHORT).show();
            }
        } else if (navItem == R.id.nav_scoreboard) {
            replaceFragment(new ScoreboardFragment(), "Bảng điểm");
            toolbar.getMenu().findItem(R.id.item_filter).setVisible(false);
        } else {
            toolbar.getMenu().findItem(R.id.item_filter).setVisible(true);
            replaceFragment(HistoryFragment.newInstance(0), "Lịch sử thi");
        }
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        navigationView.getMenu().findItem(navItem).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.item_exam:
                showActivity(ExamActivity.class);
                break;
            case R.id.item_filter:
                showToggleDialog();
                break;
        }
        return true;
    }

    private void showToggleDialog() {
        if (navItem == R.id.nav_home) {
            ToggleDialog dialog = new ToggleDialog();
            dialog.show(getSupportFragmentManager(), dialog.getTag());
        } else {
            SubjectDialog dialog = new SubjectDialog();
            dialog.show(getSupportFragmentManager(), dialog.getTag());
        }
    }

    @Override
    public void onSubjectChange(Subject subject) {
        if (navItem == R.id.nav_history) {
            replaceFragment(HistoryFragment.newInstance(subject.getId()), "Lịch sử thi");
        } else {
            replaceFragment(ImportedFragment.newInstance(subject.getId()), "Đề thi đã tải lên");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                toggleFragment(item);
                break;
            case R.id.nav_imported:
                toggleFragment(item);
                break;
            case R.id.nav_exam:
                //sửa chỗ này
                try {
                    if (mAuth.getCurrentUser() != null) {
                        showActivity(ExamActivity.class);
                        break;
                    } else {
                        Toast.makeText(MainActivity.this, "Bạn cần phải đăng nhập!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Bạn cần phải đăng nhập!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_on_exam:
                showActivity(Onthionline.class);
                break;
            case R.id.nav_history:
                toggleFragment(item);
                break;
            case R.id.nav_scoreboard:
                toggleFragment(item);
                break;
            case R.id.nav_import:
                showActivity(ImportActivity.class);
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                navigationView.getMenu().findItem(R.id.nav_import_deon).setVisible(false);
                ID_GV = "MEO";
                break;
            case R.id.nav_import_gv:
                showActivity(Timgiaovien.class);
                break;
            case R.id.nav_import_deon:
                showActivity(Importdeon.class);
                break;
            //case R.id.nav_exam: showActivity(Onthionline.class);break;
            //case R.id.nav_imported_gv: toggleFragment(ImportedgvFregment.class);break;
            //case R.id.nav_changepass:
            //Intent intent = new Intent(MainActivity.this,Changepass.class);
            //startActivity(intent);break;

        }
        return true;
    }

    private final Handler handler = new Handler();

    private void toggleFragment(final MenuItem item) {
        navItem = item.getItemId();
        initFragment();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(navigationView, true);
            }
        }, 250);
    }

    private long backPressTime;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        } else {
            if (backPressTime + 2000 > System.currentTimeMillis()) {
                finish();
            } else {
                Toast.makeText(this, "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT).show();
            }
            backPressTime = System.currentTimeMillis();
        }
    }

    private void showActivity(Class T) {
        Intent intent = new Intent(getApplicationContext(), T);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.no_animation);
    }

    private void replaceFragment(Fragment f, String title) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, f).commit();
        toolbar.setTitle(title);
    }

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    initNavigationLogin();
                } else {
                    initNavigationNoLogin();
                }
            }
        };
    }

    private void initNavigationNoLogin() {
        layoutHeader.removeAllViews();
        TextView tvLogin = new TextView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                Units.dpToPx(40)
        );
        tvLogin.setText(R.string.login_now);
        tvLogin.setGravity(Gravity.CENTER);
        tvLogin.setBackgroundResource(R.drawable.bg_border_all_corner_20_transparent);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(LoginActivity.class);
            }
        });
        layoutHeader.addView(tvLogin, params);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_import_gv).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_import_deon).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_on_exam).setVisible(false);
    }

    private static final int IV_AVATAR_ID = 1;
    private static final int TV_NAME_ID = 2;

    private void initNavigationLogin() {
        layoutHeader.removeAllViews();
        layoutHeader.setBackgroundResource(R.drawable.bg_item_click);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        //navigationView.getMenu().findItem(R.id.nav_changepass).setVisible(true);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                Units.dpToPx(40), Units.dpToPx(40)
        );
        params1.setMargins(0, 0, Units.dpToPx(8), 0);
        params1.addRule(RelativeLayout.CENTER_VERTICAL);

        if (pref.getData(Pref.AVATAR) != null && !pref.getData(Pref.AVATAR).equals("")) {
            CircleImageView ivAvatar = new CircleImageView(this);
            ivAvatar.setId(IV_AVATAR_ID);
            UniversalImageLoader.setImage(pref.getData(Pref.AVATAR), ivAvatar, null);
            layoutHeader.addView(ivAvatar, params1);
        } else {
            ImageView ivAvatar = new ImageView(this);
            ivAvatar.setId(IV_AVATAR_ID);
            String s = InputHelper.getCharAvatar(pref.getData(Pref.NAME));
            TextDrawable drawable = TextDrawable.builder().buildRound(s, Color.BLUE);
            ivAvatar.setImageDrawable(drawable);
            layoutHeader.addView(ivAvatar, params1);
        }

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params2.addRule(RelativeLayout.END_OF, IV_AVATAR_ID);
        TextView tvName = new TextView(this);
        tvName.setId(TV_NAME_ID);
        tvName.setText(pref.getData(Pref.NAME));

        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        params3.addRule(RelativeLayout.END_OF, IV_AVATAR_ID);
        params3.addRule(RelativeLayout.BELOW, TV_NAME_ID);
        TextView tvEmail = new TextView(this);
        tvEmail.setText(pref.getData(Pref.EMAIL));

        layoutHeader.addView(tvName, params2);
        layoutHeader.addView(tvEmail, params3);
        String str = tvEmail.getText().toString();
        if (str.contains("@uneti.edu.vn")) {
            navigationView.getMenu().findItem(R.id.nav_import_gv).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_import_deon).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_import).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_exam).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_on_exam).setVisible(false);
            //navigationView.getMenu().findItem(R.id.nav_imported_gv).setVisible(true);
            //navigationView.getMenu().findItem(R.id.nav_imported).setVisible(false);
        } else {
            navigationView.getMenu().findItem(R.id.nav_import).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_import_gv).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_import_deon).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_exam).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_on_exam).setVisible(true);
            //navigationView.getMenu().findItem(R.id.nav_imported_gv).setVisible(false);
            //navigationView.getMenu().findItem(R.id.nav_imported).setVisible(true);
        }
        layoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show profile
                showActivity(ProfileActivity.class);
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
                        ID_GV = value;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("idGV", "Lỗi truy vấn!");
                    }
                });
            }
        } catch (Exception e) {
            Log.d("idGV", "Lỗi truy vấn");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

}
