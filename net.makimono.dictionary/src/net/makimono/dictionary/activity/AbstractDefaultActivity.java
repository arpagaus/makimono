package net.makimono.dictionary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
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
}
