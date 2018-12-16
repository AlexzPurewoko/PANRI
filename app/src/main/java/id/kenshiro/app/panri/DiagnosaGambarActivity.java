package id.kenshiro.app.panri;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.widget.CardView;
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
import id.kenshiro.app.panri.helper.ShowPenyakitDiagnoseHelper;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.helper.TampilDiagnosaGambarHelper;
import id.kenshiro.app.panri.helper.TampilListPenyakitHelper;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;
import io.fabric.sdk.android.Fabric;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class DiagnosaGambarActivity extends MylexzActivity {
    Toolbar toolbar;
    SQLiteDatabase sqlDB;
    TampilDiagnosaGambarHelper tampilDiagnosaGambarHelper;
    ShowPenyakitDiagnoseHelper showPenyakitDiagnoseHelper;
    Button mTextPetaniDesc;
    private Handler handlerPetani;
    private RelativeLayout relativeLayout;
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
            setContentView(R.layout.actdiagnoseimg_main);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setMyActionBar();
            setDB();
            setContentV();
        } catch (Throwable e) {
            String keyEx = getClass().getName() + "_onCreate()";
            String resE = String.format("UnHandled Exception Occurs(Throwable) e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            LOGE(keyEx, resE);
        }
    }

    private void setContentV() {
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

                            showPenyakitDiagnoseHelper = new ShowPenyakitDiagnoseHelper(DiagnosaGambarActivity.this, sqlDB, (RelativeLayout) findViewById(R.id.actdim_id_layoutcontainer));
                            tampilDiagnosaGambarHelper = new TampilDiagnosaGambarHelper(DiagnosaGambarActivity.this, (RelativeLayout) findViewById(R.id.actdim_id_layoutcontainer), sqlDB);
                            tampilDiagnosaGambarHelper.setOnItemListener(new TampilDiagnosaGambarHelper.OnItemListener() {
                                                                             @Override
                                                                             public void onBtnYaClicked(View v, View btn, int position) {
                                                                                 //v.setVisibility(View.GONE);
                                                                                 //btn.setVisibility(View.GONE);
                                                                                 tampilDiagnosaGambarHelper.hideContentView();
                                                                                 showPenyakitDiagnoseHelper.setmTextPetaniDesc(mTextPetaniDesc);
                                                                                 showPenyakitDiagnoseHelper.show(position);
                                                                                 //mTextPetaniDesc.setText(getString(R.string.actdiagnose_string_speechfarmer_img_2));
                                                                                 onButtonPetaniClicked(getString(R.string.actdiagnose_string_speechfarmer_img_2));
                                                                             }

                                                                             @Override
                                                                             public void onBtnTidakClicked(View v, View btn, int position) {
                                                                                 onButtonPetaniClicked(getString(R.string.actdiagnose_string_speechfarmer_img_1));
                                                                             }

                                                                             @Override
                                                                             public void onIsAfterLastListPosition(View v, View btn, int position, int size_list) {
                                                                                 // v.setVisibility(View.GONE);
                                                                                 //btn.setVisibility(View.GONE);
                                                                                 setAlertDialog(v, btn);
                                                                             }

                                                                             private void setAlertDialog(View v, final View btn) {
                                                                                 if (relativeLayout == null) {
                                                                                     final RelativeLayout root = findViewById(R.id.actdim_id_layoutcontainer);
                                                                                     relativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.layout_onnotselected, root, false);
                                                                                     CardView cardView = relativeLayout.findViewById(R.id.layoutonselected_id_card_ya);
                                                                                     CardView cardView1 = relativeLayout.findViewById(R.id.layoutonselected_id_card_tidak);
                                                                                     cardView.setOnClickListener(new View.OnClickListener() {
                                                                                         @Override
                                                                                         public void onClick(View v) {
                                                                                             relativeLayout.setVisibility(View.GONE);
                                                                                             v.setVisibility(View.VISIBLE);
                                                                                             btn.setVisibility(View.VISIBLE);
                                                                                             tampilDiagnosaGambarHelper.setPosition(1);
                                                                                         }
                                                                                     });
                                                                                     cardView1.setOnClickListener(new View.OnClickListener() {
                                                                                         @Override
                                                                                         public void onClick(View v) {
                                                                                             SwitchIntoMainActivity.switchToMain(DiagnosaGambarActivity.this);
                                                                                         }
                                                                                     });
                                                                                     root.addView(relativeLayout);
                                                                                 } else {
                                                                                     relativeLayout.setVisibility(View.VISIBLE);
                                                                                 }
                                                                             }
                                                                         }
                            );
                            showPenyakitDiagnoseHelper.setOnHaveFinalRequests(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showPenyakitDiagnoseHelper.countBtn -= 2;
                                    showPenyakitDiagnoseHelper.klikBawahText.setText(R.string.actdiagnose_string_klikcaramenanggulangi);
                                    v.setVisibility(View.GONE);

                                    tampilDiagnosaGambarHelper.setItemPosition(1);
                                    tampilDiagnosaGambarHelper.updateContentAfter();
                                    tampilDiagnosaGambarHelper.showContentView();
                                }
                            });
                            showPenyakitDiagnoseHelper.setOnHandlerClickCardBottom(new ShowPenyakitDiagnoseHelper.OnHandlerClickCardBottom() {
                                @Override
                                public void onHandleClick(int btnCondition) {
                                    onButtonPetaniClicked(getString(R.string.actdiagnose_string_speechfarmer_img_2));
                                }
                            });
                            showPenyakitDiagnoseHelper.build();
                            tampilDiagnosaGambarHelper.buildAndShow();
                            tampilDiagnosaGambarHelper.showContentView();
                            onButtonPetaniClicked(getString(R.string.actdiagnose_string_speechfarmer_img_1));
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

    private void onButtonPetaniClicked(String text) {

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
    public void onConfigurationChanged(Configuration newConfig) {

    }

    private void setDB() {
        sqlDB = SQLiteDatabase.openOrCreateDatabase("/data/data/id.kenshiro.app.panri/files/database_penyakitpadi.db", null);
    }

    @Override
    protected void onPause() {
        System.gc();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        releaseGifNpc();
        try {
            tampilDiagnosaGambarHelper.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            showPenyakitDiagnoseHelper.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (toolbar != null)
            toolbar.removeAllViews();
        if (sqlDB != null)
            sqlDB.close();
        mTextPetaniDesc = null;
        handlerPetani = null;
        if (relativeLayout != null)
            relativeLayout.removeAllViews();
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
                if (!tampilDiagnosaGambarHelper.isContentViewHidden()) {
                    SwitchIntoMainActivity.switchToMain(this);
                    return true;
                } else
                    return false;
            } else {
                if (showPenyakitDiagnoseHelper.getmContent2().getVisibility() == View.VISIBLE) {
                    showPenyakitDiagnoseHelper.getmContent2().setVisibility(View.GONE);
                    showPenyakitDiagnoseHelper.getmContent1().setVisibility(View.VISIBLE);
                    showPenyakitDiagnoseHelper.mScrollContent.pageScroll(0);
                    --showPenyakitDiagnoseHelper.countBtn;
                    showPenyakitDiagnoseHelper.klikBawahText.setText(R.string.actdiagnose_string_klikcaramenanggulangi);
                    return false;
                } else if (showPenyakitDiagnoseHelper.getmContent1().getVisibility() == View.VISIBLE) {
                    showPenyakitDiagnoseHelper.getmContent1().setVisibility(View.GONE);
                    showPenyakitDiagnoseHelper.getmContentView().setVisibility(View.GONE);
                    tampilDiagnosaGambarHelper.showContentView();
                    mTextPetaniDesc.setOnClickListener(null);
                    //mTextPetaniDesc.setText(getString(R.string.actdiagnose_string_speechfarmer_img_1));
                    onButtonPetaniClicked(getString(R.string.actdiagnose_string_speechfarmer_img_1));
                    tampilDiagnosaGambarHelper.mContentView.pageScroll(0);
                    //--showPenyakitDiagnoseHelper.countBtn;
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
