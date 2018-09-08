package id.kenshiro.app.panri.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mylexz.utils.MylexzActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.adapter.AdapterRecycler;

public class TampilListPenyakitHelper {
    MylexzActivity activity;
    SQLiteDatabase sqlDB;
    RelativeLayout rootView;
    AdapterRecycler.OnItemClickListener onItemClickListener;
    List<DataPenyakit> dataPenyakitList;
    ScrollView mContentView;
    LinearLayout childView;

    public TampilListPenyakitHelper(MylexzActivity activity, SQLiteDatabase sqlDB, RelativeLayout rootView) {
        this.activity = activity;
        this.sqlDB = sqlDB;
        this.rootView = rootView;
    }

    public void buildAndShow() throws IOException {
        setContentViewer();
        getDataPenyakitFromDB();
        inflateListAndAddTouchable();
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

            // gets the image first in assets stream
            InputStream is = activity.getAssets().open(data.getPath_image());
            mImg.setImageDrawable(Drawable.createFromStream(is, null));
            is.close();

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
