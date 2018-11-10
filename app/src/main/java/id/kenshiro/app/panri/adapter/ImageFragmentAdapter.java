package id.kenshiro.app.panri.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.LruCache;

import com.mylexz.utils.MylexzActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImageFragmentAdapter extends FragmentStatePagerAdapter {
    private static final int LIST_CACHE_BITMAP = 0xef;
    private static final int LIST_DATA_INTEGER_RES = 0xfe;
    private Fragment[] mListImageFragment;
    private List<Integer> data;
    private Context mCurrentContext;
    private Point requestedSize;
    private LruCache<Integer, Bitmap> bitmapLruCache;
    private int mode_operations = 0;

    public ImageFragmentAdapter(@NotNull Context mCurrentContext, @NotNull FragmentManager fm, @NotNull List<Integer> data, @NotNull Point requestedSize) {
        super(fm);
        this.mCurrentContext = mCurrentContext;
        this.data = data;
        mListImageFragment = new Fragment[data.size()];
        this.requestedSize = requestedSize;
        this.mode_operations = LIST_DATA_INTEGER_RES;
    }

    public ImageFragmentAdapter(@NotNull Context mCurrentContext, @NotNull FragmentManager fm, @NotNull LruCache<Integer, Bitmap> bitmapLruCache, @NotNull Point requestedSize) {
        super(fm);
        this.mCurrentContext = mCurrentContext;
        this.bitmapLruCache = bitmapLruCache;
        mListImageFragment = new Fragment[bitmapLruCache.size()];
        this.requestedSize = requestedSize;
        this.mode_operations = LIST_CACHE_BITMAP;
    }


    @Override
    public Fragment getItem(int i) {
        if(mode_operations == LIST_DATA_INTEGER_RES) {
            Fragment fragment;
            int items = data.get(i);

            ViewImageSelectorAdapter vImgSelectorAdapter = new ViewImageSelectorAdapter();
            vImgSelectorAdapter.setResImageLocation(items);
            vImgSelectorAdapter.setRequestedImageSize(requestedSize);
            fragment = vImgSelectorAdapter;

            if (mListImageFragment[i] == null)
                mListImageFragment[i] = fragment;
        }
        else if(mode_operations == LIST_CACHE_BITMAP){
            Fragment fragment;
            Bitmap items = bitmapLruCache.get(i);
            ViewImageSelectorAdapter vImgSelectorAdapter = new ViewImageSelectorAdapter();
            vImgSelectorAdapter.setMode(2);
            vImgSelectorAdapter.setBitmapLocation(items);
            vImgSelectorAdapter.setRequestedImageSize(requestedSize);
            fragment = vImgSelectorAdapter;

            if (mListImageFragment[i] == null)
                mListImageFragment[i] = fragment;
        }
        return mListImageFragment[i];
    }

    @Override
    public int getCount() {
        if(mode_operations == LIST_DATA_INTEGER_RES) {
            if(data != null)
                return data.size();
        }
        else if(mode_operations == LIST_CACHE_BITMAP){
            if(this.bitmapLruCache != null)
                return bitmapLruCache.size();
        }

        return 0;
    }
}
