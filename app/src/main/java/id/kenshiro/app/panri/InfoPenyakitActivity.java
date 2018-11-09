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
import pl.droidsonroids.gif.GifImageView;

public class InfoPenyakitActivity extends MylexzActivity {
    private Toolbar toolbar;
    private TampilListPenyakitHelper tampil;
    private SQLiteDatabase sqlDB;
    private ShowPenyakitDiagnoseHelper showPenyakitDiagnoseHelper;
    private Handler handlerPetani;
    Button mTextPetaniDesc;
    private boolean doubleBackToExitPressedOnce;
    private GifImageView imgPetaniKedipView;

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
    private void setContent() throws IOException {
        loadLayoutAndShow();
        mTextPetaniDesc = (Button) findViewById(R.id.actmain_id_section_petani_btn);
        imgPetaniKedipView = findViewById(R.id.actsplash_id_gifpetanikedip);
        mTextPetaniDesc.setTextColor(Color.BLACK);
        mTextPetaniDesc.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.NORMAL);
        tampil = new TampilListPenyakitHelper(this, sqlDB, (RelativeLayout) findViewById(R.id.actinfo_id_layoutcontainer));
        tampil.setOnItemClickListener(new AdapterRecycler.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                view.setVisibility(View.GONE);
                showPenyakitDiagnoseHelper.show(position + 1);
                onButtonPetaniClicked(getText(R.string.actinfo_string_speechfarmer_2));
            }
        });
        tampil.buildAndShow();
        onButtonPetaniClicked(getText(R.string.actinfo_string_speechfarmer_1));
    }

    private void loadLayoutAndShow() {
        showPenyakitDiagnoseHelper = new ShowPenyakitDiagnoseHelper(this, sqlDB, (RelativeLayout) this.findViewById(R.id.actinfo_id_layoutcontainer));
        showPenyakitDiagnoseHelper.build();
        showPenyakitDiagnoseHelper.setOnHaveFinalRequests(new View.OnClickListener() {
            @Override
            public void onClick(View mContentView) {
                mContentView.setVisibility(View.GONE);
                // back into begin diagnostics
                onButtonPetaniClicked(getText(R.string.actinfo_string_speechfarmer_1));
                tampil.getmContentView().setVisibility(View.VISIBLE);
            }
        });
    }

    private void setDB() {
        sqlDB = SQLiteDatabase.openOrCreateDatabase("/data/data/id.kenshiro.app.panri/files/database_penyakitpadi.db", null);
    }

    private void onButtonPetaniClicked(CharSequence text) {

        mTextPetaniDesc.setText(text);
        imgPetaniKedipView.setImageResource(R.drawable.petani_bicara);
        if (handlerPetani == null) {
            handlerPetani = new Handler();
            handlerPetani.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handlerPetani = null;
                    System.gc();
                    imgPetaniKedipView.setImageResource(R.drawable.petani_kedip);
                }
            }, 4000);
        }
        System.gc();
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
        sqlDB.close();
        try {
            tampil.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    onButtonPetaniClicked(getText(R.string.actinfo_string_speechfarmer_1));
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
