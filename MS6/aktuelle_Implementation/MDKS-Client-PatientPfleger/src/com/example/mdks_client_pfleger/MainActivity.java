package com.example.mdks_client_pfleger;

import com.example.mdks_client_pfleger.R;
import com.example.mdks_client_pfleger.view.Dispenser;
import com.example.mdks_client_pfleger.view.MedikationsplanActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Button mButtonMedikationsplan = (Button) findViewById(R.id.buttonMedikationsplan);
		mButtonMedikationsplan.setOnClickListener(this);

		Button mButtonDispenser = (Button) findViewById(R.id.buttonDispenser);
		mButtonDispenser.setOnClickListener(this);

		startService(new Intent(getApplicationContext(), rabbitmq.PushService.class));
	}

	public void onClick(View v) {
		// Perform action on click
		Intent intent;
		switch (v.getId()) {
		case R.id.buttonMedikationsplan:
			intent = new Intent(getApplicationContext(), MedikationsplanActivity.class);
			startActivity(intent);
			break;

		case R.id.buttonDispenser:
			intent = new Intent(getApplicationContext(), Dispenser.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
