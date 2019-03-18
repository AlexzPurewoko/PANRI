package id.kenshiro.app.panri.opt.onmain;

import android.content.DialogInterface;
import android.os.AsyncTask;

import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.mylexz.utils.MylexzActivity;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import id.kenshiro.app.panri.helper.DialogShowHelper;
import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.CheckConnection;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;
import id.kenshiro.app.panri.opt.onsplash.ThreadPerformCallbacks;

public class CheckDBUpdateThread extends AsyncTask<Void, Integer, Integer> {
    private WeakReference<MylexzActivity> actReference;
    DialogShowHelper dialogLoadingHelper;
    FirebaseStorage firebaseStorage;
    String[] fetchedVerson = null;
    Boolean status = false;

    public CheckDBUpdateThread(MylexzActivity activity) {
        this.actReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialogLoadingHelper = new DialogShowHelper(actReference.get());
        dialogLoadingHelper.buildLoadingLayout();
        dialogLoadingHelper.showDialog();
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        boolean isConnected = false;
        try {
            isConnected = CheckConnection.isConnected(actReference.get(), 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            isConnected = false;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            String keyEx = "interrupt_doInBackground_CheckDBUpdate";
            String resE = String.format("Interrupt when sleep millis e -> %s", e.toString());
            LogIntoCrashlytics.logException(keyEx, resE, e);
            actReference.get().LOGE(keyEx, resE);
        }
        // jika tidak connect?
        if (!isConnected) {
            return KeyListClasses.UPDATE_DB_NOT_AVAILABLE_INTERNET_MISSING;
        }
        publishProgress(0);
        // get the firebase
        CheckDBCloudThread checkDBCloudThread = new CheckDBCloudThread(actReference.get(), new ThreadPerformCallbacks() {

            @Override
            public void onStarting(@NotNull Runnable runnedThread) {

            }

            @Override
            public void onCompleted(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {
                synchronized (status) {
                    fetchedVerson = (String[]) returnedCallbacks;
                    status = true;
                }
            }

            @Override
            public void onRunning(@NotNull Runnable runnedThread, @NotNull Object returnedCallbacks) {

            }

            @Override
            public void onCancelled(@NotNull Runnable runnedThread, @NotNull Throwable caused, @NotNull Object returnedCallbacks) {

            }
        });
        Thread t1 = new Thread(checkDBCloudThread, "checkCloud");
        t1.start();
        // wait for the threads
        while (!status) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                String keyEx = "interrupt_doInBackground_CheckDBUpdate_wait";
                String resE = String.format("Interrupt when wait thread for millis e -> %s", e.toString());
                LogIntoCrashlytics.logException(keyEx, resE, e);
                actReference.get().LOGE(keyEx, resE);
            }
        }

        int availVersion = Integer.parseInt(fetchedVerson[0]);
        int newVersion = Integer.parseInt(fetchedVerson[1]);
        if (availVersion < newVersion)
            return KeyListClasses.UPDATE_DB_IS_AVAILABLE;
        else
            return KeyListClasses.UPDATE_DB_NOT_AVAILABLE;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0] == 0) {
            FirebaseApp.initializeApp(actReference.get());
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        dialogLoadingHelper.stopDialog();
        Integer result = integer;
        switch (result) {
            case KeyListClasses.UPDATE_DB_NOT_AVAILABLE_INTERNET_MISSING:
                DialogOnMain.showUpdateDBDialog(actReference.get(), result, null);
                break;
            case KeyListClasses.UPDATE_DB_NOT_AVAILABLE:
                DialogOnMain.showUpdateDBDialog(actReference.get(), result, null);
                break;
            case KeyListClasses.UPDATE_DB_IS_AVAILABLE:
                DialogOnMain.showUpdateDBDialog(actReference.get(), result, new Object[]{
                        fetchedVerson[0],
                        fetchedVerson[1],
                        "Update",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new TaskDownloadDBUpdates(actReference.get(), fetchedVerson[1]).execute();
                            }
                        }
                });

        }
    }
}
