package id.kenshiro.app.panri;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import id.kenshiro.app.panri.adapter.AdapterRecycler;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.helper.TampilListPenyakitHelper;
import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;
import id.kenshiro.app.panri.opt.WebViewDestroy;
import id.kenshiro.app.panri.opt.ads.DownloadIklanFiles;
import id.kenshiro.app.panri.opt.ads.GetResultedIklanThr;
import id.kenshiro.app.panri.opt.ads.SendAdsBReceiver;
import id.kenshiro.app.panri.opt.ads.UpdateAdsService;
import id.kenshiro.app.panri.opt.onmain.DialogOnMain;
import id.kenshiro.app.panri.opt.onmain.DialogShowPasangIklan;
import io.fabric.sdk.android.Fabric;
import pl.droidsonroids.gif.GifDrawable;
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
    public LruCache<Integer, GifDrawable> mImagePetani;
    private ImageView gifNpcView;
    private Handler handlerPetani;
    private boolean firstCondition = true;
    private CardView webCard;
    private CardView basePasangIklan;
    private LinearLayout iklanHolder, iklanPasang;
    public LruCache<Integer, GifDrawable> mImageProducts;
    private SendAdsBReceiver sendAdsBReceiver = null;
    //List<com.felipecsl.gifimageview.library.GifImageView> gifImageViewListIklan = new ArrayList<>();
    private DialogShowPasangIklan dialog;

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
            requestIklan();
            dialog = new DialogShowPasangIklan(this);
            dialog.build(Uri.parse("https://api.whatsapp.com/send?phone=6282223518455&text=Hallo%20Admin%20Saya%20Mau%20Pasang%20Iklan"));
            dialog.load("introduce_ads_howto");
        } catch (Throwable e) {
            String keyEx = getClass().getName() + "_onCreate()";
            String resE = String.format("UnHandled Exception Occurs(Throwable) e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            LOGE(keyEx, resE);
        }
    }

    private void requestIklan() {
        sendAdsBReceiver = new SendAdsBReceiver(this, new SendAdsBReceiver.OnReceiveAds() {
            @Override
            public void onReceiveByteAds(GetResultedIklanThr.ByteArray[] ads, DownloadIklanFiles.DBIklanCollection[] information) {
                if (HowToResolveActivity.this != null && iklanHolder != null && ads != null && ads.length > 0) {
                    //List<com.felipecsl.gifimageview.library.GifImageView> gifImageViewListIklan = new ArrayList<>();
                    int x = 0;
                    int size_counter = 0;
                    for (GetResultedIklanThr.ByteArray byteArr : ads) {
                        byte[] bArr = byteArr.getArray();
                        if (bArr != null && bArr.length > 1) {
                            //gifImageViewListIklan.add(setGifImgView(bArr, information[x]));
                            size_counter += bArr.length;
                        }
                        x++;
                        //
                    }
                    mImageProducts = new LruCache<>(size_counter * 2);
                    x = 0;
                    for (GetResultedIklanThr.ByteArray byteArr : ads) {
                        byte[] bArr = byteArr.getArray();
                        if (bArr != null && bArr.length > 1) {
                            try {
                                mImageProducts.put(x, new GifDrawable(bArr));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (mImageProducts.get(x) != null) {
                                mImageProducts.get(x).stop();
                                ImageView v = new ImageView(HowToResolveActivity.this);
                                v.setLayoutParams(new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                ));
                                v.setImageDrawable(mImageProducts.get(x));
                                final DownloadIklanFiles.DBIklanCollection foo = information[x];
                                v.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String url = foo.getUrl();
                                        String info_produk = foo.getInfo_produk();
                                        int methodPost = foo.getTipe_url();
                                        DialogOnMain.showOnClickedIklanViews(HowToResolveActivity.this, url, info_produk, methodPost);
                                    }
                                });
                                iklanHolder.addView(v);
                                mImageProducts.get(x).start();
                            }

                        }
                        x++;
                        //
                    }
                    // add its views
                    for (int y = mImageProducts.size(); y < 2; y++) {
                        iklanPasang.getChildAt(iklanPasang.getChildCount() - (y + 1)).setVisibility(View.VISIBLE);
                    }
                } else if (ads == null) {
                    for (int x = 1; x <= 2; x++) {
                        iklanPasang.getChildAt(iklanPasang.getChildCount() - x).setVisibility(View.VISIBLE);
                    }
                }
                //
            }

        });

        registerReceiver(sendAdsBReceiver, new IntentFilter(KeyListClasses.INTENT_BROADCAST_SEND_IKLAN));
        Intent intentService = new Intent(this, UpdateAdsService.class);
        intentService.putExtra(KeyListClasses.GET_ADS_MODE_START_SERVICE, KeyListClasses.IKLAN_MODE_GET_IKLAN);
        intentService.putExtra(KeyListClasses.NUM_REQUEST_IKLAN_MODES, KeyListClasses.ADS_PLACED_ON_HOWTO);
        startService(intentService);
        // send request into service
    }

    private void stopAnimIklan() {
        if (mImageProducts != null && mImageProducts.size() >= 1) {
            for (int x = 0; x < mImageProducts.size(); x++) {
                if (mImageProducts.get(x).isRunning())
                    mImageProducts.get(x).stop();
            }
        }
    }

    private void startAnimIklan() {
        if (mImageProducts != null && mImageProducts.size() >= 1) {
            for (int x = 0; x < mImageProducts.size(); x++) {
                if (!mImageProducts.get(x).isRunning())
                    mImageProducts.get(x).start();
            }
        }
    }

    private void setContent() {
        mTextPetaniDesc = (Button) findViewById(R.id.actall_id_section_petani_btn);
        gifNpcView = findViewById(R.id.actall_id_gifpetanikedip);
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

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    setPetaniHolders();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                if (mImagePetani != null) {
                    gifNpcView.post(new Runnable() {
                        @Override
                        public void run() {
                            gifNpcView.setVisibility(View.VISIBLE);
                            gifNpcView.setImageDrawable(mImagePetani.get(1));
                            mImagePetani.get(1).start();
                            tampil = new TampilListPenyakitHelper(HowToResolveActivity.this, sqlDB, (RelativeLayout) findViewById(R.id.acthowto_id_layoutcontainer));
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
                    });
                }

            }
        });

        iklanPasang = findViewById(R.id.howto_iklan_pasanglayout);
        for (int x = 0; x < 2; x++) {
            CardView cardView = (CardView) CardView.inflate(this, R.layout.actmain_instadds, null);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                }
            });
            cardView.setVisibility(View.GONE);
            iklanPasang.addView(cardView);
        }
        iklanHolder = findViewById(R.id.howto_iklan_layout);
    }

    private void setPetaniHolders() throws IOException {
        int[] res_gif_npc = {
                R.raw.petani_bicara,
                R.raw.petani_kedip
        };
        List<byte[]> listOfByte = new ArrayList<>();
        int counter = 0;
        for (int x = 0; x < res_gif_npc.length; x++) {
            InputStream inputStream = getResources().openRawResource(res_gif_npc[x]);
            listOfByte.add(new byte[inputStream.available()]);
            counter += inputStream.available();
            inputStream.read(listOfByte.get(x));
            inputStream.close();
        }
        mImagePetani = new LruCache<>(counter * 2);
        for (int x = 0; x < res_gif_npc.length; x++) {
            mImagePetani.put(x, new GifDrawable(listOfByte.get(x)));
            mImagePetani.get(x).stop();
        }
        listOfByte.clear();
        listOfByte = null;
        System.gc();
    }

    private void releaseGifNpc() {
        for (int x = 0; x < mImagePetani.size(); x++) {
            mImagePetani.get(x).stop();
            mImagePetani.get(x).recycle();
        }
        mImagePetani.evictAll();
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
        //imgPetaniKedipView.setImageResource(R.drawable.petani_bicara);
        mImagePetani.get(1).stop();
        gifNpcView.setImageDrawable(mImagePetani.get(0));
        mImagePetani.get(0).start();
        if (handlerPetani == null) {
            handlerPetani = new Handler();
            handlerPetani.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handlerPetani = null;
                    System.gc();
                    //imgPetaniKedipView.setImageResource(R.drawable.petani_kedip);
                    if (mImagePetani != null && mImagePetani.size() != 0 && !mImagePetani.get(0).isRecycled()) {
                        mImagePetani.get(0).stop();
                        gifNpcView.setImageDrawable(mImagePetani.get(1));
                        mImagePetani.get(1).start();
                    }
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
        startAnimIklan();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    protected void onPause() {
        stopAnimIklan();
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
        releaseGifNpc();
        stopAnimIklan();
        unregisterReceiver(sendAdsBReceiver);
        if (mImageProducts != null && mImageProducts.size() >= 1) {
            for (int x = 0; x < mImageProducts.size(); x++) {
                mImageProducts.get(x).recycle();
            }
            mImageProducts.evictAll();
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
                SwitchIntoMainActivity.switchToMain(this);
                return true;
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

    private void clearViewOn(ViewGroup baseLayout, int index) {
        WebViewDestroy.destroyWebView(baseLayout, (WebView) baseLayout.getChildAt(index));
        System.gc();
    }
}
