package id.kenshiro.app.panri.adapter;


import android.graphics.Bitmap;
import android.graphics.Point;
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
import id.kenshiro.app.panri.helper.DecodeBitmapHelper;

public class ViewImageSelectorAdapter extends Fragment {
    private ImageView mImageContainer;
    private int resImageLocation;
    private String assetsImgLocation;
    private int mode = 0;
    Point requestedImageSize;
    private Bitmap bitmapLocation;

    public ViewImageSelectorAdapter() {
        super();
        setDefaultRequestedSize();
    }

    private void setDefaultRequestedSize() {
        requestedImageSize = new Point();
        requestedImageSize.y = requestedImageSize.x = 100;
    }

    public void setRequestedImageSize(Point requestedImageSize) {
        this.requestedImageSize = requestedImageSize;
    }

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
            mImageContainer.setImageBitmap(DecodeBitmapHelper.decodeAndResizeBitmapsResources(getResources(), resImageLocation, requestedImageSize.y, requestedImageSize.x));
            //mImageContainer.setImageResource(resImageLocation);
        else if (mode == 1) {
            //InputStream is = null;
            try {
                /*is = getContext().getAssets().open(assetsImgLocation);
                mImageContainer.setImageDrawable(Drawable.createFromStream(is, null));
                mImageContainer.setScaleType(ImageView.ScaleType.FIT_XY);
                is.close();*/
                mImageContainer.setImageBitmap(DecodeBitmapHelper.decodeAndResizeBitmapsAssets(getActivity().getAssets(), assetsImgLocation, requestedImageSize.y, requestedImageSize.x));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(mode == 2){
            mImageContainer.setImageBitmap(bitmapLocation);
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

    public void setBitmapLocation(Bitmap bitmapLocation) {
        this.bitmapLocation = bitmapLocation;
    }

    public int getResImageLocation(){
        return resImageLocation;
    }
    public ImageView getImageContainer(){
        return mImageContainer;
    }
}
