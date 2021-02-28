package com.sh.onlinehighschool.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.activity.PracticeActivity;
import com.sh.onlinehighschool.adapter.SubjectAdapter;
import com.sh.onlinehighschool.callback.OnRecyclerViewListener;
import com.sh.onlinehighschool.model.Subject;
import com.sh.onlinehighschool.utils.DBAssetHelper;
import com.sh.onlinehighschool.utils.Pref;
import com.sh.onlinehighschool.views.AutoRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initWidgets(view);
        return view;
    }

    private TextView tvTitle;
    private TextView tvStatus;
    private AutoRecyclerView recyclerView;

    private void initWidgets(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvStatus = view.findViewById(R.id.tv_status);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private Pref pref;
    private DBAssetHelper dbAssetHelper;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pref = new Pref(getActivity());
        dbAssetHelper = new DBAssetHelper(getActivity());
        initData(pref.getFaculty(), pref.getYear());
    }

    private void initData(String faculty, String year) {
        final ArrayList<Subject> subjects = dbAssetHelper.subjects(faculty, year);
        if (subjects.size() == 0) {
            tvTitle.setVisibility(View.GONE);
            tvStatus.setVisibility(View.VISIBLE);
            tvStatus.setText(Html.fromHtml("Không có dữ liệu cho điều kiện lọc:" +
                    "<br>• " + year +
                    "<br>• " + faculty +
                    "<br>Vui lòng chọn điều kiện lọc khác"));
        } else {
            tvStatus.setVisibility(View.GONE);
            if (getTitle(faculty, year).equals("")) {
                tvTitle.setVisibility(View.GONE);
            } else {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(getTitle(faculty, year));
            }
        }
        SubjectAdapter adapter = new SubjectAdapter(subjects, new OnRecyclerViewListener() {
            @Override
            public void onItemChange(View view, int position) {
                Subject subject = subjects.get(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("SUBJECT", subject);
                Intent intent = new Intent(getActivity(), PracticeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.no_animation);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
    }

    private String getTitle(String faculty, String year) {
        String title = "";
        if (!faculty.equals(Pref.DEFAULT_FACULTY)) {
            title = faculty.toUpperCase();
        }
        if (!year.equals(Pref.DEFAULT_YEAR)) {
            if (title.equals("")) {
                title = year.toUpperCase();
            } else {
                title = title + "\n(" + year + ")";
            }
        }
        return title;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HashMap<String, String> hashMap) {
        if (hashMap != null) {
            String faculty = hashMap.get("faculty");
            String year = hashMap.get("year");
            if ((faculty != null && !faculty.equals(pref.getFaculty())) ||
                    (year != null && !year.equals(pref.getYear()))) {
                initData(faculty, year);
                pref.saveData(Pref.FACULTY, faculty);
                pref.saveData(Pref.YEAR, year);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }
}
