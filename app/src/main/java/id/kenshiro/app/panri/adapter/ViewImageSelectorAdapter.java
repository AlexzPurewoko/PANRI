package id.kenshiro.app.panri.adapter;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import id.kenshiro.app.panri.R;
public class ViewImageSelectorAdapter extends Fragment {
    private ImageView mImageContainer;
    private int resImageLocation;
    private String assetsImgLocation;
    private int mode = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.actmain_fragment_viewpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageContainer = (ImageView) view.findViewById(R.id.actmain_id_fragmentimgselector);
        if (mode == 0)
            mImageContainer.setImageResource(resImageLocation);
        else if (mode == 1) {
            InputStream is = null;
            try {
                is = getContext().getAssets().open(assetsImgLocation);
                mImageContainer.setImageDrawable(Drawable.createFromStream(is, null));
                mImageContainer.setScaleType(ImageView.ScaleType.FIT_XY);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setAssetsImgLocation(String assetsImgLocation) {
        this.assetsImgLocation = assetsImgLocation;
    }

    public void setResImageLocation(@IdRes int resId){
        resImageLocation = resId;
    }
    public int getResImageLocation(){
        return resImageLocation;
    }
    public ImageView getImageContainer(){
        return mImageContainer;
    }
}
