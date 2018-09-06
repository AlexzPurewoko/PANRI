package id.kenshiro.app.panri;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import id.kenshiro.app.panri.helper.ShowPenyakitDiagnoseHelper;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.helper.TampilDiagnosaGambarHelper;

public class DiagnosaGambarActivity extends MylexzActivity {
    Toolbar toolbar;
    SQLiteDatabase sqlDB;
    TampilDiagnosaGambarHelper tampilDiagnosaGambarHelper;
    ShowPenyakitDiagnoseHelper showPenyakitDiagnoseHelper;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actdiagnoseimg_main);
        setMyActionBar();
        setDB();
        setContentV();
    }

    private void setContentV() {
        showPenyakitDiagnoseHelper = new ShowPenyakitDiagnoseHelper(this, sqlDB, (RelativeLayout) findViewById(R.id.actdim_id_layoutcontainer));
        tampilDiagnosaGambarHelper = new TampilDiagnosaGambarHelper(this, (RelativeLayout) findViewById(R.id.actdim_id_layoutcontainer), sqlDB);
        tampilDiagnosaGambarHelper.setOnItemListener(new TampilDiagnosaGambarHelper.OnItemListener() {
                                                         @Override
                                                         public void onBtnYaClicked(View v, int position) {
                                                             v.setVisibility(View.GONE);
                                                             TOAST(Toast.LENGTH_LONG, "position at %d", position);
                                                             showPenyakitDiagnoseHelper.show(position);
                                                         }

                                                         @Override
                                                         public void onBtnTidakClicked(View v, int position) {

                                                         }

                                                         @Override
                                                         public void onIsAfterLastListPosition(View v, int position, int size_list) {
                                                             v.setVisibility(View.GONE);
                                                             TOAST(Toast.LENGTH_LONG, "position at %d is last (%d)", position, size_list);
                                                         }
                                                     }
        );
        showPenyakitDiagnoseHelper.setOnHaveFinalRequests(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPenyakitDiagnoseHelper.countBtn -= 2;
                showPenyakitDiagnoseHelper.klikBawahText.setText(R.string.actdiagnose_string_klikcaramenanggulangi);
                v.setVisibility(View.GONE);

                tampilDiagnosaGambarHelper.setItemPosition(1);
                tampilDiagnosaGambarHelper.updateContentAfter();
                tampilDiagnosaGambarHelper.showContentView();
            }
        });
        showPenyakitDiagnoseHelper.build();
        tampilDiagnosaGambarHelper.buildAndShow();
        tampilDiagnosaGambarHelper.showContentView();
    }

    private void setDB() {
        sqlDB = SQLiteDatabase.openOrCreateDatabase("/data/data/id.kenshiro.app.panri/databases/database_penyakitpadi.db", null);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int repeat = event.getRepeatCount();
        int maxRepeat = 2;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (showPenyakitDiagnoseHelper.getmContentView().getVisibility() == View.GONE) {
                if (!tampilDiagnosaGambarHelper.isContentViewHidden()) {
                    if (doubleBackToExitPressedOnce) {
                        SwitchIntoMainActivity.switchToMain(this);
                        return true;
                    }

                    this.doubleBackToExitPressedOnce = true;
                    TOAST(Toast.LENGTH_SHORT, "Klik lagi untuk kembali");
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                    return false;
                } else
                    return false;
            } else {
                if (showPenyakitDiagnoseHelper.getmContent2().getVisibility() == View.VISIBLE) {
                    showPenyakitDiagnoseHelper.getmContent2().setVisibility(View.GONE);
                    showPenyakitDiagnoseHelper.getmContent1().setVisibility(View.VISIBLE);
                    showPenyakitDiagnoseHelper.mScrollContent.pageScroll(0);
                    --showPenyakitDiagnoseHelper.countBtn;
                    showPenyakitDiagnoseHelper.klikBawahText.setText(R.string.actdiagnose_string_klikcaramenanggulangi);
                    return false;
                } else if (showPenyakitDiagnoseHelper.getmContent1().getVisibility() == View.VISIBLE) {
                    showPenyakitDiagnoseHelper.getmContent1().setVisibility(View.GONE);
                    showPenyakitDiagnoseHelper.getmContentView().setVisibility(View.GONE);
                    tampilDiagnosaGambarHelper.showContentView();
                    tampilDiagnosaGambarHelper.mContentView.pageScroll(0);
                    --showPenyakitDiagnoseHelper.countBtn;
                    return false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        SwitchIntoMainActivity.switchToMain(this);
        return true;
    }
}
