package id.kenshiro.app.panri;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.TextSpanFormat;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import java.io.File;
import java.io.IOException;

import id.kenshiro.app.panri.adapter.AdapterRecycler;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.helper.TampilListPenyakitHelper;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;
import io.fabric.sdk.android.Fabric;
import pl.droidsonroids.gif.GifImageView;

public class HowToResolveActivity extends MylexzActivity {
    Toolbar toolbar;
    TampilListPenyakitHelper tampil;
    SQLiteDatabase sqlDB;
    ScrollView content_caraatasi;
    CardView cardBottom;
    //WebView webContent;
    TextView penyakitnama, latinnya;
    String data_url;
    String name_penyakit;
    String name_latin;
    Button mTextPetaniDesc;
    private boolean doubleBackToExitPressedOnce;
    private GifImageView imgPetaniKedipView;
    private Handler handlerPetani;
    private boolean firstCondition = true;
    private CardView webCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)  // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);
        try {
            setContentView(R.layout.acthowto_main);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setMyActionBar();
            setDB();
            setContent();
        } catch (Throwable e) {
            String keyEx = getClass().getName() + "_onCreate()";
            String resE = String.format("UnHandled Exception Occurs(Throwable) e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            LOGE(keyEx, resE);
        }
    }

    private void setContent() {
        mTextPetaniDesc = (Button) findViewById(R.id.actmain_id_section_petani_btn);
        imgPetaniKedipView = findViewById(R.id.actsplash_id_gifpetanikedip);
        mTextPetaniDesc.setTextColor(Color.BLACK);
        mTextPetaniDesc.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.NORMAL);
        content_caraatasi = findViewById(R.id.acthowto_id_scrollpage);
        content_caraatasi.setVisibility(View.GONE);
        cardBottom = findViewById(R.id.acthowto_id_klikbawah);
        cardBottom.setVisibility(View.GONE);
        cardBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mTextPetaniDesc.setText(getString(R.string.acthowto_string_speechfarmer_1));
                onButtonPetaniClicked(getText(R.string.acthowto_string_speechfarmer_1));
                content_caraatasi.setVisibility(View.GONE);
                cardBottom.setVisibility(View.GONE);
                tampil.getmContentView().setVisibility(View.VISIBLE);
            }
        });
        penyakitnama = findViewById(R.id.acthowto_id_judulpenyakit);
        latinnya = findViewById(R.id.acthowto_id_namalatin);
        webCard = findViewById(R.id.howto_id_howtocard);
        //webContent = findViewById(R.id.acthowto_id_caraatasi);
        tampil = new TampilListPenyakitHelper(this, sqlDB, (RelativeLayout) findViewById(R.id.acthowto_id_layoutcontainer));
        tampil.setOnItemClickListener(new AdapterRecycler.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                view.setVisibility(View.GONE);
                content_caraatasi.setVisibility(View.VISIBLE);
                cardBottom.setVisibility(View.VISIBLE);
                //mTextPetaniDesc.setText(getString(R.string.acthowto_string_speechfarmer_2));
                onButtonPetaniClicked(getText(R.string.acthowto_string_speechfarmer_2));
                // SETS content to show
                HowToResolveActivity.this.loadDataFromDB(position + 1);
                HowToResolveActivity.this.setCaraAtasiContent();
            }
        });
        tampil.buildAndShow();
        onButtonPetaniClicked(getText(R.string.acthowto_string_speechfarmer_1));

    }

    private void loadDataFromDB(int position) {
        // load url path from assets
        Cursor cursor = sqlDB.rawQuery("select cara_atasi_path from penyakit where no=" + position, null);
        cursor.moveToFirst();
        File tmp = new File(getFilesDir(), "data_hama_html");
        this.data_url = "file://" + tmp.getAbsolutePath() + "/" + cursor.getString(0);
        cursor.close();
        System.gc();

        // load nama penyakit
        cursor = sqlDB.rawQuery("select nama from penyakit where no=" + position, null);
        cursor.moveToFirst();
        this.name_penyakit = cursor.getString(0);
        cursor.close();
        System.gc();

        // load nama latin penyakit
        cursor = sqlDB.rawQuery("select latin from penyakit where no=" + position, null);
        cursor.moveToFirst();
        this.name_latin = cursor.getString(0);
        cursor.close();
        System.gc();
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
    public void onTrimMemory(int level) {

    }

    private void setCaraAtasiContent() {
        penyakitnama.setText(this.name_penyakit);
        latinnya.setText(this.name_latin == null ? "" : this.name_latin);
        if (!firstCondition)
            clearViewOn(webCard, webCard.getChildCount() - 1);
        else
            firstCondition = false;
        WebView webContent = setWebView(webCard);
        webContent.loadUrl(this.data_url);
    }

    private void setDB() {
        sqlDB = SQLiteDatabase.openOrCreateDatabase("/data/data/id.kenshiro.app.panri/files/database_penyakitpadi.db", null);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            tampil.close();
        } catch (IOException e) {
            String keyEx = getClass().getName() + "_onDestroy()";
            String resE = String.format("Unable to execute tampil.close(); e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            LOGE(keyEx, resE);
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
            if (content_caraatasi.getVisibility() == View.VISIBLE) {
                content_caraatasi.setVisibility(View.GONE);
                cardBottom.setVisibility(View.GONE);
                //mTextPetaniDesc.setText(getString(R.string.acthowto_string_speechfarmer_1));
                onButtonPetaniClicked(getText(R.string.acthowto_string_speechfarmer_1));
            }
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
            } else {

                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        SwitchIntoMainActivity.switchToMain(this);
        return true;
    }

    private WebView setWebView(ViewGroup baseLayout) {
        WebView web = new WebView(this);
        web.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        WebSettings webGejalaSettings = web.getSettings();
        webGejalaSettings.setAllowContentAccess(true);
        webGejalaSettings.setAllowFileAccessFromFileURLs(true);
        webGejalaSettings.setJavaScriptEnabled(true);
        baseLayout.addView(web);
        return web;
    }

    private void clearViewOn(View baseLayout, int index) {
        if (baseLayout instanceof LinearLayout) {
            ((LinearLayout) baseLayout).removeViewAt(index);
        } else if (baseLayout instanceof CardView) {
            ((CardView) baseLayout).removeViewAt(index);
        }
    }
}
