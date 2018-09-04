package id.kenshiro.app.panri;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.helper.TampilDiagnosaGambarHelper;

public class DiagnosaGambarActivity extends MylexzActivity {
    Toolbar toolbar;
    SQLiteDatabase sqlDB;
    TampilDiagnosaGambarHelper tampilDiagnosaGambarHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actdiagnoseimg_main);
        setMyActionBar();
        setDB();
        setContentV();
    }

    private void setContentV() {
        tampilDiagnosaGambarHelper = new TampilDiagnosaGambarHelper(this, (RelativeLayout) findViewById(R.id.actdim_id_layoutcontainer), sqlDB);
        tampilDiagnosaGambarHelper.setOnItemClickListener((v, p) -> {
            TOAST(Toast.LENGTH_LONG, "Selected at" + p);
        });
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
        // TODO: Implement this method
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
            if (repeat == maxRepeat) {
                SwitchIntoMainActivity.switchToMain(this);
            } else {
                TOAST(Toast.LENGTH_SHORT, "Tekan tombol %d untuk kembali", maxRepeat - repeat);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        SwitchIntoMainActivity.switchToMain(this);
        return true;
    }
}
