package id.kenshiro.app.panri;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Fade;
import android.support.transition.Scene;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionValues;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.mylexz.utils.DiskLruObjectCache;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SimpleDiskLruCache;

import io.fabric.sdk.android.Fabric;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.kenshiro.app.panri.helper.CheckAndMoveDB;
import id.kenshiro.app.panri.helper.DecodeBitmapHelper;
import id.kenshiro.app.panri.helper.ListCiriCiriPenyakit;
import id.kenshiro.app.panri.helper.ListNamaPenyakit;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.helper.TampilListPenyakitHelper;
import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.CheckConnection;
import id.kenshiro.app.panri.opt.onsplash.CheckCacheAndConfThread;
import id.kenshiro.app.panri.opt.onsplash.ConfigureCache;
import id.kenshiro.app.panri.opt.onsplash.ExtractAndConfigureData;
import id.kenshiro.app.panri.opt.onsplash.LoaderTask;
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
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.actsplash_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setAllViews();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getColor(R.color.color_status_white_dark));
            } else
                getWindow().setStatusBarColor(getResources().getColor(R.color.color_status_white_dark));
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

}