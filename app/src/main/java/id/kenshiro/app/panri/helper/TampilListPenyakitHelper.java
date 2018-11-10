package id.kenshiro.app.panri.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SimpleDiskLruCache;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.adapter.AdapterRecycler;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;

public class TampilListPenyakitHelper implements Closeable{
    MylexzActivity activity;
    SQLiteDatabase sqlDB;
    RelativeLayout rootView;
    AdapterRecycler.OnItemClickListener onItemClickListener;
    List<DataPenyakit> dataPenyakitList;
    ScrollView mContentView;
    LinearLayout childView;
    private final DialogShowHelper dialogShowHelper;
    private volatile LruCache<Integer, Bitmap> mImagecache = null;
    private int finished_mode = 0;

    public TampilListPenyakitHelper(MylexzActivity activity, SQLiteDatabase sqlDB, RelativeLayout rootView) {
        this.activity = activity;
        this.sqlDB = sqlDB;
        this.rootView = rootView;
        dialogShowHelper = new DialogShowHelper(activity);
        dialogShowHelper.buildLoadingLayout();
    }

    public synchronized void buildAndShow(){
        dialogShowHelper.showDialog();
        setContentViewer();
        if(finished_mode != 0) return;
        finished_mode = 1;
        try {
            int size_images = Math.round(activity.getResources().getDimension(R.dimen.actmain_dimen_opimg_incard_wh));
            mImagecache = new LruCache<Integer, Bitmap>(size_images * 12);
            PrepareTask prepareTask = new PrepareTask(this);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(prepareTask, 50);
        } catch (IOException e) {
            String keyEx = "buildAndShow_TampilListPenyakitHelper";
            String resE = String.format("Error execute the whole of methods e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            activity.LOGE(keyEx, resE);
        }
    }
    private synchronized void recycleBitmaps(){
        if(mImagecache != null) {
            for (int x = 0; x < mImagecache.size(); x++) {
                mImagecache.get(x).recycle();
            }
            mImagecache.evictAll();
        }
        mImagecache = null;
        System.gc();
    }

    private void setContentViewer() {
        mContentView = (ScrollView) activity.getLayoutInflater().inflate(R.layout.adapter_listpenyakitnamacontentview, rootView, false);
        mContentView.setVisibility(View.VISIBLE);
        rootView.addView(mContentView);
        childView = (LinearLayout) mContentView.getChildAt(0);
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

    private static class PrepareTask implements Runnable {
        volatile SimpleDiskLruCache diskLruObjectCache;
        final File fileCache;
        private WeakReference<TampilListPenyakitHelper> tampilListPenyakitHelper;
        PrepareTask(TampilListPenyakitHelper tampilListPenyakitHelper) throws IOException {
            this.tampilListPenyakitHelper = new WeakReference<>(tampilListPenyakitHelper);

            this.fileCache = new File(this.tampilListPenyakitHelper.get().activity.getCacheDir(), "cache");
            fileCache.mkdir();
            diskLruObjectCache = SimpleDiskLruCache.getsInstance(fileCache);
        }
        @Override
        public void run() {
            tampilListPenyakitHelper.get().getDataPenyakitFromDB();
            try {
                checkAndLoadAllBitmaps();
            } catch (IOException e) {
                String keyEx = "run_PrepareTask_cLoad2";
                String resE = String.format("Error execute checkAndLoadAllBitmaps(); e -> %s", e.toString());
                LogIntoCrashlytics.logException(keyEx, resE, e);
                tampilListPenyakitHelper.get().activity.LOGE(keyEx, resE);
            }
            synchronized (diskLruObjectCache){
                try {
                    diskLruObjectCache.close();
                } catch (IOException e) {
                    String keyEx = "run_PrepareTask_closeDisk";
                    String resE = String.format("Error execute diskLruObjectCache.close(); e -> %s", e.toString());
                    LogIntoCrashlytics.logException(keyEx, resE, e);
                    tampilListPenyakitHelper.get().activity.LOGE(keyEx, resE);
                }
            }
            postExecute();
        }

        private synchronized void postExecute() {
            try {
                inflateListAndAddTouchable();
            } catch (IOException e) {
                String keyEx = "run_PrepareTask_postExecute";
                String resE = String.format("Error execute inflateListAndAddTouchable(); e -> %s", e.toString());
                LogIntoCrashlytics.logException(keyEx, resE, e);
                tampilListPenyakitHelper.get().activity.LOGE(keyEx, resE);
            }
            tampilListPenyakitHelper.get().finished_mode = 0;
            tampilListPenyakitHelper.get().dialogShowHelper.stopDialog();
        }

        private synchronized void inflateListAndAddTouchable() throws IOException {
            int size = tampilListPenyakitHelper.get().dataPenyakitList.size();
            for (int x = 0; x < size; x++) {
                inflateToList(x);
            }
        }

        private void inflateToList(final int x) {
            CardView mContent = (CardView) tampilListPenyakitHelper.get().activity.getLayoutInflater().inflate(R.layout.adapter_namapenyakit, null);
            ImageView mImg = mContent.findViewById(R.id.adapter_id_imgnamapenyakit);
            TextView mText = mContent.findViewById(R.id.adapter_id_namapenyakit);
            DataPenyakit data = tampilListPenyakitHelper.get().dataPenyakitList.get(x);
            synchronized (tampilListPenyakitHelper.get().mImagecache.get(x)) {
                final Bitmap bitmap = tampilListPenyakitHelper.get().mImagecache.get(x);
                synchronized (bitmap) {
                    Log.i("tampilInflater", String.format("Size of lruCache = %d, size of bitmap value %d = %d, isRecycled? : %s", tampilListPenyakitHelper.get().mImagecache.size(), x, bitmap.getByteCount(), String.valueOf(bitmap.isRecycled())));
                    mImg.setImageBitmap(bitmap);
                    // apply the name of penyakit
                    mText.setText(data.getNama_penyakit());

                    // sets the item touchable
                    final int y = x;
                    mContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tampilListPenyakitHelper.get().onItemClickListener != null)
                                tampilListPenyakitHelper.get().onItemClickListener.onClick(tampilListPenyakitHelper.get().mContentView, y);
                        }
                    });

                    // applying into content section
                    tampilListPenyakitHelper.get().childView.addView(mContent);
                }
            }
        }

        private synchronized void checkAndLoadAllBitmaps() throws IOException {
            for(int x = 0; x < tampilListPenyakitHelper.get().dataPenyakitList.size(); x++){
                String name = tampilListPenyakitHelper.get().dataPenyakitList.get(x).path_image;
                String nameID = getLasts(name);
                Log.i("CobaLoad", "nameID is " + diskLruObjectCache.isKeyExists(nameID));
                if (diskLruObjectCache.isKeyExists(nameID)) {
                    InputStream is = null;
                    try {
                        is = diskLruObjectCache.get(nameID);
                    } catch (IOException e) {
                        String keyEx = "run_PrepareTask_checkAndLoadAllBitmaps";
                        String resE = String.format("Error while getting the name of {%s} e -> %s", nameID, e.toString());
                        LogIntoCrashlytics.logException(keyEx, resE, e);
                        tampilListPenyakitHelper.get().activity.LOGE(keyEx, resE);
                    }
                    if(is == null){
                        diskLruObjectCache.closeReading();
                        continue;
                    }
                    final Bitmap resultBitmap = BitmapFactory.decodeStream(is);
                    // applying into content section
                    Log.i("tampilThread", "Bitmap " + x + " isRecycled? : " + resultBitmap.isRecycled());
                    tampilListPenyakitHelper.get().mImagecache.put(x, resultBitmap);
                    diskLruObjectCache.closeReading();
                }
            }
            Log.i("tampilThread", String.format("Size of lruCache = %d, size of bitmap value 1 = %d, isRecycled? : %s", tampilListPenyakitHelper.get().mImagecache.size(), tampilListPenyakitHelper.get().mImagecache.get(1).getByteCount(), String.valueOf(tampilListPenyakitHelper.get().mImagecache.get(1).isRecycled())));
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
            /*if (countImg > 0) {
                Random random = new Random();
                int selectedImg = random.nextInt(countImg);
                this.path_image = path + "/" + img[selectedImg] + ".jpg";
            } else
                this.path_image = null;*/
            this.path_image = path + "/" + img[0] + ".jpg";
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
