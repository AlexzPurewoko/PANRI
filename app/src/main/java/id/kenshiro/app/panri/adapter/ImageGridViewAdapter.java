package id.kenshiro.app.panri.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mylexz.utils.MylexzActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import id.kenshiro.app.panri.MainActivity;
import id.kenshiro.app.panri.R;

public class ImageGridViewAdapter {
    private List<Integer> listLocationResImages;
    private List<String> listLocationAssetsImages;
    private List<LinearLayout> listRow;
    private List<ImageView> imageViewList;
    private Point screenSize;
    private Point imageItemSize;
    private int resRootLayout;
    private MylexzActivity ctx;
    public int columnCount = 2;
    private int rowCount;
    private LinearLayout rootElement;
    OnItemClickListener onItemClickListener;

    private int marginTop = 20;
    private int marginBottom = 20;
    private int marginLeft = 15;
    private int marginRight = 15;
    private int mode = 0;

    public ImageGridViewAdapter(MylexzActivity ctx, List<Integer> listLocationResImages, Point screenSize, @IdRes int resRootLayout){
        this.ctx = ctx;
        this.listLocationResImages = listLocationResImages;
        this.screenSize = screenSize;
        this.resRootLayout = resRootLayout;
        imageItemSize = new Point(0,0);
    }

    public ImageGridViewAdapter(MylexzActivity ctx, List<String> listLocationAssetsImages, Point screenSize, @IdRes int resRootLayout, @Nullable String flags) {
        this.ctx = ctx;
        mode = 1;
        this.listLocationAssetsImages = listLocationAssetsImages;
        this.screenSize = screenSize;
        this.resRootLayout = resRootLayout;
        imageItemSize = new Point(0, 0);
    }
    public void setColumnCount(int columnCount){
        this.columnCount = columnCount;
    }
    public void setMargin(@Px int marginTop, @Px int marginBottom, @Px int marginLeft, @Px int marginRight){
        this.marginBottom = marginBottom;
        this.marginTop = marginTop;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
    }
    public void setImagePerItemHeight(int newHeight){
        imageItemSize.y = newHeight;
    }
    public void buildAndShow(){
        if (rootElement != null) {
            rootElement.removeViewsInLayout(0, rootElement.getChildCount());
        }
        buildRootLayout();
        buildContentLayout();
    }
    private int getRoundedUp(float num){
        return (num / Math.round(num) == 0)?Math.round(num):Math.round(num)+1;
    }
    private void buildRootLayout() {
        rootElement = (LinearLayout) ctx.findViewById(resRootLayout);
        // set size per images
        int screenHeight = screenSize.y;
        int screenWidth  = screenSize.x;
        ///// section width
        int imageWidth = screenWidth / columnCount - (columnCount * marginLeft + columnCount * marginRight);
        imageItemSize.x = imageWidth;
        ///// section height
        if(imageItemSize.y == 0){
            int imageHeight = imageWidth;
            imageItemSize.y = imageHeight;
        }
        // gets the row count
        rowCount = Math.round(getItemCount()/columnCount);
    }
    private void buildContentLayout(){
        int items = 0;
        for(int x = 0; x < rowCount; x++){
            LinearLayout element = (LinearLayout) ctx.getLayoutInflater().inflate(R.layout.adapter_imagegridview, rootElement, false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, marginTop, 0, marginBottom);
            element.setLayoutParams(params);
            element.setGravity(Gravity.CENTER);
            for(int y = 0; y < columnCount; y++){
                ImageView img = new ImageView(ctx);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        imageItemSize.x,
                        imageItemSize.y
                );
                params1.rightMargin = marginRight;
                params1.leftMargin = marginLeft;
                img.setLayoutParams(params1);
                if (mode == 1) {
                    try {
                        InputStream is = ctx.getAssets().open(listLocationAssetsImages.get(items));
                        img.setImageDrawable(Drawable.createFromStream(is, null));
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    img.setImageResource(listLocationResImages.get(items));
                img.setMaxHeight(imageItemSize.y);
                img.setMaxWidth(imageItemSize.x);
                img.setScaleType(ImageView.ScaleType.FIT_XY);
                img.setAdjustViewBounds(false);
                final int items1 = items;
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null)
                            onItemClickListener.onItemClick(v, items1);
                    }
                });
                element.addView(img);
                items++;
            }
            rootElement.addView(element);
        }
        System.gc();
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public int getItemCount(){
        if (mode == 0)
            return listLocationResImages.size();
        else
            return listLocationAssetsImages.size();
    }
    public interface OnItemClickListener {
        public void onItemClick(View v, int position);
    }
}
