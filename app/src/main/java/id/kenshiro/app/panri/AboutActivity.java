package id.kenshiro.app.panri;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;
import io.fabric.sdk.android.Fabric;

public class AboutActivity extends MylexzActivity {
    TextView judul, version;
    LinearLayout about_people_container;
    WebView content_special_thanks;
    WebView content_third_party;
    private Toolbar toolbar;
    private static final String[] listsThanksTo = {
            "SMKN 1 Giritontro",
            "Lian Ratna Sari, S.Pd selaku pembimbing",
            "Pak Sitam selaku narasumber",
            "Pak Wahyudi selaku narasumber dari validasi data penyakit",
            "BPP (Balai Penyuluhan Pertanian) 'Harjaning Tani' Giriwoyo",
            "Android Studio",
            "<a href=\"https://stackoverflow.com\">StackOverflow</a>"
    };

    private static final String[] listThirdPartyLib = {
            "<a href=\"https://github.com/koral--/android-gif-drawable\">GifImageView (pl.droidsonroids.gif:android-gif-drawable)</a>",
            "<a href=\"https://github.com/AlexzPurewoko/MyLEXZ-Library\">MyLEXZ Library</a>",
            "<a href=\"https://commons.apache.org/codec\">Apache Commons Codec</a>",
            "<a href=\"https://firebase.google.com\">Google Firebase</a>",
            "<a href=\"https://fabric.io\">Fabric Crashlytics</a>",
            "<a href=\"https://commons.apache.org/proper/commons-io\">Apache Commons IO</a>",
            "<a href=\"https://github.com/felipecsl/GifImageView\">GifImageView felpecsl(com.felipecsl:gifimageview:2.2.0)</a>"
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)  // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);
        // if any exception occurs
        try {
            setContentView(R.layout.actabout_main);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setMyActionBar();
            prepareLayout();
            prepareAboutPeople();
            setsWebContent();
            setsThirdParty();
        } catch (Throwable e) {
            String keyEx = getClass().getName() + "_onCreate()";
            String resE = String.format("UnHandled Exception Occurs(Throwable) e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            LOGE(keyEx, resE);
        }
    }

    private void setsThirdParty() {
        StringBuffer strHTML = new StringBuffer();
        strHTML.append("<ul> <br />");
        for (String x : listThirdPartyLib) {
            strHTML.append("<li>");
            strHTML.append(x);
            strHTML.append("</li> <br />");
        }
        strHTML.append("</ul>");
        content_third_party.loadData(strHTML.toString(), "text/html", "utf-8");
    }

    private void setsWebContent() {
        StringBuffer strHTML = new StringBuffer();
        strHTML.append("<ul> <br />");
        for (String x : listsThanksTo) {
            strHTML.append("<li>");
            strHTML.append(x);
            strHTML.append("</li> <br />");
        }
        strHTML.append("</ul>");
        content_special_thanks.loadData(strHTML.toString(), "text/html", "utf-8");
    }

    private void prepareAboutPeople() {
        int[] imageResLocation = {
                R.drawable.bu_purwari_puji,
                R.drawable.roman_av,
                R.drawable.bagus_cahyono,
                R.drawable.alexpw,
                R.drawable.anggi_mw,
                R.drawable.wahyu_catur,
        };
        String[] names = {
                "Purwari Puji Rahayu, S.Pd",
                "Roman Aqviriyoso",
                "Bagus Cahyono",
                "Alexzander Purwoko W",
                "Anggi Mundita Wangi",
                "Wahyu Catur"
        };
        String[] jobs = {
                "Penanggung jawab",
                "Produser\nDesainer\nArtist 2D",
                "Programmer\nContent Writer",
                "Software Engineer\nApp Developer\nProgrammer",
                "Content Writer",
                "Content Writer\nArtist 2D"
        };
        for (int x = 0; x < jobs.length; x++) {
            CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.cardview_adapter, about_people_container, false);
            cardView.setContentPadding(5, 5, 5, 5);
            LinearLayout linearInnerContent = buildContentPeople(cardView, imageResLocation[x], names[x], jobs[x]);
            cardView.addView(linearInnerContent);
            about_people_container.addView(cardView);
        }
    }

    private LinearLayout buildContentPeople(CardView cardView, int i, String name, String job) {
        LinearLayout linearInner = (LinearLayout) getLayoutInflater().inflate(R.layout.adapter_aboutdev, cardView, false);
        ImageView imagePeople = linearInner.findViewById(R.id.adapter_aboutdev_id_imgpeople);
        TextView jobsText = linearInner.findViewById(R.id.adapter_aboutdev_id_jobs);
        TextView namesText = linearInner.findViewById(R.id.adapter_aboutdev_id_name);
        imagePeople.setImageResource(i);
        jobsText.setText(job);
        namesText.setAllCaps(true);
        namesText.setText(name);
        return linearInner;
    }


    private void prepareLayout() {
        judul = findViewById(R.id.actabout_id_txtjudul);
        version = findViewById(R.id.actabout_id_txtversion);
        about_people_container = findViewById(R.id.actabout_id_container_add);
        content_special_thanks = findViewById(R.id.actabout_id_content_special_thanks);
        content_third_party = findViewById(R.id.actabout_id_content_third_party);
        setTxtV();
    }

    private void setTxtV() {
        judul.setTypeface(Typeface.createFromAsset(getAssets(), "Gecko_PersonalUseOnly.ttf"), Typeface.BOLD);
        String versionId = BuildConfig.VERSION_NAME;
        version.append(versionId);
        switch (versionId.charAt(3) - '0') {
            case 0:
                version.append(" (alpha)");
                break;
            case 1:
                version.append(" (beta)");
                break;
            case 2:
                version.append(" (stable)");
                break;
        }
    }

    private void setMyActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            SpannableString strTitle = new SpannableString(getTitle());
            Typeface tTitle = Typeface.createFromAsset(getAssets(), "Gecko_PersonalUseOnly.ttf");
            strTitle.setSpan(new CustomTypefaceSpan(tTitle), 0, getTitle().length(), 0);
            toolbar.setTitle(strTitle);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public boolean onSupportNavigateUp() {
        SwitchIntoMainActivity.switchToMain(this);
        return true;
    }

    @Override
    public void onBackPressed() {
        SwitchIntoMainActivity.switchToMain(this);
    }
}
