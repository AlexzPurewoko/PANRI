package id.kenshiro.app.panri.opt.onmain;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.mylexz.utils.MylexzActivity;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;
import id.kenshiro.app.panri.opt.UnzipFile;
import id.kenshiro.app.panri.opt.onsplash.ThreadPerformCallbacks;

public class TaskDownloadDBUpdates extends AsyncTask<Void, Object, Integer> {
    private WeakReference<MylexzActivity> activity;
    AlertDialog dialog;
    LinearLayout rootElement;
    TextView textProgress;
    ProgressBar progressBar;
    private final int fact_1 = 95;
    File panriTmp = null;
    private final int STATE_UPDATE_PROGRESS_DOWNLOADING_ZIP = 0x7;
    private final int STATE_EXTRACTING_ZIP = 0xa;
    private Boolean state_download_success = false;
    String newDBVersion;
    Object[] update = {
            STATE_UPDATE_PROGRESS_DOWNLOADING_ZIP,
            null
    };

    public TaskDownloadDBUpdates(MylexzActivity activity, String newDBVersion) {
        this.activity = new WeakReference<>(activity);
        this.newDBVersion = newDBVersion;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity.get());
        rootElement = buildAndConfigureRootelement();
        textProgress = rootElement.findViewById(R.id.loading_progress_text);
        progressBar = rootElement.findViewById(R.id.loading_progress_bar);
        textProgress.setText("Mempersiapkan...");
        progressBar.setMax(100);
        progressBar.setProgress(0);
        builder.setView(rootElement);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        UpdateDBThread updateDBThread = new UpdateDBThread(activity.get(), new ThreadPerformCallbacks() {
            @Override
            public void onStarting(@NotNull Runnable runnedThread) {

            }

            @Override
            public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                update[0] = STATE_EXTRACTING_ZIP;
                state_download_success = true;
                panriTmp = (File) returnedCallbacks;
            }

            @Override
            public void onRunning(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                synchronized (update) {
                    double percent = (double) returnedCallbacks;
                    update[1] = percent;
                }
            }

            @Override
            public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks) {
                synchronized (state_download_success) {
                    state_download_success = true;
                }
            }
        });
        Thread t1 = new Thread(updateDBThread);
        t1.start();

        while (!state_download_success) {
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                String keyEx = "checkState_TaskDownloadDBUpdates";
                String resE = String.format("interrupt when pause for check state for thread e -> %s", e.toString());
                LogIntoCrashlytics.logException(keyEx, resE, e);
                activity.get().LOGE(keyEx, resE);
            }
            publishProgress(update);
        }
        update[0] = STATE_EXTRACTING_ZIP;
        update[1] = (double) 99;
        publishProgress(update);
        File disk = activity.get().getFilesDir();

        // clean the files before update
        try {
            //FileUtils.deleteDirectory(disk);
            // directory data for app
            FileUtils.deleteDirectory(new File(disk, "data"));
            FileUtils.deleteDirectory(new File(disk, "data_hama_html"));
            FileUtils.deleteQuietly(new File(disk, "database_penyakitpadi.db"));
        } catch (IOException e) {
            String keyEx = "fileUtils_deleteDirectory_TaskDownloadDBUpdates";
            String resE = String.format("Cannot delete the selected directory e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            activity.get().LOGE(keyEx, resE);
        }
        disk.mkdirs();
        // extract a file
        try {
            FileInputStream fisZip = new FileInputStream(panriTmp);
            UnzipFile unzipFile = new UnzipFile();
            unzipFile.unzip(fisZip, disk);
            fisZip.close();

        } catch (IOException e) {
            String keyEx = "UnzipFile_TaskDownloadDBUpdates";
            String resE = String.format("Cannot unzip the selected zip = {%s} e -> %s", panriTmp.getAbsolutePath(), e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            activity.get().LOGE(keyEx, resE);
        }
        update[1] = (double) 100;
        File fileCache = new File(activity.get().getCacheDir(), "cache");
        //clean the cache
        try {
            FileUtils.deleteDirectory(fileCache);
        } catch (IOException e) {
            String keyEx = "fileUtils_cleanCache_TaskDownloadDBUpdates";
            String resE = String.format("Cannot clean the cache e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            activity.get().LOGE(keyEx, resE);
        }
        fileCache.mkdirs();
        SharedPreferences sharedPreferences = activity.get().getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(KeyListClasses.KEY_DATA_LIBRARY_VERSION, newDBVersion).commit();
        sharedPreferences.edit().putString(KeyListClasses.KEY_VERSION_ON_CLOUD, newDBVersion).commit();
        sharedPreferences.edit().putInt(KeyListClasses.KEY_VERSION_BOOL_NEW, KeyListClasses.DB_IS_SAME_VERSION).commit();
        return 1;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        Object[] vals = values;
        switch ((int) vals[0]) {
            case STATE_UPDATE_PROGRESS_DOWNLOADING_ZIP: {
                textProgress.setText("Downloading... (" + vals[1] + "%)");
                progressBar.setProgress((int) ((double) (vals[1])));
            }
            break;
            case STATE_EXTRACTING_ZIP: {
                textProgress.setText("Extracting... (" + vals[1] + "%)");
                progressBar.setProgress((int) ((double) (vals[1])));
            }
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        dialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity.get());
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Restart");
        builder.setCancelable(false);
        builder.setMessage("Perubahan telah disimpan, Klik tombol 'restart' untuk me-restart aplikasi untuk mengaplikasikan perubahan yang telah dilakukan");
        builder.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent i = activity.get().getPackageManager()
                        .getLaunchIntentForPackage(activity.get().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.get().startActivity(i);
            }
        });
        builder.show();
    }

    private LinearLayout buildAndConfigureRootelement() {
        return (LinearLayout) activity.get().getLayoutInflater().inflate(R.layout.dialog_layout_progress, null);
    }

    private static class UpdateDBThread implements Runnable {
        ThreadPerformCallbacks threadPerformCallbacks = null;
        MylexzActivity activity;
        private FirebaseStorage firebaseStorage;

        UpdateDBThread(MylexzActivity activity, ThreadPerformCallbacks threadPerformCallbacks) {
            this.activity = activity;
            this.threadPerformCallbacks = threadPerformCallbacks;
        }

        @Override
        public void run() {
            if (threadPerformCallbacks != null)
                threadPerformCallbacks.onStarting(this);
            firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference pathReference = storageReference.child("data_panri.zip");
            try {
                final File panriTmp = File.createTempFile("data_panri", "zip");
                pathReference.getFile(panriTmp)
                        .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                long totalByteCount = taskSnapshot.getTotalByteCount();
                                long receivedByteCount = taskSnapshot.getBytesTransferred();
                                double percent = receivedByteCount * 100 / totalByteCount;
                                if (threadPerformCallbacks != null)
                                    threadPerformCallbacks.onRunning(UpdateDBThread.this, percent);
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                if (task.isComplete())
                                    if (threadPerformCallbacks != null)
                                        threadPerformCallbacks.onCompleted(UpdateDBThread.this, panriTmp);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (threadPerformCallbacks != null)
                                    threadPerformCallbacks.onCancelled(UpdateDBThread.this, e, null);
                            }
                        });
            } catch (IOException e) {
                String keyEx = "run_UpdateDBThread";
                String resE = String.format("Exception occured when executing the whole of run() method e -> %s", e.toString());
                LogIntoCrashlytics.logException(keyEx, resE, e);
                activity.LOGE(keyEx, resE);
            }
        }
    }
}
