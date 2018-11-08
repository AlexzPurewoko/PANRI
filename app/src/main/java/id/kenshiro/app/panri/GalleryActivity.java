package id.kenshiro.app.panri;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import java.util.ArrayList;
import java.util.List;

import id.kenshiro.app.panri.adapter.ImageGridViewAdapter;
import id.kenshiro.app.panri.helper.DialogShowHelper;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;

public class GalleryActivity extends MylexzActivity {
    Toolbar toolbar;
    ImageGridViewAdapter adapterGrid;
    private boolean doubleBackToExitPressedOnce;
    private SQLiteDatabase sqlDB;
    List<String> dataPathGambar;
    private DialogShowHelper dialogShowHelper;
    private String image_default_dirs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actgallery_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.image_default_dirs = getFilesDir().getAbsolutePath() + "/data/images/list";
        setMyActionBar();
        loadFromDB();

    }

    private void loadFromDB() {
        dialogShowHelper = new DialogShowHelper(this);
        dialogShowHelper.buildLoadingLayout();
        dialogShowHelper.showDialog();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    sqlDB = SQLiteDatabase.openOrCreateDatabase("/data/data/id.kenshiro.app.panri/files/database_penyakitpadi.db", null);
                    dataPathGambar = new ArrayList<>();
                    Cursor cursor = sqlDB.rawQuery("select path_gambar from gambar_penyakit", null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        String res = cursor.getString(0);
                        String[] list = res.split(",");
                        for (String idx : list) {
                            dataPathGambar.add(image_default_dirs + '/' + idx + ".jpg");
                        }
                        cursor.moveToNext();
                    }
                    cursor.close();
                    sqlDB.close();
                    System.gc();
                    Point p = new Point();
                    getWindowManager().getDefaultDisplay().getSize(p);
                    int dimen = Math.round(getResources().getDimension(R.dimen.margin_img_penyakit));
                    adapterGrid = new ImageGridViewAdapter(GalleryActivity.this, p, R.id.actgallery_id_gridimage);
                    adapterGrid.setColumnCount(2);
                    adapterGrid.setListLocationFileImages(dataPathGambar, "gallery_act");
                    adapterGrid.setMargin(dimen, dimen, dimen, dimen, dimen);
                    adapterGrid.setOnItemClickListener(new ImageGridViewAdapter.OnItemClickListener() {
                                                           @Override
                                                           public void onItemClick(View v, int position) {

                                                               Toast.makeText(GalleryActivity.this, "selected at position = " + position, Toast.LENGTH_SHORT).show();
                                                           }
                                                       }
                    );
                    adapterGrid.buildAndShow();
                    dialogShowHelper.stopDialog();
                }
            }
        }, 200);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        adapterGrid.close();
        super.onDestroy();
    }

    private void setMyActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            SpannableString strTitle = new SpannableString(getTitle());
            Typeface tTitle = Typeface.createFromAsset(getAssets(), "Gecko_PersonalUseOnly.ttf");
            strTitle.setSpan(new CustomTypefaceSpan(tTitle), 0, getTitle().length(), 0);
            toolbar.setTitle(strTitle);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            SwitchIntoMainActivity.switchToMain(this);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        TOAST(Toast.LENGTH_SHORT, "Klik lagi untuk kembali");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onSupportNavigateUp() {
        SwitchIntoMainActivity.switchToMain(this);
        return true;
    }

}
