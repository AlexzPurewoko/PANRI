package id.kenshiro.app.panri.opt.onsplash;

import android.content.res.AssetManager;

import com.mylexz.utils.MylexzActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import id.kenshiro.app.panri.SplashScreenActivity;
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
}
