package net.makimono.dictionary.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import net.makimono.dictionary.R;
import net.makimono.dictionary.navigation.NavigationDrawerCallbacks;
import net.makimono.dictionary.navigation.NavigationDrawerFragment;

public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks {

	private Toolbar mToolbar;
	private NavigationDrawerFragment mNavigationDrawerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
		mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onBackPressed() {
		if (mNavigationDrawerFragment.isDrawerOpen())
			mNavigationDrawerFragment.closeDrawer();
		else
			super.onBackPressed();
	}
}
