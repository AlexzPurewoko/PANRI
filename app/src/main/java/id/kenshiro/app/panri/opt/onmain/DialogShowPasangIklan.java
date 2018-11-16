package id.kenshiro.app.panri.opt.onmain;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.TextSpanFormat;

import java.io.IOException;
import java.io.InputStream;

import id.kenshiro.app.panri.R;

public class DialogShowPasangIklan {
    MylexzActivity activity;
    AlertDialog dialog;
    TextView textView;

    public DialogShowPasangIklan(MylexzActivity activity) {
        this.activity = activity;
    }

    public void build(final Uri uriData) {
        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        builder.setTitle("Pasang Iklan");
        builder.setIcon(R.mipmap.ic_launcher);
        ScrollView base = buildContainerLayout();
        int padding = Math.round(activity.getResources().getDimension(R.dimen.dialogshdup_margin_all));
        textView.setPadding(padding, padding, padding, padding);
        builder.setView(base);
        builder.setPositiveButton("Hubungi Kami", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(uriData);
                PackageManager pm = activity.getPackageManager();
                PackageInfo info = null;
                try {
                    info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    info = null;
                }
                if (info != null)
                    i.setPackage("com.whatsapp");
                activity.startActivity(Intent.createChooser(i, "Hubungi Dengan"));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        dialog = builder.create();
    }

    private ScrollView buildContainerLayout() {
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setLayoutParams(new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        LinearLayout layout = new LinearLayout(activity);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layout.setOrientation(LinearLayout.VERTICAL);
        textView = new TextView(activity);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        textView.setTextColor(Color.BLACK);
        layout.addView(textView);
        scrollView.addView(layout);
        return scrollView;
    }

    public void load(String pathAssets) throws IOException {
        InputStream inputStream = activity.getAssets().open(pathAssets);
        byte[] b = new byte[inputStream.available()];
        inputStream.read(b);
        inputStream.close();
        String out = new String(b);
        textView.setText(TextSpanFormat.convertStrToSpan(activity, out, 0));
        System.gc();
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.cancel();
    }
}
