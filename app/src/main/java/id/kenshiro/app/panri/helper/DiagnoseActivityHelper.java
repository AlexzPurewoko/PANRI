package id.kenshiro.app.panri.helper;

import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import com.mylexz.utils.MylexzActivity;

import java.util.HashMap;

import id.kenshiro.app.panri.DiagnoseActivity;

public class DiagnoseActivityHelper {
    private MylexzActivity activity;
    private HashMap<Integer, ListNamaPenyakit> listNamaPenyakitHashMap;
    private HashMap<Integer, ListCiriCiriPenyakit> listCiriCiriPenyakitHashMap;
    private RelativeLayout mRootView, mAskLayout;
    private RecyclerView mListFirstPage;
    public DiagnoseActivityHelper(MylexzActivity activity, HashMap<Integer, ListNamaPenyakit> listNamaPenyakitHashMap, HashMap<Integer, ListCiriCiriPenyakit> listCiriCiriPenyakitHashMap){
        this.activity = activity;
        this.listNamaPenyakitHashMap = listNamaPenyakitHashMap;
        this.listCiriCiriPenyakitHashMap = listCiriCiriPenyakitHashMap;
    }
}
