package com.example.eis_poc;

import com.example.eis_poc_rabbitmq.R;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Poc2PushService extends Activity {
	
	protected static final String ACTION_BROADCAST = "com.example.eis_poc_pushes.publish";
	private TextView mOutput;
	private String EXCHANGE_NAME = "amq.topic";
	public String message = "";
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == ACTION_BROADCAST) {
				String text = intent.getExtras().getString("publish");
				mOutput = (TextView) findViewById(R.id.POC2nachrichten);
				mOutput.append("\n" + text);
			}
		}

	};
	private Button mButtonSend;
	private EditText edittext;
	private Button mButtonStartService;
	private Button mButtonStopService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poc2);
		
		setTitle("POC 2 Push Service");
		
		edittext = (EditText) findViewById(R.id.POC2editText);
		
		mButtonSend = (Button) findViewById(R.id.POC2button_senden);
		
		mButtonStartService = (Button)findViewById(R.id.POC2button_startservice);
		
		mButtonStopService = (Button)findViewById(R.id.POC2button_stopservice);
		
		if(isMyServiceRunning(PushService.class)){
			mButtonStartService.setEnabled(false);
			mButtonStopService.setEnabled(true);
		}else{
			mButtonStartService.setEnabled(true);
			mButtonStopService.setEnabled(false);
		}
			
		
		mButtonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				message = edittext.getText().toString();
				new send().execute(message);
				edittext.setText("");
			}
		});
		
		mButtonStartService.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startService(new Intent(getApplicationContext(), PushService.class));
				mButtonStartService.setEnabled(false);
				mButtonStopService.setEnabled(true);
			}
		});
		
		mButtonStopService.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stopService(new Intent(getApplicationContext(), PushService.class));
				if(isMyServiceRunning(PushService.class)){
					mButtonStartService.setEnabled(false);
					mButtonStopService.setEnabled(false);
				}else{
					mButtonStartService.setEnabled(true);
					mButtonStopService.setEnabled(false);
				}
			}
		});

	}

	private class send extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... Message) {
			try {

				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost("192.168.2.118");
				factory.setUsername("test");
				factory.setPassword("test");
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();

				String tempstr = "";
				for (int i = 0; i < Message.length; i++)
					tempstr += Message[i];

				channel.basicPublish(EXCHANGE_NAME, "verordnung", null, tempstr.getBytes());

				channel.close();

				connection.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if(isMyServiceRunning(PushService.class)){
			mButtonStartService.setEnabled(false);
			mButtonStopService.setEnabled(true);
		}else{
			mButtonStartService.setEnabled(true);
			mButtonStopService.setEnabled(false);
		}
		
		IntentFilter filter = new IntentFilter(ACTION_BROADCAST);

		getApplicationContext().registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
