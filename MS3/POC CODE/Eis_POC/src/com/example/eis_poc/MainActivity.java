package com.example.eis_poc;

import com.example.eis_poc_rabbitmq.R;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button mButtonPoc1;

	private Button mButtonPoc2;

	private Button mButtonPoc3;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		setTitle("Proove-of-Concepts");

		mButtonPoc1 = (Button) findViewById(R.id.button_poc1);

		mButtonPoc2 = (Button) findViewById(R.id.button_poc2);

		mButtonPoc3 = (Button) findViewById(R.id.button_poc3);

		mButtonPoc1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(), Poc1RabbitMQ.class);

				startActivity(intent);
			}

		});

		mButtonPoc2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(), Poc2PushService.class);

				startActivity(intent);

			}

		});

		mButtonPoc3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(), Poc3MySQL.class);

				startActivity(intent);

			}

		});

	}

	@Override
	protected void onResume() {

		super.onResume();

	}

	@Override
	protected void onPause() {

		super.onPause();

	}

}