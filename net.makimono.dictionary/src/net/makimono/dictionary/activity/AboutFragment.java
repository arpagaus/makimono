package net.makimono.dictionary.activity;

import org.apache.commons.lang3.StringUtils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import net.makimono.dictionary.R;

public class AboutFragment extends Fragment {

	private WebView aboutWebView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.about, container, false);
		TextView versionView = (TextView) view.findViewById(R.id.version);
		versionView.setText(getVersionText());

		aboutWebView = (WebView) view.findViewById(R.id.about_html);
		aboutWebView.loadUrl("file:///android_asset/about/about.html");
		aboutWebView.setOnKeyListener(new AboutWebViewKeyListener());
		return view;
	}

	private String getVersionText() {
		String versionName = "";
		try {
			PackageManager manager = getActivity().getPackageManager();
			PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
			versionName = info.versionName;
		} catch (Exception e) {
		}
		CharSequence appName = getResources().getText(R.string.app_name);
		return StringUtils.join(appName, " ", versionName);
	}

	private static class AboutWebViewKeyListener implements OnKeyListener {
		@Override
		public boolean onKey(View view, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN && view instanceof WebView) {
				WebView webView = (WebView) view;
				if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
					webView.goBack();
					return true;
				}
			}
			return false;
		}
	}
}
