package id.kenshiro.app.panri.opt.onsplash;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.mylexz.utils.MylexzActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import id.kenshiro.app.panri.BuildConfig;
import id.kenshiro.app.panri.SplashScreenActivity;
import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.UnzipFile;

public class ExtractAndConfigureData {
    public static void extractData(MylexzActivity act, File dirs, String fileName) throws IOException {
        AssetManager assetManager = act.getAssets();
        InputStream is = assetManager.open(fileName);
        // unzip a file
        UnzipFile unzipFile = new UnzipFile();
        unzipFile.unzip(is, dirs);
        is.close();
    }

    public static void configureData(@NotNull MylexzActivity activity) {
        SharedPreferences shareds = activity.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String db_version = null, iklan_version = null;
        try {
            db_version = getStringFromAssets(activity, "db_version");
            iklan_version = getStringFromAssets(activity, "iklan_version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (db_version != null && iklan_version != null) {
            if (!shareds.contains(KeyListClasses.KEY_DATA_LIBRARY_VERSION))
                shareds.edit().putString(KeyListClasses.KEY_DATA_LIBRARY_VERSION, db_version).commit();
            if (!shareds.contains(KeyListClasses.KEY_IKLAN_VERSION))
                shareds.edit().putString(KeyListClasses.KEY_IKLAN_VERSION, iklan_version).commit();
        }

        if (!shareds.contains(KeyListClasses.KEY_APP_VERSION))
            shareds.edit().putInt(KeyListClasses.KEY_APP_VERSION, BuildConfig.VERSION_CODE).commit();

        if (!shareds.contains(KeyListClasses.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER))
            shareds.edit().putInt(KeyListClasses.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER, 0).commit();
        if (!shareds.contains(KeyListClasses.KEY_AUTOCHECKUPDATE_APPDATA))
            shareds.edit().putBoolean(KeyListClasses.KEY_AUTOCHECKUPDATE_APPDATA, true).commit();
        // is avail new version
        if (!shareds.contains(KeyListClasses.KEY_VERSION_BOOL_NEW))
            shareds.edit().putInt(KeyListClasses.KEY_VERSION_BOOL_NEW, KeyListClasses.DB_IS_SAME_VERSION).commit();
        if (!shareds.contains(KeyListClasses.KEY_VERSION_ON_CLOUD))
            shareds.edit().putString(KeyListClasses.KEY_VERSION_ON_CLOUD, "undefined").commit();
    }

    @NotNull
    public static String getStringFromShareds(@NotNull Context c, @NotNull String key, @Nullable String defValue) throws IOException {
        return c.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(key, defValue);
    }

    @NotNull
    public static String getStringFromAssets(@NotNull Context c, @NotNull String filename) throws IOException {
        InputStream fileStream = c.getAssets().open(filename);
        byte[] buf = new byte[fileStream.available()];
        fileStream.read(buf);
        fileStream.close();
        return new String(buf);
    }

    public static Map<String, String> getHashMapResource(Context c, int hashMapResId) {
        Map<String, String> map = null;
        XmlResourceParser parser = c.getResources().getXml(hashMapResId);

        String key = null, value = null;

        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("utils", "Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("map")) {
                        boolean isLinked = parser.getAttributeBooleanValue(null, "linked", false);

                        map = isLinked ? new LinkedHashMap<String, String>() : new HashMap<String, String>();
                    } else if (parser.getName().equals("entry")) {
                        key = parser.getAttributeValue(null, "key");

                        if (null == key) {
                            parser.close();
                            return null;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equals("entry")) {
                        map.put(key, value);
                        key = null;
                        value = null;
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    if (null != key) {
                        value = parser.getText();
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    public static void configureStringInShareds(@NotNull SplashScreenActivity ctx, String keyData, String data) {
        ctx.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE).edit().putString(keyData, data).commit();
    }
}
