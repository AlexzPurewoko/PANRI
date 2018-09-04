package id.kenshiro.app.panri;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.TextSpanFormat;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import java.io.IOException;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acthowto_main);
        setMyActionBar();
        setDB();
        try {
            setContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setContent() throws IOException {
        content_caraatasi = findViewById(R.id.acthowto_id_scrollpage);
        content_caraatasi.setVisibility(View.GONE);
        cardBottom = findViewById(R.id.acthowto_id_klikbawah);
        cardBottom.setVisibility(View.GONE);
        cardBottom.setOnClickListener((v) -> {
            content_caraatasi.setVisibility(View.GONE);
            cardBottom.setVisibility(View.GONE);
            tampil.getmContentView().setVisibility(View.VISIBLE);
        });
        penyakitnama = findViewById(R.id.acthowto_id_judulpenyakit);
        latinnya = findViewById(R.id.acthowto_id_namalatin);
        webContent = findViewById(R.id.acthowto_id_caraatasi);
        tampil = new TampilListPenyakitHelper(this, sqlDB, (RelativeLayout) findViewById(R.id.acthowto_id_layoutcontainer));
        tampil.setOnItemClickListener((view, position) -> {
            view.setVisibility(View.GONE);
            content_caraatasi.setVisibility(View.VISIBLE);
            cardBottom.setVisibility(View.VISIBLE);

            // SETS content to show
            loadDataFromDB(position + 1);
            setCaraAtasiContent();
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
            if (content_caraatasi.getVisibility() == View.VISIBLE)
                content_caraatasi.setVisibility(View.GONE);
            if (!tampil.onBackButtonPressed()) {
                SwitchIntoMainActivity.switchToMain(this);
                return false;
            } else
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        SwitchIntoMainActivity.switchToMain(this);
        return true;
    }
}
