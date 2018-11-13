package id.kenshiro.app.panri.helper;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mylexz.utils.MylexzActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.adapter.AdapterRecycler;

public class DiagnoseActivityHelper{
    private MylexzActivity activity;
    private HashMap<Integer, ListNamaPenyakit> listNamaPenyakitHashMap;
    private HashMap<Integer, ListCiriCiriPenyakit> listCiriCiriPenyakitHashMap;
    private RelativeLayout mRootView, mAskLayout;
    private RecyclerView mListFirstPage;
    // for ask layout
    Button yes;
    Button no;
    TextView mDescCiri;
    public static final int ON_BTN_YES_CLICKED = 0xcf;
    public static final int ON_BTN_NO_CLICKED = 0xca;
    // for list ciri layout
    private AdapterRecycler dataAdapter;

    // for counter
    //private int counter = 0;
    private int count_when_accept = 0;
    private int count_when_decline = 0;
    private int count_position_data = 0;
    private int savedItemDataPosition = 0;
    private int view_modes = ListCiriCiriPenyakit.MODE_BIND;
    private List<Integer> saved_btn_yesno_modes = new ArrayList<Integer>();
    private List<Integer> view_mode_saved = new ArrayList<Integer>();
    // for temporary list
    private List<Integer> temp_list_nums;
    private List<List<Integer>> saved_temp_list_nums = new ArrayList<List<Integer>>();
    private List<AdapterRecycler.DataPerItems> data;

    private OnPenyakitHaveSelected onPenyakitHaveSelected = null;
    private LinearLayout btnBawah;

    public DiagnoseActivityHelper(MylexzActivity activity, HashMap<Integer, ListNamaPenyakit> listNamaPenyakitHashMap, HashMap<Integer, ListCiriCiriPenyakit> listCiriCiriPenyakitHashMap){
        this.activity = activity;
        this.listNamaPenyakitHashMap = listNamaPenyakitHashMap;
        this.listCiriCiriPenyakitHashMap = listCiriCiriPenyakitHashMap;
    }
    public void buildAndShow(){
        prepareRootLayout();
        buildLayout();
        selectFirstTampil();
    }

    public void showAgain() {
        // set it visible into mListFirstPage
        mListFirstPage.setVisibility(View.VISIBLE);
        mAskLayout.setVisibility(View.GONE);

        selectFirstTampil();
    }

    // return false if its content is firstItem another is true
    public boolean setOnPushBackButtonPressed(boolean on) {
        // if viewmodes is 0 indicates its first page
        if (!on) return false;
        /*if (counter == 0)
            return false;*/
        if (view_mode_saved == null)
            return false;
        int mCurrPosSaved = view_mode_saved.size() - 1;
        if (mCurrPosSaved <= -1)
            return false;
        if (view_mode_saved.get(mCurrPosSaved) == 0)
            return false;
        else if (view_mode_saved.size() == 1 && view_mode_saved.get(mCurrPosSaved) != ListCiriCiriPenyakit.MODE_SEQUENCE) {
            /*counter = */
            count_when_accept = count_when_decline = 0;
            mAskLayout.setVisibility(View.GONE);
            mListFirstPage.setVisibility(View.VISIBLE);
            selectFirstTampil();
            return true;
        } else {
            if (view_mode_saved.get(mCurrPosSaved) == ListCiriCiriPenyakit.MODE_BIND) {
                // load previous lists
                //counter--;
                count_when_accept--;
                temp_list_nums = saved_temp_list_nums.get(mCurrPosSaved);
                // change the content of data
                data = new ArrayList<AdapterRecycler.DataPerItems>();
                for (int x = 0; x < temp_list_nums.size(); x++) {
                    data.add(new AdapterRecycler.DataPerItems(listCiriCiriPenyakitHashMap.get(temp_list_nums.get(x)).getCiri()));
                }
                // apply these changes into RecyclerView
                dataAdapter = new AdapterRecycler(data, activity);
                dataAdapter.setOnItemClickListener(new AdapterRecycler.OnItemClickListener() {
                    @Override
                    public void onClick(View a, int b) {
                        count_position_data = temp_list_nums.size();
                        // call this function itself
                        DiagnoseActivityHelper.this.onItemCardTouch(b);
                    }
                });
                mListFirstPage.setAdapter(dataAdapter);
                mListFirstPage.setVisibility(View.VISIBLE);
                mAskLayout.setVisibility(View.GONE);
                mRootView.setGravity(Gravity.TOP | Gravity.CENTER);
                if (onPenyakitHaveSelected != null)
                    onPenyakitHaveSelected.onPilihCiriSection();
                System.gc();
            } else if (view_mode_saved.get(mCurrPosSaved) == ListCiriCiriPenyakit.MODE_SEQUENCE) {
                if (savedItemDataPosition > 0)
                    savedItemDataPosition = --count_position_data;
                if (view_mode_saved.size() == 1) {
                    /*counter = */
                    count_when_accept = count_when_decline = 0;
                    mAskLayout.setVisibility(View.GONE);
                    mListFirstPage.setVisibility(View.VISIBLE);
                    selectFirstTampil();
                    return true;

                }
                if (onPenyakitHaveSelected != null)
                    onPenyakitHaveSelected.onTanyaSection();
                //counter--;

                if (saved_btn_yesno_modes.size() > 0)
                    switch (saved_btn_yesno_modes.remove(saved_btn_yesno_modes.size() - 1)) {
                        case ON_BTN_YES_CLICKED:
                            count_when_accept--;
                            break;
                        case ON_BTN_NO_CLICKED:
                            count_when_decline--;
                            break;
                        default:
                    }
                count_position_data = savedItemDataPosition;
                String ciri = listCiriCiriPenyakitHashMap.get(temp_list_nums.get(count_position_data)).getCiri();

                mDescCiri.setText(ciri);
                mListFirstPage.setVisibility(View.GONE);
                mAskLayout.setVisibility(View.VISIBLE);

                System.gc();
            }
            view_mode_saved.remove(mCurrPosSaved);
            saved_temp_list_nums.remove(mCurrPosSaved);
            return true;
        }
    }

    public void setOnPenyakitHaveSelected(OnPenyakitHaveSelected onPenyakitHaveSelected) {
        this.onPenyakitHaveSelected = onPenyakitHaveSelected;
    }

    private void selectFirstTampil() {
        data = new ArrayList<AdapterRecycler.DataPerItems>();
        temp_list_nums = new ArrayList<Integer>();
        for(int x = 0; x < listCiriCiriPenyakitHashMap.size(); x++){
            ListCiriCiriPenyakit penyakit = listCiriCiriPenyakitHashMap.get(x+1);
            if(penyakit.isUsefirst_flags()) {
                data.add(new AdapterRecycler.DataPerItems(penyakit.getCiri()));
                temp_list_nums.add(x + 1);
            }
        }

        dataAdapter = new AdapterRecycler(data, activity);
        dataAdapter.setOnItemClickListener(new AdapterRecycler.OnItemClickListener() {
            @Override
            public void onClick(View a, int b) {
                DiagnoseActivityHelper.this.onItemCardTouch(b);
            }
        });
        mListFirstPage.setAdapter(dataAdapter);
        mListFirstPage.scrollToPosition(0);
        view_mode_saved.clear();
        saved_temp_list_nums.clear();
        count_when_accept = count_when_decline = 0;
        view_modes = ListCiriCiriPenyakit.MODE_BIND;
        //view_mode_saved.add(ListCiriCiriPenyakit.MODE_BIND);
        //saved_temp_list_nums.add(temp_list_nums);
        if (onPenyakitHaveSelected != null)
            onPenyakitHaveSelected.onPilihCiriSection();
        System.gc();
    }

    private void prepareRootLayout() {
        mRootView = activity.findViewById(R.id.actdiagnose_id_layoutcontainer);
        mRootView.setVisibility(View.VISIBLE);
    }

    private void buildLayout() {
        // creaTES LAYOUT mAskLayout
        createAskLayout();
        // creates layout mListFirstPage
        createMlistFirstPage();
        // append it into root layout
        mRootView.addView(mListFirstPage);
        mRootView.addView(mAskLayout);
        // set it visible into mListFirstPage
        mListFirstPage.setVisibility(View.VISIBLE);
        mAskLayout.setVisibility(View.GONE);
    }

    private void createMlistFirstPage() {
        mListFirstPage = (RecyclerView) activity.getLayoutInflater().inflate(R.layout.adapter_recycler, null);
        mListFirstPage.setHasFixedSize(true);
        mListFirstPage.setLayoutManager(new LinearLayoutManager(activity));
    }
    // for handling onItemCardTouch
    private void onItemCardTouch(int cardPosition){
        //counter++;
        count_when_accept++;
        view_mode_saved.add(view_modes);
        saved_temp_list_nums.add(temp_list_nums);
        int itemPosition =
                temp_list_nums.get(cardPosition);

        view_modes = listCiriCiriPenyakitHashMap.get(itemPosition).listused_mode_flags;
        // if reached the end

        if (count_position_data >= temp_list_nums.size() || listCiriCiriPenyakitHashMap.get(itemPosition).listused_flags == null) {
            // check whether the data ciri next is any or not
            boolean any = listCiriCiriPenyakitHashMap.get(itemPosition).listused_flags != null;
            if(!any) {
                // end and gets the penyakit type
                int results_penyakit = listCiriCiriPenyakitHashMap.get(itemPosition).pointo_flags.get(0);
                //saved_counter = counter;
                double percentage = count_when_accept * 100 / view_mode_saved.size();//counter ;
                if (onPenyakitHaveSelected != null)
                    onPenyakitHaveSelected.onPenyakitSelected(this.mListFirstPage, this.mAskLayout, this.listNamaPenyakitHashMap, results_penyakit, percentage);                return;
            }
        }
        // if these condition is never we change its content view
        temp_list_nums = listCiriCiriPenyakitHashMap.get(itemPosition).listused_flags;

        // if view_modes is VIEW_BIND the content is will be displayed as list
        if(view_modes == ListCiriCiriPenyakit.MODE_BIND){
            // load another lists
            // change the content of data
            data = new ArrayList<AdapterRecycler.DataPerItems>();
            for(int x = 0; x < temp_list_nums.size(); x++){
                data.add(new AdapterRecycler.DataPerItems(listCiriCiriPenyakitHashMap.get(temp_list_nums.get(x)).getCiri()));
            }
            // apply these changes into RecyclerView
            dataAdapter = new AdapterRecycler(data, activity);
            dataAdapter.setOnItemClickListener(new AdapterRecycler.OnItemClickListener() {
                @Override
                public void onClick(View a, int b) {
                    count_position_data = temp_list_nums.size();
                    // call this function itself
                    DiagnoseActivityHelper.this.onItemCardTouch(b);
                }
            });
            mListFirstPage.setAdapter(dataAdapter);
            mListFirstPage.setVisibility(View.VISIBLE);
            mAskLayout.setVisibility(View.GONE);
            mRootView.setGravity(Gravity.TOP | Gravity.CENTER);
            if (onPenyakitHaveSelected != null)
                onPenyakitHaveSelected.onPilihCiriSection();
            System.gc();
        }
        // and if is another its content will be displayed as asking
        else if(view_modes == ListCiriCiriPenyakit.MODE_SEQUENCE){
                // load another data
            count_position_data = 0;
            // sets the content into asking
            mListFirstPage.setVisibility(View.GONE);
            mAskLayout.setVisibility(View.VISIBLE);

            // sets the textView
            String ciri = listCiriCiriPenyakitHashMap.get(temp_list_nums.get(count_position_data)).getCiri();
            mDescCiri.setText(ciri);
            if (onPenyakitHaveSelected != null)
                onPenyakitHaveSelected.onTanyaSection();
            System.gc();
        }
    }

    // for handling button touch event
    private void onBtnClicked(int conditionBTN){
        int itemPosition;
        //counter++;
        view_modes = ListCiriCiriPenyakit.MODE_SEQUENCE;
        count_position_data++;
        switch (conditionBTN){
            case ON_BTN_YES_CLICKED:
                count_when_accept++;
                saved_btn_yesno_modes.add(ON_BTN_YES_CLICKED);
                break;
            case ON_BTN_NO_CLICKED:
                count_when_decline++;
                saved_btn_yesno_modes.add(ON_BTN_NO_CLICKED);
                break;
            default:
        }
        view_mode_saved.add(ListCiriCiriPenyakit.MODE_SEQUENCE);
        saved_temp_list_nums.add(temp_list_nums);
        if(count_position_data >= temp_list_nums.size()){
            // check whether the data ciri next is any or not
            itemPosition = temp_list_nums.get(count_position_data - 1);
            boolean any = listCiriCiriPenyakitHashMap.get(temp_list_nums.get(count_position_data - 1)).listused_flags != null;
            if(!any) {
                // end and gets the penyakit type
                int results_penyakit = listCiriCiriPenyakitHashMap.get(itemPosition).pointo_flags.get(0);
                double percentage = count_when_accept * 100 / view_mode_saved.size();//counter;
                //saved_counter = counter;
                if (onPenyakitHaveSelected != null)
                    onPenyakitHaveSelected.onPenyakitSelected(this.mListFirstPage, this.mAskLayout, this.listNamaPenyakitHashMap, results_penyakit, percentage);
                return;
            }
            temp_list_nums = listCiriCiriPenyakitHashMap.get(temp_list_nums.get(count_position_data - 1)).listused_flags;
            count_position_data = 0;
            view_modes = listCiriCiriPenyakitHashMap.get(itemPosition).listused_mode_flags;
        }
        itemPosition = temp_list_nums.get(count_position_data);
        savedItemDataPosition = count_position_data;
        if(view_modes == ListCiriCiriPenyakit.MODE_SEQUENCE) {
            // handling into the next ciri - ciri if possible
            if (count_position_data >= temp_list_nums.size()) {
                temp_list_nums = listCiriCiriPenyakitHashMap.get(temp_list_nums.get(count_position_data - 1)).listused_flags;
                count_position_data = 0;
            }

            String ciri = listCiriCiriPenyakitHashMap.get(temp_list_nums.get(count_position_data)).getCiri();
            mDescCiri.setText(ciri);
            mListFirstPage.setVisibility(View.GONE);
            mAskLayout.setVisibility(View.VISIBLE);
            if (onPenyakitHaveSelected != null)
                onPenyakitHaveSelected.onTanyaSection();
            System.gc();
        }
        else if(view_modes == ListCiriCiriPenyakit.MODE_BIND){
            // next into mode bind
            // load another lists
            temp_list_nums = listCiriCiriPenyakitHashMap.get(itemPosition).listused_flags;
            // change the content of data
            data = new ArrayList<AdapterRecycler.DataPerItems>();
            for(int x = 0; x < temp_list_nums.size(); x++){
                data.add(new AdapterRecycler.DataPerItems(listCiriCiriPenyakitHashMap.get(temp_list_nums.get(x)).getCiri()));
            }
            // apply these changes into RecyclerView
            dataAdapter = new AdapterRecycler(data, activity);
            dataAdapter.setOnItemClickListener(new AdapterRecycler.OnItemClickListener() {
                @Override
                public void onClick(View a, int b) {
                    count_position_data = temp_list_nums.size();
                    // call this function itself
                    DiagnoseActivityHelper.this.onItemCardTouch(b);
                }
            });
            mListFirstPage.setAdapter(dataAdapter);
            mListFirstPage.setVisibility(View.VISIBLE);
            mAskLayout.setVisibility(View.GONE);
            if (onPenyakitHaveSelected != null)
                onPenyakitHaveSelected.onPilihCiriSection();
            System.gc();
        }
    }
    private void createAskLayout() {

        mAskLayout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.actdiagnose_dialog_ask, mRootView, false);
        btnBawah = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.adapter_btnbawahdiag, mAskLayout, false);
        RelativeLayout.LayoutParams paramsbtn = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        paramsbtn.addRule(RelativeLayout.ABOVE, R.id.adapter_id_imgdiag_layout);
        yes = (Button) btnBawah.findViewById(R.id.actimgdiagnose_buttonya);
        no = (Button) btnBawah.findViewById(R.id.actimgdiagnose_buttontidak);
        mDescCiri = (TextView) mAskLayout.findViewById(R.id.actdiagnose_id_contentforask);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiagnoseActivityHelper.this.onBtnClicked(ON_BTN_YES_CLICKED);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiagnoseActivityHelper.this.onBtnClicked(ON_BTN_NO_CLICKED);
            }
        });
        mDescCiri.setTextColor(Color.BLACK);
        mDescCiri.setTextSize(16.5f);
        mAskLayout.addView(btnBawah);
    }
    public interface OnPenyakitHaveSelected{
        void onPenyakitSelected(RecyclerView list, RelativeLayout mAskLayout, HashMap<Integer, ListNamaPenyakit> listNamaPenyakitHashMap, int keyId, double percentage);

        void onTanyaSection();

        void onPilihCiriSection();
    }

}
