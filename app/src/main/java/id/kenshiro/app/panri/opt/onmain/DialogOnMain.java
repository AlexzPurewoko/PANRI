package id.kenshiro.app.panri.opt.onmain;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.TextSpanFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import id.kenshiro.app.panri.MainActivity;
import id.kenshiro.app.panri.R;
import id.kenshiro.app.panri.important.KeyListClasses;

public class DialogOnMain {

    public static void showExitDialog(final MylexzActivity activity) {
        AlertDialog.Builder build = new AlertDialog.Builder(activity);
        LinearLayout layoutDialog = (LinearLayout) LinearLayout.inflate(activity, R.layout.actmain_dialog_on_exit, null);
        TextView text = (TextView) layoutDialog.findViewById(R.id.actmain_id_dialogexit_content);
        Button btnyes = (Button) layoutDialog.findViewById(R.id.actmain_id_dialogexit_btnyes);
        Button btnno = (Button) layoutDialog.findViewById(R.id.actmain_id_dialogexit_btnno);
        text.setTextColor(Color.BLACK);
        text.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Comic_Sans_MS3.ttf"), Typeface.BOLD);
        text.setText(R.string.actmain_string_dialogexit_desc);
        btnyes.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Comic_Sans_MS3.ttf"), Typeface.BOLD);
        btnyes.setTextColor(Color.WHITE);
        btnyes.setText(R.string.actmain_string_dialogexit_btnyes);
        btnno.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Comic_Sans_MS3.ttf"), Typeface.BOLD);
        btnno.setTextColor(Color.WHITE);
        btnno.setText(R.string.actmain_string_dialogexit_btnno);
        build.setView(layoutDialog);
        final AlertDialog mAlert = build.create();
        btnyes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                mAlert.cancel();
                activity.finish();
                Handler kill = new Handler();
                kill.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.killProcess(Process.myPid());
                    }
                }, 2000);

            }


        });
        btnno.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                mAlert.cancel();
            }


        });
        mAlert.show();
    }
    public static void showDialogWhatsNew(final MylexzActivity activity, DialogInterface.OnClickListener onbtnOk) {
        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        builder.setTitle("Apa yang baru?");
        builder.setIcon(R.mipmap.ic_launcher);
        TextView textView = new TextView(activity);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        textView.setTextColor(Color.BLACK);
        int padding = Math.round(activity.getResources().getDimension(R.dimen.dialogshdup_margin_all));
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(TextSpanFormat.convertStrToSpan(activity, readFromAsset(activity, "whats_new"), 0));
        builder.setView(textView);
        builder.setPositiveButton("Okay!", onbtnOk);
        builder.show();
    }

    private static String readFromAsset(MylexzActivity activity, String pathAssets) {
        try {
            InputStream is = activity.getAssets().open(pathAssets);
            byte[] b = new byte[is.available()];
            is.read(b);
            is.close();
            return new String(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * showUpdateNotAvailable()
     * @param activity
     * @param conditionUpdate
     * @param updateValueIfExists -> {
     *      0 -> db Original Version
     *      1 -> latest version
     *      2 -> message OnUPdateListener
     *      3 -> onHandleClickListener (positive)
     *
     * }
     *
     */
    public static void showUpdateDBDialog(final MylexzActivity activity, int conditionUpdate, Object updateValueIfExists) {
        AlertDialog.Builder build = new AlertDialog.Builder(activity);
        build.setTitle("Update Data Aplikasi!");
        build.setIcon(R.mipmap.ic_launcher);
        switch (conditionUpdate) {
            case KeyListClasses.UPDATE_DB_NOT_AVAILABLE_INTERNET_MISSING: {
                build.setMessage("Update Databases tidak tersedia karena koneksi internet tidak aktif. Silahkan aktifkan koneksi internet anda terlebih dahulu.");
            }
            break;
            case KeyListClasses.UPDATE_DB_NOT_AVAILABLE: {
                build.setMessage("Database anda sudah versi terbaru.");
            }
            break;
            case KeyListClasses.UPDATE_DB_IS_AVAILABLE: {
                // 0 -> original
                // 1 latest release
                Object[] version = (Object[]) updateValueIfExists;
                build.setMessage("Update Data Aplikasi tersedia!\n\nVersi Data Aplikasi : " + version[0] + "\nVersi Data App terkini : " + version[1] + "\n\nApakah anda ingin memperbaharui data aplikasi?");
            }
            break;
            default:
                return;
        }
        if (conditionUpdate == KeyListClasses.UPDATE_DB_NOT_AVAILABLE_INTERNET_MISSING || conditionUpdate == KeyListClasses.UPDATE_DB_NOT_AVAILABLE) {
            build.setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else if (conditionUpdate == KeyListClasses.UPDATE_DB_IS_AVAILABLE) {
            Object[] v = (Object[]) updateValueIfExists;
            build.setPositiveButton((String) v[2], (DialogInterface.OnClickListener) v[3]);
            build.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        build.show();
    }

    public static void showUpdateDBDialogMain(final MylexzActivity activity, int conditionUpdate, Object updateValueIfExists) {
        AlertDialog.Builder build = new AlertDialog.Builder(activity);
        build.setTitle("Update Data Aplikasi!");
        build.setIcon(R.mipmap.ic_launcher);
        LinearLayout linearLayout = (LinearLayout) LinearLayout.inflate(activity, R.layout.dialog_show_dbupdate, null);
        TextView textView = linearLayout.findViewById(R.id.dialogshdup_text);
        final CheckBox checkBox = linearLayout.findViewById(R.id.dialogshdup_check);
        checkBox.setChecked(false);
        checkBox.setOnClickListener(new View.OnClickListener() {
            boolean isChecked = checkBox.isChecked();

            @Override
            public void onClick(View v) {
                if (isChecked) {
                    isChecked = true;
                } else {
                    isChecked = false;
                }
                SharedPreferences sharedPreferences = activity.getSharedPreferences(KeyListClasses.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(KeyListClasses.KEY_AUTOCHECKUPDATE_APPDATA, isChecked).commit();
            }
        });
        Object[] version = (Object[]) updateValueIfExists;
        textView.setText("Update Data Aplikasi tersedia!\n\nVersi Data Aplikasi : " + version[0] + "\nVersi Data App terkini : " + version[1] + "\n\nApakah anda ingin memperbaharui data aplikasi?");
        build.setView(linearLayout);

        if (conditionUpdate == KeyListClasses.UPDATE_DB_NOT_AVAILABLE_INTERNET_MISSING || conditionUpdate == KeyListClasses.UPDATE_DB_NOT_AVAILABLE) {
            build.setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else if (conditionUpdate == KeyListClasses.UPDATE_DB_IS_AVAILABLE) {
            Object[] v = (Object[]) updateValueIfExists;
            build.setPositiveButton((String) v[2], (DialogInterface.OnClickListener) v[3]);
            build.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        build.show();
    }

    public static void showReportDialog(final MylexzActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.drawable.baseline_bug_report_black);
        builder.setCancelable(false);
        builder.setTitle("Kirim Masukan");
        LinearLayout views = (LinearLayout) LinearLayout.inflate(activity, R.layout.dialog_send_input_report, null);
        final EditText name = views.findViewById(R.id.name_text),
                title = views.findViewById(R.id.judul_report),
                desc = views.findViewById(R.id.description_report);
        Button yes = views.findViewById(R.id.actmain_id_dialogexit_btnyes),
                no = views.findViewById(R.id.actmain_id_dialogexit_btnno);
        TextView textView = views.findViewById(R.id.description_txtbawahreport);
        textView.setText(TextSpanFormat.convertStrToSpan(activity, "__*) Anda harus mengaktifkan koneksi internet untuk mengirim masukan!__", 0));
        builder.setView(views);
        final AlertDialog dialog = builder.create();
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recNames = name.getText().toString();
                String recTitle = title.getText().toString();
                String recDesc = desc.getText().toString();
                if (recNames == null || recNames.length() < 1) {
                    name.setError("field Nama belum diisi!");

                } else if (recTitle == null || recTitle.length() < 1) {
                    title.setError("field Judul belum diisi!");
                } else if (recDesc == null || recDesc.length() < 1) {
                    desc.setError("field Deskripsi belum diisi!");
                } else {
                    Answers.getInstance().logCustom(new CustomEvent("Data Masukan User")
                            .putCustomAttribute("User Names", recNames)
                            .putCustomAttribute("Judul", recTitle)
                            .putCustomAttribute("Deskripsi", recDesc)
                    );
                    dialog.dismiss();
                    new AlertDialog.Builder(activity)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("Restart Aplikasi")
                            .setMessage("Restart aplikasi anda untuk mengirim masukan yang anda berikan.")
                            .setCancelable(false)
                            .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent i = activity.getPackageManager()
                                            .getLaunchIntentForPackage(activity.getPackageName());
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    activity.startActivity(i);
                                }
                            })
                            .show();
                }

            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showOnClickedIklanViews(final MylexzActivity activity, final String url, final String info_produk, final int methodPost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Iklan");
        builder.setIcon(R.mipmap.ic_launcher);
        TextView textView = new TextView(activity);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        textView.setTextColor(Color.BLACK);
        int padding = Math.round(activity.getResources().getDimension(R.dimen.dialogshdup_margin_all));
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(TextSpanFormat.convertStrToSpan(activity, info_produk, 0));
        builder.setView(textView);
        builder.setPositiveButton("Hubungi Kami", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                switch (methodPost) {
                    case KeyListClasses.CALL_BY_WHATSAPP: {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
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
                        activity.startActivity(Intent.createChooser(i, "Hubungi Pengiklan"));
                    }
                    break;
                    case KeyListClasses.CALL_BY_WEB: {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        activity.startActivity(Intent.createChooser(i, "Hubungi Pengiklan"));
                    }
                    break;
                    case KeyListClasses.CALL_BY_TELEPHONE: {
                        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", url, null));
                        activity.startActivity(Intent.createChooser(i, "Hubungi Pengiklan"));
                    }
                    break;
                    //case KeyListClasses.CALL_BY_EMAIL:
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
