package id.kenshiro.app.panri.opt.onsplash;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import com.mylexz.utils.MylexzActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public static void configureData(MylexzActivity activity) {
        SharedPreferences shareds = activity.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (!shareds.contains(KeyListClasses.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER))
            shareds.edit().putInt(KeyListClasses.KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER, 0).commit();
        if (!shareds.contains(KeyListClasses.KEY_AUTOCHECKUPDATE_APPDATA))
            shareds.edit().putBoolean(KeyListClasses.KEY_AUTOCHECKUPDATE_APPDATA, true).commit();
    }
}
