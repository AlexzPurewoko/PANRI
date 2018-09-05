package id.kenshiro.app.panri;

import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylexz.utils.MylexzActivity;

public class SplashScreenActivity extends MylexzActivity {
    TextView judul;
    Button btnNext;
    private int marginBtnFactor = 8;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actsplash_main);
        judul = findViewById(R.id.actsplash_id_txtjudul);
        btnNext = findViewById(R.id.actsplash_id_btnlanjut);
        judul.setTypeface(Typeface.createFromAsset(getAssets(), "Gecko_PersonalUseOnly.ttf"), Typeface.BOLD);
        btnNext.setTypeface(Typeface.createFromAsset(getAssets(), "Gecko_PersonalUseOnly.ttf"), Typeface.BOLD);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);

        int newSizeBtn = p.x - ((p.x / marginBtnFactor) * 2);
        btnNext.setMinimumWidth(newSizeBtn);
    }
}