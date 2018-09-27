package id.kenshiro.app.panri.helper;

import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylexz.utils.MylexzActivity;

import id.kenshiro.app.panri.R;
import pl.droidsonroids.gif.GifImageView;

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
        int sizeDialog =
                Math.round(activity.getResources().getDimension(R.dimen.actsplash_dimen_loading_wh));
        LinearLayout resultElement = new LinearLayout(activity);
        LinearLayout.LayoutParams paramRoot = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        resultElement.setLayoutParams(paramRoot);
        resultElement.setPadding(10, 10, 10, 10);
        resultElement.setOrientation(LinearLayout.HORIZONTAL);

        GifImageView gifImg = new GifImageView(activity);
        gifImg.setLayoutParams(new LinearLayout.LayoutParams(
                sizeDialog,
                sizeDialog
        ));
        gifImg.setImageResource(R.drawable.loading);
        resultElement.addView(gifImg);

        TextView textView = new TextView(activity);
        textView.setText("Loading...");
        LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        //paramsText.leftMargin = 40;
        paramsText.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        textView.setLayoutParams(paramsText);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        resultElement.addView(textView);
        return resultElement;
    }
}
