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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.TextSpanFormat;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import java.io.IOException;

import id.kenshiro.app.panri.adapter.AdapterRecycler;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.helper.TampilListPenyakitHelper;

public class HowToResolveActivity extends MylexzActivity {
    Toolbar toolbar;
    TampilListPenyakitHelper tampil;
    SQLiteDatabase sqlDB;
    ScrollView content_caraatasi;
    CardView cardBottom;
    WebView webContent;
    TextView penyakitnama, latinnya;
    String data_url;
    String name_penyakit;
    String name_latin;
    private HowToResolveActivity.ImgPetaniKedip imgPetaniKedip;
    ImageView imgPetani;
    Button mTextPetaniDesc;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acthowto_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setMyActionBar();
        setDB();
        try {
            setContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTask() {
        imgPetaniKedip = new ImgPetaniKedip();
        imgPetaniKedip.execute();
    }

    private void stopTask() {
        if (imgPetaniKedip != null) {
            imgPetaniKedip.cancel(true);
            imgPetaniKedip = null;
        }
    }
    private void setContent() throws IOException {
        imgPetani = (ImageView) findViewById(R.id.actmain_id_section_petani_img);
        mTextPetaniDesc = (Button) findViewById(R.id.actmain_id_section_petani_btn);
        mTextPetaniDesc.setTextColor(Color.BLACK);
        mTextPetaniDesc.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.NORMAL);
        mTextPetaniDesc.setText(getText(R.string.acthowto_string_speechfarmer_1));
        imgPetani.setImageResource(R.drawable.petani);
        imgPetani.setImageLevel(4);
        content_caraatasi = findViewById(R.id.acthowto_id_scrollpage);
        content_caraatasi.setVisibility(View.GONE);
        cardBottom = findViewById(R.id.acthowto_id_klikbawah);
        cardBottom.setVisibility(View.GONE);
        cardBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextPetaniDesc.setText(getString(R.string.acthowto_string_speechfarmer_1));
                content_caraatasi.setVisibility(View.GONE);
                cardBottom.setVisibility(View.GONE);
                tampil.getmContentView().setVisibility(View.VISIBLE);
            }
        });
        penyakitnama = findViewById(R.id.acthowto_id_judulpenyakit);
        latinnya = findViewById(R.id.acthowto_id_namalatin);
        webContent = findViewById(R.id.acthowto_id_caraatasi);
        tampil = new TampilListPenyakitHelper(this, sqlDB, (RelativeLayout) findViewById(R.id.acthowto_id_layoutcontainer));
        tampil.setOnItemClickListener(new AdapterRecycler.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                view.setVisibility(View.GONE);
                content_caraatasi.setVisibility(View.VISIBLE);
                cardBottom.setVisibility(View.VISIBLE);
                mTextPetaniDesc.setText(getString(R.string.acthowto_string_speechfarmer_2));
                // SETS content to show
                HowToResolveActivity.this.loadDataFromDB(position + 1);
                HowToResolveActivity.this.setCaraAtasiContent();
            }
        });
        tampil.buildAndShow();

    }

    private void loadDataFromDB(int position) {
        // load url path from assets
        Cursor cursor = sqlDB.rawQuery("select cara_atasi_path from penyakit where no=" + position, null);
        cursor.moveToFirst();
        this.data_url = "file:///android_asset/" + cursor.getString(0);
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

    private void setCaraAtasiContent() {
        penyakitnama.setText(this.name_penyakit);
        latinnya.setText(this.name_latin == null ? "" : this.name_latin);
        webContent.loadUrl(this.data_url);
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
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTask();
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
            if (content_caraatasi.getVisibility() == View.VISIBLE) {
                content_caraatasi.setVisibility(View.GONE);
                cardBottom.setVisibility(View.GONE);
                mTextPetaniDesc.setText(getString(R.string.acthowto_string_speechfarmer_1));
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
