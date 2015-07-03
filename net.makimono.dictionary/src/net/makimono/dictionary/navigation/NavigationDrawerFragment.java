package net.makimono.dictionary.navigation;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.makimono.dictionary.R;
import net.makimono.dictionary.activity.PreferenceEnum;

/**
 * Created by poliveira on 24/10/2014.
 */
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerCallbacks {
	private static final String SELECTED_NAVIGATION_DRAWER_POSITION = "selected_navigation_drawer_position";

	private NavigationDrawerCallbacks mCallbacks;
	private RecyclerView mDrawerList;
	private View mFragmentContainerView;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mActionBarDrawerToggle;
	private boolean mUserLearnedDrawer;
	private boolean mFromSavedInstanceState;
	private int mCurrentSelectedPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_navigation_google, container, false);
		mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mDrawerList.setLayoutManager(layoutManager);
		mDrawerList.setHasFixedSize(true);

		NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(getNavigationItems());
		adapter.setNavigationDrawerCallbacks(this);
		mDrawerList.setAdapter(adapter);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sharedPreferences.getBoolean(PreferenceEnum.USER_LEARNED_NAVIGATION_DRAWER.key(), false);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState.getInt(SELECTED_NAVIGATION_DRAWER_POSITION);
			mFromSavedInstanceState = true;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		selectItem(mCurrentSelectedPosition);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	public ActionBarDrawerToggle getSupportActionBarDrawerToggle() {
		return mActionBarDrawerToggle;
	}

	public void setActionBarDrawerToggle(ActionBarDrawerToggle actionBarDrawerToggle) {
		mActionBarDrawerToggle = actionBarDrawerToggle;
	}

	public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;
		mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

		mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded())
					return;
				getActivity().invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded())
					return;
				if (!mUserLearnedDrawer) {
					mUserLearnedDrawer = true;
					SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
					editor.putBoolean(PreferenceEnum.USER_LEARNED_NAVIGATION_DRAWER.key(), mUserLearnedDrawer);
					editor.commit();
				}

				getActivity().invalidateOptionsMenu();
			}
		};

		if (!mUserLearnedDrawer && !mFromSavedInstanceState)
			mDrawerLayout.openDrawer(mFragmentContainerView);

		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mActionBarDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
	}

	public void openDrawer() {
		mDrawerLayout.openDrawer(mFragmentContainerView);
	}

	public void closeDrawer() {
		mDrawerLayout.closeDrawer(mFragmentContainerView);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	public List<NavigationDrawerItem> getNavigationItems() {
		List<NavigationDrawerItem> items = new ArrayList<NavigationDrawerItem>();
		items.add(new NavigationDrawerItem("Dictionary", R.drawable.ic_find_in_page_black_24dp));
		items.add(new NavigationDrawerItem("Kanji", R.drawable.ic_brush_black_24dp));
		items.add(new NavigationDrawerItem("Example", R.drawable.ic_message_black_24dp));
		items.add(new NavigationDrawerItem("Settings", R.drawable.ic_settings_black_24dp));
		items.add(new NavigationDrawerItem("About", R.drawable.ic_info_outline_black_24dp));
		return items;
	}

	/**
	 * Changes the icon of the drawer to back
	 */
	public void showBackButton() {
		if (getActivity() instanceof AppCompatActivity) {
			((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	/**
	 * Changes the icon of the drawer to menu
	 */
	public void showDrawerButton() {
		if (getActivity() instanceof AppCompatActivity) {
			((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		}
		mActionBarDrawerToggle.syncState();
	}

	void selectItem(int position) {
		mCurrentSelectedPosition = position;
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
		((NavigationDrawerAdapter) mDrawerList.getAdapter()).selectPosition(position);
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mActionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SELECTED_NAVIGATION_DRAWER_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		selectItem(position);
	}

	public DrawerLayout getDrawerLayout() {
		return mDrawerLayout;
	}

	public void setDrawerLayout(DrawerLayout drawerLayout) {
		mDrawerLayout = drawerLayout;
	}

	public static void saveSharedSetting(Context context, String key, boolean value) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean readSharedSetting(Context context, String key, boolean value) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(key, value);
	}
}
