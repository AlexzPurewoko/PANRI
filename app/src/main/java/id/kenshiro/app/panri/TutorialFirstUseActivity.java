package id.kenshiro.app.panri;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mylexz.utils.MylexzActivity;

import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.page_fragment_first_usage.FragmentFirstUse1;
import id.kenshiro.app.panri.page_fragment_first_usage.FragmentFirstUse2;
import id.kenshiro.app.panri.page_fragment_first_usage.FragmentFirstUse3;
import id.kenshiro.app.panri.page_fragment_first_usage.FragmentFirstUse4;

public class TutorialFirstUseActivity extends MylexzActivity {
    private static final int ON_BTN_BACK_CLICKED = 0xaaf;
    private static final int ON_BTN_NEXT_CLICKED = 0xbeef;
    private static final Fragment[] LIST_ALL_FRAGMENT = {
            new FragmentFirstUse1(),
            new FragmentFirstUse2(),
            new FragmentFirstUse3(),
            new FragmentFirstUse4()
    };
    private static final int COUNT_ALL_FRAGMENTS = LIST_ALL_FRAGMENT.length;
    private static final int mDotCount = COUNT_ALL_FRAGMENTS;
    private static final int[] colorResListsBtn = {
            R.color.frag_color_green,
            R.color.frag_color_green,
            R.color.colorPrimary,
            R.color.colorPrimary
    }; // must be same size as COUNT_ALL_FRAGMENTS
    private static final int[] drawableSelectedPositionRes = {
            R.drawable.indicator_selected_item_oval_green,
            R.drawable.indicator_selected_item_oval_green,
            R.drawable.indicator_selected_item_oval,
            R.drawable.indicator_selected_item_oval
    }; // must be same size as COUNT_ALL_FRAGMENTS
    private static final int[] colorStatusBars = {
            R.color.frag_color_green_dark,
            R.color.frag_color_green_dark,
            R.color.colorPrimaryDark,
            R.color.colorPrimaryDark
    }; // must be same size as COUNT_ALL_FRAGMENTS
    FragmentTransaction fragmentManager;
    Button back, next;
    LinearLayout indicators;
    LinearLayout[] mDots;
    Bundle argsNext;
    private int current_position = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acttutor_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setLayout();
        setColorsLyt(colorResListsBtn[current_position]);
        replaceFragment();
    }

    private void setColorsLyt(int resColors) {
        int availcolors = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            availcolors = getColor(resColors);
        } else {
            availcolors = getResources().getColor(resColors);
        }
        ((RelativeLayout) (findViewById(R.id.acttutor_id_mainlayout))).setBackgroundColor(availcolors);
    }

    private void setArgs() {
        argsNext = getIntent().getExtras();
    }

    private void setLayout() {
        back = findViewById(R.id.acttutor_id_btnprev);
        next = findViewById(R.id.acttutor_id_btnnext);
        back.setVisibility(View.GONE);
        indicators = (LinearLayout) findViewById(R.id.acttutor_id_layoutIndicators);
        setIndicators();
        setBtnClickListener();
    }

    private void setBtnClickListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnClick(v, ON_BTN_BACK_CLICKED);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnClick(v, ON_BTN_NEXT_CLICKED);
            }
        });
    }

    private void onBtnClick(View v, int ClickedOn) {
        switch (ClickedOn) {
            case ON_BTN_BACK_CLICKED: {
                if (--current_position <= 0) {
                    next.setVisibility(View.VISIBLE);
                    back.setVisibility(View.GONE);
                    current_position = 0;
                } else if (current_position != 0) {
                    back.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                }
                replaceFragment();
            }
            break;
            case ON_BTN_NEXT_CLICKED: {
                if (++current_position == COUNT_ALL_FRAGMENTS - 1) {
                    next.setVisibility(View.GONE);
                    back.setVisibility(View.VISIBLE);
                } else if (current_position != 0) {
                    back.setVisibility(View.VISIBLE);
                }
                replaceFragment();

            }
        }
    }

    private void replaceFragment() {
        if (current_position == COUNT_ALL_FRAGMENTS - 1) {
            FragmentFirstUse4 f = (FragmentFirstUse4) LIST_ALL_FRAGMENT[current_position];
            f.setmListener(new FragmentFirstUse4.OnFragmentInteractionListener() {
                @Override
                public void onFinalRequests(Fragment results, View btn) {
                    onRequestsFinal(results, btn);
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getColor(colorStatusBars[current_position]));
            } else
                getWindow().setStatusBarColor(getResources().getColor(colorStatusBars[current_position]));
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.acttutor_id_placeholder, LIST_ALL_FRAGMENT[current_position]);
        transaction.commit();
        setColorsLyt(colorResListsBtn[current_position]);
        for (int x = 0; x < mDotCount; x++) {
            mDots[x].setBackgroundResource(R.drawable.indicator_unselected_item_oval);
        }
        mDots[current_position].setBackgroundResource(drawableSelectedPositionRes[current_position]);
        System.gc();
    }

    private void setIndicators() {
        mDots = new LinearLayout[mDotCount];
        int margin_LR = Math.round(getResources().getDimension(R.dimen.acttutor_indicator_marginlr));
        for (int x = 0; x < mDotCount; x++) {
            mDots[x] = new LinearLayout(this);
            mDots[x].setBackgroundResource(R.drawable.indicator_unselected_item_oval);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(margin_LR, 0, margin_LR, 4);
            mDots[x].setGravity(Gravity.RIGHT | Gravity.BOTTOM | Gravity.END);
            indicators.addView(mDots[x], params);
        }
        mDots[current_position].setBackgroundResource(drawableSelectedPositionRes[current_position]);
    }

    private void onRequestsFinal(Fragment results, View btn) {
        this.finish();
        Bundle to = getIntent().getExtras();
        SwitchIntoMainActivity.switchTo(this, MainActivity.class, to);
        System.gc();
    }
}
