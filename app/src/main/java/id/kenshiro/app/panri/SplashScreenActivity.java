package id.kenshiro.app.panri;

import android.content.Intent;
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

import com.mylexz.utils.DiskLruObjectCache;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SimpleDiskLruCache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import id.kenshiro.app.panri.helper.CheckAndMoveDB;
import id.kenshiro.app.panri.helper.DecodeBitmapHelper;
import id.kenshiro.app.panri.helper.ListCiriCiriPenyakit;
import id.kenshiro.app.panri.helper.ListNamaPenyakit;
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
    public static final String LIST_PENYAKIT_CIRI_KEY_CACHE = "key_ciri_data_penyakit";
    int app_condition = 0;
    int db_condition = 0;
    TextView judul;
    Button btnNext;
    LinearLayout linearIndicator;
    TextView indicators;
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
        indicators.setText("Mempersiapkan data...");
    }

    private static class LoaderTask extends AsyncTask<Void, Integer, Integer> {
        private static final long MAX_CACHE_BUFFERED_SIZE = 1048576;
        private static final int QUALITY_FACTOR = 40;
        String folder_app_version = "app_version";
        String file_db_version = "db_version";
        File fileCache;
        SimpleDiskLruCache diskCache;
        private SQLiteDatabase sqlDB;
        private HashMap<Integer, ListCiriCiriPenyakit> listCiriCiriPenyakitHashMap;
        SplashScreenActivity ctx;

        LoaderTask(SplashScreenActivity ctx) {
            this.ctx = ctx;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fileCache = new File(ctx.getCacheDir(), "cache");
            fileCache.mkdir();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                // if is firstUsage then execute command below
                checkAndSaveAppVersion();
                updateDBIfItsNewVersion();
            } catch (IOException e) {
                ctx.LOGE("Task.background()", "IOException occured when executing checkAndSaveAppVersion() & updateDBIfItsNewVersion();", e);
            }
            // creates cache directory if not exists
            try {
                diskCache = new SimpleDiskLruCache(fileCache);
            } catch (IOException e) {
                ctx.LOGE("Task.background()", "IOException occured when initialize DiskLruObjectCache instance", e);
            }
            publishProgress(0);
            synchronized (this) {
                switch (ctx.app_condition) {
                    case APP_IS_OLDER_VERSION:
                    case APP_IS_NEWER_VERSION: {
                        cleanCache();
                        createCacheOperation();
                    }
                    break;
                    case APP_IS_FIRST_USAGE: {
                        createCacheOperation();
                    }
                    break;
                    case APP_IS_SAME_VERSION: {
                        boolean status_cache_dirs = validateCacheDirs();
                        if (!status_cache_dirs) {
                            createCacheOperation();
                        }
                    }
                }
            }
            publishProgress(1);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                ctx.LOGE("Task.background()", "Interrupted signal exception!", e);
            }
            try {
                diskCache.close();
            } catch (IOException e) {
                ctx.LOGE("Task.background()", "IOException occured when closing diskCache", e);
            }
            return ctx.app_condition;
        }

        private boolean validateCacheDirs() {
            if (fileCache.list() == null)
                return true;
            return false;
        }

        private void createCacheOperation() {
            // caching images
            cachingBitmapsViewPager();
            // caching Objects
            try {
                cachingListPenyakit();
            } catch (IOException e) {
                ctx.LOGE("Task.background()", "IOException occured when create listCiriCiriPenyakit Cache", e);
            }
        }

        private void cachingListPenyakit() throws IOException {
            sqlDB = SQLiteDatabase.openOrCreateDatabase("/data/data/id.kenshiro.app.panri/databases/database_penyakitpadi.db", null);
            loadAllDataCiri();
            if (listCiriCiriPenyakitHashMap != null) {
                synchronized (diskCache) {
                    diskCache.putObjectWithEncode(LIST_PENYAKIT_CIRI_KEY_CACHE, listCiriCiriPenyakitHashMap);
                }
            }
        }

        private void loadAllDataCiri() {
            listCiriCiriPenyakitHashMap = new HashMap<Integer, ListCiriCiriPenyakit>();
            // input ciri ciri penyakit
            Cursor curr = sqlDB.rawQuery("select ciri from ciriciri", null);
            curr.moveToFirst();
            while (!curr.isAfterLast()) {
                String ciri = curr.getString(0);
                listCiriCiriPenyakitHashMap.put(curr.getPosition() + 1, new ListCiriCiriPenyakit(ciri, false, false));
                curr.moveToNext();
            }
            curr.close();
            System.gc();
            ///////////////////////////
            // input usefirst flags
            curr = sqlDB.rawQuery("select usefirst from ciriciri", null);
            curr.moveToFirst();
            while (!curr.isAfterLast()) {
                String ciri = curr.getString(0);
                listCiriCiriPenyakitHashMap.get(curr.getPosition() + 1).setUsefirst_flags(Boolean.parseBoolean(ciri));
                curr.moveToNext();
            }
            curr.close();
            System.gc();
            ///////////////////////////
            // input ask flags
            curr = sqlDB.rawQuery("select ask from ciriciri", null);
            curr.moveToFirst();
            while (!curr.isAfterLast()) {
                String ciri = curr.getString(0);
                listCiriCiriPenyakitHashMap.get(curr.getPosition() + 1).setAsk_flags(Boolean.parseBoolean(ciri));
                curr.moveToNext();
            }
            curr.close();
            System.gc();
            ///////////////////////////
            // input listused flags
            curr = sqlDB.rawQuery("select listused from ciriciri", null);
            curr.moveToFirst();
            while (!curr.isAfterLast()) {
                String ciri = curr.getString(0);
                listCiriCiriPenyakitHashMap.get(curr.getPosition() + 1).setListused_flags(ciri);
                curr.moveToNext();
            }
            curr.close();
            System.gc();
            ///////////////////////////
            // input pointo flags
            curr = sqlDB.rawQuery("select pointo from ciriciri", null);
            curr.moveToFirst();
            while (!curr.isAfterLast()) {
                String ciri = curr.getString(0);
                listCiriCiriPenyakitHashMap.get(curr.getPosition() + 1).setPointo_flags(ciri);
                curr.moveToNext();
            }
            curr.close();
            System.gc();
            //////////////////////////
            //////////////////////////////////////// load successfully
        }

        private void cachingBitmapsViewPager() {
            String[] keyImageLists = {
                    "viewpager_area_1",
                    "viewpager_area_2",
                    "viewpager_area_3",
                    "viewpager_area_4"
            };
            Point point = new Point();
            ctx.getWindowManager().getDefaultDisplay().getSize(point);
            point.y = Math.round(ctx.getResources().getDimension(R.dimen.actmain_dimen_viewpager_height));
            for (String key : keyImageLists) {
                int resDrawable = ctx.getResources().getIdentifier(key, "drawable", ctx.getPackageName());
                //gets the Bitmap
                Bitmap bitmap = DecodeBitmapHelper.decodeAndResizeBitmapsResources(ctx.getResources(), resDrawable, point.y, point.x);
                // creates the scaled bitmaps
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, point.x, point.y, false);
                //gets the byte of bitmap
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                float scaling = bitmap.getHeight() / point.y;
                scaling = ((scaling < 1.0f) ? 1.0f : scaling);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, Math.round(QUALITY_FACTOR / scaling), bos);
                // put into cache
                try {
                    diskCache.put(key, bos.toByteArray());
                } catch (IOException e) {
                    ctx.LOGE("Task.background()", "IOException occured when putting a cache image", e);
                }
                try {
                    bos.close();
                } catch (IOException e) {
                    ctx.LOGE("Task.background()", "IOException occured when releasing ByteOutputStream", e);
                }
                bitmap.recycle();
                scaledBitmap.recycle();
                System.gc();
            }
        }

        private void cleanCache() {
            fileCache.delete();
            fileCache.mkdir();
            /*try {
                diskCache.clean();
            } catch (IOException e) {
                ctx.LOGE("Task.background()", "IOException occured when cleaning a cache", e);
            }*/
        }

        private void updateDBIfItsNewVersion() throws IOException {
            File path = new File(ctx.getApplicationInfo().dataDir, "files");
            path.mkdir();
            File content = new File(path, this.file_db_version);
            if (!content.exists()) {
                AssetManager assetManager = ctx.getAssets();
                InputStream fis = assetManager.open(this.file_db_version);
                FileOutputStream fos = new FileOutputStream(content);
                int read;
                while ((read = fis.read()) != -1) {
                    fos.write(read);
                }
                fis.close();
                fos.close();
                ctx.db_condition = DB_IS_FIRST_USAGE;
                // update into databases
                new CheckAndMoveDB(ctx, "database_penyakitpadi.db").upgradeDB();
            }
            // if exists
            else {
                int current_db_version = getDBVersion();
                int available_db_version = getDBVersionInAssets();
                // apply newer version
                if (current_db_version < available_db_version) {
                    AssetManager assetManager = ctx.getAssets();
                    InputStream fis = assetManager.open(this.file_db_version);
                    FileOutputStream fos = new FileOutputStream(content);
                    int read;
                    while ((read = fis.read()) != -1) {
                        fos.write(read);
                    }
                    fis.close();
                    fos.close();
                    // update into databases
                    new CheckAndMoveDB(ctx, "database_penyakitpadi.db").upgradeDB();
                    ctx.db_condition = DB_IS_NEWER_VERSION;
                } else if (current_db_version > available_db_version)
                    ctx.db_condition = DB_IS_OLDER_IN_APP_VERSION;
                else
                    ctx.db_condition = DB_IS_SAME_VERSION;
            }

        }

        private int getDBVersionInAssets() throws IOException {
            // gets the version
            InputStream fis = ctx.getAssets().open(this.file_db_version);
            byte[] available = new byte[fis.available()];
            fis.read(available);
            String out = new String(available);
            fis.close();
            return Integer.parseInt(out);
        }

        private int getDBVersion() throws IOException {
            File path = new File(ctx.getApplicationInfo().dataDir, "files");
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
            File path = new File(ctx.getApplicationInfo().dataDir, "files");
            path.mkdir();
            File content = new File(path, this.folder_app_version);
            if (!content.exists()) {
                ctx.app_condition = APP_IS_FIRST_USAGE;
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
                    ctx.app_condition = APP_IS_NEWER_VERSION;
                    String data = String.valueOf(version);
                    FileOutputStream fos = new FileOutputStream(content);
                    fos.write(data.getBytes());
                    fos.close();
                } else if (current_version > version) {
                    ctx.app_condition = APP_IS_OLDER_VERSION;
                } else
                    ctx.app_condition = APP_IS_SAME_VERSION;
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
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values[0] == 0) {
                ctx.indicators.setText("Membuat cache...");
            } else if (values[0] == 1) {
                ctx.indicators.setText("Membuka aplikasi");
            }
        }

        private void animateAndForward() {
            Fade in = new Fade(Fade.IN);
            in.setDuration(1200);
            TransitionManager.beginDelayedTransition((RelativeLayout) ctx.findViewById(R.id.actsplash_id_bawah_layout), in);
            ctx.linearIndicator.setVisibility(View.GONE);
            if (ctx.app_condition == APP_IS_FIRST_USAGE)
                ctx.btnNext.setVisibility(View.VISIBLE);
            else {
                Bundle args = new Bundle();
                args.putInt(APP_CONDITION_KEY, ctx.app_condition);
                args.putInt(DB_CONDITION_KEY, ctx.db_condition);
                MylexzActivity activity = ctx;
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