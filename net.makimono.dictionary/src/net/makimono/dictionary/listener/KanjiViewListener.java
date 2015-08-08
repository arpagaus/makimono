package net.makimono.dictionary.listener;

import net.makimono.dictionary.activity.KanjiEntryFragment;
import net.makimono.dictionary.model.KanjiEntry;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class KanjiViewListener implements OnClickListener {

	private Activity activity;
	private KanjiEntry entry;

	public KanjiViewListener(Activity activity, KanjiEntry entry) {
		this.activity = activity;
		this.entry = entry;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(activity, KanjiEntryFragment.class);
		intent.putExtra(KanjiEntryFragment.EXTRA_KANJI_ENTRY, entry);
		activity.startActivity(intent);
	}
}
