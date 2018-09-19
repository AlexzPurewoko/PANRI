package id.kenshiro.app.panri;

import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;

public class GalleryActivity extends MylexzActivity {
    Toolbar toolbar;
    ImageGridViewAdapter adapterGrid;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actgallery_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setMyActionBar();
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        List<Integer> l = new ArrayList<Integer>();
        l.add(R.drawable.viewpager_area_1);
        l.add(R.drawable.viewpager_area_2);
        l.add(R.drawable.viewpager_area_3);
        l.add(R.drawable.viewpager_area_4);
        adapterGrid = new ImageGridViewAdapter(this, p, R.id.actgallery_id_gridimage);
        adapterGrid.setColumnCount(2);
        adapterGrid.setOnItemClickListener(new ImageGridViewAdapter.OnItemClickListener() {
                                               @Override
                                               public void onItemClick(View v, int position) {

                                                   Toast.makeText(GalleryActivity.this, "selected at position = " + position, Toast.LENGTH_SHORT).show();
                                               }
                                           }
        );
        adapterGrid.setListLocationResImages(l);
        adapterGrid.buildAndShow();
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
