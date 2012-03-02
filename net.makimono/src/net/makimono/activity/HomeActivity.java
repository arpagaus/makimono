package net.makimono.activity;

import net.makimono.R;
import android.os.Bundle;
import android.support.v4.view.MenuItem;

public class HomeActivity extends AbstractDefaultActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
