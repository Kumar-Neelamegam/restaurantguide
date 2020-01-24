package at.jku.assistivetechnology.myapplication.coreModules.coreModules;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.yariksoffice.lingver.Lingver;

import at.jku.assistivetechnology.myapplication.R;
import at.jku.assistivetechnology.myapplication.coreModules.utilities.SharedPrefUtils;

public class SplashActivity extends CoreActivity
{

    private static final long SPLASH_DURATION = 3000;
    SpinKitView spinKitView;
    SharedPrefUtils sharedPrefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPrefUtils sharedPrefUtils = SharedPrefUtils.getInstance(this);
        if (sharedPrefUtils.isDarkMode()) setTheme(R.style.AppTheme);
        else setTheme(R.style.AppTheme_Light);
        setContentView(R.layout.activity_splash);

        try {
            isStoragePermissionGranted();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onPermissionsGranted(int requestCode) {

        try {

            getInit();

        } catch (Exception e) {

            e.printStackTrace();
        }


    }


    private void getInit() {
        Lingver.init(getApplication(), Common.defaultLanguage);

        sharedPrefUtils = SharedPrefUtils.getInstance(this);
        if (!sharedPrefUtils.getLatestLanguage().equals("")) {
            Lingver.getInstance().setLocale(this, sharedPrefUtils.getLatestLanguage());   //set the saved language
            sharedPrefUtils.saveLatestLanguage(sharedPrefUtils.getLatestLanguage());
        }

        getSupportActionBar().hide();
        spinKitView=findViewById(R.id.progressBar);
        Sprite animate = new Wave();
        spinKitView.setIndeterminateDrawable(animate);
        ImageView logo=findViewById(R.id.imageView);
        doBounceAnimation(logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate();
            }
        }, SPLASH_DURATION);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void navigate() {
        startActivity(new Intent(SplashActivity.this, ListActivity.class));
        finish();
    }

    private void doBounceAnimation(View targetView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationY", 0, 100, 0);
        animator.setInterpolator(new BounceInterpolator());
        animator.setStartDelay(500);
        animator.setDuration(2500);
        animator.start();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void bindViews() {

    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
