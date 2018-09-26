package id.kenshiro.app.panri.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mylexz.utils.MylexzActivity;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

import id.kenshiro.app.panri.DiagnoseActivity;
import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.adapter.ImageGridViewAdapter;
import pl.droidsonroids.gif.GifImageView;

public class ShowPenyakitDiagnoseHelper implements Closeable{
    private MylexzActivity activity;
    private SQLiteDatabase sqLiteDatabase;
    private static final String path_to_asset = "file:///android_asset/";
    private static final String image_default_dirs = "data_hama/foto";
    // load layout elements
    private RelativeLayout mRootView, mContentView;
    private WebView gejala, umum, caraatasi;
    private TextView judul, latin;
    private CardView klikBawah;
    public TextView klikBawahText;
    private LinearLayout mContent1, mContent2; // 1 & 2 position
    private ImageGridViewAdapter imageViewPenyakit;
    // data elements
    private DataPath dataPath;
    public int countBtn = 0;
    private final int maxCount = 2;
    View.OnClickListener onClickListener;
    public ScrollView mScrollContent;
    private Button mTextPetaniDesc;
    boolean mTxtPeralihan = false;
    // dialog Alert
    private AlertDialog dialog = null;
    public ShowPenyakitDiagnoseHelper(@NonNull MylexzActivity activity, @NonNull SQLiteDatabase sqLiteDatabase, @NonNull RelativeLayout mRootView){
        this.activity = activity;
        this.sqLiteDatabase = sqLiteDatabase;
        this.mRootView = mRootView;
    }

    public void setmTextPetaniDesc(Button mTextPetaniDesc) {
        this.mTextPetaniDesc = mTextPetaniDesc;
    }

    public void build(){
        prepareAndBuildLayout();
        // sets the layout visibility and apply into rootView
        mContentView.setVisibility(View.GONE);
        mRootView.addView(mContentView);
    }
    private void showKeyId(final int keyId){
        setPenyakitText(keyId);
        selectContentOnDB(keyId);
        setImagePager(keyId);
        mContentView.setVisibility(View.VISIBLE);
        mContent1.setVisibility(View.VISIBLE);
        mContent2.setVisibility(View.GONE);
        if (mTextPetaniDesc != null)
            mTextPetaniDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContent1.getVisibility() == View.VISIBLE) {
                        if (mTxtPeralihan) {
                            mTextPetaniDesc.setText(activity.getString(R.string.actdiagnose_string_speechfarmer_img_2));
                            mTxtPeralihan = false;
                        } else {
                            mTextPetaniDesc.setText(activity.getString(R.string.actdiagnose_string_speechfarmer_img_s2));
                            mTxtPeralihan = true;
                        }
                    }
                }
            });
        mScrollContent.pageScroll(1);
        umum.loadUrl(path_to_asset+""+dataPath.getUmum_path());
        gejala.loadUrl(path_to_asset+""+dataPath.getGejala_path());
        caraatasi.loadUrl(path_to_asset+""+dataPath.getCara_atasi_path());
    }
    public void show(final int keyId){
        buildLoadingLayout();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyId(keyId);
                /*try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                dialog.cancel();
            }
        }, 2000);
    }

    public RelativeLayout getmContentView() {
        return mContentView;
    }

    public LinearLayout getmContent1() {
        return mContent1;
    }

    public LinearLayout getmContent2() {
        return mContent2;
    }

    private void setImagePager(int keyId) {
        List<String> mListResImage = new ArrayList<String>();
        //add your items here
        Cursor cursor = sqLiteDatabase.rawQuery("select count_img from gambar_penyakit where no=" + keyId, null);
        cursor.moveToFirst();
        int count = Integer.parseInt(cursor.getString(0));
        cursor.close();

        cursor = sqLiteDatabase.rawQuery("select path_gambar from gambar_penyakit where no=" + keyId, null);
        cursor.moveToFirst();
        String[] s = cursor.getString(0).split(",");
        cursor.close();
        for (int x = 0; x < count; x++) {
            mListResImage.add(image_default_dirs + "/" + s[x] + ".jpg");
        }

        //////////
        Point p = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(p);
        if (imageViewPenyakit == null)
            imageViewPenyakit = new ImageGridViewAdapter(activity, p, R.id.actgallery_id_gridimage);
        imageViewPenyakit.setColumnCount(2);
        imageViewPenyakit.setListLocationAssetsImages(mListResImage, "show_diagnose");
        int dimen = Math.round(activity.getResources().getDimension(R.dimen.margin_img_penyakit));
        imageViewPenyakit.setMargin(dimen, dimen, dimen, dimen);
        imageViewPenyakit.buildAndShow();
    }

    private void setPenyakitText(int keyId) {
        Cursor cursor = sqLiteDatabase.rawQuery("select nama from penyakit where no=" + keyId, null);
        cursor.moveToFirst();
        judul.setText(cursor.getString(0));
        cursor.close();
        System.gc();
        cursor = sqLiteDatabase.rawQuery("select latin from penyakit where no=" + keyId, null);
        cursor.moveToFirst();
        String lt = cursor.getString(0);
        if (lt != null)
            latin.setText(lt);
        else
            latin.setVisibility(View.GONE);
        cursor.close();
        System.gc();
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
        judul.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Comic_Sans_MS3.ttf"), Typeface.BOLD);
        latin.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Comic_Sans_MS3.ttf"), Typeface.ITALIC);
        judul.setGravity(Gravity.CENTER);
        latin.setGravity(Gravity.CENTER);

        mScrollContent = (ScrollView) mContentView.findViewById(R.id.adapter_id_scrollresultdiagnose);
        // prepare ScrollView
        mContent1 = (LinearLayout) mContentView.findViewById(R.id.actdiagnose_id_results1);
        mContent2 = (LinearLayout) mContentView.findViewById(R.id.actdiagnose_id_results2);

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
        klikBawah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (++countBtn >= maxCount && onClickListener != null) {
                    onClickListener.onClick(mContentView); // the 1st parameters is main Content View so if you unvisible the layout its become easier
                    klikBawahText.setText(R.string.actdiagnose_string_klikcaramenanggulangi);
                    countBtn = 0;
                }
                else {
                    // Do into cara_atasi
                    mContent1.setVisibility(View.GONE);
                    mContent2.setVisibility(View.VISIBLE);
                    mScrollContent.pageScroll(1);
                    klikBawahText.setText(R.string.actdiagnose_string_klikbalikdiagnosa);
                    /////////////////////
                }
            }
        });

    }
    private void buildLoadingLayout() {
        if(dialog == null) {
            LinearLayout rootElement = buildAndConfigureRootelement();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(rootElement);
            builder.setCancelable(false);
            dialog = builder.create();
        }
        dialog.show();
    }

    private LinearLayout buildAndConfigureRootelement() {
        int sizeDialog =
                Math.round(activity.getResources().getDimension(R.dimen.actsplash_dimen_loading_wh));
        LinearLayout resultElement = new LinearLayout(activity);
        LinearLayout.LayoutParams paramRoot = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        resultElement.setLayoutParams(paramRoot);
        resultElement.setPadding(10, 10, 10, 10);
        resultElement.setOrientation(LinearLayout.HORIZONTAL);

        GifImageView gifImg = new GifImageView(activity);
        gifImg.setLayoutParams(new LinearLayout.LayoutParams(
                sizeDialog,
                sizeDialog
        ));
        gifImg.setImageResource(R.drawable.loading);
        resultElement.addView(gifImg);

        TextView textView = new TextView(activity);
        textView.setText("Loading...");
        LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        //paramsText.leftMargin = 40;
        paramsText.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        textView.setLayoutParams(paramsText);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        resultElement.addView(textView);
        return resultElement;
    }
    @Override
    public void close() throws IOException {
        if(imageViewPenyakit != null)
            imageViewPenyakit.close();
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
