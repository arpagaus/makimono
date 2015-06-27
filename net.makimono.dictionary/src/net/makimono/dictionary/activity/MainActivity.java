/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.makimono.dictionary.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import net.makimono.dictionary.R;

public class MainActivity extends Activity {

	private enum DrawerItem {
		Dictionary, Kanji, Examples, Settings, HelpFeedback;
	}

	private DrawerLayout drawerLayout;
	private ListView drawerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.drawer_list);
		drawerList.setAdapter(new DrawerListAdapter(this));
		drawerList.setOnItemClickListener(new DrawerItemClickListener());
	}

	private static class DrawerListAdapter extends BaseAdapter {

		private final LayoutInflater layoutInflater;

		private DrawerListAdapter(Context context) {
			layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return DrawerItem.values().length;
		}

		@Override
		public Object getItem(int position) {
			return DrawerItem.values()[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			TextView textView;
			ImageView imageView;

			if (convertView == null) {
				view = layoutInflater.inflate(R.layout.drawer_list_item, parent, false);
			} else {
				view = convertView;
			}

			textView = (TextView) view.findViewById(R.id.itemText);
			imageView = (ImageView) view.findViewById(R.id.itemIcon);

			textView.setText(DrawerItem.values()[position].name());

			return view;
		}
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			System.out.println("click: " + position);
		}
	}
}