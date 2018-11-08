package id.kenshiro.app.panri;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.widget.Toast;

import com.mylexz.utils.text.style.CustomTypefaceSpan;

import id.kenshiro.app.panri.important.KeyListClasses;

public class PanriSettingActivity extends AppCompatActivity {
    Toolbar toolbar;
    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actpreference_panrisettings);
        setMyActionBar();
        pref = getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        getFragmentManager().beginTransaction().replace(R.id.actsettings_id_placeholder, new PanriFragment()).commit();
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

    public static class PanriFragment extends PreferenceFragment {
        CheckBoxPreference preference;
        boolean checkedPref;
        SharedPreferences sharedPreferences;

        public PanriFragment() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.panri_settings);
            getPreferenceManager().setSharedPreferencesName(KeyListClasses.SHARED_PREF_NAME);
            sharedPreferences = getPreferenceManager().getSharedPreferences();
            preference = (CheckBoxPreference) findPreference("autoupdate");
            preference.setChecked(sharedPreferences.getBoolean(KeyListClasses.KEY_AUTOCHECKUPDATE_APPDATA, true));
            checkedPref = preference.isChecked();

            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (checkedPref) {
                        checkedPref = false;
                        preference.setDefaultValue(false);
                    } else {
                        checkedPref = true;
                        preference.setDefaultValue(true);
                    }
                    Log.e("checkbox", "setval to = " + checkedPref);
                    sharedPreferences.edit().putBoolean(KeyListClasses.KEY_AUTOCHECKUPDATE_APPDATA, checkedPref).commit();
                    return false;
                }
            });
        }
    }
}
