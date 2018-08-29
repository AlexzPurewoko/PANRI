package id.kenshiro.app.panri;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.text.SpannableString;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.SystemBarTintManager;
import com.mylexz.utils.text.style.CustomTypefaceSpan;
import android.graphics.Typeface;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import id.kenshiro.app.panri.adapter.CustomViewPager;
import id.kenshiro.app.panri.adapter.FadePageViewTransformer;
import id.kenshiro.app.panri.adapter.ImageFragmentAdapter;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.view.PagerAdapter;
import id.kenshiro.app.panri.adapter.CustomPageViewTransformer;
import id.kenshiro.app.panri.helper.CheckAndMoveDB;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import android.support.v7.widget.CardView;
import android.support.v7.app.AlertDialog;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.content.Intent;

public class MainActivity extends MylexzActivity
implements NavigationView.OnNavigationItemSelectedListener
{
	Toolbar toolbar;
	// for view image pager
    LinearLayout indicators;
    private static CustomViewPager mImageSelector;
    private int mDotCount;
    private static List<Integer> mListResImage;
    private LinearLayout[] mDots;
    private ImageFragmentAdapter mImageControllerFragment;
	private TextView mTextDetails;

	// for section petani
	private static ImageView imgPetani;
	private Button mTextPetaniDesc;
	private int[] TextPetaniDesc = {
		R.string.actmain_string_speechfarmer_1,
		R.string.actmain_string_speechfarmer_2
	};
	private int mPosTxtPetani = 0;
	private static boolean mPetaniIsKedip = false;
	
	// for section operation
	private LinearLayout mListOp;
	private List<CardView> mListCard;
	// Important Task 
	private static ImageAutoSwipe imgSw;
	private static ImgPetaniKedip imgKedip;
	private static TalkingFarmer imgFarmerTalk;
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDB();
        setMyActionBar();
		setInitialPagerData();
		setInitialTextInds();
		setInitialSectPetani();
		setInitialSectOpIntent();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
			this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
		startTask();
    }

	private void setDB() {
		try {
			new CheckAndMoveDB(this, "database_penyakitpadi.db").upgradeDB();
		} catch (IOException e) {
			LOGE("MainActivity","ERROR WHEN HANDLING checkAndMoveDB()", e);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		startTask();
	}

	@Override
	protected void onPause() {
		stopTask();
		super.onPause();
	}

	private void setMyActionBar()
	{
		// TODO: Implement this method
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		SpannableString strTitle = new SpannableString(getTitle());
		Typeface tTitle = Typeface.createFromAsset(getAssets(), "Gecko_PersonalUseOnly.ttf");
		strTitle.setSpan(new CustomTypefaceSpan(tTitle), 0, getTitle().length(), 0);
		toolbar.setTitle(strTitle);
        setSupportActionBar(toolbar);
	}

    @Override
    public void onBackPressed()
	{
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
		{
            drawer.closeDrawer(GravityCompat.START);
        }
		else
		{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
	{
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
		{
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
	{
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera)
		{
            // Handle the camera action
        }
		else if (id == R.id.nav_gallery)
		{

        }
		else if (id == R.id.nav_slideshow)
		{

        }
		else if (id == R.id.nav_manage)
		{

        }
		else if (id == R.id.nav_share)
		{

        }
		else if (id == R.id.nav_send)
		{

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
	private void startTask()
	{
		imgSw = new ImageAutoSwipe();
		imgKedip = new ImgPetaniKedip();
		imgSw.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		imgKedip.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	private void stopTask()
	{
		if (imgSw != null)
			imgSw.cancel(true);
		if (imgFarmerTalk != null)
			imgFarmerTalk.cancel(true);
		if (imgKedip != null)
			imgKedip.cancel(true);
		imgSw = null;
		imgFarmerTalk = null;
		imgKedip = null;
		System.gc();

	}
	private void setCardTouchEvent(final Class<?>[] cls){
		
		for(int x = 0; x < mListCard.size(); x++){
			final int y = x;
			mListCard.get(x).setOnClickListener((v)->
				{
					if(cls != null){
						MainActivity.this.finish();
						startActivity(new Intent(MainActivity.this, cls[y]));
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
				}
			);
		}
	}
	private void setInitialSectOpIntent(){
		mListOp = (LinearLayout) findViewById(R.id.actmain_id_listmainoperation);
		int[][] listConfOp = {
			// { @Res to Image Drawable, @Res to text}
			{R.drawable.ic_actmain_diagnose, R.string.actmain_string_startdiagnose},
			{R.drawable.ic_actmain_howto, R.string.actmain_string_howto},
			{R.drawable.ic_actmain_aboutpenyakit, R.string.actmain_string_aboutpenyakit}
		};
		Class<?>[] listClass = {
			DiagnoseActivity.class,
			null,
			null
		};
		mListCard = new ArrayList<CardView>();
		// add a cardView
		for(int x = 0; x < listConfOp.length; x++){
			mListCard.add((CardView) CardView.inflate(this, R.layout.cardview_adapter, null));
			LinearLayout content = (LinearLayout) LinearLayout.inflate(this, R.layout.actmain_content_op_incard, null);
			ImageView imgOpC = (ImageView) content.getChildAt(0);
			TextView txtOpC = (TextView) content.getChildAt(1);
			imgOpC.setImageResource(listConfOp[x][0]);
			txtOpC.setTypeface(Typeface.createFromAsset(getAssets(), "Gill_SansMT.ttf"), Typeface.BOLD_ITALIC);
			txtOpC.setText(listConfOp[x][1]);
			mListCard.get(x).setContentPadding(10,10,10,10);
			mListCard.get(x).addView(content);
		}
		setCardTouchEvent(listClass);
		// fill into LinearLayout
		for(int x = 0; x < mListCard.size(); x++){
			mListOp.addView(mListCard.get(x));
		}
	}
	private void onButtonPetaniClicked()
	{
		if (++mPosTxtPetani == TextPetaniDesc.length)
			mPosTxtPetani = 0;
		mTextPetaniDesc.setText(TextPetaniDesc[mPosTxtPetani]);
		
		if(imgKedip != null){
			imgKedip.cancel(true);
			imgKedip = null;
		}
		if(imgFarmerTalk != null){
			imgFarmerTalk.cancel(true);
			imgFarmerTalk = null;
		}
		System.gc();
		imgFarmerTalk = new TalkingFarmer();
		imgFarmerTalk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}
	private void setInitialSectPetani()
	{
		mTextPetaniDesc = (Button) findViewById(R.id.actmain_id_section_petani_btn);
		imgPetani = (ImageView) findViewById(R.id.actmain_id_section_petani_img);
		mTextPetaniDesc.setTextColor(Color.BLACK);
		mTextPetaniDesc.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.NORMAL);
		mTextPetaniDesc.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					onButtonPetaniClicked();
				}
			});
		imgPetani.setImageResource(R.drawable.petani);
		imgPetani.setImageLevel(4);
		mTextPetaniDesc.setText(TextPetaniDesc[mPosTxtPetani]);
	}
	private void setInitialTextInds()
	{
		mTextDetails = (TextView) findViewById(R.id.actmain_id_textIndicatorViewPager);
		mTextDetails.setTypeface(Typeface.createFromAsset(getAssets(), "Gill_SansMT.ttf"), Typeface.ITALIC);
		mTextDetails.setText("MUDAHKAN HIDUPMU KENALI PENYAKIT PADIMU");

	}
    private void setInitialPagerData()
	{
	    // initialize the view container
        mListResImage  = new ArrayList<Integer>();
        indicators = (LinearLayout) findViewById(R.id.actmain_id_layoutIndicators);
        mImageSelector = (CustomViewPager) findViewById(R.id.actmain_id_viewpagerimg);
        //add your items here
	    mListResImage.add(R.drawable.viewpager_area_1);
	    mListResImage.add(R.drawable.viewpager_area_2);
	    mListResImage.add(R.drawable.viewpager_area_3);
	    mListResImage.add(R.drawable.viewpager_area_4);
        //////////
	    mImageControllerFragment = new ImageFragmentAdapter(this, getSupportFragmentManager(), mListResImage);
	    mImageSelector.setAdapter(mImageControllerFragment);
	    mImageSelector.setCurrentItem(0);
		mImageSelector.setPageTransformer(true, new CustomPageViewTransformer());
		mImageSelector.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int curr_img = mImageSelector.getCurrentItem();
				mImageSelector.setPageTransformer(true, new FadePageViewTransformer());
				if(++curr_img == mListResImage.size())
					curr_img = 0;
				mImageSelector.setCurrentItem(curr_img);

				System.gc();
				mImageSelector.setPageTransformer(true, new CustomPageViewTransformer());
				System.gc();
			}
		});
	    mImageSelector.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageScrolled(int i, float v, int i1)
				{
					
				}

				@Override
				public void onPageSelected(int i)
				{
					for (int x = 0; x < mDotCount; x++)
					{
						mDots[x].setBackgroundResource(R.drawable.indicator_unselected_item_oval);
					}
					mDots[i].setBackgroundResource(R.drawable.indicator_selected_item_oval);				
				}

				@Override
				public void onPageScrollStateChanged(int i)
				{
					int pos = mImageSelector.getCurrentItem();
					// if reaching last and state is DRAGGING, back into first
					if (pos == mListResImage.size() - 1 && i == ViewPager.SCROLL_STATE_DRAGGING)
						mImageSelector.setCurrentItem(0, true);
				}
			});
	    // set the indicators
        mDotCount = mImageControllerFragment.getCount();
        mDots = new LinearLayout[mDotCount];
        for (int x = 0; x < mDotCount; x++)
		{
            mDots[x] = new LinearLayout(this);
            mDots[x].setBackgroundResource(R.drawable.indicator_unselected_item_oval);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 4, 4);
			mDots[x].setGravity(Gravity.RIGHT | Gravity.BOTTOM | Gravity.END);
            indicators.addView(mDots[x], params);

        }
        mDots[0].setBackgroundResource(R.drawable.indicator_selected_item_oval);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO: Implement this method
		if(keyCode == KeyEvent.KEYCODE_BACK){
			showExitDialog();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		stopTask();
		super.onDestroy();
	}
	private void showExitDialog(){
		AlertDialog.Builder build = new AlertDialog.Builder(this);
		LinearLayout layoutDialog = (LinearLayout) LinearLayout.inflate(this, R.layout.actmain_dialog_on_exit, null);
		TextView text = (TextView) layoutDialog.findViewById(R.id.actmain_id_dialogexit_content);
		Button btnyes = (Button) layoutDialog.findViewById(R.id.actmain_id_dialogexit_btnyes);
		Button btnno = (Button) layoutDialog.findViewById(R.id.actmain_id_dialogexit_btnno);
		text.setTextColor(Color.BLACK);
		text.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.BOLD);
		text.setText(R.string.actmain_string_dialogexit_desc);
		btnyes.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.BOLD);
		btnyes.setTextColor(Color.WHITE);
		btnyes.setText(R.string.actmain_string_dialogexit_btnyes);
		btnno.setTypeface(Typeface.createFromAsset(getAssets(), "Comic_Sans_MS3.ttf"), Typeface.BOLD);
		btnno.setTextColor(Color.WHITE);
		btnno.setText(R.string.actmain_string_dialogexit_btnno);
		build.setView(layoutDialog);
		final AlertDialog mAlert = build.create();
		btnyes.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					mAlert.cancel();
					MainActivity.this.finish();
				}
				
			
		});
		btnno.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					mAlert.cancel();
				}


			});
		mAlert.show();
	}
	static class TalkingFarmer extends AsyncTask<Void, Integer, Void>
	{

		@Override
		protected void onPreExecute()
		{
			// TODO: Implement this method
			super.onPreExecute();
			imgPetani.setImageLevel(2);
		}
		private void sleep(int mil){
			try
			{
				Thread.sleep(mil);
			}
			catch (InterruptedException e)
			{Log.e("Main_Exception", "Interrupted in method ImageAutoSwipe.doInBackground()", e);}
		}
		@Override
		protected Void doInBackground(Void[] p1)
		{
			// TODO: Implement this method
			sleep(300);
			publishProgress(1);
			sleep(300);
			publishProgress(3);
			sleep(300);
			publishProgress(1);
			sleep(300);
			publishProgress(3);
			sleep(300);
			publishProgress(1);
			sleep(300);
			publishProgress(2);
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer[] values)
		{
			// TODO: Implement this method
			super.onProgressUpdate(values);
			int x = values[0];
			imgPetani.setImageLevel(x);
		}

		@Override
		protected void onPostExecute(Void result)
		{
			// TODO: Implement this method
			super.onPostExecute(result);
			imgPetani.setImageLevel(4);
			imgKedip = null;
			System.gc();
			imgKedip = new ImgPetaniKedip();
			imgKedip.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}

		@Override
		protected void onCancelled()
		{
			// TODO: Implement this method
			super.onCancelled();
			imgPetani.setImageLevel(4);
		}
		
		
	}
	static class ImgPetaniKedip extends AsyncTask<Void, Integer, Void>
	{
		private void sleep(int mil){
			try
			{
				Thread.sleep(mil);
			}
			catch (InterruptedException e)
			{Log.e("Main_Exception", "Interrupted in method ImageAutoSwipe.doInBackground()", e);}
		}
		@Override
		protected Void doInBackground(Void[] p1)
		{
			// TODO: Implement this method
			while(true){
				sleep(400);
				publishProgress(1);
				sleep(3000);
				publishProgress(4);
			}
		}

		@Override
		protected void onProgressUpdate(Integer[] values)
		{
			// TODO: Implement this method
			super.onProgressUpdate(values);
			int pos = values[0];
			imgPetani.setImageLevel(pos);
		}

	}
	static class ImageAutoSwipe extends AsyncTask<Void, Integer, Void>
	{
		private final long pause_swipe_in_millis = 6000;
		private int maxImages;
		@Override
		protected void onPreExecute()
		{
			// TODO: Implement this method
			super.onPreExecute();
			maxImages = mListResImage.size();
		}//5s

		@Override
		protected Void doInBackground(Void[] p1)
		{
			// TODO: Implement this method
			while (true)
			{
				try
				{
					Thread.sleep(pause_swipe_in_millis);
				}
				catch (InterruptedException e)
				{Log.e("Main_Exception", "Interrupted in method ImageAutoSwipe.doInBackground()", e);}
				if(!mImageSelector.isFakeDragging())
					publishProgress(mImageSelector.getCurrentItem());
			}
		}

		@Override
		protected void onProgressUpdate(Integer[] values)
		{
			// TODO: Implement this method
			super.onProgressUpdate(values);
			int pos_result = values[0];
			if (++pos_result == maxImages)
				mImageSelector.setCurrentItem(0, true);
			else
				mImageSelector.setCurrentItem(pos_result, true);
		}


	}
}
