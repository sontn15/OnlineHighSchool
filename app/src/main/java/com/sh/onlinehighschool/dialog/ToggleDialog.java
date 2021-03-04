package com.sh.onlinehighschool.dialog;

import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.core.view.ViewCompat;

import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.adapter.YearAdapter;
import com.sh.onlinehighschool.base.BaseDialog;
import com.sh.onlinehighschool.utils.DBAssetHelper;
import com.sh.onlinehighschool.utils.Pref;
import com.sh.onlinehighschool.views.AutoRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;


public class ToggleDialog extends BaseDialog {

    private Pref pref;
    private DBAssetHelper dbAssetHelper;

    @Override
    protected void getData() {
        pref = new Pref(requireActivity());
        dbAssetHelper = new DBAssetHelper(requireActivity());
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    protected int layoutRes() {
        return R.layout.dialog_toggle;
    }

    @Override
    protected double dialogWidth() {
        return 1;
    }

    @Override
    protected double dialogHeight() {
        return 0;
    }

    @Override
    protected int dialogGravity() {
        return Gravity.BOTTOM;
    }

    private RelativeLayout layoutDialog;
    private AutoRecyclerView recyclerViewYear;
    private ImageButton btConfirm;

    @Override
    protected void initWidgets(View view) {
        layoutDialog = view.findViewById(R.id.layout_dialog);
        recyclerViewYear = view.findViewById(R.id.recycler_view_year);
        btConfirm = view.findViewById(R.id.bt_confirm);
    }

    @Override
    protected void configView() {
        layoutDialog.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up));
        initRecyclerViewYear();
        initConfirm();
    }

    private YearAdapter yearAdapter;

    private void initRecyclerViewYear() {
        yearAdapter = new YearAdapter(pref.getYear(), dbAssetHelper.years());
        recyclerViewYear.setHasFixedSize(true);
        recyclerViewYear.setAdapter(yearAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewYear, false);
    }

    private void initConfirm() {
        btConfirm.setOnClickListener(v -> {
            EventBus.getDefault().post(hashMap());
            dismiss();
        });
    }

    private HashMap<String, String> hashMap() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("filter", "1");
        hashMap.put("year", yearAdapter.getCurrentYear());
        return hashMap;
    }
}
