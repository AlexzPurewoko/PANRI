package id.kenshiro.app.panri;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
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

import com.mylexz.utils.MylexzActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import id.kenshiro.app.panri.helper.CheckAndMoveDB;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import pl.droidsonroids.gif.GifImageView;

public class SplashScreenActivity extends MylexzActivity {
    public static final int APP_IS_OLDER_VERSION = 0xfab;
    public static final int APP_IS_SAME_VERSION = 0xfaf;
    public static final int APP_IS_NEWER_VERSION = 0xf44;
    public static final int APP_IS_FIRST_USAGE = 0xfaa;
    public static final int DB_IS_FIRST_USAGE = 0xaac;
    public static final int DB_IS_NEWER_VERSION = 0xaab;
    public static final int DB_IS_OLDER_IN_APP_VERSION = 0xaaf;
    public static final int DB_IS_SAME_VERSION = 0xaca;
    public static final String APP_CONDITION_KEY = "APP_CONDITION_KEY_EXTRAS";
    public static final String DB_CONDITION_KEY = "DB_CONDITION_KEY_EXTRAS";
    private static final String TAG = "SplashScreenActivity";
    int app_condition = 0;
    int db_condition = 0;
    TextView judul;
    Button btnNext;
    private GifImageView gifImageView;
    private int marginBtnFactor = 8;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        new LoaderTask().execute();
    }

    private void setAllViews() {
        judul = findViewById(R.id.actsplash_id_txtjudul);
        btnNext = findViewById(R.id.actsplash_id_btnlanjut);
        gifImageView = findViewById(R.id.actsplash_id_loadingview);
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
                args.putInt(APP_CONDITION_KEY, app_condition);
                args.putInt(DB_CONDITION_KEY, db_condition);
                MylexzActivity activity = SplashScreenActivity.this;
                activity.finish();
                System.gc();
                Intent a = new Intent(activity, TutorialFirstUseActivity.class);
                a.putExtras(args);
                activity.startActivity(a);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });
    }

    private class LoaderTask extends AsyncTask<Void, Void, Integer> {
        String folder_app_version = "app_version";
        String file_db_version = "db_version";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                // if is firstUsage then execute command below
                checkAndSaveAppVersion();
                updateDBIfItsNewVersion();
            } catch (IOException e) {
                SplashScreenActivity.this.LOGE("Task.background()", "IOException occured when executing checkAndSaveAppVersion() & updateDBIfItsNewVersion();", e);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                SplashScreenActivity.this.LOGE("Task.background()", "InterruptedException during call Thread.sleep()", e);
            }
            publishProgress();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                SplashScreenActivity.this.LOGE("Task.background()", "InterruptedException during call Thread.sleep()", e);
            }
            return app_condition;
        }

        private void updateDBIfItsNewVersion() throws IOException {
            File path = new File(SplashScreenActivity.this.getApplicationInfo().dataDir, "files");
            path.mkdir();
            File content = new File(path, this.file_db_version);
            if (!content.exists()) {
                AssetManager assetManager = getAssets();
                InputStream fis = assetManager.open(this.file_db_version);
                FileOutputStream fos = new FileOutputStream(content);
                int read;
                while ((read = fis.read()) != -1) {
                    fos.write(read);
                }
                fis.close();
                fos.close();
                db_condition = DB_IS_FIRST_USAGE;
                // update into databases
                new CheckAndMoveDB(SplashScreenActivity.this, "database_penyakitpadi.db").upgradeDB();
            }
            // if exists
            else {
                int current_db_version = getDBVersion();
                int available_db_version = getDBVersionInAssets();
                // apply newer version
                if (current_db_version < available_db_version) {
                    AssetManager assetManager = getAssets();
                    InputStream fis = assetManager.open(this.file_db_version);
                    FileOutputStream fos = new FileOutputStream(content);
                    int read;
                    while ((read = fis.read()) != -1) {
                        fos.write(read);
                    }
                    fis.close();
                    fos.close();
                    // update into databases
                    new CheckAndMoveDB(SplashScreenActivity.this, "database_penyakitpadi.db").upgradeDB();
                    db_condition = DB_IS_NEWER_VERSION;
                } else if (current_db_version > available_db_version)
                    db_condition = DB_IS_OLDER_IN_APP_VERSION;
                else
                    db_condition = DB_IS_SAME_VERSION;
            }

        }

        private int getDBVersionInAssets() throws IOException {
            // gets the version
            InputStream fis = getAssets().open(this.file_db_version);
            byte[] available = new byte[fis.available()];
            fis.read(available);
            String out = new String(available);
            fis.close();
            return Integer.parseInt(out);
        }

        private int getDBVersion() throws IOException {
            File path = new File(SplashScreenActivity.this.getApplicationInfo().dataDir, "files");
            File content = new File(path, this.file_db_version);
            // gets the version
            FileInputStream fis = new FileInputStream(content);
            byte[] available = new byte[fis.available()];
            fis.read(available);
            String out = new String(available);
            fis.close();
            return Integer.parseInt(out);
        }

        private void checkAndSaveAppVersion() throws IOException {
            int version = BuildConfig.VERSION_CODE;
            File path = new File(SplashScreenActivity.this.getApplicationInfo().dataDir, "files");
            path.mkdir();
            File content = new File(path, this.folder_app_version);
            if (!content.exists()) {
                app_condition = APP_IS_FIRST_USAGE;
                String data = String.valueOf(version);
                FileOutputStream fos = new FileOutputStream(content);
                fos.write(data.getBytes());
                fos.close();
            } else {
                // check app version and if its newer, push command to Bundle args
                FileInputStream fis = new FileInputStream(content);
                byte[] buf = new byte[fis.available()];
                fis.read(buf);
                int current_version = Integer.parseInt(new String(buf));
                fis.close();
                if (current_version < version) {
                    app_condition = APP_IS_NEWER_VERSION;
                    String data = String.valueOf(version);
                    FileOutputStream fos = new FileOutputStream(content);
                    fos.write(data.getBytes());
                    fos.close();
                } else if (current_version > version) {
                    app_condition = APP_IS_OLDER_VERSION;
                } else
                    app_condition = APP_IS_SAME_VERSION;
            }
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            //gifImageView.setVisibility(View.GONE);
            //btnNext.setVisibility(View.VISIBLE);
            animateAndForward();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            gifImageView.setFreezesAnimation(true);
        }

        private void animateAndForward() {
            Fade in = new Fade(Fade.IN);
            in.setDuration(1200);
            TransitionManager.beginDelayedTransition((RelativeLayout) findViewById(R.id.actsplash_id_bawah_layout), in);
            gifImageView.setVisibility(View.GONE);
            if (app_condition == APP_IS_FIRST_USAGE)
                btnNext.setVisibility(View.VISIBLE);
            else {
                Bundle args = new Bundle();
                args.putInt(APP_CONDITION_KEY, app_condition);
                args.putInt(DB_CONDITION_KEY, db_condition);
                MylexzActivity activity = SplashScreenActivity.this;
                activity.finish();
                System.gc();
                Intent a = new Intent(activity, MainActivity.class);
                a.putExtras(args);
                activity.startActivity(a);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }
}