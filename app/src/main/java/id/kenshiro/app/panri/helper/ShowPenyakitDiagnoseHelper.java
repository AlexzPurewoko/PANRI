package id.kenshiro.app.panri.helper;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.Gravity;
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

import com.mylexz.utils.MylexzActivity;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

import org.jetbrains.annotations.NotNull;

import id.kenshiro.app.panri.DiagnoseActivity;
import id.kenshiro.app.panri.HowToResolveActivity;
import id.kenshiro.app.panri.MainActivity;
import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.adapter.ImageGridViewAdapter;
import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;
import id.kenshiro.app.panri.opt.WebViewDestroy;
import id.kenshiro.app.panri.opt.ads.DownloadIklanFiles;
import id.kenshiro.app.panri.opt.ads.GetResultedIklanThr;
import id.kenshiro.app.panri.opt.ads.SendAdsBReceiver;
import id.kenshiro.app.panri.opt.ads.UpdateAdsService;
import id.kenshiro.app.panri.opt.onmain.DialogOnMain;
import id.kenshiro.app.panri.opt.onmain.DialogShowPasangIklan;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ShowPenyakitDiagnoseHelper implements Closeable{
    private MylexzActivity activity;
    private SQLiteDatabase sqLiteDatabase;
    private String path_to_file;
    private String image_default_dirs;
    // load layout elements
    private RelativeLayout mRootView, mContentView;
    //private WebView gejala, umum, caraatasi;
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
    private DialogShowHelper dialogShowHelper;

    // for item
    LinearLayout baseUmumLayout;
    CardView baseGejala, baseCaraAtasi;
    LinearLayout pasangIklanHolder;
    boolean firstCondition = true;
    private OnHandlerClickCardBottom onHandlerClickCardBottom = null;

    private LinearLayout iklanHolder;
    private SendAdsBReceiver sendAdsBReceiver = null;
    public LruCache<Integer, GifDrawable> mImageProducts;
    //List<com.felipecsl.gifimageview.library.GifImageView> gifImageViewListIklan = new ArrayList<>();
    private DialogShowPasangIklan dialog;

    // dialog Alert
    public ShowPenyakitDiagnoseHelper(@NonNull MylexzActivity activity, @NonNull SQLiteDatabase sqLiteDatabase, @NonNull RelativeLayout mRootView){
        this.activity = activity;
        this.sqLiteDatabase = sqLiteDatabase;
        this.mRootView = mRootView;
        dialogShowHelper = new DialogShowHelper(activity);
        dialogShowHelper.buildLoadingLayout();
        File tmp = new File(activity.getFilesDir(), "data_hama_html");
        this.path_to_file = "file://" + tmp.getAbsolutePath() + "/";
        this.image_default_dirs = activity.getFilesDir().getAbsolutePath() + "/" + "data/images/list";
    }

    public void setmTextPetaniDesc(Button mTextPetaniDesc) {
        this.mTextPetaniDesc = mTextPetaniDesc;
    }

    public void build(){
        prepareAndBuildLayout();
        // sets the layout visibility and apply into rootView
        mContentView.setVisibility(View.GONE);
        mRootView.addView(mContentView);
        requestIklan();
    }

    private WebView[] showKeyId(final int keyId) {
        setPenyakitText(keyId);
        selectContentOnDB(keyId);
        setImagePager(keyId);
        mContentView.setVisibility(View.VISIBLE);
        mContent1.setVisibility(View.VISIBLE);
        mContent2.setVisibility(View.GONE);
        if (!firstCondition) {
            clearViewOn(baseUmumLayout, baseUmumLayout.getChildCount() - 1);
            clearViewOn(baseGejala, baseGejala.getChildCount() - 1);
            clearViewOn(baseCaraAtasi, baseCaraAtasi.getChildCount() - 1);
            System.gc();
        } else
            firstCondition = false;
        WebView umum = setWebView(baseUmumLayout);
        WebView gejala = setWebView(baseGejala);
        WebView caraatasi = setWebView(baseCaraAtasi);
        umum.setVisibility(View.GONE);
        gejala.setVisibility(View.GONE);
        caraatasi.setVisibility(View.GONE);
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

        umum.loadUrl(path_to_file + "" + dataPath.getUmum_path());
        gejala.loadUrl(path_to_file + "" + dataPath.getGejala_path());
        caraatasi.loadUrl(path_to_file + "" + dataPath.getCara_atasi_path());
        startAnimIklan();
        return new WebView[]{umum, gejala, caraatasi};
    }
    public void show(final int keyId){
        dialogShowHelper.showDialog();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WebView[] key = showKeyId(keyId);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    String keyEx = "show_showPDHelper";
                    String resE = String.format("Interrupted when pause a main thread e -> %s", e.toString());
                    LogIntoCrashlytics.logException(keyEx, resE, e);
                    activity.LOGE(keyEx, resE);
                }
                key[0].setVisibility(View.VISIBLE);
                key[1].setVisibility(View.VISIBLE);
                key[2].setVisibility(View.VISIBLE);
                dialogShowHelper.stopDialog();
            }
        }, 1000);
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
        imageViewPenyakit.setListLocationFileImages(mListResImage, "show_diagnose");
        int dimen = Math.round(activity.getResources().getDimension(R.dimen.margin_img_penyakit));
        imageViewPenyakit.setMargin(0, dimen, dimen, dimen, dimen, Math.round(activity.getResources().getDimension(R.dimen.content_imggrid_padding)) + 2);
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

    private void requestIklan() {
        sendAdsBReceiver = new SendAdsBReceiver(activity, new SendAdsBReceiver.OnReceiveAds() {
            @Override
            public void onReceiveByteAds(GetResultedIklanThr.ByteArray[] ads, DownloadIklanFiles.DBIklanCollection[] information) {
                if (activity != null && iklanHolder != null && ads != null && ads.length > 0) {
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
                                ImageView v = new ImageView(activity);
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
                                        DialogOnMain.showOnClickedIklanViews(activity, url, info_produk, methodPost);
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
                        pasangIklanHolder.getChildAt(pasangIklanHolder.getChildCount() - (y + 1)).setVisibility(View.VISIBLE);
                    }
                } else if (ads == null) {
                    for (int x = 1; x <= 2; x++) {
                        pasangIklanHolder.getChildAt(pasangIklanHolder.getChildCount() - x).setVisibility(View.VISIBLE);
                    }
                }
                //
            }

        });
        activity.registerReceiver(sendAdsBReceiver, new IntentFilter(KeyListClasses.INTENT_BROADCAST_SEND_IKLAN));
        Intent intentService = new Intent(activity, UpdateAdsService.class);
        intentService.putExtra(KeyListClasses.GET_ADS_MODE_START_SERVICE, KeyListClasses.IKLAN_MODE_GET_IKLAN);
        intentService.putExtra(KeyListClasses.NUM_REQUEST_IKLAN_MODES, KeyListClasses.ADS_PLACED_ON_HOWTO);
        activity.startService(intentService);
        // send request into service
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

    public void setOnHandlerClickCardBottom(OnHandlerClickCardBottom onHandlerClickCardBottom) {
        this.onHandlerClickCardBottom = onHandlerClickCardBottom;
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
        baseUmumLayout = mContentView.findViewById(R.id.actdiagnose_id_umumcard_baselayout);
        baseGejala = mContentView.findViewById(R.id.actdiagnose_id_gejalacard);
        baseCaraAtasi = mContentView.findViewById(R.id.actdiagnose_id_howtocard);
        //iklanHolder = mContentView.findViewById(R.id.diagnose_iklan_layout);
        pasangIklanHolder = mContentView.findViewById(R.id.diagnose_iklan_pasanglayout);
        for (int x = 0; x < 2; x++) {
            CardView cardView = (CardView) CardView.inflate(activity, R.layout.actmain_instadds, null);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    //DialogOnMain.showDialogPasangIklan(activity, Uri.parse("https://api.whatsapp.com/send?phone=6282223518455&text=Hallo%20Admin%20Saya%20Mau%20Pasang%20Iklan"), "file:///android_asset/introduce_ads_howto.html");
                }
            });
            cardView.setVisibility(View.GONE);
            pasangIklanHolder.addView(cardView);
        }
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
                    stopAnimIklan();
                    if (onHandlerClickCardBottom != null)
                        onHandlerClickCardBottom.onHandleClick(1);
                }
                else {
                    // Do into cara_atasi
                    dialogShowHelper.showDialog();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mContent1.setVisibility(View.GONE);
                            mContent2.setVisibility(View.VISIBLE);
                            mScrollContent.pageScroll(1);
                            startAnimIklan();
                            klikBawahText.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    klikBawahText.setText(R.string.actdiagnose_string_klikbalikdiagnosa);
                                    dialogShowHelper.stopDialog();
                                    if (onHandlerClickCardBottom != null)
                                        onHandlerClickCardBottom.onHandleClick(0);
                                }
                            }, 1000);

                        }
                    }, 1000);
                    /////////////////////
                }
            }
        });
        dialog = new DialogShowPasangIklan(activity);
        dialog.build(Uri.parse("https://api.whatsapp.com/send?phone=6282223518455&text=Hallo%20Admin%20Saya%20Mau%20Pasang%20Iklan"));
        try {
            dialog.load("introduce_ads_howto");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopAnimIklan() {
        if (mImageProducts != null && mImageProducts.size() >= 1) {
            for (int x = 0; x < mImageProducts.size(); x++) {
                if (mImageProducts.get(x).isRunning())
                    mImageProducts.get(x).stop();
            }
        }
    }

    public void startAnimIklan() {
        if (mImageProducts != null && mImageProducts.size() >= 1) {
            for (int x = 0; x < mImageProducts.size(); x++) {
                if (!mImageProducts.get(x).isRunning())
                    mImageProducts.get(x).start();
            }
        }
    }


    private WebView setWebView(@NotNull ViewGroup baseLayout) {
        WebView web = new WebView(activity);
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

    // usages only for webview
    private void clearViewOn(ViewGroup baseLayout, int index) {
        if (baseLayout.getChildAt(index) instanceof WebView)
            WebViewDestroy.destroyWebView(baseLayout, (WebView) baseLayout.getChildAt(index));
        System.gc();
    }
    @Override
    public void close() throws IOException {
        if(imageViewPenyakit != null)
            imageViewPenyakit.close();
        stopAnimIklan();
        activity.unregisterReceiver(sendAdsBReceiver);
        if (mImageProducts != null && mImageProducts.size() >= 1) {
            for (int x = 0; x < mImageProducts.size(); x++) {
                mImageProducts.get(x).recycle();
            }
            mImageProducts.evictAll();
        }
        // release the webview
        clearViewOn(baseUmumLayout, baseUmumLayout.getChildCount() - 1);
        clearViewOn(baseGejala, baseGejala.getChildCount() - 1);
        clearViewOn(baseCaraAtasi, baseCaraAtasi.getChildCount() - 1);
        activity = null;
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
            sqLiteDatabase.close();
        sqLiteDatabase = null;
        path_to_file = image_default_dirs = null;
        if (mRootView != null) {
            mRootView.removeAllViews();
            mRootView = null;
        }

        if (mContentView != null) {
            mContentView.removeAllViews();
            mContentView = null;
        }
        judul = latin = null;
        if (klikBawah != null) {
            klikBawah.removeAllViews();
            klikBawah = null;
        }
        klikBawahText = null;
        if (mContent1 != null) {
            mContent1.removeAllViews();
            mContent1 = null;
        }
        if (mContent2 != null) {
            mContent2.removeAllViews();
            mContent2 = null;
        }
        imageViewPenyakit = null;
        dataPath = null;
        onClickListener = null;
        if (mScrollContent != null) {
            mScrollContent.removeAllViews();
            mScrollContent = null;
        }
        mTextPetaniDesc = null;
        dialogShowHelper = null;
        if (baseUmumLayout != null) {
            baseUmumLayout.removeAllViews();
            baseUmumLayout = null;
        }
        if (baseGejala != null) {
            baseGejala.removeAllViews();
            baseGejala = null;
        }
        if (baseCaraAtasi != null) {
            baseCaraAtasi.removeAllViews();
            baseCaraAtasi = null;
        }
        if (pasangIklanHolder != null) {
            pasangIklanHolder.removeAllViews();
            pasangIklanHolder = null;
        }
        onHandlerClickCardBottom = null;
        if (iklanHolder != null) {
            iklanHolder.removeAllViews();
            iklanHolder = null;
        }
        sendAdsBReceiver = null;
        /*if (gifImageViewListIklan != null) {
            gifImageViewListIklan.clear();
            gifImageViewListIklan = null;
        }*/
        dialog = null;
        System.gc();
    }

    public interface OnHandlerClickCardBottom {
        // 0 is first, 1 is last
        public void onHandleClick(int btnCondition);
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
