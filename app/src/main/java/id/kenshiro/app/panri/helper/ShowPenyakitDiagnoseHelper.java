package id.kenshiro.app.panri.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mylexz.utils.MylexzActivity;

import java.util.List;

import id.kenshiro.app.panri.R;

public class ShowPenyakitDiagnoseHelper {
    private MylexzActivity activity;
    private SQLiteDatabase sqLiteDatabase;
    private static final String path_to_asset = "file:///android_asset/";
    // load layout elements
    private RelativeLayout mRootView, mContentView;
    private WebView gejala, umum, caraatasi;
    private TextView judul, latin;
    private CardView klikBawah;
    private TextView klikBawahText;
    private ScrollView mContent1, mContent2; // 2 & 3 position
    // data elements
    private DataPath dataPath;
    private int countBtn = 0;
    private final int maxCount = 2;
    View.OnClickListener onClickListener;
    public ShowPenyakitDiagnoseHelper(@NonNull MylexzActivity activity, @NonNull SQLiteDatabase sqLiteDatabase, @NonNull RelativeLayout mRootView){
        this.activity = activity;
        this.sqLiteDatabase = sqLiteDatabase;
        this.mRootView = mRootView;
    }
    public void build(){
        prepareAndBuildLayout();
        // sets the layout visibility and apply into rootView
        mContentView.setVisibility(View.GONE);
        mRootView.addView(mContentView);
    }
    public void show(int keyId){
        selectContentOnDB(keyId);
        mContentView.setVisibility(View.VISIBLE);
        mContent1.setVisibility(View.VISIBLE);
        mContent2.setVisibility(View.GONE);
        umum.loadUrl(path_to_asset+""+dataPath.getUmum_path());
        gejala.loadUrl(path_to_asset+""+dataPath.getGejala_path());
        caraatasi.loadUrl(path_to_asset+""+dataPath.getCara_atasi_path());
    }

    private void selectContentOnDB(int keyId) {
        dataPath = new DataPath(null, null, null);
        // select umum_path
        Cursor cursor = sqLiteDatabase.rawQuery("select umum_path from penyakit where no="+keyId, null);
        cursor.moveToFirst();
        dataPath.setUmum_path(cursor.getString(0));
        cursor.close();
        System.gc();

        // select gejala_path
        cursor = sqLiteDatabase.rawQuery("select gejala_path from penyakit where no="+keyId, null);
        cursor.moveToFirst();
        dataPath.setGejala_path(cursor.getString(0));
        cursor.close();
        System.gc();

        // select gejala_path
        cursor = sqLiteDatabase.rawQuery("select cara_atasi_path from penyakit where no="+keyId, null);
        cursor.moveToFirst();
        dataPath.setCara_atasi_path(cursor.getString(0));
        cursor.close();
        System.gc();

    }


    public void setOnHaveFinalRequests(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private void prepareAndBuildLayout() {
        buildContentLayout();
    }

    private void buildContentLayout() {
        mContentView = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.actdiagnose_dialog_whenpenyakitselected, null);
        // gets all views
        judul = (TextView) mContentView.findViewById(R.id.actdiagnose_id_judulpenyakit);
        latin = (TextView) mContentView.findViewById(R.id.actdiagnose_id_namalatin);
        /// sets into Comic SAns
        judul.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Comic_Sans_MS3.ttf"));
        latin.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Comic_Sans_MS3.ttf"));

        // prepare ScrollView
        mContent1 = (ScrollView) mContentView.getChildAt(2);
        mContent2 = (ScrollView) mContentView.getChildAt(3);

        /// sets the visibility
        mContent2.setVisibility(View.GONE);
        mContent1.setVisibility(View.VISIBLE);
        // prepare webView

        gejala = (WebView) mContentView.findViewById(R.id.actdiagnose_id_gejala);
        umum = (WebView) mContentView.findViewById(R.id.actdiagnose_id_umum);
        caraatasi = (WebView) mContentView.findViewById(R.id.actdiagnose_id_caraatasi);
        //// settings the whole webview
        WebSettings webGejalaSettings = gejala.getSettings();
        webGejalaSettings.setAllowContentAccess(true);
        webGejalaSettings.setAllowFileAccessFromFileURLs(true);
        webGejalaSettings.setJavaScriptEnabled(true);

        WebSettings webUmumSettings = umum.getSettings();
        webUmumSettings.setAllowContentAccess(true);
        webUmumSettings.setAllowFileAccessFromFileURLs(true);
        webUmumSettings.setJavaScriptEnabled(true);

        WebSettings webCaraAtasiSettings = caraatasi.getSettings();
        webCaraAtasiSettings.setAllowContentAccess(true);
        webCaraAtasiSettings.setAllowFileAccessFromFileURLs(true);
        webCaraAtasiSettings.setJavaScriptEnabled(true);
        klikBawah = (CardView) mContentView.findViewById(R.id.actdiagnose_id_klikbawah);
        klikBawahText = (TextView) klikBawah.getChildAt(0);
        klikBawahText.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Comic_Sans_MS3.ttf"));
        klikBawah.setOnClickListener((v) -> {

            if(++countBtn == maxCount && onClickListener != null)
                onClickListener.onClick(mContentView); // the 1st parameters is main Content View so if you unvisible the layout its become easier
            else{
                // Do into cara_atasi
                mContent1.setVisibility(View.GONE);
                mContent2.setVisibility(View.VISIBLE);
                klikBawahText.setText(R.string.actdiagnose_string_klikbalikdiagnosa);
                /////////////////////
            }
        });

    }

    private class DataPath{
        String umum_path;
        String gejala_path;
        String cara_atasi_path;
        public DataPath(String umum_path, String gejala_path, String cara_atasi_path){
            this.cara_atasi_path = cara_atasi_path;
            this.gejala_path = gejala_path;
            this.umum_path = umum_path;
        }

        public void setUmum_path(String umum_path) {
            this.umum_path = umum_path;
        }

        public void setCara_atasi_path(String cara_atasi_path) {
            this.cara_atasi_path = cara_atasi_path;
        }

        public void setGejala_path(String gejala_path) {
            this.gejala_path = gejala_path;
        }

        public String getCara_atasi_path() {
            return cara_atasi_path;
        }

        public String getGejala_path() {
            return gejala_path;
        }

        public String getUmum_path() {
            return umum_path;
        }
    }
}
