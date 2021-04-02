package com.sh.onlinehighschool.activity;

import android.content.Intent;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;

import com.daimajia.androidanimations.library.Techniques;
import com.sh.onlinehighschool.R;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class SplashScreenActivity extends AwesomeSplash {


    @Override
    public void initSplash(ConfigSplash configSplash) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // background animation
        configSplash.setBackgroundColor(R.color.white);
        configSplash.setAnimCircularRevealDuration(500);
        configSplash.setRevealFlagX(Flags.REVEAL_LEFT);
        configSplash.setRevealFlagX(Flags.REVEAL_BOTTOM);

        // logo
        configSplash.setLogoSplash(R.drawable.ic_logo_truong);
        configSplash.setOriginalHeight(30);
        configSplash.setOriginalWidth(30);
        // title
        configSplash.setTitleSplash("OHS - Trường học trực tuyến");
        configSplash.setTitleTextColor(R.color.colorPrimary);
        configSplash.setTitleTextSize(26f);
        configSplash.setAnimTitleDuration(1000);
        configSplash.setAnimTitleTechnique(Techniques.FlipInX);
    }

    @Override
    public void animationsFinished() {
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        finish();
    }
}