package id.kenshiro.app.panri.adapter;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class ImageAssetsFragmentAdapter extends FragmentStatePagerAdapter {
    private Fragment[] mListImageFragment;
    private List<String> data;
    private Context mCurrentContext;
    private Point requestedSize;

    public ImageAssetsFragmentAdapter(Context mCurrentContext, FragmentManager fm, List<String> data, Point requestedSize) {
        super(fm);
        this.mCurrentContext = mCurrentContext;
        this.data = data;
        mListImageFragment = new Fragment[data.size()];
        this.requestedSize = requestedSize;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        String items = data.get(i);

        ViewImageSelectorAdapter vImgSelectorAdapter = new ViewImageSelectorAdapter();
        vImgSelectorAdapter.setMode(1);// mode assets
        vImgSelectorAdapter.setAssetsImgLocation(items);
        vImgSelectorAdapter.setRequestedImageSize(requestedSize);
        fragment = vImgSelectorAdapter;

        if (mListImageFragment[i] == null)
            mListImageFragment[i] = fragment;
        return mListImageFragment[i];
    }

    @Override
    public int getCount() {
        if (data != null)
            return data.size();
        return 0;
    }
}