package com.sh.onlinehighschool.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.model.Notification;
import com.sh.onlinehighschool.model.PushNotification;
import com.sh.onlinehighschool.model.Response;
import com.sh.onlinehighschool.service.NotificationAPI;
import com.sh.onlinehighschool.utils.Constants;
import com.sh.onlinehighschool.utils.Pref;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendNotificationActivity extends AppCompatActivity implements View.OnClickListener {
    public static String TOPIC = "/topics/";

    private Toolbar toolbar;
    private EditText edtTitle, edtContent;
    private TextView btnClear, btnSend;

    private Pref pref;
    private String nameGV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);
        initWidgets();
        initData();
    }

    private void initData() {
        pref = new Pref(this);
        nameGV = pref.getData(Pref.NAME);
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Gửi thông báo");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        edtTitle = this.findViewById(R.id.edtTieuDeThongBao);
        edtContent = this.findViewById(R.id.edtNoiDungThongBao);

        btnClear = this.findViewById(R.id.tvClear);
        btnSend = this.findViewById(R.id.tvSendNotification);

        btnClear.setOnClickListener(this);
        btnSend.setOnClickListener(this);
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
            case R.id.tvClear: {
                edtTitle.setText("");
                edtContent.setText("");
                break;
            }
            case R.id.tvSendNotification: {
                String title = edtTitle.getText().toString();
                String content = edtContent.getText().toString();

                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    String nameGV = pref.getData(Pref.NAME);

                    title = "[GV: " + nameGV + "] - " + title;
                    TOPIC = TOPIC + "khoi" + pref.getDataInt(Pref.KHOI);

                    Notification message = new Notification(title, content);
                    PushNotification data = new PushNotification(message, TOPIC);
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    NotificationAPI apiService = retrofit.create(NotificationAPI.class);
                    Call<Response> responseCall = apiService.postNotification(data);
                    if (responseCall != null) {
                        responseCall.enqueue(new Callback<Response>() {
                            @Override
                            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                edtTitle.setText("");
                                edtContent.setText("");
                                Log.d("SONTN7", "Send message to topic " + TOPIC);
                                Toast.makeText(SendNotificationActivity.this, "Gửi thông báo thành công", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<Response> call, Throwable t) {
                                Toast.makeText(SendNotificationActivity.this, "Gửi không thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                break;
            }
        }
    }
}
