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
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerCallback {
	private static final String SELECTED_NAVIGATION_DRAWER_POSITION = "selected_navigation_drawer_position";

	private NavigationDrawerCallback callbacks;
	private RecyclerView drawerList;
	private View fragmentContainerView;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private boolean userLearnedDrawer;
	private boolean fromSavedInstanceState;
	private int currentSelectedPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_navigation_google, container, false);
		drawerList = (RecyclerView) view.findViewById(R.id.drawerList);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		drawerList.setLayoutManager(layoutManager);
		drawerList.setHasFixedSize(true);

		NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(getNavigationItems());
		adapter.setNavigationDrawerCallbacks(this);
		drawerList.setAdapter(adapter);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		userLearnedDrawer = sharedPreferences.getBoolean(PreferenceEnum.USER_LEARNED_NAVIGATION_DRAWER.key(), false);

		if (savedInstanceState != null) {
			currentSelectedPosition = savedInstanceState.getInt(SELECTED_NAVIGATION_DRAWER_POSITION);
			fromSavedInstanceState = true;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		selectItem(currentSelectedPosition);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			callbacks = (NavigationDrawerCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	public ActionBarDrawerToggle getSupportActionBarDrawerToggle() {
		return actionBarDrawerToggle;
	}

	public void setActionBarDrawerToggle(ActionBarDrawerToggle actionBarDrawerToggle) {
		this.actionBarDrawerToggle = actionBarDrawerToggle;
	}

	public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
		fragmentContainerView = getActivity().findViewById(fragmentId);
		this.drawerLayout = drawerLayout;
		drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

		actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
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
				if (!userLearnedDrawer) {
					userLearnedDrawer = true;
					SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
					editor.putBoolean(PreferenceEnum.USER_LEARNED_NAVIGATION_DRAWER.key(), userLearnedDrawer);
					editor.commit();
				}

				getActivity().invalidateOptionsMenu();
			}
		};

		if (!userLearnedDrawer && !fromSavedInstanceState)
			drawerLayout.openDrawer(fragmentContainerView);

		drawerLayout.post(new Runnable() {
			@Override
			public void run() {
				actionBarDrawerToggle.syncState();
			}
		});

		drawerLayout.setDrawerListener(actionBarDrawerToggle);
	}

	public void openDrawer() {
		drawerLayout.openDrawer(fragmentContainerView);
	}

	public void closeDrawer() {
		drawerLayout.closeDrawer(fragmentContainerView);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		callbacks = null;
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
		actionBarDrawerToggle.syncState();
	}

	void selectItem(int position) {
		currentSelectedPosition = position;
		if (drawerLayout != null) {
			drawerLayout.closeDrawer(fragmentContainerView);
		}
		if (callbacks != null) {
			callbacks.onNavigationDrawerItemSelected(position);
		}
		((NavigationDrawerAdapter) drawerList.getAdapter()).selectPosition(position);
	}

	public boolean isDrawerOpen() {
		return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SELECTED_NAVIGATION_DRAWER_POSITION, currentSelectedPosition);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		selectItem(position);
	}

	public DrawerLayout getDrawerLayout() {
		return drawerLayout;
	}

	public void setDrawerLayout(DrawerLayout drawerLayout) {
		this.drawerLayout = drawerLayout;
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
