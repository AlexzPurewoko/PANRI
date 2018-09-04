package id.kenshiro.app.panri;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import java.io.IOException;

import id.kenshiro.app.panri.helper.DiagnoseActivityHelper;
import id.kenshiro.app.panri.helper.ShowPenyakitDiagnoseHelper;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.helper.TampilListPenyakitHelper;

public class InfoPenyakitActivity extends MylexzActivity {
    private Toolbar toolbar;
    private TampilListPenyakitHelper tampil;
    private SQLiteDatabase sqlDB;
    private ShowPenyakitDiagnoseHelper showPenyakitDiagnoseHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actinfo_main);
        setMyActionBar();
        setDB();
        try {
            setContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setContent() throws IOException {
        loadLayoutAndShow();
        tampil = new TampilListPenyakitHelper(this, sqlDB, (RelativeLayout) findViewById(R.id.actinfo_id_layoutcontainer));
        tampil.setOnItemClickListener((view, position) -> {
            view.setVisibility(View.GONE);
            showPenyakitDiagnoseHelper.show(position + 1);
        });
        tampil.buildAndShow();
    }

    private void loadLayoutAndShow() {
        showPenyakitDiagnoseHelper = new ShowPenyakitDiagnoseHelper(this, sqlDB, (RelativeLayout) this.findViewById(R.id.actinfo_id_layoutcontainer));
        showPenyakitDiagnoseHelper.build();
        showPenyakitDiagnoseHelper.setOnHaveFinalRequests((mContentView) -> {
            mContentView.setVisibility(View.GONE);
            // back into begin diagnostics
            tampil.getmContentView().setVisibility(View.VISIBLE);
        });
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
        SpannableString strTitle = new SpannableString(getTitle());
        Typeface tTitle = Typeface.createFromAsset(getAssets(), "Gecko_PersonalUseOnly.ttf");
        strTitle.setSpan(new CustomTypefaceSpan(tTitle), 0, getTitle().length(), 0);
        toolbar.setTitle(strTitle);
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
                if (!tampil.onBackButtonPressed()) {
                    SwitchIntoMainActivity.switchToMain(this);
                    return false;
                } else
                    return false;
            } else {
                if (showPenyakitDiagnoseHelper.getmContent2().getVisibility() == View.VISIBLE) {
                    showPenyakitDiagnoseHelper.getmContent2().setVisibility(View.GONE);
                    showPenyakitDiagnoseHelper.getmContent1().setVisibility(View.VISIBLE);
                    --showPenyakitDiagnoseHelper.countBtn;
                    showPenyakitDiagnoseHelper.klikBawahText.setText(R.string.actdiagnose_string_klikcaramenanggulangi);
                    return false;
                } else if (showPenyakitDiagnoseHelper.getmContent1().getVisibility() == View.VISIBLE) {
                    showPenyakitDiagnoseHelper.getmContent1().setVisibility(View.GONE);
                    showPenyakitDiagnoseHelper.getmContentView().setVisibility(View.GONE);
                    tampil.getmContentView().setVisibility(View.VISIBLE);
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
