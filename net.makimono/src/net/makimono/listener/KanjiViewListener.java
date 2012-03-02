package net.makimono.listener;

import net.makimono.activity.KanjiEntryActivity;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class KanjiViewListener implements OnClickListener {

	private Activity activity;
	private int codePoint;

	public KanjiViewListener(Activity activity, int codePoint) {
		this.activity = activity;
		this.codePoint = codePoint;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(activity, KanjiEntryActivity.class);
		intent.putExtra(KanjiEntryActivity.EXTRA_CODE_POINT, codePoint);
		activity.startActivity(intent);
	}
}
