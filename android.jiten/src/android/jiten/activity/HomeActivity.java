package android.jiten.activity;

import android.jiten.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_home);

		((Button) findViewById(R.id.button_home_search)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSearchRequested();
			}
		});
	}
}
