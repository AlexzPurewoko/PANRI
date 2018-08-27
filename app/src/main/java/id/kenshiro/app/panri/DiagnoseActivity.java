package id.kenshiro.app.panri;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.widget.Toast;

import com.mylexz.utils.MylexzActivity;
import com.mylexz.utils.text.style.CustomTypefaceSpan;

import java.util.ArrayList;
import java.util.List;

import id.kenshiro.app.panri.adapter.AdapterRecycler;

public class DiagnoseActivity extends MylexzActivity
{

	private Toolbar toolbar;
	private List<AdapterRecycler.DataPerItems> data;
	private RecyclerView mListView;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actdiagnose_maincontent);
		setMyActionBar();
		setData();
	}

	private void setData() {
	    data = new ArrayList<AdapterRecycler.DataPerItems>();
		data.add(new AdapterRecycler.DataPerItems("Hello World"));
		data.add(new AdapterRecycler.DataPerItems("Alexzander Purwoko Widiantoro"));
		data.add(new AdapterRecycler.DataPerItems("Roman Av"));
		data.add(new AdapterRecycler.DataPerItems("Anggi Mundita"));
		data.add(new AdapterRecycler.DataPerItems("Catur lagi kentut"));

		mListView = (RecyclerView) findViewById(R.id.actdiagnose_id_contentrecycler);
		mListView.setHasFixedSize(true);
		mListView.setLayoutManager(new LinearLayoutManager(this));
		AdapterRecycler recycler = new AdapterRecycler(data);
		recycler.setOnItemClickListener((a,b)->{
            Toast.makeText(DiagnoseActivity.this, "selected at position "+b, Toast.LENGTH_LONG).show();
        });
		mListView.setAdapter(recycler);

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
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

	}

	@Override
	public boolean onSupportNavigateUp() {
		// back into main activity
		this.finish();
		startActivity(new Intent(this, MainActivity.class));
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		return true;
	}
}
