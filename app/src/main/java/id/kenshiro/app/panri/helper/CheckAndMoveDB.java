package id.kenshiro.app.panri.helper;

import com.mylexz.utils.MylexzActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CheckAndMoveDB {
    MylexzActivity activity;
    // we force import database from assets path
    String dbName;
    public CheckAndMoveDB(MylexzActivity activity, String dbName){
        this.activity = activity;
        this.dbName = dbName;
    }
    public void checkAndMove() throws IOException {
        File dbDirPath = new File(activity.getApplicationInfo().dataDir+"/databases/");
        if(!dbDirPath.exists())
            dbDirPath.mkdir();
        // check the database on @link dbDirPath whether is any or not
        File dbPath = new File(dbDirPath, dbName);
        if(dbPath.exists())return;
        moveDB(dbPath);
    }
    public void upgradeDB()throws IOException {
        File dbDirPath = new File("/data/data/"+activity.getPackageName()+"/databases");
        dbDirPath.mkdirs();
        // check the database on @link dbDirPath whether is any or not
        File dbPath = new File(dbDirPath, dbName);
        moveDB(dbPath);
    }
    private void moveDB(File dbPath)throws IOException{
        InputStream inputStream = activity.getAssets().open(dbName);
        // copy into databases folder
        FileOutputStream fosDbOut = new FileOutputStream(dbPath);
        byte[] buf = new byte[inputStream.available()];
        inputStream.read(buf);
        fosDbOut.write(buf);
        inputStream.close();
        fosDbOut.close();
    }
}
