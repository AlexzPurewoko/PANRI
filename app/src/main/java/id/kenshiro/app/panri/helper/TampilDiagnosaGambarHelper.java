package id.kenshiro.app.panri.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.support.v4.util.LruCache;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.adapter.AdapterRecycler;
import id.kenshiro.app.panri.adapter.CustomViewPager;
import id.kenshiro.app.panri.adapter.FadePageViewTransformer;
import id.kenshiro.app.panri.adapter.ImageFragmentAdapter;

public class TampilDiagnosaGambarHelper implements Closeable{
    private RelativeLayout mRootView;
    private SQLiteDatabase sqlDB;
    private MylexzActivity activity;
    private LinearLayout mChildView;
    private LinearLayout btnBawah;
    public ScrollView mContentView;
    DataCiriPenyakit dataCiriPenyakit;
    private AdapterRecycler.OnItemClickListener onItemClickListener;
    private LruCache<Integer, Bitmap> mImagecache = null;

    private static final int ON_BTN_YA = 0x6;
    private static final int ON_BTN_TIDAK = 0x6f;
    private int mSizeList = 0;
    private int mPositionList = 1;
    private CardView content;
    private OnItemListener onItemListener;
    private int modes = 0;
    private int finished_mods = 0;
    private BuilderTaskUpdaterContent builderTask = null;

    public TampilDiagnosaGambarHelper(MylexzActivity activity, RelativeLayout mRootView, SQLiteDatabase sqlDB) {
        this.mRootView = mRootView;
        this.sqlDB = sqlDB;
        this.activity = activity;
    }

    public void buildAndShow() {
        createAndApplyContentLayout();
        getsTheSizeData();
        getDataFromDB(mPositionList);
        buildContent();
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

    private void getsTheSizeData() {
        Cursor cursor = sqlDB.rawQuery("select nama from penyakit", null);
        cursor.moveToFirst();
        mSizeList = cursor.getCount();
        cursor.close();
        System.gc();

        mPositionList = 1;
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public boolean isContentViewHidden() {
        return mContentView.getVisibility() == View.GONE;
    }

    public void hideContentView() {
        mContentView.setVisibility(View.GONE);
    }

    public void showContentView() {
        mContentView.setVisibility(View.VISIBLE);
        btnBawah.setVisibility(View.VISIBLE);
    }

    public void setOnItemClickListener(AdapterRecycler.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void buildContent() {
        updateContentAfter();
    }

    public void setItemPosition(int position) {
        this.mPositionList = position;
    }

    public void updateContentAfter() {
        if(finished_mods != 0) return;
        finished_mods = 1;
        builderTask = new BuilderTaskUpdaterContent(this);
        builderTask.execute();
    }

    private void onClickBtn(int whichType, int x) {
        if(finished_mods != 0)return;
        switch (whichType) {
            case ON_BTN_TIDAK: {
                mPositionList++;
                updateContentAfter();
                if (onItemListener != null)
                    onItemListener.onBtnTidakClicked(mContentView, btnBawah, mPositionList);
            }
            break;
            case ON_BTN_YA:
                if (onItemListener != null)
                    onItemListener.onBtnYaClicked(mContentView, btnBawah, mPositionList);
                break;
        }
    }

    private void setBtn(Button btnYa, Button btnTidak) {
        btnYa.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Gill_SansMT.ttf"), Typeface.BOLD);
        btnYa.setTextColor(Color.WHITE);
        btnTidak.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Gill_SansMT.ttf"), Typeface.BOLD);
        btnTidak.setTextColor(Color.WHITE);

        btnYa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TampilDiagnosaGambarHelper.this.onClickBtn(ON_BTN_YA, mPositionList);
            }
        });
        btnTidak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TampilDiagnosaGambarHelper.this.onClickBtn(ON_BTN_TIDAK, mPositionList);
            }
        });
    }

    private void setBtnMulai(Button btnMulai, final int x) {
        btnMulai.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Gill_SansMT.ttf"), Typeface.BOLD);
        btnMulai.setTextColor(Color.WHITE);
        btnMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onClick(mContentView, x);
            }
        });
    }

    private void setCiriPenyakitText(WebView ciriP, String html) {
        ciriP.loadData(html, "text/html", "utf-8");
    }

    private void setViewPagerImage(final CustomViewPager customViewPager, LinearLayout indicators) {
        final int mDotCount = mImagecache.size();
        final LinearLayout[] mDots = new LinearLayout[mDotCount];
        Point reqSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(reqSize);
        reqSize.y = Math.round(activity.getResources().getDimension(R.dimen.actmain_dimen_viewpager_height));
        ImageFragmentAdapter mImageControllerFragment = new ImageFragmentAdapter(activity, activity.getSupportFragmentManager(), mImagecache, reqSize);
        customViewPager.setAdapter(mImageControllerFragment);
        customViewPager.setCurrentItem(0);
        customViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curr_img = customViewPager.getCurrentItem();
                customViewPager.setPageTransformer(true, new FadePageViewTransformer());
                if (++curr_img == mImagecache.size())
                    curr_img = 0;
                customViewPager.setCurrentItem(curr_img);
                System.gc();
            }
        });
        customViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                for (int y = 0; y < mDotCount; y++) {
                    mDots[y].setBackgroundResource(R.drawable.indicator_unselected_item_oval);
                }
                mDots[i].setBackgroundResource(R.drawable.indicator_selected_item_oval);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                int pos = customViewPager.getCurrentItem();
                // if reaching last and state is DRAGGING, back into first
                if (pos == mImagecache.size() - 1 && i == ViewPager.SCROLL_STATE_DRAGGING)
                    customViewPager.setCurrentItem(0, true);
            }
        });
        // set the indicators
        if (indicators != null)
            indicators.removeAllViewsInLayout();
        for (int y = 0; y < mDotCount; y++) {
            mDots[y] = new LinearLayout(activity);
            mDots[y].setBackgroundResource(R.drawable.indicator_unselected_item_oval);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 4, 4);
            mDots[y].setGravity(Gravity.RIGHT | Gravity.BOTTOM | Gravity.END);
            indicators.addView(mDots[y], params);

        }
        mDots[0].setBackgroundResource(R.drawable.indicator_selected_item_oval);
    }

    private void setJudulText(TextView judul, String txt) {
        judul.setText(txt);
    }

    private void getDataFromDB(int position) {
        dataCiriPenyakit = new DataCiriPenyakit(null, null, null);

        // gets the nama penyakit
        Cursor cursor = sqlDB.rawQuery("select nama from penyakit where no=" + position, null);
        cursor.moveToFirst();
        dataCiriPenyakit.setNama_penyakit(cursor.getString(0));
        cursor.close();
        System.gc();

        // gets the latin
        cursor = sqlDB.rawQuery("select latin from penyakit where no=" + position, null);
        cursor.moveToFirst();
        dataCiriPenyakit.setNama_latin(cursor.getString(0));
        cursor.close();
        System.gc();

        // gets the ciriciri
        cursor = sqlDB.rawQuery("select listciri from penyakit where no=" + position, null);
        cursor.moveToFirst();
        dataCiriPenyakit.setListCiriHtml(cursor.getString(0));
        cursor.close();
        System.gc();

        // gets the listGambar
        cursor = sqlDB.rawQuery("select gambarid from list_gambarid where no=" + position, null);
        cursor.moveToFirst();
        dataCiriPenyakit.setListGambarId(cursor.getString(0));
        cursor.close();
        System.gc();
    }

    private void createAndApplyContentLayout() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        mChildView = new LinearLayout(activity);
        mChildView.setLayoutParams(params);
        mChildView.setOrientation(LinearLayout.VERTICAL);
        mChildView.setVisibility(View.VISIBLE);
        mContentView = new ScrollView(activity);
        mContentView.setLayoutParams(new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT
        ));
        mContentView.addView(mChildView);
        btnBawah = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.adapter_btnbawahdiag, mRootView, false);
        mRootView.addView(mContentView);
        mRootView.addView(btnBawah);

        RelativeLayout.LayoutParams paramsbtn = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        paramsbtn.addRule(RelativeLayout.ABOVE, R.id.adapter_id_imgdiag_layout);
        mContentView.setLayoutParams(paramsbtn);
    }

    @Override
    public void close() throws IOException {
        recycleBitmaps();
    }

    public interface OnItemListener {
        void onBtnYaClicked(View v, View button, int position);

        void onBtnTidakClicked(View v, View button, int position);

        void onIsAfterLastListPosition(View v, View button, int position, int size_list);
    }

    private class DataCiriPenyakit implements Serializable{
        String nama_penyakit;
        String nama_latin;
        String listCiriHtml;
        //SpannableString listCiriHtml;
        List<Integer> listGambarId;

        public DataCiriPenyakit(String nama_penyakit, String nama_latin, String gambarList) {
            this.nama_latin = nama_latin;
            this.nama_penyakit = nama_penyakit;
            setListGambarId(gambarList);
        }

        public void setNama_penyakit(String nama_penyakit) {
            this.nama_penyakit = nama_penyakit;
        }

        public void setListCiriHtml(String listCiri) {
            if (listCiri == null) return;
            String[] list = listCiri.split(",");
            StringBuffer bufHtml = new StringBuffer();
            bufHtml.append("<font color=\"black\" size=\"5pt\">");
            bufHtml.append("<b>Ciri - ciri : </b>");
            bufHtml.append("</font>");
            bufHtml.append("<ul>");
            //bufHtml.append("<li>");
            for (int x = 0; x < list.length; x++) {
                bufHtml.append("<li>");
                bufHtml.append(list[x]);
                bufHtml.append("</li>");
            }
            bufHtml.append("</ul>");
            this.listCiriHtml = bufHtml.toString();
        }

        public void setListGambarId(String listId) {
            if (listId == null) return;
            String[] list = listId.split(",");
            this.listGambarId = new ArrayList<Integer>();
            for (int x = 0; x < list.length; x++) {
                int resId = activity.getResources().getIdentifier(
                        list[x],
                        "drawable",
                        activity.getPackageName()
                );
                this.listGambarId.add(resId);
            }
        }

        public void setNama_latin(String nama_latin) {
            this.nama_latin = nama_latin;
        }
    }
    private static class BuilderTaskUpdaterContent extends AsyncTask<Void, Void, Integer>{
        SimpleDiskLruCache diskLruObjectCache;
        private static final int QUALITY_FACTOR = 10;
        private static final long MAX_CACHE_BUFFERED_SIZE = 1048576;
        TampilDiagnosaGambarHelper tampilDiagnosaGambarHelper;
        BuilderTaskUpdaterContent(TampilDiagnosaGambarHelper tampilDiagnosaGambarHelper){
            this.tampilDiagnosaGambarHelper = tampilDiagnosaGambarHelper;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            File fileCache = new File(tampilDiagnosaGambarHelper.activity.getCacheDir(),"cache");
            fileCache.mkdir();
            try {
                diskLruObjectCache = new SimpleDiskLruCache(fileCache);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        @Override
        protected Integer doInBackground(Void... voids) {
            tampilDiagnosaGambarHelper.recycleBitmaps();
            if(tampilDiagnosaGambarHelper.modes == 0){
                tampilDiagnosaGambarHelper.getsTheSizeData();
                tampilDiagnosaGambarHelper.getDataFromDB(tampilDiagnosaGambarHelper.mPositionList);
                try {
                    checkAndLoadAllBitmaps();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tampilDiagnosaGambarHelper.modes = 1;
                return 2;
            }
            else if (tampilDiagnosaGambarHelper.mPositionList <= tampilDiagnosaGambarHelper.mSizeList) {
                tampilDiagnosaGambarHelper.dataCiriPenyakit = null;
                tampilDiagnosaGambarHelper.getDataFromDB(tampilDiagnosaGambarHelper.mPositionList);
                try {
                    checkAndLoadAllBitmaps();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.gc();
                return 1;
            } else if (tampilDiagnosaGambarHelper.onItemListener != null)
                tampilDiagnosaGambarHelper.onItemListener.onIsAfterLastListPosition(tampilDiagnosaGambarHelper.mContentView, tampilDiagnosaGambarHelper.btnBawah, tampilDiagnosaGambarHelper.mPositionList, tampilDiagnosaGambarHelper.mSizeList);

            return 0;
        }
        private void checkAndLoadAllBitmaps() throws IOException {
            Point reqSize = new Point();
            tampilDiagnosaGambarHelper.activity.getWindowManager().getDefaultDisplay().getSize(reqSize);
            reqSize.y = Math.round(tampilDiagnosaGambarHelper.activity.getResources().getDimension(R.dimen.actmain_dimen_viewpager_height));
            tampilDiagnosaGambarHelper.mImagecache = new LruCache<Integer, Bitmap>(reqSize.x * reqSize.y);
            int sizeslist = tampilDiagnosaGambarHelper.dataCiriPenyakit.listGambarId.size();
            for(int x = 0; x < sizeslist; x++){
                int resID = tampilDiagnosaGambarHelper.dataCiriPenyakit.listGambarId.get(x);
                String nameID = getLasts(tampilDiagnosaGambarHelper.activity.getResources().getResourceName(resID));
                if(!diskLruObjectCache.isKeyExists(nameID)){
                    final Bitmap bitmap = DecodeBitmapHelper.decodeAndResizeBitmapsResources(tampilDiagnosaGambarHelper.activity.getResources(), resID, reqSize.y, reqSize.x);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, reqSize.x, reqSize.y, false);
                    //gets the byte of bitmap
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    float scaling = bitmap.getHeight() / reqSize.y;
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
                    tampilDiagnosaGambarHelper.mImagecache.put(x, scaledBitmap);
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
                    tampilDiagnosaGambarHelper.mImagecache.put(x, BitmapFactory.decodeStream(is));
                    diskLruObjectCache.closeReading();
                }
            }
        }

        private String getLasts(String resourceName) {
            StringBuffer stringBuffer = new StringBuffer();
            for(int x = resourceName.length() - 1; x >= 0; x--){
                char s = resourceName.charAt(x);
                if(s == '/')break;
                else stringBuffer.append(s);
            }
            stringBuffer.reverse();
            return stringBuffer.append("res1").toString();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            try {
                diskLruObjectCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(integer == 1) {
                tampilDiagnosaGambarHelper.mContentView.pageScroll(0);
                TextView judul = tampilDiagnosaGambarHelper.content.findViewById(R.id.actimgdiagnose_judulpenyakit);
                CustomViewPager customViewPager = tampilDiagnosaGambarHelper.content.findViewById(R.id.actimgdiagnose_id_viewpagerimg);
                LinearLayout indicators = tampilDiagnosaGambarHelper.content.findViewById(R.id.actimgdiagnose_id_layoutIndicators);
                WebView ciriP = tampilDiagnosaGambarHelper.content.findViewById(R.id.actimgdiagnose_ciriciri);
                // sets the judul
                tampilDiagnosaGambarHelper.setJudulText(judul, tampilDiagnosaGambarHelper.dataCiriPenyakit.nama_penyakit);

                // sets the largeImage
                tampilDiagnosaGambarHelper.setViewPagerImage(customViewPager, indicators);

                // sets the TextView CiriP
                tampilDiagnosaGambarHelper.setCiriPenyakitText(ciriP, tampilDiagnosaGambarHelper.dataCiriPenyakit.listCiriHtml);
                System.gc();
            }
            else if(integer == 2){
                tampilDiagnosaGambarHelper.createAndApplyContentLayout();
                //Load CardView
                tampilDiagnosaGambarHelper.content = (CardView) tampilDiagnosaGambarHelper.activity.getLayoutInflater().inflate(R.layout.adapter_imgdiagnose, tampilDiagnosaGambarHelper.mRootView, false);
                TextView judul = tampilDiagnosaGambarHelper.content.findViewById(R.id.actimgdiagnose_judulpenyakit);
                CustomViewPager customViewPager = tampilDiagnosaGambarHelper.content.findViewById(R.id.actimgdiagnose_id_viewpagerimg);
                LinearLayout indicators = tampilDiagnosaGambarHelper.content.findViewById(R.id.actimgdiagnose_id_layoutIndicators);
                WebView ciriP = tampilDiagnosaGambarHelper.content.findViewById(R.id.actimgdiagnose_ciriciri);
                Button btnYa = tampilDiagnosaGambarHelper.btnBawah.findViewById(R.id.actimgdiagnose_buttonya);
                Button btnTidak = tampilDiagnosaGambarHelper.btnBawah.findViewById(R.id.actimgdiagnose_buttontidak);

                // sets the judul
                tampilDiagnosaGambarHelper.setJudulText(judul, tampilDiagnosaGambarHelper.dataCiriPenyakit.nama_penyakit);

                // sets the largeImage
                tampilDiagnosaGambarHelper.setViewPagerImage(customViewPager, indicators);

                // sets the TextView CiriP
                tampilDiagnosaGambarHelper.setCiriPenyakitText(ciriP, tampilDiagnosaGambarHelper.dataCiriPenyakit.listCiriHtml);

                // sets the button
                tampilDiagnosaGambarHelper.setBtn(btnYa, btnTidak);

                // add and apply into view
                tampilDiagnosaGambarHelper.mChildView.addView(tampilDiagnosaGambarHelper.content);
            }
            tampilDiagnosaGambarHelper.finished_mods = 0;
            tampilDiagnosaGambarHelper.builderTask = null;
            System.gc();
        }
    }
}
