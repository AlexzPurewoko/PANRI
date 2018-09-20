package id.kenshiro.app.panri.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v4.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mylexz.utils.DiskLruObjectCache;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SimpleDiskLruCache;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.adapter.AdapterRecycler;

public class TampilListPenyakitHelper implements Closeable{
    MylexzActivity activity;
    SQLiteDatabase sqlDB;
    RelativeLayout rootView;
    AdapterRecycler.OnItemClickListener onItemClickListener;
    List<DataPenyakit> dataPenyakitList;
    ScrollView mContentView;
    LinearLayout childView;
    private LruCache<Integer, Bitmap> mImagecache = null;
    private int finished_mode = 0;

    public TampilListPenyakitHelper(MylexzActivity activity, SQLiteDatabase sqlDB, RelativeLayout rootView) {
        this.activity = activity;
        this.sqlDB = sqlDB;
        this.rootView = rootView;
    }

    public void buildAndShow(){
        setContentViewer();
        if(finished_mode != 0) return;
        finished_mode = 1;
        new BuilderTask(this).execute();//OnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private void setContentViewer() {
        mContentView = (ScrollView) activity.getLayoutInflater().inflate(R.layout.adapter_listpenyakitnamacontentview, rootView, false);
        mContentView.setVisibility(View.VISIBLE);
        rootView.addView(mContentView);
        childView = (LinearLayout) mContentView.getChildAt(0);
    }

    private void inflateListAndAddTouchable() throws IOException {
        int size = dataPenyakitList.size();
        for (int x = 0; x < size; x++) {
            CardView mContent = (CardView) activity.getLayoutInflater().inflate(R.layout.adapter_namapenyakit, null);
            ImageView mImg = mContent.findViewById(R.id.adapter_id_imgnamapenyakit);
            TextView mText = mContent.findViewById(R.id.adapter_id_namapenyakit);
            DataPenyakit data = dataPenyakitList.get(x);

            mImg.setImageBitmap(mImagecache.get(x));
            // apply the name of penyakit
            mText.setText(data.getNama_penyakit());

            // sets the item touchable
            final int y = x;
            mContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.onClick(mContentView, y);
                }
            });

            // applying into content section
            childView.addView(mContent);
        }
        System.gc();
    }

    public boolean onBackButtonPressed() {
        if (mContentView == null) return false;
        if (mContentView.getVisibility() == View.GONE) {
            mContentView.setVisibility(View.VISIBLE);
            return true;
        } else
            return false;
    }

    private void getDataPenyakitFromDB() {
        dataPenyakitList = new ArrayList<DataPenyakit>();
        List<Integer> countImg = new ArrayList<>();
        // gets the countImg first!!
        Cursor cursor = sqlDB.rawQuery("select count_img from gambar_penyakit", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            countImg.add(cursor.getInt(0));
            cursor.moveToNext();
        }
        cursor.close();
        System.gc();
        // gets the img
        int counter = 0;
        cursor = sqlDB.rawQuery("select path_gambar from gambar_penyakit", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] buf = cursor.getString(0).split(",");
            dataPenyakitList.add(new DataPenyakit(buf, countImg.get(counter++), null));
            cursor.moveToNext();
        }
        cursor.close();
        System.gc();
        // gets the nama penyakit
        counter = 0;
        cursor = sqlDB.rawQuery("select nama from penyakit", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            dataPenyakitList.get(counter++).setNama_penyakit(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        System.gc();
    }

    public ScrollView getmContentView() {
        return mContentView;
    }

    public void setOnItemClickListener(AdapterRecycler.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void close() throws IOException {
        recycleBitmaps();
    }
    private static class BuilderTask extends AsyncTask<Void, Void, Void>{
        SimpleDiskLruCache diskLruObjectCache;
        private static final int QUALITY_FACTOR = 10;
        File fileCache;
        private static final long MAX_CACHE_BUFFERED_SIZE = 1048576;
        TampilListPenyakitHelper tampilListPenyakitHelper;
        BuilderTask(TampilListPenyakitHelper tampilListPenyakitHelper){
            this.tampilListPenyakitHelper = tampilListPenyakitHelper;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fileCache = new File(tampilListPenyakitHelper.activity.getCacheDir(),"cache");
            fileCache.mkdir();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            synchronized (this){
            try {
                diskLruObjectCache = new SimpleDiskLruCache(fileCache);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            tampilListPenyakitHelper.getDataPenyakitFromDB();
            try {
                checkAndLoadAllBitmaps();
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (diskLruObjectCache){
                try {
                    diskLruObjectCache.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private void checkAndLoadAllBitmaps() throws IOException {
            int size_images = Math.round(tampilListPenyakitHelper.activity.getResources().getDimension(R.dimen.actmain_dimen_opimg_incard_wh));
            tampilListPenyakitHelper.mImagecache = new LruCache<Integer, Bitmap>(size_images * 2);
            for(int x = 0; x < tampilListPenyakitHelper.dataPenyakitList.size(); x++){
                String name = tampilListPenyakitHelper.dataPenyakitList.get(x).path_image;
                String nameID = getLasts(name);
                if(!diskLruObjectCache.isKeyExists(nameID)){
                    final Bitmap bitmap = DecodeBitmapHelper.decodeAndResizeBitmapsAssets(tampilListPenyakitHelper.activity.getAssets(), name, size_images, size_images);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, size_images, size_images, false);
                    //gets the byte of bitmap
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    float scaling = bitmap.getHeight() / size_images;
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
                    tampilListPenyakitHelper.mImagecache.put(x, scaledBitmap);
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
                    tampilListPenyakitHelper.mImagecache.put(x, BitmapFactory.decodeStream(is));
                    diskLruObjectCache.closeReading();
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                tampilListPenyakitHelper.inflateListAndAddTouchable();
            } catch (IOException e) {
                e.printStackTrace();
            }
            tampilListPenyakitHelper.finished_mode = 0;
        }
    }
    private class DataPenyakit {
        String path_image;
        String nama_penyakit;
        private final String path = "data_hama/foto";

        public DataPenyakit(String[] img, int countImg, String nama_penyakit) {
            this.nama_penyakit = nama_penyakit;
            setPath_image(img, countImg);
        }

        public void setPath_image(String[] img, int countImg) {
            if (countImg > 0) {
                Random random = new Random();
                int selectedImg = random.nextInt(countImg);
                this.path_image = path + "/" + img[selectedImg] + ".jpg";
            } else
                this.path_image = null;
        }

        public void setNama_penyakit(String nama_penyakit) {
            this.nama_penyakit = nama_penyakit;
        }

        public String getNama_penyakit() {
            return nama_penyakit;
        }

        public String getPath_image() {
            return path_image;
        }
    }
}
