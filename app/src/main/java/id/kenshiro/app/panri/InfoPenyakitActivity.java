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
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
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

import com.crashlytics.android.Crashlytics;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import id.kenshiro.app.panri.adapter.AdapterRecycler;
import id.kenshiro.app.panri.helper.DiagnoseActivityHelper;
import id.kenshiro.app.panri.helper.ShowPenyakitDiagnoseHelper;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.helper.TampilListPenyakitHelper;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;
import io.fabric.sdk.android.Fabric;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class InfoPenyakitActivity extends MylexzActivity {
    private Toolbar toolbar;
    private TampilListPenyakitHelper tampil;
    private SQLiteDatabase sqlDB;
    private ShowPenyakitDiagnoseHelper showPenyakitDiagnoseHelper;
    private Handler handlerPetani;
    Button mTextPetaniDesc;
    public LruCache<Integer, GifDrawable> mImagePetani;
    private ImageView gifNpcView;
    //private GifImageView imgPetaniKedipView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)  // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);
        try {
            setContentView(R.layout.actinfo_main);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    private void setContent() {
        loadLayoutAndShow();
        mTextPetaniDesc = (Button) findViewById(R.id.actall_id_section_petani_btn);
        gifNpcView = findViewById(R.id.actall_id_gifpetanikedip);
        mTextPetaniDesc.setTextColor(Color.BLACK);
        mTextPetaniDesc.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.NORMAL);
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
                            tampil = new TampilListPenyakitHelper(InfoPenyakitActivity.this, sqlDB, (RelativeLayout) findViewById(R.id.actinfo_id_layoutcontainer));
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
                    });
                }

            }
        });
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

    private void loadLayoutAndShow() {
        showPenyakitDiagnoseHelper = new ShowPenyakitDiagnoseHelper(this, sqlDB, (RelativeLayout) this.findViewById(R.id.actinfo_id_layoutcontainer));
        showPenyakitDiagnoseHelper.build();
        showPenyakitDiagnoseHelper.setOnHandlerClickCardBottom(new ShowPenyakitDiagnoseHelper.OnHandlerClickCardBottom() {
            @Override
            public void onHandleClick(int btnCondition) {
                switch (btnCondition) {
                    case 0:
                        onButtonPetaniClicked(getText(R.string.actinfo_string_speechfarmer_2));
                        break;
                    case 1:
                        onButtonPetaniClicked(getText(R.string.actinfo_string_speechfarmer_1));
                }
            }
        });
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
                    /*if (imgPetaniKedipView != null)
                        imgPetaniKedipView.setImageResource(R.drawable.petani_kedip);*/
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
            String keyEx = getClass().getName() + "_onDestroy()";
            String resE = String.format("Unable to execute tampil.close(); e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            LOGE(keyEx, resE);
        }
        try {
            showPenyakitDiagnoseHelper.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        releaseGifNpc();
        System.gc();
        if (toolbar != null)
            toolbar.removeAllViews();
        tampil = null;
        sqlDB = null;
        showPenyakitDiagnoseHelper = null;
        handlerPetani = null;
        mTextPetaniDesc = null;
        //imgPetaniKedipView = null;
        System.gc();
        System.gc();
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
                    SwitchIntoMainActivity.switchToMain(this);
                    return true;
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
