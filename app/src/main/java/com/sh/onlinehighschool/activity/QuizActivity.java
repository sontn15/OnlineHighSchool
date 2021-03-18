package com.sh.onlinehighschool.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.adapter.QuestionAdapter;
import com.sh.onlinehighschool.callback.OnCautionDialogListener;
import com.sh.onlinehighschool.callback.OnQuizListener;
import com.sh.onlinehighschool.dialog.CautionDialog;
import com.sh.onlinehighschool.dialog.ListQuestionDialog;
import com.sh.onlinehighschool.dialog.ResultDialog;
import com.sh.onlinehighschool.model.Exam;
import com.sh.onlinehighschool.model.History;
import com.sh.onlinehighschool.model.Question;
import com.sh.onlinehighschool.utils.DBAssetHelper;
import com.sh.onlinehighschool.utils.DBHelper;
import com.sh.onlinehighschool.utils.IOHelper;
import com.sh.onlinehighschool.utils.Pref;
import com.sh.onlinehighschool.utils.QuizHelper;
import com.sh.onlinehighschool.utils.Units;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class QuizActivity extends AppCompatActivity implements View.OnClickListener,
        OnQuizListener, OnCautionDialogListener {

    public static final String TYPE = "EXAM_TYPE";
    public static final String EXAM = "EXAM_MODEL";
    public static final String STATUS = "STATUS";
    public static final String IDGV = "IDGV";
    public static final String IDGV2 = "IDGV2";
    public static String TYPE2 = "TYPE2";

    private boolean isLoading;

    private Bundle bundle;
    private String type;
    private String type2 = "xx";
    private Exam exam;
    private String idGV;
    private String idGV2;

    private DBAssetHelper dbAssetHelper;
    private DBHelper dbHelper;
    private Pref pref;

    private ArrayList<Question> questions;
    private int currentPage;
    /*
     * case: 0- bắt đầu thi
     * case: 1- chỉ xem điểm
     * case: 2- xem đáp án
     * */
    private int status;

    private long timeInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbAssetHelper = new DBAssetHelper(this);
        dbHelper = new DBHelper(this);
        pref = new Pref(this);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString(TYPE);
            type2 = bundle.getString(TYPE2);
            exam = bundle.getParcelable(EXAM);
            idGV = bundle.getString(IDGV);
            idGV2 = bundle.getString(IDGV2);
        }
        setContentView(R.layout.activity_quiz);
        initWidgets();
        if (savedInstanceState == null) {
            isLoading = true;
            questions = bundle.getParcelableArrayList("questions");
            currentPage = 0;
            timeInMillis = exam.getTime() * 60 * 1000;
            status = bundle.getInt(STATUS, 0);
        } else {
            isLoading = savedInstanceState.getBoolean("loading");
            questions = savedInstanceState.getParcelableArrayList("questions");
            currentPage = savedInstanceState.getInt("page");
            timeInMillis = savedInstanceState.getLong("time_in_millis");
            status = savedInstanceState.getInt("status");
        }

        if (questions != null && questions.size() > 0) {
            showQuestions();
            if (savedInstanceState == null && status != 0) {
                showResultDialog();
            }
        } else {
            loadQuestions();
        }
    }

    /**
     * @return thay đổi trạng thái sau khi làm bài
     * nếu exam-status (firebase) = 0 -> status = 1
     * nếu exam-status (firebase) = 1 -> status = 2
     */
    private int toggleStatus(int examStatus) {
        if (examStatus == 0)
            return 1;
        else return 2;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("loading", isLoading);
        outState.putParcelableArrayList("questions", questions);
        outState.putInt("page", currentPage);
        outState.putLong("time_in_millis", timeInMillis);
        outState.putInt("status", status);
    }

    private TextView tvEnd;
    private TextView tvTitle;
    private RelativeLayout layoutBody;
    private ProgressBar progressBar;

    private void initWidgets() {
        tvTitle = findViewById(R.id.tv_title);
        layoutBody = findViewById(R.id.layout_body);
        progressBar = findViewById(R.id.progressbar);
        tvEnd = findViewById(R.id.tv_end);
        tvEnd.setOnClickListener(this);
    }

    /*Hiển thị câu hỏi*/
    private QuestionAdapter adapter;

    private void showQuestions() {
        layoutBody.removeAllViews();
        addControlLayout();
        addViewPager();
        adapter = new QuestionAdapter(this, status, questions);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPage);
        initControl(currentPage);
        viewPager.addOnPageChangeListener(onPageChange);
        goneViews(progressBar);
        initEnd();
        initTitle();
        isLoading = false;
    }

    //Cấu hình nút thoát
    private void initEnd() {
        if (status == 0 && questions != null && questions.size() > 0) {
            //Nếu đang thi
            tvEnd.setText(R.string.end_quiz);
            tvEnd.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_timer_off, 0, 0, 0);
        } else {
            tvEnd.setText(R.string.exit);
            tvEnd.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_close, 0, 0, 0);
        }
    }

    //Cấu hình tv_title
    //Nếu đang thi -> Hiển thị bộ đếm thời gian
    //Nếu đang xem kết quả -> Hiển thị điểm
    private final Handler timer = new Handler();

    private void initTitle() {
        if (status != 0) {
            tvTitle.setText(Html.fromHtml("Điểm thi: <u>" +
                    QuizHelper.getStringScore(QuizHelper.getScore(questions)) + "</u>"));
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Hiển thị Dialog thông báo điểm
                    showResultDialog();
                }
            });
        } else {
            tvTitle.setText(QuizHelper.getTimeCountdown(timeInMillis));
            timer.removeCallbacksAndMessages(null);
            timer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    timeInMillis = timeInMillis - 1000;
                    if (timeInMillis != 0) {
                        tvTitle.setText(QuizHelper.getTimeCountdown(timeInMillis));
                        timer.postDelayed(this, 1000);
                    } else {
                        tvTitle.setText(R.string.end_time);
                        status = toggleStatus(exam.getStatus());
                        showResult();
                    }
                }
            }, 1000);
        }
    }

    private void showResultDialog() {
        ResultDialog dialog = ResultDialog.newInstance(exam, questions);
        dialog.show(getSupportFragmentManager(), dialog.getTag());
    }

    /*Tạo ViewPager hiển thị câu hỏi*/
    private ViewPager viewPager;

    private void addViewPager() {
        viewPager = new ViewPager(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.addRule(RelativeLayout.ABOVE, LAYOUT_CONTROL_ID);
        layoutBody.addView(viewPager, params);
    }

    private void initControl(int page) {
        if (adapter.getCount() <= 1) {
            invisibleViews(btNext, btPrevious);
        } else {
            if (page == 0) {
                invisibleViews(btPrevious);
                visibleViews(btNext);
            } else if (page == adapter.getCount() - 1) {
                invisibleViews(btNext);
                visibleViews(btPrevious);
            } else {
                visibleViews(btNext, btPrevious);
            }
        }
        tvPageCount.setText(String.format(Locale.getDefault(), "Câu %d/%d",
                page + 1, adapter.getCount()));
    }

    /*Tạo bảng điều khiển câu hỏi*/
    private ImageButton btPrevious;
    private ImageButton btNext;
    private TextView tvPageCount;
    private static final int LAYOUT_CONTROL_ID = 1;
    private static final int BT_PREVIOUS_ID = 2;
    private static final int BT_NEXT_ID = 3;
    private static final int TV_PAGE_COUNT_ID = 4;

    private void addControlLayout() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, Units.dpToPx(48)
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        RelativeLayout layoutControl = new RelativeLayout(this);
        layoutControl.setId(LAYOUT_CONTROL_ID);
        layoutControl.setBackgroundResource(R.drawable.bg_border_top);
        //TODO: Add button Previous
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                Units.dpToPx(40), Units.dpToPx(40)
        );
        btPrevious = new ImageButton(this);
        btPrevious.setId(BT_PREVIOUS_ID);
        initButton(btPrevious, R.drawable.ic_previous);

        //TODO: Add button Next
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                Units.dpToPx(40), Units.dpToPx(40)
        );
        btNext = new ImageButton(this);
        btNext.setId(BT_NEXT_ID);
        initButton(btNext, R.drawable.ic_next);
        params2.addRule(RelativeLayout.ALIGN_PARENT_END);
        params2.addRule(RelativeLayout.CENTER_VERTICAL);

        //TODO: Add TextView pageCount
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        tvPageCount = new TextView(this);
        tvPageCount.setId(TV_PAGE_COUNT_ID);
        tvPageCount.setBackgroundResource(R.drawable.bg_page_count);
        tvPageCount.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvPageCount.setPadding(Units.dpToPx(8), Units.dpToPx(8), Units.dpToPx(8), Units.dpToPx(8));
        tvPageCount.setGravity(Gravity.CENTER);
        tvPageCount.setOnClickListener(this);
        tvPageCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvPageCount.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_list, 0);
        tvPageCount.setCompoundDrawablePadding(Units.dpToPx(8));
        tvPageCount.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        params3.addRule(RelativeLayout.CENTER_IN_PARENT);

        layoutControl.addView(btPrevious, params1);
        layoutControl.addView(btNext, params2);
        layoutControl.addView(tvPageCount, params3);

        layoutBody.addView(layoutControl, params);
    }

    private void showErrorPage(String err) {
        TextView textView = new TextView(this);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(Html.fromHtml(err));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutBody.removeAllViews();
        layoutBody.addView(textView, params);
        goneViews(progressBar);
    }

    private ViewPager.OnPageChangeListener onPageChange = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //On item change
            currentPage = position;
            initControl(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_end:
                onBackPressed();
                break;
            case BT_PREVIOUS_ID:
                gotoPage(currentPage - 1);
                break;
            case BT_NEXT_ID:
                gotoPage(currentPage + 1);
                break;
            case TV_PAGE_COUNT_ID:
                showListQuestionDialog();
                break;
        }
    }

    private void gotoPage(int position) {
        viewPager.setCurrentItem(position);
    }

    private void showListQuestionDialog() {
        ListQuestionDialog dialog = ListQuestionDialog.newInstance(status, questions);
        dialog.show(getSupportFragmentManager(), dialog.getTag());
    }

    @Override
    public void onQuestionChange(int position) {
        gotoPage(position);
    }

    @Override
    public void onBackPressed() {
        if (!isLoading) {
            if (questions != null && questions.size() > 0) {
                showCautionDialog();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void showCautionDialog() {
        String tag;
        String title;
        String message;
        if (status == 0) {
            tag = "endPlay";
            title = "Nộp bài";
            message = "Bạn có muốn dừng thi ngay bây giờ?";
        } else {
            tag = "exit";
            title = "Thoát";
            message = "Bạn có muốn thoát?";
        }
        CautionDialog dialog = CautionDialog.newInstance(tag, title, message);
        dialog.show(getSupportFragmentManager(), tag);
    }

    @Override
    public void onConfirm(String tag) {
        if (tag.equals("endPlay")) {
            showResult();
        }
        finish();
    }

    private void showResult() {
        updateHistory();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE, type);
        exam.setLastHistory(lastHistory());
        bundle.putParcelable(EXAM, exam);
        bundle.putInt(STATUS, toggleStatus(exam.getStatus()));
        bundle.putParcelableArrayList("questions", questions);
        Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private History lastHistory() {
        History history = new History();
        history.setType(type);
        history.setName(exam.getName());
        history.setExamID(exam.getId());
        history.setSubjectID(exam.getSubjectID());
        history.setScore(QuizHelper.getScore(questions));
        history.setSubmitted(System.currentTimeMillis());
        history.setTimePlay(exam.getTime() * 60 * 1000 - timeInMillis);
        history.setChoice(QuizHelper.setChoice(questions));
        return history;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_animation, R.anim.scale_out);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bundle.clear();
        timer.removeCallbacksAndMessages(null);
        //Gửi thông điệp để thay đổi giao diện luyện tập
        if (status == 0 && type.equals("practice")) {
            EventBus.getDefault().post(exam);
        }
    }

    //Lưu kết quả thi
    private DatabaseReference myRef;

    private void updateHistory() {
        //Lưu kết quả vào database
        dbHelper.insertHistory(lastHistory());
        if (type.equals("exam")) {
            myRef = FirebaseDatabase.getInstance().getReference("histories");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String path = pref.getData(Pref.UID) + "/" + lastHistory().getSubmitted();
                    myRef.child(path).child("name").setValue(pref.getData(Pref.NAME));
                    myRef.child(path).child("subjectID").setValue(exam.getSubjectID());
                    myRef.child(path).child("type").setValue(type);
                    myRef.child(path).child("examID").setValue(exam.getId());
                    myRef.child(path).child("timePlay").setValue(lastHistory().getTimePlay());
                    myRef.child(path).child("score").setValue(lastHistory().getScore());
                    myRef.child(path).child("choice").setValue(lastHistory().getChoice());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * Helper
     **/
    private void loadQuestions() {
        if (type.equals("practice")) {
            //Tải đề thi từ database trên assets
            questions = dbAssetHelper.questions(exam.getId());
            if (questions.size() > 0) {
                //Cập nhật lại câu hỏi
                updateQuestions();
                //Hiển thị câu hỏi
                showQuestions();
                if (status != 0) {
                    showResultDialog();
                }
            } else {
                showErrorPage("Chưa có câu hỏi cho phần ôn luyện này");
                initEnd();
            }
        } else {
            if (status == 2 && type.equals("exam")) {
                loadHistoryStatus();
            } else {
                loadJsonFile();
            }
        }
        isLoading = false;
    }

    //Tải lại trạng thái xem kết quả trên firebase (trong TH đọc lịch sử thi)
    //Mặc định khi xem lịch sử thi sẽ để là 2
    //Nhưng nếu tải lịch sử thi của "exam" thì tải lại xem người dùng có được xem đáp án hay không
    private void loadHistoryStatus() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("exams");
        myRef.child(String.valueOf(exam.getSubjectID())).child(String.valueOf(exam.getId()))
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    Exam exam = dataSnapshot.getValue(Exam.class);
                                    status = toggleStatus(exam.getStatus());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                loadJsonFile();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
    }

    //Tải đề thi từ file lưu trên thư mục firebase
    private void loadJsonFile() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String path;
        if (type.equals("exam")) {
            //Toast.makeText(this, type2, Toast.LENGTH_SHORT).show();
            if (type2 != null) {
                if (type2.equals("exam2")) {
                    String[] words = exam.getName().split("\\s");
                    path = "onluyen/" + exam.getSubjectID() + "/" + words[0] + "/" + exam.getId() + ".json";
                    //Toast.makeText(QuizActivity.this, path, Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(this, exam.getName(), Toast.LENGTH_SHORT).show();
                    String[] words = exam.getName().split("\\s");
                    //Toast.makeText(this, "Chuoi cat: "+words[0]+"|"+words[1]+"|"+words[2], Toast.LENGTH_LONG).show();
                    path = "exams/" + exam.getSubjectID() + "/" + words[0] + "/" + exam.getId() + ".json";
                    //Toast.makeText(QuizActivity.this, type2+path, Toast.LENGTH_SHORT).show();
                }
            } else {
                String[] words = exam.getName().split("\\s");
                path = "onluyen/" + exam.getSubjectID() + "/" + words[0] + "/" + exam.getId() + ".json";
            }
        } else {
            path = "upload/" + pref.getData(Pref.UID) + "/" + exam.getId() + ".json";
        }

        storageRef.child(path).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        readJsonFile(uri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (type2 == null) {
                            showErrorPage("<b>Bạn không thể xem lại đề thi!</b>");
                            initEnd();
                        } else {
                            showErrorPage("<b>Lỗi:</b> Không tìm thấy File câu hỏi");
                            initEnd();
                        }
                    }
                });
    }

    private void readJsonFile(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String s = response.toString();
                        if (s != null) {
                            questions = IOHelper.questions(s);
                            if (questions.size() > 0) {
                                updateQuestions();
                                showQuestions();
                                if (status != 0) {
                                    showResultDialog();
                                }
                            } else {
                                showErrorPage("<b>Lỗi</b>: Định dạng file không đúng");
                                initEnd();
                            }
                        } else {
                            showErrorPage("Chưa có câu hỏi cho đề thi này");
                            initEnd();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorPage("<b>Lỗi</b>: Định dạng file không đúng");
                        initEnd();
                    }
                });
        requestQueue.add(request);
    }

    //Cập nhật lại câu hỏi sau khi tải
    private void updateQuestions() {
        if (status == 0) {
            //Nếu bắt đầu thi -> Đảo vị trí ngẫu nhiên các câu hỏi
            Collections.shuffle(questions);
        } else {
            //Nếu xem kết quả -> Cập nhật câu trả lời của người dùng
            questions = QuizHelper.historyQuestions(exam.getLastHistory().getChoice(), questions);
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

    private void initButton(ImageButton button, int icon) {
        button.setImageResource(icon);
        button.setBackgroundResource(R.drawable.bg_button_circle_white);
        button.setOnClickListener(this);
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

    protected void invisibleViews(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
