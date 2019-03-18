package id.kenshiro.app.panri.opt.onsplash;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SimpleDiskLruCache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.helper.DecodeBitmapHelper;
import id.kenshiro.app.panri.helper.ListCiriCiriPenyakit;
import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;

public class ConfigureCache {
    private static final int QUALITY_FACTOR = 40;
    private SQLiteDatabase sqlDB;
    private MylexzActivity ctx;
    private SimpleDiskLruCache diskCache;
    private HashMap<Integer, ListCiriCiriPenyakit> listCiriCiriPenyakitHashMap;
    private String pathImageListPenyakit = "data/images/list";

    public ConfigureCache(MylexzActivity ctx, SimpleDiskLruCache diskCache) {
        this.ctx = ctx;
        this.diskCache = diskCache;
    }

    public void configureCache() {
        if (validateCacheDirs()) {
            // caching images
            cachingBitmapsViewPager();
            // caching Objects
            try {
                cachingListPenyakit();
            } catch (IOException e) {
                LogIntoCrashlytics.logException("IOExCreateCache1", String.format("IOException occured when create listCiriCiriPenyakit Cache e -> %s", e.toString()), e);
                ctx.LOGE("Task.background()", "IOException occured when create listCiriCiriPenyakit Cache", e);
            }
            try {
                cachingListImageCard1();
            } catch (IOException e) {
                LogIntoCrashlytics.logException("IOExCreateCache2", String.format("IOException occured when create image card listCiriCiriPenyakit Cache e -> %s", e.toString()), e);
                ctx.LOGE("Task.background()", "IOException occured when create image card listCiriCiriPenyakit Cache", e);
            }
            sqlDB.close();
        }
    }

    private void cachingListImageCard1() throws IOException {
        int counter = 0;
        File imgListPath = new File(ctx.getFilesDir(), pathImageListPenyakit);
        Cursor cursor = sqlDB.rawQuery("select path_gambar from gambar_penyakit", null);
        cursor.moveToFirst();
        int size_images = Math.round(ctx.getResources().getDimension(R.dimen.actmain_dimen_opimg_incard_wh));
        while (!cursor.isAfterLast()) {
            String[] buf = cursor.getString(0).split(",");
            String name = buf[0];
            String nameID = getLasts(name) + "jpg";
            final InputStream inputStream = new FileInputStream(imgListPath.getAbsolutePath() + "/" + name + ".jpg");
            final Bitmap bitmap = DecodeBitmapHelper.decodeBitmapStream(inputStream);
            final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, size_images, size_images, false);
            //gets the byte of bitmap
            inputStream.close();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            float scaling = bitmap.getHeight() / size_images;
            scaling = ((scaling < 1.0f) ? 1.0f : scaling);
            // compressing
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, Math.round(QUALITY_FACTOR / scaling), bos);
            // put into cache
            try {
                diskCache.put(nameID, bos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap.recycle();
            System.gc();
            cursor.moveToNext();
        }
        cursor.close();
        System.gc();
    }

    private boolean validateCacheDirs() {
        File fileCache = new File(ctx.getCacheDir(), "cache");
        fileCache.mkdir();
        String[] fileList = fileCache.list();
        return fileList.length == 0;
    }


    private void cachingListPenyakit() throws IOException {
        sqlDB = SQLiteDatabase.openOrCreateDatabase("/data/data/id.kenshiro.app.panri/files/database_penyakitpadi.db", null);
        loadAllDataCiri();
        if (listCiriCiriPenyakitHashMap != null) {
            synchronized (diskCache) {
                diskCache.putObjectWithEncode(KeyListClasses.LIST_PENYAKIT_CIRI_KEY_CACHE, listCiriCiriPenyakitHashMap);
            }
        }
    }

    private void loadAllDataCiri() {
        listCiriCiriPenyakitHashMap = new HashMap<Integer, ListCiriCiriPenyakit>();
        // input ciri ciri penyakit
        Cursor curr = sqlDB.rawQuery("select ciri from ciriciri", null);
        curr.moveToFirst();
        while (!curr.isAfterLast()) {
            String ciri = curr.getString(0);
            listCiriCiriPenyakitHashMap.put(curr.getPosition() + 1, new ListCiriCiriPenyakit(ciri, false, false));
            curr.moveToNext();
        }
        curr.close();
        System.gc();
        ///////////////////////////
        // input usefirst flags
        curr = sqlDB.rawQuery("select usefirst from ciriciri", null);
        curr.moveToFirst();
        while (!curr.isAfterLast()) {
            String ciri = curr.getString(0);
            listCiriCiriPenyakitHashMap.get(curr.getPosition() + 1).setUsefirst_flags(Boolean.parseBoolean(ciri));
            curr.moveToNext();
        }
        curr.close();
        System.gc();
        ///////////////////////////
        // input ask flags
        curr = sqlDB.rawQuery("select ask from ciriciri", null);
        curr.moveToFirst();
        while (!curr.isAfterLast()) {
            String ciri = curr.getString(0);
            listCiriCiriPenyakitHashMap.get(curr.getPosition() + 1).setAsk_flags(Boolean.parseBoolean(ciri));
            curr.moveToNext();
        }
        curr.close();
        System.gc();
        ///////////////////////////
        // input listused flags
        curr = sqlDB.rawQuery("select listused from ciriciri", null);
        curr.moveToFirst();
        while (!curr.isAfterLast()) {
            String ciri = curr.getString(0);
            listCiriCiriPenyakitHashMap.get(curr.getPosition() + 1).setListused_flags(ciri);
            curr.moveToNext();
        }
        curr.close();
        System.gc();
        ///////////////////////////
        // input pointo flags
        curr = sqlDB.rawQuery("select pointo from ciriciri", null);
        curr.moveToFirst();
        while (!curr.isAfterLast()) {
            String ciri = curr.getString(0);
            listCiriCiriPenyakitHashMap.get(curr.getPosition() + 1).setPointo_flags(ciri);
            curr.moveToNext();
        }
        curr.close();
        System.gc();
        //////////////////////////
        //////////////////////////////////////// load successfully
    }

    private void cachingBitmapsViewPager() {
        String[] keyImageLists = {
                "viewpager_area_1",
                "viewpager_area_2",
                "viewpager_area_3",
                "viewpager_area_4"
        };
        Point point = new Point();
        ctx.getWindowManager().getDefaultDisplay().getSize(point);
        point.y = Math.round(ctx.getResources().getDimension(R.dimen.actmain_dimen_viewpager_height));
        for (String key : keyImageLists) {
            int resDrawable = ctx.getResources().getIdentifier(key, "drawable", ctx.getPackageName());
            //gets the Bitmap
            Bitmap bitmap = DecodeBitmapHelper.decodeAndResizeBitmapsResources(ctx.getResources(), resDrawable, point.y, point.x);
            // creates the scaled bitmaps
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, point.x, point.y, false);
            //gets the byte of bitmap
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            float scaling = bitmap.getHeight() / point.y;
            scaling = ((scaling < 1.0f) ? 1.0f : scaling);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, Math.round(QUALITY_FACTOR / scaling), bos);
            // put into cache
            try {
                diskCache.put(key, bos.toByteArray());
            } catch (IOException e) {
                ctx.LOGE("Task.background()", "IOException occured when putting a cache image", e);
            }
            try {
                bos.close();
            } catch (IOException e) {
                ctx.LOGE("Task.background()", "IOException occured when releasing ByteOutputStream", e);
            }
            bitmap.recycle();
            scaledBitmap.recycle();
            System.gc();
        }
        // for nav header background
    }

    private String getLasts(String name) {
        StringBuffer results = new StringBuffer();
        for (int x = name.length() - 1; x >= 0; x--) {
            char s = name.charAt(x);
            if (s == '.') continue;
            else if (s == '/') break;
            else results.append(s);
        }
        results.reverse();
        return results.toString().toLowerCase();
    }
}
