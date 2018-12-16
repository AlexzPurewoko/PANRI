package id.kenshiro.app.panri;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.mylexz.utils.MylexzActivity;

import id.kenshiro.app.panri.opt.LogIntoCrashlytics;
import io.fabric.sdk.android.Fabric;
import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.onsplash.LoaderTask;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SplashScreenActivity extends MylexzActivity {

    private static final String TAG = "SplashScreenActivity";
    public int app_condition = 0;
    public int db_condition = 0;
    public TextView judul;
    public Button btnNext;
    public LinearLayout linearIndicator;
    public TextView indicators;
    private GifImageView gifImageView;
    private int marginBtnFactor = 8;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)  // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);
        try {
            setContentView(R.layout.actsplash_main);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setAllViews();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getWindow().setStatusBarColor(getColor(R.color.color_status_white_dark));
                } else
                    getWindow().setStatusBarColor(getResources().getColor(R.color.color_status_white_dark));
            }
        } catch (Throwable e) {
            String keyEx = getClass().getName() + "_onCreate()";
            String resE = String.format("UnHandled Exception Occurs(Throwable) e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            LOGE(keyEx, resE);
        }
        // start a task into main activity
        new LoaderTask(this).execute();
    }

    private void setAllViews() {
        judul = findViewById(R.id.actsplash_id_txtjudul);
        btnNext = findViewById(R.id.actsplash_id_btnlanjut);
        gifImageView = findViewById(R.id.actsplash_id_loadingview);
        indicators = findViewById(R.id.actsplash_id_txtsplash_indicator);
        linearIndicator = findViewById(R.id.actsplash_id_linear_indicator);
        judul.setTypeface(Typeface.createFromAsset(getAssets(), "Gecko_PersonalUseOnly.ttf"), Typeface.BOLD);
        btnNext.setTypeface(Typeface.createFromAsset(getAssets(), "RifficFree-Bold.ttf"), Typeface.BOLD);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        int newSizeBtn = p.x - ((p.x / marginBtnFactor) * 2);
        btnNext.setMinimumWidth(newSizeBtn);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt(KeyListClasses.APP_CONDITION_KEY, app_condition);
                args.putInt(KeyListClasses.DB_CONDITION_KEY, db_condition);
                MylexzActivity activity = SplashScreenActivity.this;
                activity.finish();
                System.gc();
                Intent a = new Intent(activity, TutorialFirstUseActivity.class);
                a.putExtras(args);
                activity.startActivity(a);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });
        indicators.setText("Mempersiapkan data...");
    }

    @Override
    protected void onDestroy() {
        //get my pid
        final int pid = Process.myPid();
        GifDrawable b;
        Handler postClose = new Handler();
        postClose.postDelayed(new Runnable() {
            @Override
            public void run() {
                Process.killProcess(pid);
            }
        }, 2500);
        super.onDestroy();
    }
}