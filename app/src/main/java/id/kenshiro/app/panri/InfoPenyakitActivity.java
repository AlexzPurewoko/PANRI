package id.kenshiro.app.panri;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import java.io.IOException;

import id.kenshiro.app.panri.adapter.AdapterRecycler;
import id.kenshiro.app.panri.helper.DiagnoseActivityHelper;
import id.kenshiro.app.panri.helper.ShowPenyakitDiagnoseHelper;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.helper.TampilListPenyakitHelper;

public class InfoPenyakitActivity extends MylexzActivity {
    private Toolbar toolbar;
    private TampilListPenyakitHelper tampil;
    private SQLiteDatabase sqlDB;
    private ShowPenyakitDiagnoseHelper showPenyakitDiagnoseHelper;
    private InfoPenyakitActivity.ImgPetaniKedip imgPetaniKedip;
    ImageView imgPetani;
    Button mTextPetaniDesc;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actinfo_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setMyActionBar();
        setDB();
        try {
            setContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    private void setTask() {
        imgPetaniKedip = new InfoPenyakitActivity.ImgPetaniKedip();
        imgPetaniKedip.execute();
    }

    private void stopTask() {
        if (imgPetaniKedip != null) {
            imgPetaniKedip.cancel(true);
            imgPetaniKedip = null;
        }
    }
    private void setContent() throws IOException {
        loadLayoutAndShow();
        imgPetani = (ImageView) findViewById(R.id.actmain_id_section_petani_img);
        mTextPetaniDesc = (Button) findViewById(R.id.actmain_id_section_petani_btn);
        mTextPetaniDesc.setTextColor(Color.BLACK);
        mTextPetaniDesc.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.NORMAL);
        mTextPetaniDesc.setText(getText(R.string.actinfo_string_speechfarmer_1));
        imgPetani.setImageResource(R.drawable.petani);
        imgPetani.setImageLevel(4);
        tampil = new TampilListPenyakitHelper(this, sqlDB, (RelativeLayout) findViewById(R.id.actinfo_id_layoutcontainer));
        tampil.setOnItemClickListener(new AdapterRecycler.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                view.setVisibility(View.GONE);
                showPenyakitDiagnoseHelper.show(position + 1);
                mTextPetaniDesc.setText(getText(R.string.actinfo_string_speechfarmer_2));
            }
        });
        tampil.buildAndShow();
    }

    private void loadLayoutAndShow() {
        showPenyakitDiagnoseHelper = new ShowPenyakitDiagnoseHelper(this, sqlDB, (RelativeLayout) this.findViewById(R.id.actinfo_id_layoutcontainer));
        showPenyakitDiagnoseHelper.build();
        showPenyakitDiagnoseHelper.setOnHaveFinalRequests(new View.OnClickListener() {
            @Override
            public void onClick(View mContentView) {
                mContentView.setVisibility(View.GONE);
                // back into begin diagnostics
                mTextPetaniDesc.setText(getText(R.string.actinfo_string_speechfarmer_1));
                tampil.getmContentView().setVisibility(View.VISIBLE);
            }
        });
    }

    private void setDB() {
        sqlDB = SQLiteDatabase.openOrCreateDatabase("/data/data/id.kenshiro.app.panri/databases/database_penyakitpadi.db", null);
    }
    @Override
    protected void onResume() {
        super.onResume();
        setTask();
    }

    @Override
    protected void onPause() {
        stopTask();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopTask();
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
                if (!tampil.onBackButtonPressed()) {
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
                    --showPenyakitDiagnoseHelper.countBtn;
                    showPenyakitDiagnoseHelper.klikBawahText.setText(R.string.actdiagnose_string_klikcaramenanggulangi);
                    return false;
                } else if (showPenyakitDiagnoseHelper.getmContent1().getVisibility() == View.VISIBLE) {
                    showPenyakitDiagnoseHelper.getmContent1().setVisibility(View.GONE);
                    showPenyakitDiagnoseHelper.getmContentView().setVisibility(View.GONE);
                    mTextPetaniDesc.setText(getText(R.string.actinfo_string_speechfarmer_1));
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

    private class ImgPetaniKedip extends AsyncTask<Void, Integer, Void> {
        private void sleep(int mil) {
            try {
                Thread.sleep(mil);
            } catch (InterruptedException e) {
                Log.e("Main_Exception", "Interrupted in method ImageAutoSwipe.doInBackground()", e);
            }
        }

        @Override
        protected Void doInBackground(Void[] p1) {
            // TODO: Implement this method
            while (true) {
                sleep(400);
                publishProgress(1);
                sleep(2000);
                publishProgress(4);
            }
        }

        @Override
        protected void onProgressUpdate(Integer[] values) {
            // TODO: Implement this method
            super.onProgressUpdate(values);
            int pos = values[0];
            imgPetani.setImageLevel(pos);
        }

    }
}
