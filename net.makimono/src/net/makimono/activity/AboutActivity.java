package net.makimono.activity;

import net.makimono.R;

import org.apache.commons.lang3.StringUtils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.TextView;

public class AboutActivity extends AbstractDefaultActivity {

	private WebView aboutView;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		initializeContentView();
	}

	private void initializeContentView() {
		setContentView(R.layout.about);

		TextView versionView = (TextView) findViewById(R.id.version);
		versionView.setText(getVersionText());

		aboutView = (WebView) findViewById(R.id.about_html);
		aboutView.loadUrl("file:///android_asset/about/about.html");
	}

	private String getVersionText() {
		String versionName = "";
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info;
			info = manager.getPackageInfo(this.getPackageName(), 0);
			versionName = info.versionName;
		} catch (Exception e) {
		}
		CharSequence appName = getResources().getText(R.string.app_name);
		return StringUtils.join(appName, " ", versionName);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && aboutView.canGoBack()) {
			aboutView.goBack();
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}
}
