package id.kenshiro.app.panri;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import id.kenshiro.app.panri.adapter.AdapterRecycler;
import id.kenshiro.app.panri.helper.DiagnoseActivityHelper;
import id.kenshiro.app.panri.helper.ListCiriCiriPenyakit;
import id.kenshiro.app.panri.helper.ListNamaPenyakit;
import id.kenshiro.app.panri.helper.ShowPenyakitDiagnoseHelper;
import id.kenshiro.app.panri.helper.SwitchIntoMainActivity;

public class DiagnoseActivity extends MylexzActivity
{

	private Toolbar toolbar;
	private List<AdapterRecycler.DataPerItems> data;
	private RecyclerView mListView;
	private SQLiteDatabase sqlDB;
	private HashMap<Integer, ListNamaPenyakit> listNamaPenyakitHashMap;
	private HashMap<Integer, ListCiriCiriPenyakit> listCiriCiriPenyakitHashMap;
	private DiagnoseActivityHelper diagnoseActivityHelper;
	private ShowPenyakitDiagnoseHelper showPenyakitDiagnoseHelper;
	private ImgPetaniKedip imgPetaniKedip;
	ImageView imgPetani;
	Button mTextPetaniDesc;
	private boolean doubleBackToExitPressedOnce;
	private boolean isDiagnosting = true;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actdiagnose_maincontent);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setMyActionBar();
		setDB();
		loadListPenyakit();
		loadAllDataCiri();
		loadLayoutAndShow();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setTask();
	}

	private void setTask() {
		imgPetaniKedip = new ImgPetaniKedip();
		imgPetaniKedip.execute();
	}

	private void stopTask() {
		if (imgPetaniKedip != null) {
			imgPetaniKedip.cancel(true);
			imgPetaniKedip = null;
		}
	}

	@Override
	protected void onPause() {
		stopTask();
		System.gc();
		super.onPause();
	}

	private void loadLayoutAndShow() {
		imgPetani = (ImageView) findViewById(R.id.actmain_id_section_petani_img);
		mTextPetaniDesc = (Button) findViewById(R.id.actmain_id_section_petani_btn);
		mTextPetaniDesc.setTextColor(Color.BLACK);
		mTextPetaniDesc.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.NORMAL);
		imgPetani.setImageResource(R.drawable.petani);
		imgPetani.setImageLevel(4);
	    diagnoseActivityHelper = new DiagnoseActivityHelper(this, this.listNamaPenyakitHashMap, this.listCiriCiriPenyakitHashMap);
		showPenyakitDiagnoseHelper = new ShowPenyakitDiagnoseHelper(this, sqlDB, (RelativeLayout) this.findViewById(R.id.actdiagnose_id_layoutcontainer));
        diagnoseActivityHelper.setOnPenyakitHaveSelected(new DiagnoseActivityHelper.OnPenyakitHaveSelected() {
            @Override
            public void onPenyakitSelected(RecyclerView a, RelativeLayout b, HashMap<Integer, ListNamaPenyakit> c, int d, double e) {
                String penyakit = c.get(d).getName();
				//DiagnoseActivity.this.TOAST(Toast.LENGTH_LONG, "Padi Anda terdiagnosa penyakit %s sebesar %s.", penyakit, String.valueOf(e));
				mTextPetaniDesc.setText(String.format(getString(R.string.actdiagnose_string_speechfarmer_3), penyakit, Math.round(e)));
                a.setVisibility(View.GONE);
                b.setVisibility(View.GONE);
				isDiagnosting = false;
                // switching into the next
                showPenyakitDiagnoseHelper.show(d);
            }

			@Override
			public void onTanyaSection() {
				mTextPetaniDesc.setText(getString(R.string.actdiagnose_string_speechfarmer_2));
			}

			@Override
			public void onPilihCiriSection() {
				mTextPetaniDesc.setText(getString(R.string.actdiagnose_string_speechfarmer_1));

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
		mTextPetaniDesc.setText(getString(R.string.actdiagnose_string_speechfarmer_1));
	}
	private void loadAllDataCiri() {
		listCiriCiriPenyakitHashMap = new HashMap<Integer, ListCiriCiriPenyakit>();
		// input ciri ciri penyakit
		Cursor curr = sqlDB.rawQuery("select ciri from ciriciri", null);
		curr.moveToFirst();
		while(!curr.isAfterLast()) {
			String ciri = curr.getString(0);
			listCiriCiriPenyakitHashMap.put(curr.getPosition()+1, new ListCiriCiriPenyakit(ciri, false, false));
			curr.moveToNext();
		}
		curr.close();
		System.gc();
		///////////////////////////
		// input usefirst flags
		curr = sqlDB.rawQuery("select usefirst from ciriciri", null);
		curr.moveToFirst();
		while(!curr.isAfterLast()) {
			String ciri = curr.getString(0);
			listCiriCiriPenyakitHashMap.get(curr.getPosition()+1).setUsefirst_flags(Boolean.parseBoolean(ciri));
			curr.moveToNext();
		}
		curr.close();
		System.gc();
		///////////////////////////
		// input ask flags
		curr = sqlDB.rawQuery("select ask from ciriciri", null);
		curr.moveToFirst();
		while(!curr.isAfterLast()) {
			String ciri = curr.getString(0);
			listCiriCiriPenyakitHashMap.get(curr.getPosition()+1).setAsk_flags(Boolean.parseBoolean(ciri));
			curr.moveToNext();
		}
		curr.close();
		System.gc();
		///////////////////////////
		// input listused flags
		curr = sqlDB.rawQuery("select listused from ciriciri", null);
		curr.moveToFirst();
		while(!curr.isAfterLast()) {
			String ciri = curr.getString(0);
			listCiriCiriPenyakitHashMap.get(curr.getPosition()+1).setListused_flags(ciri);
			curr.moveToNext();
		}
		curr.close();
		System.gc();
		///////////////////////////
		// input pointo flags
		curr = sqlDB.rawQuery("select pointo from ciriciri", null);
		curr.moveToFirst();
		while(!curr.isAfterLast()) {
			String ciri = curr.getString(0);
			listCiriCiriPenyakitHashMap.get(curr.getPosition()+1).setPointo_flags(ciri);
			curr.moveToNext();
		}
		curr.close();
		System.gc();
		//////////////////////////
		//////////////////////////////////////// load successfully
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
		sqlDB = SQLiteDatabase.openOrCreateDatabase("/data/data/id.kenshiro.app.panri/databases/database_penyakitpadi.db", null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int repeat = event.getRepeatCount();
		int maxRepeat = 2;
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
					mTextPetaniDesc.setText(getString(R.string.actdiagnose_string_speechfarmer_1));
					isDiagnosting = true;
					return false;
				}
			}

			return false;
		}
		//else if(keyCode == )
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		stopTask();
		System.gc();
		sqlDB.close();
		super.onDestroy();
	}

	private void setData() {
	    data = new ArrayList<AdapterRecycler.DataPerItems>();
		data.add(new AdapterRecycler.DataPerItems("Hello World"));
		data.add(new AdapterRecycler.DataPerItems("Alexzander Purwoko Widiantoro"));
		data.add(new AdapterRecycler.DataPerItems("Roman Av"));
		data.add(new AdapterRecycler.DataPerItems("Anggi Mundita"));
		data.add(new AdapterRecycler.DataPerItems("Catur lagi kentut"));
		for(int x = 0; x < data.size(); x++)
		    Log.i("MainActivity", String.format("position %d text %s.", x, data.get(x).items));
		mListView = (RecyclerView) findViewById(R.id.actdiagnose_id_contentrecycler);
		mListView.setHasFixedSize(true);
		mListView.setLayoutManager(new LinearLayoutManager(this));
		AdapterRecycler recycler = new AdapterRecycler(data);
        recycler.setOnItemClickListener(new AdapterRecycler.OnItemClickListener() {
            @Override
            public void onClick(View a, int b) {
                Toast.makeText(DiagnoseActivity.this, "selected at position " + b, Toast.LENGTH_LONG).show();
            }
        });
		mListView.setAdapter(recycler);

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

	private class ImgPetaniKedip extends AsyncTask<Void, Integer, Void> {
		private void sleep(int mil) {
			try {
				Thread.sleep(mil);
			} catch (InterruptedException e) {
				Log.e("Main_Exception", "Interrupted in method ImageAutoSwipe.doInBackground()", e);
			}
		}

		@Override
		protected Void doInBackground(Void[] p1) {
			// TODO: Implement this method
			while (true) {
				sleep(400);
				publishProgress(1);
				sleep(2000);
				publishProgress(4);
			}
		}

		@Override
		protected void onProgressUpdate(Integer[] values) {
			// TODO: Implement this method
			super.onProgressUpdate(values);
			int pos = values[0];
			imgPetani.setImageLevel(pos);
		}

	}
}
