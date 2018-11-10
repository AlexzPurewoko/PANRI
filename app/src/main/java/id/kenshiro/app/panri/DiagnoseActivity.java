package id.kenshiro.app.panri;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.mylexz.utils.DiskLruObjectCache;
import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SimpleDiskLruCache;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import id.kenshiro.app.panri.adapter.AdapterRecycler;
import id.kenshiro.app.panri.helper.DiagnoseActivityHelper;
import id.kenshiro.app.panri.helper.DialogShowHelper;
import id.kenshiro.app.panri.helper.ListCiriCiriPenyakit;
import id.kenshiro.app.panri.helper.ListNamaPenyakit;
import id.kenshiro.app.panri.helper.ShowPenyakitDiagnoseHelper;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;
import id.kenshiro.app.panri.important.KeyListClasses;
import id.kenshiro.app.panri.opt.LogIntoCrashlytics;
import io.fabric.sdk.android.Fabric;
import pl.droidsonroids.gif.GifImageView;

public class DiagnoseActivity extends MylexzActivity
{

	private Toolbar toolbar;
	private SQLiteDatabase sqlDB;
	private HashMap<Integer, ListNamaPenyakit> listNamaPenyakitHashMap;
	private HashMap<Integer, ListCiriCiriPenyakit> listCiriCiriPenyakitHashMap;
	private DiagnoseActivityHelper diagnoseActivityHelper;
	private ShowPenyakitDiagnoseHelper showPenyakitDiagnoseHelper;
	private SimpleDiskLruCache diskCache;
	private DialogShowHelper dialogShowHelper;
	Button mTextPetaniDesc;
	private boolean doubleBackToExitPressedOnce;
	private boolean isDiagnosting = true;

    private Handler handlerPetani;
    private GifImageView imgPetaniKedipView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final Fabric fabric = new Fabric.Builder(this)
				.kits(new Crashlytics())
				.debuggable(true)  // Enables Crashlytics debugger
				.build();
		Fabric.with(fabric);
		try {
			setContentView(R.layout.actdiagnose_maincontent);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setMyActionBar();
			setDB();
			dialogShowHelper = new DialogShowHelper(this);
			dialogShowHelper.buildLoadingLayout();
			dialogShowHelper.showDialog();
			PrepareHandlerTask prepareHandlerTask = new PrepareHandlerTask(this);
			Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(prepareHandlerTask, 50);
		} catch (Throwable e) {
			String keyEx = getClass().getName() + "_onCreate()";
			String resE = String.format("UnHandled Exception Occurs(Throwable) e -> %s", e.toString());
			LogIntoCrashlytics.logException(keyEx, resE, e);
			LOGE(keyEx, resE);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		System.gc();
		super.onPause();
	}

	private void loadLayoutAndShow() {
		mTextPetaniDesc = (Button) findViewById(R.id.actmain_id_section_petani_btn);
        imgPetaniKedipView = findViewById(R.id.actsplash_id_gifpetanikedip);
		mTextPetaniDesc.setTextColor(Color.BLACK);
		mTextPetaniDesc.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.NORMAL);
	    diagnoseActivityHelper = new DiagnoseActivityHelper(this, this.listNamaPenyakitHashMap, this.listCiriCiriPenyakitHashMap);
		showPenyakitDiagnoseHelper = new ShowPenyakitDiagnoseHelper(this, sqlDB, (RelativeLayout) this.findViewById(R.id.actdiagnose_id_layoutcontainer));
        diagnoseActivityHelper.setOnPenyakitHaveSelected(new DiagnoseActivityHelper.OnPenyakitHaveSelected() {
            @Override
            public void onPenyakitSelected(RecyclerView a, RelativeLayout b, HashMap<Integer, ListNamaPenyakit> c, int d, double e) {
                String penyakit = c.get(d).getName();
				//DiagnoseActivity.this.TOAST(Toast.LENGTH_LONG, "Padi Anda terdiagnosa penyakit %s sebesar %s.", penyakit, String.valueOf(e));
                //mTextPetaniDesc.setText(String.format(getString(R.string.actdiagnose_string_speechfarmer_3), penyakit, Math.round(e)));
                onButtonPetaniClicked(String.format(getString(R.string.actdiagnose_string_speechfarmer_3), penyakit, Math.round(e)));
                a.setVisibility(View.GONE);
                b.setVisibility(View.GONE);
				isDiagnosting = false;
                // switching into the next
                showPenyakitDiagnoseHelper.show(d);
            }

			@Override
			public void onTanyaSection() {
                //mTextPetaniDesc.setText(getString(R.string.actdiagnose_string_speechfarmer_2));
                onButtonPetaniClicked(getString(R.string.actdiagnose_string_speechfarmer_2));
			}

			@Override
			public void onPilihCiriSection() {
                //mTextPetaniDesc.setText(getString(R.string.actdiagnose_string_speechfarmer_1));
                onButtonPetaniClicked(getString(R.string.actdiagnose_string_speechfarmer_1));

			}
		});
		showPenyakitDiagnoseHelper.build();
        showPenyakitDiagnoseHelper.setOnHaveFinalRequests(new View.OnClickListener() {
            @Override
            public void onClick(View mContentView) {
                mContentView.setVisibility(View.GONE);
                // back into begin diagnostics
                diagnoseActivityHelper.showAgain();
            }
		});
	    diagnoseActivityHelper.buildAndShow();
        //mTextPetaniDesc.setText(getString(R.string.actdiagnose_string_speechfarmer_1));
        onButtonPetaniClicked(getString(R.string.actdiagnose_string_speechfarmer_1));
    }

    private void onButtonPetaniClicked(String text) {

        mTextPetaniDesc.setText(text);
        imgPetaniKedipView.setImageResource(R.drawable.petani_bicara);
        if (handlerPetani == null) {
            handlerPetani = new Handler();
            handlerPetani.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handlerPetani = null;
                    System.gc();
                    imgPetaniKedipView.setImageResource(R.drawable.petani_kedip);
                }
            }, 4000);
        }
        System.gc();
    }

	private void loadAllDataCiri() throws IOException, ClassNotFoundException {
		listCiriCiriPenyakitHashMap = (HashMap<Integer, ListCiriCiriPenyakit>) diskCache.getObjectWithDecode(KeyListClasses.LIST_PENYAKIT_CIRI_KEY_CACHE);
		diskCache.closeReading();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

	}

	private void loadListPenyakit() {
		listNamaPenyakitHashMap = new HashMap<Integer, ListNamaPenyakit>();
		Cursor curr = sqlDB.rawQuery("select nama from penyakit", null);
		curr.moveToFirst();
		while(!curr.isAfterLast()) {
			String nama = curr.getString(0);
			listNamaPenyakitHashMap.put(curr.getPosition()+1, new ListNamaPenyakit(nama, null));
			curr.moveToNext();
		}
		curr.close();
		System.gc();
		curr = sqlDB.rawQuery("select latin from penyakit", null);
		curr.moveToFirst();
		while(!curr.isAfterLast()) {
			String latin = curr.getString(0);
			listNamaPenyakitHashMap.get(curr.getPosition()+1).setLatin(latin);
			curr.moveToNext();
		}
		curr.close();
		System.gc();
	}
	private void setDB() {
		sqlDB = SQLiteDatabase.openOrCreateDatabase("/data/data/id.kenshiro.app.panri/files/database_penyakitpadi.db", null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isDiagnosting) {
				if (!diagnoseActivityHelper.setOnPushBackButtonPressed(true)) {
					if (doubleBackToExitPressedOnce) {
						SwitchIntoMainActivity.switchToMain(this);
						return true;
					}

					this.doubleBackToExitPressedOnce = true;
					TOAST(Toast.LENGTH_SHORT, "Klik lagi untuk kembali");
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							doubleBackToExitPressedOnce = false;
						}
					}, 2000);
					return false;
				}
			} else {
				if (showPenyakitDiagnoseHelper.getmContent2().getVisibility() == View.VISIBLE) {
					showPenyakitDiagnoseHelper.getmContent2().setVisibility(View.GONE);
					showPenyakitDiagnoseHelper.getmContent1().setVisibility(View.VISIBLE);
					showPenyakitDiagnoseHelper.mScrollContent.pageScroll(0);
					--showPenyakitDiagnoseHelper.countBtn;
					showPenyakitDiagnoseHelper.klikBawahText.setText(R.string.actdiagnose_string_klikcaramenanggulangi);
					return false;
				} else if (showPenyakitDiagnoseHelper.getmContent1().getVisibility() == View.VISIBLE) {
					showPenyakitDiagnoseHelper.getmContent1().setVisibility(View.GONE);
					showPenyakitDiagnoseHelper.getmContentView().setVisibility(View.GONE);
					mTextPetaniDesc.setOnClickListener(null);
                    //mTextPetaniDesc.setText(getString(R.string.actdiagnose_string_speechfarmer_1));
                    onButtonPetaniClicked(getString(R.string.actdiagnose_string_speechfarmer_1));
					diagnoseActivityHelper.setOnPushBackButtonPressed(true);
					isDiagnosting = true;
					return false;
				}
			}

			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		System.gc();
		sqlDB.close();
		try {
			diskCache.close();
			showPenyakitDiagnoseHelper.close();
		} catch (IOException e) {
			String keyEx = getClass().getName() + "_onDestroy()";
			String resE = String.format("Unable to close diskCache and showPenyakitDiagnoseHelper e -> %s", e.toString());
			LogIntoCrashlytics.logException(keyEx, resE, e);
			LOGE(keyEx, resE);
		}
		super.onDestroy();
	}

    private void setMyActionBar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            SpannableString strTitle = new SpannableString(getTitle());
            Typeface tTitle = Typeface.createFromAsset(getAssets(), "Gecko_PersonalUseOnly.ttf");
            strTitle.setSpan(new CustomTypefaceSpan(tTitle), 0, getTitle().length(), 0);
            toolbar.setTitle(strTitle);
        }
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
	}

	@Override
	public boolean onSupportNavigateUp() {
		SwitchIntoMainActivity.switchToMain(this);
		return true;
	}

	private static class PrepareHandlerTask implements Runnable {
		private WeakReference<DiagnoseActivity> diagnoseActivity;
		private WeakReference<SimpleDiskLruCache> diskLruCache;
		PrepareHandlerTask(DiagnoseActivity diagnoseActivity) throws IOException {
			this.diagnoseActivity = new WeakReference<>(diagnoseActivity);

			final File cacheFile = new File(this.diagnoseActivity.get().getCacheDir(), "cache");
			this.diagnoseActivity.get().diskCache = SimpleDiskLruCache.getsInstance(cacheFile);
			diskLruCache = new WeakReference<>(this.diagnoseActivity.get().diskCache);
		}
		@Override
		public void run() {
			diagnoseActivity.get().loadListPenyakit();
			try {
				diagnoseActivity.get().loadAllDataCiri();
			} catch (Exception e) {
				String keyEx = "PrepareHandleTask_run_gallery";
				String resE = String.format("Unable to execute diagnoseActivity.get().loadAllDataCiri(); e -> %s", e.toString());
				LogIntoCrashlytics.logException(keyEx, resE, e);
				diagnoseActivity.get().LOGE(keyEx, resE);
			}
			diagnoseActivity.get().loadLayoutAndShow();
			diagnoseActivity.get().dialogShowHelper.stopDialog();
		}
	}

}
