package id.kenshiro.app.panri;

import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class SplashScreenActivity extends MylexzActivity {
    private static final int APP_IS_OLDER_VERSION = 0xfab;
    private static final int APP_IS_SAME_VERSION = 0xfaf;
    public static final int APP_IS_NEWER_VERSION = 0xf44;
    public static final int APP_IS_FIRST_USAGE = 0xfaa;
    TextView judul;
    Button btnNext;
    private ProgressBar dialog;
    private int marginBtnFactor = 8;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actsplash_main);
        setAllViews();
    }

    private void setAllViews() {
        judul = findViewById(R.id.actsplash_id_txtjudul);
        btnNext = findViewById(R.id.actsplash_id_btnlanjut);
        dialog = findViewById(R.id.actsplash_id_progress);
        judul.setTypeface(Typeface.createFromAsset(getAssets(), "Gecko_PersonalUseOnly.ttf"), Typeface.BOLD);
        btnNext.setTypeface(Typeface.createFromAsset(getAssets(), "Geometric_black.ttf"), Typeface.BOLD);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);

        int newSizeBtn = p.x - ((p.x / marginBtnFactor) * 2);
        btnNext.setMinimumWidth(newSizeBtn);
    }

    private class LoaderTask extends AsyncTask<Void, Void, Integer> {
        String folder_app_version = "app_version";
        String file_db_version = "db_version";
        int app_condition = 0;

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
                // update into databases
                new CheckAndMoveDB(SplashScreenActivity.this, "database_penyakitpadi.db").upgradeDB();
            }
            // if exists
            else {
                int current_db_version = getDBVersion();
                int available_db_version = getDBVersionInAssets();
                // apply newer version
                if (current_db_version != available_db_version) {
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
                }
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
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}