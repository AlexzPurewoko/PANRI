package id.kenshiro.app.panri;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.text.SpannableString;

import com.crashlytics.android.Crashlytics;
import com.mylexz.utils.DiskLruObjectCache;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SimpleDiskLruCache;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import id.kenshiro.app.panri.adapter.CustomViewPager;
import id.kenshiro.app.panri.adapter.ImageFragmentAdapter;

import android.view.Gravity;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import id.kenshiro.app.panri.adapter.CustomPageViewTransformer;
import id.kenshiro.app.panri.helper.CheckAndMoveDB;
import id.kenshiro.app.panri.helper.DecodeBitmapHelper;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;
import id.kenshiro.app.panri.opt.onmain.CheckDBUpdateThread;
import id.kenshiro.app.panri.opt.onmain.DialogOnMain;
import id.kenshiro.app.panri.opt.onmain.PrepareBitmapViewPager;
import id.kenshiro.app.panri.opt.onmain.TaskDownloadDBUpdates;
import io.fabric.sdk.android.Fabric;
import pl.droidsonroids.gif.GifImageView;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import android.support.v7.widget.CardView;
import android.support.v7.app.AlertDialog;
import android.graphics.Color;
import android.view.KeyEvent;

public class MainActivity extends MylexzActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final long TIME_BETWEEN_IMAGE = 6000;
    private static final long TIME_AUTO_UPDATE_TEXT_MILLIS = 5000; // 5s
    Toolbar toolbar;
    // for view image pager
    public LinearLayout indicators;
    public CustomViewPager mImageSelector;
    private TextView mTextDetails;
    private Handler handlerPetani;
    private Handler autoClick = null;
    // for section petani
    private Button mTextPetaniDesc;
    private GifImageView imgPetaniKedipView;
    private int[] TextPetaniDesc = {
            R.string.actmain_string_speechfarmer_1,
            R.string.actmain_string_speechfarmer_2,
            R.string.actmain_string_speechfarmer_3,
            R.string.actmain_string_speechfarmer_4,
            R.string.actmain_string_speechfarmer_5
    };
    private int mPosTxtPetani = 0;

    // for section operation
    private LinearLayout mListOp;
    private List<CardView> mListCard;
    private volatile Runnable mImageSwitcher = null;
    private volatile Runnable mAutoClickHandler = null;
    private volatile Handler mImageHandlerSw = null;
    public volatile int curr_pos_image = 0;

    private boolean doubleBackToExitPressedOnce;
    public LruCache<Integer, Bitmap> mImageMemCache;
    private WeakReference<PrepareBitmapViewPager> prepareBitmapViewPagerWeakReference;
    public int has_finished = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)  // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);
        try {
            setContentView(R.layout.activity_main);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setDB();
            setMyActionBar();
            setInitialPagerData();
            setInitialTextInds();
            setInitialSectPetani();
            setInitialSectOpIntent();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            checkVersion();
        } catch (Throwable e) {
            String keyEx = getClass().getName() + "_onCreate()";
            String resE = String.format("UnHandled Exception Occurs(Throwable) e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            LOGE(keyEx, resE);
        }
    }

    public void setAutoClickUpdate() {
        if (mAutoClickHandler == null && autoClick == null) {
            mAutoClickHandler = new Runnable() {
                @Override
                public void run() {
                    onButtonPetaniClicked(false);

                    Handler inner = new Handler(Looper.getMainLooper());
                    inner.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            clearAutoClickUpdate(mAutoClickHandler);
                            setAutoClickUpdate();
                        }
                    }, TIME_AUTO_UPDATE_TEXT_MILLIS);
                }
            };
            autoClick = new Handler(Looper.getMainLooper());
            autoClick.postDelayed(mAutoClickHandler, TIME_AUTO_UPDATE_TEXT_MILLIS);
        }
    }

    public void clearAutoClickUpdate(Runnable runnable) {
        if (autoClick != null) {
            autoClick.removeCallbacks(runnable);
            autoClick = null;
            mAutoClickHandler = null;
            System.gc();
        }
    }

    public void setHandlers(final CustomViewPager customViewPager, final int size) {
        mImageSwitcher = new Runnable() {
            @Override
            public void run() {
                if (curr_pos_image >= size)
                    curr_pos_image = 0;
                customViewPager.setCurrentItem(curr_pos_image, true);
                ++curr_pos_image;
                clearHandlers(this);
                mImageHandlerSw = new Handler(Looper.getMainLooper());
                mImageHandlerSw.postDelayed(mImageSwitcher, TIME_BETWEEN_IMAGE);
            }
        };
        mImageHandlerSw = new Handler(Looper.getMainLooper());
        mImageHandlerSw.postDelayed(mImageSwitcher, TIME_BETWEEN_IMAGE);
    }

    private void clearHandlers(Runnable runnable) {
        if (mImageHandlerSw != null) {
            mImageHandlerSw.removeCallbacks(runnable);
            mImageHandlerSw = null;
            System.gc();
        }
    }

    private void checkVersion() {
        Bundle bundle = getIntent().getExtras();
        int app_cond = bundle.getInt(KeyListClasses.APP_CONDITION_KEY);
        int db_cond = bundle.getInt(KeyListClasses.DB_CONDITION_KEY);
        String messageIfNeeded = null;
        String dbErrorMesageIfNeeded = null;
        switch (app_cond) {
            case KeyListClasses.APP_IS_FIRST_USAGE:
            case KeyListClasses.APP_IS_NEWER_VERSION:
                DialogOnMain.showDialogWhatsNew(this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case KeyListClasses.APP_IS_OLDER_VERSION:
                messageIfNeeded = "Aplikasi ini sudah usang dan tidak kompatibel dengan versi sebelumnya yang lebih baru, coba copot dan pasang lagi aplikasi ini";
            case KeyListClasses.APP_IS_SAME_VERSION:
                break;
        }
        switch (db_cond) {
            case KeyListClasses.DB_REQUEST_UPDATE: {
                final String[] dbVersion = bundle.getStringArray(KeyListClasses.KEY_LIST_VERSION_DB);
                DialogOnMain.showUpdateDBDialog(this, KeyListClasses.UPDATE_DB_IS_AVAILABLE, new Object[]{
                        dbVersion[0],
                        dbVersion[1],
                        "Update",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new TaskDownloadDBUpdates(MainActivity.this, dbVersion[1]).execute();
                            }
                        }
                });
            }
            break;
            case KeyListClasses.DB_IS_FIRST_USAGE:
                break;
            case KeyListClasses.DB_IS_NEWER_VERSION:
                TOAST(Toast.LENGTH_SHORT, "The data now is new version !");
                break;
            case KeyListClasses.DB_IS_OLDER_IN_APP_VERSION:
                dbErrorMesageIfNeeded = "Current Database dengan database di aplikasi sudah usang, mohon copot dan pasang aplikasi untuk membenahi";
            case KeyListClasses.DB_IS_SAME_VERSION:
                break;
        }
        showDialog(messageIfNeeded, dbErrorMesageIfNeeded);
    }

    private void showDialog(String messageIfNeeded, final String dbErrorMesageIfNeeded) {
        int selected = -1;
        if (messageIfNeeded == null && dbErrorMesageIfNeeded == null) return;
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Peringatan!");
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        if (messageIfNeeded == null) {
            selected = 0;
            alert.setMessage(dbErrorMesageIfNeeded);
        } else {
            selected = 1;
            alert.setMessage(messageIfNeeded);
        }
        final int selectable = selected;
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (selectable == 0) {
                    MainActivity.this.finish();
                    return;
                }
                AlertDialog.Builder alert2 = new AlertDialog.Builder(MainActivity.this);
                alert2.setCancelable(false);
                alert2.setTitle("Peringatan!");
                alert2.setIcon(android.R.drawable.ic_dialog_alert);
                alert2.setMessage(dbErrorMesageIfNeeded);
                alert2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        MainActivity.this.finish();
                    }
                });
                alert2.show();
            }
        });
        alert.show();
    }

    private void setDB() {
        try {
            new CheckAndMoveDB(this, "database_penyakitpadi.db").upgradeDB();
        } catch (IOException e) {
            String keyEx = getClass().getName() + "_setDB()";
            String resE = String.format("ERROR WHEN HANDLING checkAndMoveDB() e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            LOGE(keyEx, resE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (has_finished == 1) {
            setHandlers(mImageSelector, mImageMemCache.size());
            setAutoClickUpdate();
        }
    }

    @Override
    protected void onPause() {
        clearHandlers(mImageSwitcher);
        clearAutoClickUpdate(mAutoClickHandler);
        super.onPause();
    }

    private void setMyActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        SpannableString strTitle = new SpannableString(getTitle());
        Typeface tTitle = Typeface.createFromAsset(getAssets(), "Gecko_PersonalUseOnly.ttf");
        strTitle.setSpan(new CustomTypefaceSpan(tTitle), 0, getTitle().length(), 0);
        toolbar.setTitle(strTitle);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SwitchIntoMainActivity.switchTo(this, PanriSettingActivity.class, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_gallery:
                SwitchIntoMainActivity.switchTo(this, GalleryActivity.class, null);
                break;
            case R.id.send_report:
                DialogOnMain.showReportDialog(this);
                break;
            case R.id.nav_about:
                SwitchIntoMainActivity.switchTo(this, AboutActivity.class, null);
                break;
            case R.id.nav_out:
                this.finish();
                break;
            case R.id.update_db:
                new CheckDBUpdateThread(this).execute();
                break;
            case R.id.nav_rate: {
                // https://play.google.com/store/apps/details?id=ohi.andre.consolelauncher
                String packageApp = getPackageName().toString();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageApp));
                i.setPackage("com.android.vending");
                startActivity(Intent.createChooser(i, "Beri Nilai dengan..."));
            }
                break;
            case R.id.nav_share: {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_TEXT, String.format("Download Aplikasi PANRI ke smartphone Android kamu dengan klik https://play.google.com/store/apps/details?id=%s", getPackageName().toString()));
                i.setType("text/plain");
                startActivity(Intent.createChooser(i, "Bagikan Aplikasi dengan..."));
            }
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setCardTouchEvent(final Class<?>[] cls) {

        for (int x = 0; x < mListCard.size(); x++) {
            final int y = x;
            mListCard.get(x).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (cls[y] != null) {
                                                            SwitchIntoMainActivity.switchTo(MainActivity.this, cls[y], null);
                                                        }
                                                    }
                                                }
            );
        }
    }

    private void setInitialSectOpIntent() {
        mListOp = (LinearLayout) findViewById(R.id.actmain_id_listmainoperation);
        int[][] listConfOp = {
                // { @Res to Image Drawable, @Res to text}
                {R.drawable.ic_actmain_diagnose, R.string.actmain_string_startdiagnose},
                {R.drawable.ic_actmain_imgdiagnose, R.string.actmain_string_diagnosagambar},
                {R.drawable.ic_actmain_howto, R.string.actmain_string_howto},
                {R.drawable.ic_actmain_aboutpenyakit, R.string.actmain_string_aboutpenyakit}
        };
        Class<?>[] listClass = {
                DiagnoseActivity.class,
                DiagnosaGambarActivity.class,
                HowToResolveActivity.class,
                InfoPenyakitActivity.class
        };
        mListCard = new ArrayList<CardView>();
        // add a cardView
        for (int x = 0; x < listConfOp.length; x++) {
            mListCard.add((CardView) CardView.inflate(this, R.layout.cardview_adapter, null));
            LinearLayout content = (LinearLayout) LinearLayout.inflate(this, R.layout.actmain_content_op_incard, null);
            ImageView imgOpC = (ImageView) content.getChildAt(0);
            TextView txtOpC = (TextView) content.getChildAt(1);
            imgOpC.setImageResource(listConfOp[x][0]);
            txtOpC.setTypeface(Typeface.createFromAsset(getAssets(), "Gill_SansMT.ttf"), Typeface.BOLD_ITALIC);
            txtOpC.setText(listConfOp[x][1]);
            mListCard.get(x).setContentPadding(10, 10, 10, 10);
            mListCard.get(x).addView(content);
        }
        setCardTouchEvent(listClass);
        // fill into LinearLayout
        for (int x = 0; x < mListCard.size(); x++) {
            mListOp.addView(mListCard.get(x));
        }
        addPasangIklanCard();
    }

    private void addPasangIklanCard() {
        CardView cardView = (CardView) CardView.inflate(this, R.layout.actmain_instadds, null);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogOnMain.showDialogPasangIklan(MainActivity.this);
            }
        });
        mListOp.addView(cardView);
    }

    private void onButtonPetaniClicked(boolean isOnStart) {
        if (!isOnStart) {
            if (++mPosTxtPetani == TextPetaniDesc.length)
                mPosTxtPetani = 0;
            mTextPetaniDesc.setText(TextPetaniDesc[mPosTxtPetani]);
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
        } else {
            mPosTxtPetani = 0;
            mTextPetaniDesc.setText(TextPetaniDesc[mPosTxtPetani]);
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
    }

    private void setInitialSectPetani() {
        mTextPetaniDesc = (Button) findViewById(R.id.actmain_id_section_petani_btn);
        imgPetaniKedipView = findViewById(R.id.actsplash_id_gifpetanikedip);
        mTextPetaniDesc.setTextColor(Color.BLACK);
        mTextPetaniDesc.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.NORMAL);
        mTextPetaniDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                onButtonPetaniClicked(false);
            }
        });
        //mTextPetaniDesc.setText(TextPetaniDesc[mPosTxtPetani]);
        imgPetaniKedipView.setVisibility(View.VISIBLE);
        imgPetaniKedipView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onButtonPetaniClicked(false);
            }
        });
        onButtonPetaniClicked(true);
    }

    private void setInitialTextInds() {
        mTextDetails = (TextView) findViewById(R.id.actmain_id_textIndicatorViewPager);
        mTextDetails.setTypeface(Typeface.createFromAsset(getAssets(), "Gill_SansMT.ttf"), Typeface.ITALIC);
        mTextDetails.setText("MUDAHKAN HIDUPMU KENALI PENYAKIT PADIMU");

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    private void setInitialPagerData() {
        // initialize the view container
        indicators = (LinearLayout) findViewById(R.id.actmain_id_layoutIndicators);
        mImageSelector = (CustomViewPager) findViewById(R.id.actmain_id_viewpagerimg);
        Point reqSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(reqSize);
        reqSize.y = Math.round(getResources().getDimension(R.dimen.actmain_dimen_viewpager_height));
        mImageMemCache = new LruCache<Integer, Bitmap>(reqSize.x * reqSize.y);
        //task = new TaskBitmapViewPager(this);
        //task.execute();
        has_finished = 0;
        prepareBitmapViewPagerWeakReference = new WeakReference<>(new PrepareBitmapViewPager(this, reqSize, new File(getCacheDir(), "cache")));
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(prepareBitmapViewPagerWeakReference.get(), 50);
        // set the indicators

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (doubleBackToExitPressedOnce) {
                DialogOnMain.showExitDialog(this);
                return true;
            }

            this.doubleBackToExitPressedOnce = true;
            TOAST(Toast.LENGTH_SHORT, "Klik sekali lagi!");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        clearHandlers(mImageSwitcher);
        clearAutoClickUpdate(mAutoClickHandler);
        if (mImageMemCache != null) {
            for (int x = 0; x < mImageMemCache.size(); x++) {
                mImageMemCache.get(x).recycle();
            }
            mImageMemCache.evictAll();
        }
        super.onDestroy();
    }
}
