package id.kenshiro.app.panri.helper;

import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;

import com.mylexz.utils.MylexzActivity;

import id.kenshiro.app.panri.R;

public class DialogShowHelper {
    AlertDialog dialog;
    final MylexzActivity activity;
    public DialogShowHelper(MylexzActivity activity){
        this.activity = activity;
    }

    public void stopDialog(){
        dialog.cancel();
    }

    public void showDialog(){
        dialog.show();
    }

    public void buildLoadingLayout() {
        if(dialog == null) {
            LinearLayout rootElement = buildAndConfigureRootelement();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(rootElement);
            builder.setCancelable(false);
            dialog = builder.create();
        }
    }

    private LinearLayout buildAndConfigureRootelement() {
        return (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_layout, null);
    }
}
