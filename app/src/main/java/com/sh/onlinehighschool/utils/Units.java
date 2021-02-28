package com.sh.onlinehighschool.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Units {

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(int px) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String getCost(long cost){
        if (cost == 0){
            return "Miễn phí";
        } else {
            return cost + " xu";
        }
    }
}
