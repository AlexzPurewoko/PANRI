package id.kenshiro.app.panri.opt.onmain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mylexz.utils.SimpleDiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import id.kenshiro.app.panri.MainActivity;
import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.adapter.CustomPageViewTransformer;
import id.kenshiro.app.panri.adapter.CustomViewPager;
import id.kenshiro.app.panri.adapter.ImageFragmentAdapter;
import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;

public class PrepareBitmapViewPager implements Runnable {
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

    public PrepareBitmapViewPager(MainActivity mainActivity, Point reqSize, File cacheDirs) {
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
            String keyEx = "run_PrepareBitmapViewPager";
            String resE = String.format("Cannot execute loadBitmapIntoCache(key); e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            mainActivity.get().LOGE(keyEx, resE);
        }
        final int current = mainActivity.get().getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE).getInt(KeyListClasses.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER, 0);
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
            String keyEx = "loadBitmapIntoCache_PrepareBitmapViewPager";
            String resE = String.format("Cannot execute diskLruCache = SimpleDiskLruCache.getsInstance(sourceCache); e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            mainActivity.get().LOGE(keyEx, resE);
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
        final ImageView img = mainActivity.get().findViewById(R.id.actmain_id_navheadermain_layoutimg);
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
        mainActivity.get().setAutoClickUpdate();
    }
}
