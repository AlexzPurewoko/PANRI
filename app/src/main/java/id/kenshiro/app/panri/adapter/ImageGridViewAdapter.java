package id.kenshiro.app.panri.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mylexz.utils.DiskLruObjectCache;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SimpleDiskLruCache;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import id.kenshiro.app.panri.MainActivity;
import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.SplashScreenActivity;
import id.kenshiro.app.panri.helper.DecodeBitmapHelper;

public class ImageGridViewAdapter implements Closeable{
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
    private LruCache<Integer, Bitmap> mImagecache = null;

    private int marginTop = 20;
    private int marginBottom = 20;
    private int marginLeft = 15;
    private int marginRight = 15;
    private int marginCenter = 15;
    private int mode = 0;
    private int finished_mode = 0;
    private String idSuffix = null;

    public ImageGridViewAdapter(MylexzActivity ctx, Point screenSize, @IdRes int resRootLayout){
        this.ctx = ctx;
        this.screenSize = screenSize;
        this.resRootLayout = resRootLayout;
        imageItemSize = new Point(0,0);
    }

    public void setColumnCount(int columnCount){
        this.columnCount = columnCount;
    }
    public void setMargin(@Px int marginTop, @Px int marginBottom, @Px int marginLeft, @Px int marginRight, @Px int marginCenter){
        this.marginBottom = marginBottom;
        this.marginTop = marginTop;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginCenter = marginCenter;
    }
    private void recycleBitmaps(){
        if(mImagecache != null) {
            for (int x = 0; x < mImagecache.size(); x++) {
                mImagecache.get(x).recycle();
            }
            mImagecache.evictAll();
        }
        mImagecache = null;
    }

    @Override
    public void close() {
        recycleBitmaps();
    }

    public void setListLocationAssetsImages(List<String> listLocationAssetsImages, String idSuffix) {
        this.listLocationAssetsImages = listLocationAssetsImages;
        this.idSuffix = idSuffix;
        mode = 1;
    }

    public void setListLocationResImages(List<Integer> listLocationResImages, String idSuffix) {
        this.listLocationResImages = listLocationResImages;
        this.idSuffix = idSuffix;
        mode = 0;
    }

    public void setImagePerItemHeight(int newHeight){
        imageItemSize.y = newHeight;
    }
    public void buildAndShow(){
        if(finished_mode != 0) return;
        try {
            finished_mode = 1;
            PrepareBitmapTask prepareBitmapTask = new PrepareBitmapTask(this);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(prepareBitmapTask, 50);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //new TaskLoadingBitmap(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private int getRoundedUp(float num){
        return (num / Math.round(num) == 0)?Math.round(num):Math.round(num)+1;
    }
    private void buildRootLayout() {
        rootElement = (LinearLayout) ctx.findViewById(resRootLayout);
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
            if(x+1 == rowCount)
                params.setMargins(0, marginTop, 0, marginBottom);
            else
                params.setMargins(0, marginTop, 0, 0);
            element.setLayoutParams(params);
            element.setGravity(Gravity.CENTER);
            // build in column
            for(int y = 0; y < columnCount; y++){
                ImageView img = new ImageView(ctx);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        imageItemSize.x,
                        imageItemSize.y
                );
                if(columnCount > 1 && y+1 == columnCount) {
                    params1.rightMargin = marginRight;
                    params1.leftMargin = 0;
                }
                else if (columnCount > 1 && y < columnCount){
                    if(y == 0)
                        params1.leftMargin = marginLeft;
                    else
                        params1.leftMargin = 0;
                    params1.rightMargin = marginCenter;
                }
                else {
                    params1.rightMargin = marginRight;
                    params1.leftMargin = marginLeft;
                }
                img.setLayoutParams(params1);
                img.setImageBitmap(mImagecache.get(items));
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

    private static class PrepareBitmapTask implements Runnable {
        private volatile SimpleDiskLruCache diskLruObjectCache;
        private WeakReference<ImageGridViewAdapter> ctxCls;
        private static final int QUALITY_FACTOR = 30;
        private static final long MAX_CACHE_BUFFERED_SIZE = 1048576;


        public PrepareBitmapTask(ImageGridViewAdapter ctxCls) throws IOException {
            this.ctxCls = new WeakReference<>(ctxCls);
            File fileCache = new File(ctxCls.ctx.getCacheDir(),"cache");
            diskLruObjectCache = SimpleDiskLruCache.getsInstance(fileCache);
        }
        @Override
        public void run() {
            synchronized (this){
                ctxCls.get().recycleBitmaps();
            }
            checkAndLoadAllBitmaps();
            if (ctxCls.get().rootElement != null) {
                ctxCls.get().rootElement.removeViewsInLayout(0, ctxCls.get().rootElement.getChildCount());
            }
            try {
                diskLruObjectCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            postExecute(null);
        }
        private void checkAndLoadAllBitmaps() throws IllegalStateException{
            settingSize();
            if(ctxCls.get().idSuffix == null || ctxCls.get().idSuffix.equals(""))throw new IllegalStateException("The argument idSuffix in setListLocationAssetsImages() or setListLocationResImages() is null or empty string.");
            ctxCls.get().mImagecache = new LruCache<Integer, Bitmap>(ctxCls.get().imageItemSize.x * ctxCls.get().imageItemSize.y);
            switch (ctxCls.get().mode){
                case 0:
                    checkAndLoadAllBitmapsFromRes();
                    break;
                case 1:
                    try {
                        checkAndLoadAllBitmapsFromAssets();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

        }
        private void settingSize(){
            // set size per images
            int screenHeight = ctxCls.get().screenSize.y;
            int screenWidth  = ctxCls.get().screenSize.x;
            ///// section width
            //int imageWidth = screenWidth / ctxCls.get().columnCount - (ctxCls.get().columnCount * ctxCls.get().marginLeft + ctxCls.get().columnCount * ctxCls.get().marginRight);
            int imageWidth = screenWidth / ctxCls.get().columnCount - (ctxCls.get().marginLeft + ctxCls.get().marginRight / ctxCls.get().columnCount);
            ctxCls.get().imageItemSize.x = imageWidth;
            ///// section height
            if(ctxCls.get().imageItemSize.y == 0){
                int imageHeight = imageWidth;
                ctxCls.get().imageItemSize.y = imageHeight;
            }
        }
        private void checkAndLoadAllBitmapsFromAssets() throws IOException {
            for(int x = 0; x < ctxCls.get().listLocationAssetsImages.size(); x++){
                String name = ctxCls.get().listLocationAssetsImages.get(x);
                String nameID = getLasts(name) + ctxCls.get().idSuffix;
                if(!diskLruObjectCache.isKeyExists(nameID)){
                    final Bitmap bitmap = DecodeBitmapHelper.decodeAndResizeBitmapsAssets(ctxCls.get().ctx.getAssets(), name, ctxCls.get().imageItemSize.y, ctxCls.get().imageItemSize.x);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, ctxCls.get().imageItemSize.x, ctxCls.get().imageItemSize.y, false);
                    //gets the byte of bitmap
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    float scaling = bitmap.getHeight() / ctxCls.get().imageItemSize.y;
                    scaling = ((scaling < 1.0f) ? 1.0f : scaling);
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, Math.round(QUALITY_FACTOR / scaling), bos);
                    // put into cache
                    try {
                        diskLruObjectCache.put(nameID, bos.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ctxCls.get().mImagecache.put(x, scaledBitmap);
                    bitmap.recycle();
                    System.gc();
                }
                else{
                    InputStream is = null;
                    try {
                        is = diskLruObjectCache.get(nameID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(is == null){
                        diskLruObjectCache.closeReading();
                        continue;
                    }
                    ctxCls.get().mImagecache.put(x, BitmapFactory.decodeStream(is));
                    diskLruObjectCache.closeReading();
                }
            }
        }
        private void checkAndLoadAllBitmapsFromRes() {
            for(int x = 0; x < ctxCls.get().listLocationResImages.size(); x++){
                int resId = ctxCls.get().listLocationResImages.get(x);
                String name = ctxCls.get().ctx.getResources().getResourceName(resId) + ctxCls.get().idSuffix;
                if(!diskLruObjectCache.isKeyExists(name)){
                    final Bitmap bitmap = DecodeBitmapHelper.decodeAndResizeBitmapsResources(ctxCls.get().ctx.getResources(), resId, ctxCls.get().imageItemSize.y, ctxCls.get().imageItemSize.x);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, ctxCls.get().imageItemSize.x, ctxCls.get().imageItemSize.y, false);
                    //gets the byte of bitmap
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    float scaling = bitmap.getHeight() / ctxCls.get().imageItemSize.y;
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, Math.round(QUALITY_FACTOR / scaling), bos);
                    // put into cache
                    try {
                        diskLruObjectCache.put(name, bos.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ctxCls.get().mImagecache.put(x, scaledBitmap);
                    bitmap.recycle();
                    scaledBitmap.recycle();
                    System.gc();
                }
                else{
                    InputStream is = null;
                    try {
                        is = diskLruObjectCache.get(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(is == null){
                        try {
                            diskLruObjectCache.closeReading();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    ctxCls.get().mImagecache.put(x, BitmapFactory.decodeStream(is));
                }
            }
        }
        private String getLasts(String name) {
            StringBuffer results = new StringBuffer();
            for(int x = name.length() - 1; x >= 0; x--){
                char s = name.charAt(x);
                if(s == '.')continue;
                else if(s == '/')break;
                else results.append(s);
            }
            results.reverse();
            return results.toString().toLowerCase();
        }
        private void postExecute(Void aVoid) {
            ctxCls.get().buildRootLayout();
            ctxCls.get().buildContentLayout();
            ctxCls.get().finished_mode = 0;
        }
    }
}
