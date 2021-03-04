package com.sh.onlinehighschool.callback;

import com.sh.onlinehighschool.model.YoutubeVideo;

public interface OnUploadVideoDialogListener {

    void onClickConfirmUploadVideo(YoutubeVideo model);

    void onClickCancelUploadVideo(YoutubeVideo model);

}
