package net.makimono.dictionary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import net.makimono.dictionary.R;

public abstract class AbstractDefaultActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.base, menu);
		getSupportActionBar().setHomeButtonEnabled(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		case R.id.menu_search: {
			onSearchRequested();
			return true;
		}
		case R.id.menu_preferences: {
			Intent intent = new Intent(this, PreferenceActivity.class);
			startActivity(intent);
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
