package com.sh.onlinehighschool.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.sh.onlinehighschool.R;
import com.sh.onlinehighschool.base.BaseDialog;
import com.sh.onlinehighschool.callback.OnUploadVideoDialogListener;
import com.sh.onlinehighschool.model.Subject;
import com.sh.onlinehighschool.model.YoutubeVideo;
import com.sh.onlinehighschool.utils.DBAssetHelper;
import com.sh.onlinehighschool.utils.Pref;

import java.util.ArrayList;
import java.util.Objects;

public class UploadVideoDialog extends BaseDialog implements View.OnClickListener {

    public static UploadVideoDialog newInstance(String tag, String title, String message) {
        UploadVideoDialog dialog = new UploadVideoDialog();
        Bundle args = new Bundle();
        args.putString("TAG", tag);
        args.putString("TITLE", title);
        args.putString("MESSAGE", message);
        dialog.setArguments(args);
        return dialog;
    }

    private String tag;
    private String title;
    private String message;

    @Override
    protected void getData() {
        if (getArguments() != null) {
            tag = getArguments().getString("TAG");
            title = getArguments().getString("TITLE");
            message = getArguments().getString("MESSAGE");
        }
    }

    @Override
    protected int layoutRes() {
        return R.layout.dialog_upload_video;
    }

    @Override
    protected double dialogWidth() {
        return 0.8;
    }

    @Override
    protected double dialogHeight() {
        return 0;
    }

    @Override
    protected int dialogGravity() {
        return Gravity.CENTER;
    }

    private TextView tvTitle;
    private TextInputLayout edtTitleVideo, edtVideoId, edtImageUrl;
    private Spinner spnMonHoc;

    private String titleVideo, imageUrl, videoId;

    @Override
    protected void initWidgets(View view) {
        edtTitleVideo = view.findViewById(R.id.input_title_video);
        edtImageUrl = view.findViewById(R.id.input_imageurl_video);
        edtVideoId = view.findViewById(R.id.input_id_video);

        tvTitle = view.findViewById(R.id.tv_title_video);

        Pref pref = new Pref(requireActivity());
        int khoi = pref.getDataInt(Pref.KHOI);

        DBAssetHelper dbAssetHelper = new DBAssetHelper(requireActivity());
        ArrayList<Subject> subjects = dbAssetHelper.subjects(String.valueOf(khoi));

        spnMonHoc = view.findViewById(R.id.spnMonHocUploadVideo);
        ArrayAdapter aa = new ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, subjects);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMonHoc.setAdapter(aa);

        view.findViewById(R.id.tv_cancel_video).setOnClickListener(this);
        view.findViewById(R.id.tv_confirm_video).setOnClickListener(this);


        Objects.requireNonNull(edtTitleVideo.getEditText()).setInputType(InputType.TYPE_CLASS_TEXT);
        edtTitleVideo.getEditText().setText(titleVideo);
        edtTitleVideo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                titleVideo = editable.toString();
                edtTitleVideo.setErrorEnabled(false);
            }
        });

        Objects.requireNonNull(edtImageUrl.getEditText()).setInputType(InputType.TYPE_CLASS_TEXT);
        edtImageUrl.getEditText().setText(imageUrl);
        edtImageUrl.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                imageUrl = editable.toString();
                edtImageUrl.setErrorEnabled(false);
            }
        });

        Objects.requireNonNull(edtVideoId.getEditText()).setInputType(InputType.TYPE_CLASS_TEXT);
        edtVideoId.getEditText().setText(videoId);
        edtVideoId.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                videoId = editable.toString();
                edtVideoId.setErrorEnabled(false);
            }
        });
    }

    @Override
    protected void configView() {
        tvTitle.setText(title);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_confirm_video) {
            Pref pref = new Pref(requireActivity());
            YoutubeVideo youtubeVideo = new YoutubeVideo();
            youtubeVideo.setTitle(titleVideo);
            youtubeVideo.setVideoId(videoId);
            youtubeVideo.setImageUrl(imageUrl);
            youtubeVideo.setKhoi(pref.getDataInt(Pref.KHOI));
            youtubeVideo.setSubject(spnMonHoc.getSelectedItem().toString());
            mListener.onClickConfirmUploadVideo(youtubeVideo);
        }
        dismiss();
    }

    private OnUploadVideoDialogListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnUploadVideoDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }
}
