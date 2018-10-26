package id.kenshiro.app.panri;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
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

import com.mylexz.utils.DiskLruObjectCache;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SimpleDiskLruCache;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import android.graphics.Typeface;
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
import android.widget.Toast;

import id.kenshiro.app.panri.adapter.CustomPageViewTransformer;
import id.kenshiro.app.panri.helper.CheckAndMoveDB;
import id.kenshiro.app.panri.helper.DecodeBitmapHelper;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
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
    Toolbar toolbar;
    // for view image pager
    LinearLayout indicators;
    private CustomViewPager mImageSelector;
    private TextView mTextDetails;
    private Handler handlerPetani;
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
    private volatile Handler mImageHandlerSw = null;
    private volatile int curr_pos_image = 0;

    private boolean doubleBackToExitPressedOnce;
    LruCache<Integer, Bitmap> mImageMemCache;
    private WeakReference<PrepareBitmapViewPager> prepareBitmapViewPagerWeakReference;
    private int has_finished = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void setHandlers(final CustomViewPager customViewPager, final int size) {
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
        int app_cond = bundle.getInt(SplashScreenActivity.APP_CONDITION_KEY);
        int db_cond = bundle.getInt(SplashScreenActivity.DB_CONDITION_KEY);
        String messageIfNeeded = null;
        String dbErrorMesageIfNeeded = null;
        switch (app_cond) {
            case SplashScreenActivity.APP_IS_FIRST_USAGE:
            case SplashScreenActivity.APP_IS_NEWER_VERSION:
                break;
            case SplashScreenActivity.APP_IS_OLDER_VERSION:
                messageIfNeeded = "Aplikasi ini sudah usang dan tidak kompatibel dengan versi sebelumnya yang lebih baru, coba copot dan pasang lagi aplikasi ini";
            case SplashScreenActivity.APP_IS_SAME_VERSION:
                break;
        }
        switch (db_cond) {
            case SplashScreenActivity.DB_IS_FIRST_USAGE:
                break;
            case SplashScreenActivity.DB_IS_NEWER_VERSION:
                TOAST(Toast.LENGTH_SHORT, "The data now is new version !");
                break;
            case SplashScreenActivity.DB_IS_OLDER_IN_APP_VERSION:
                dbErrorMesageIfNeeded = "Current Database dengan database di aplikasi sudah usang, mohon copot dan pasang aplikasi untuk membenahi";
            case SplashScreenActivity.DB_IS_SAME_VERSION:
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
            LOGE("MainActivity", "ERROR WHEN HANDLING checkAndMoveDB()", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (has_finished == 1)
            setHandlers(mImageSelector, mImageMemCache.size());
    }

    @Override
    protected void onPause() {
        clearHandlers(mImageSwitcher);
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
            case R.id.nav_about:
                SwitchIntoMainActivity.switchTo(this, AboutActivity.class, null);
                break;
            case R.id.nav_out:
                this.finish();
                break;
            case R.id.nav_rate:
                break;
            case R.id.nav_share:
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
    }

    private void onButtonPetaniClicked() {
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
            }, 2000);
        }
        System.gc();
    }

    private void setInitialSectPetani() {
        mTextPetaniDesc = (Button) findViewById(R.id.actmain_id_section_petani_btn);
        imgPetaniKedipView = findViewById(R.id.actsplash_id_gifpetanikedip);
        mTextPetaniDesc.setTextColor(Color.BLACK);
        mTextPetaniDesc.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.NORMAL);
        mTextPetaniDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                onButtonPetaniClicked();
            }
        });
        mTextPetaniDesc.setText(TextPetaniDesc[mPosTxtPetani]);
        imgPetaniKedipView.setVisibility(View.VISIBLE);
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
                showExitDialog();
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
        if (mImageMemCache != null) {
            for (int x = 0; x < mImageMemCache.size(); x++) {
                mImageMemCache.get(x).recycle();
            }
            mImageMemCache.evictAll();
        }
        super.onDestroy();
    }

    private void showExitDialog() {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        LinearLayout layoutDialog = (LinearLayout) LinearLayout.inflate(this, R.layout.actmain_dialog_on_exit, null);
        TextView text = (TextView) layoutDialog.findViewById(R.id.actmain_id_dialogexit_content);
        Button btnyes = (Button) layoutDialog.findViewById(R.id.actmain_id_dialogexit_btnyes);
        Button btnno = (Button) layoutDialog.findViewById(R.id.actmain_id_dialogexit_btnno);
        text.setTextColor(Color.BLACK);
        text.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.BOLD);
        text.setText(R.string.actmain_string_dialogexit_desc);
        btnyes.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.BOLD);
        btnyes.setTextColor(Color.WHITE);
        btnyes.setText(R.string.actmain_string_dialogexit_btnyes);
        btnno.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.BOLD);
        btnno.setTextColor(Color.WHITE);
        btnno.setText(R.string.actmain_string_dialogexit_btnno);
        build.setView(layoutDialog);
        final AlertDialog mAlert = build.create();
        btnyes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                mAlert.cancel();
                MainActivity.this.finish();
            }


        });
        btnno.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                mAlert.cancel();
            }


        });
        mAlert.show();
    }

    private static class PrepareBitmapViewPager implements Runnable {
        LruCache<Integer, Bitmap> memCache;
        private final File sourceCache;
        private ImageFragmentAdapter mImageControllerFragment;
        private WeakReference<CustomViewPager> mImageSelector;
        private WeakReference<Point> reqSize;
        private static volatile SimpleDiskLruCache diskLruCache;
        private WeakReference<MainActivity> mainActivity;
        //private int mDotCount;
        //private LinearLayout[] mDots;
        private WeakReference<LinearLayout> indicators;

        PrepareBitmapViewPager(MainActivity mainActivity, Point reqSize, File cacheDirs) {
            this.mainActivity = new WeakReference<MainActivity>(mainActivity);
            this.memCache = mainActivity.mImageMemCache;
            this.sourceCache = cacheDirs;
            this.mImageSelector = new WeakReference<CustomViewPager>(mainActivity.mImageSelector);
            this.reqSize = new WeakReference<Point>(reqSize);
            this.indicators = new WeakReference<LinearLayout>(mainActivity.indicators);


            sourceCache.mkdir();
        }

        @Override
        public void run() {
            //add your items here
            String[] key = {
                    "viewpager_area_1",
                    "viewpager_area_2",
                    "viewpager_area_3",
                    "viewpager_area_4"
            };
            //////////
            // loads from a cache
            try {
                loadBitmapIntoCache(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final int current = mainActivity.get().getSharedPreferences(SplashScreenActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE).getInt(SplashScreenActivity.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER, 0);
            final int current1 = (current == memCache.size()) ? 0 : current;
            postExecute(memCache.get(current1));
        }

        private void loadBitmapIntoCache(String[] key) throws IOException {
            try {
                synchronized (this) {
                    //File source = sourceCache;
                    Log.i("checkeraaa", "the curr cache dirs is " + sourceCache.getAbsolutePath());
                    diskLruCache = SimpleDiskLruCache.getsInstance(sourceCache);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            int x = 0;

            synchronized (diskLruCache) {
                for (String result : key) {
                    InputStream bis = diskLruCache.get(result);
                    memCache.put(x++, BitmapFactory.decodeStream(bis));
                    diskLruCache.closeReading();
                    bis.close();

                }
            }
            System.gc();

            synchronized (diskLruCache) {
                diskLruCache.close();
            }
        }

        private void postExecute(final Bitmap bitmapResult) {

            // sets the image for nav header
            final ImageView img = (ImageView) mainActivity.get().findViewById(R.id.actmain_id_navheadermain_layoutimg);
            if (img != null)
                synchronized (img) {
                    if (bitmapResult != null) {
                        img.setImageBitmap(bitmapResult);
                    }
                }
            mImageControllerFragment = new ImageFragmentAdapter(mainActivity.get(), mainActivity.get().getSupportFragmentManager(), memCache, reqSize.get());
            mImageSelector.get().setAdapter(mImageControllerFragment);
            mImageSelector.get().setCurrentItem(0);
            mImageSelector.get().setPageTransformer(true, new CustomPageViewTransformer());
            mImageSelector.get().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curr_img = mImageSelector.get().getCurrentItem();
                    if (++curr_img == memCache.size())
                        curr_img = 0;
                    mImageSelector.get().setCurrentItem(curr_img);

                    System.gc();
                    mImageSelector.get().setPageTransformer(true, new CustomPageViewTransformer());
                    System.gc();
                }
            });
            final int mDotCount = mImageControllerFragment.getCount();
            final LinearLayout[] mDots = new LinearLayout[mDotCount];
            for (int x = 0; x < mDotCount; x++) {
                mDots[x] = new LinearLayout(mainActivity.get());
                mDots[x].setBackgroundResource(R.drawable.indicator_unselected_item_oval);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 4, 4);
                mDots[x].setGravity(Gravity.RIGHT | Gravity.BOTTOM | Gravity.END);
                indicators.get().addView(mDots[x], params);

            }
            mDots[0].setBackgroundResource(R.drawable.indicator_selected_item_oval);
            mImageSelector.get().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    for (int x = 0; x < mDotCount; x++) {
                        mDots[x].setBackgroundResource(R.drawable.indicator_unselected_item_oval);
                    }
                    mainActivity.get().curr_pos_image = i;
                    mDots[i].setBackgroundResource(R.drawable.indicator_selected_item_oval);
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                    int pos = mImageSelector.get().getCurrentItem();
                    // if reaching last and state is DRAGGING, back into first
                    if (pos == memCache.size() - 1 && i == ViewPager.SCROLL_STATE_DRAGGING)
                        mImageSelector.get().setCurrentItem(0, true);
                }
            });
            System.gc();
            mainActivity.get().has_finished = 1;
            mainActivity.get().setHandlers(mainActivity.get().mImageSelector, memCache.size());
        }
    }
}
