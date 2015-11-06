package com.example.eis_poc_rabbitmq;

import java.io.UnsupportedEncodingException;

import com.example.eis_poc_rabbitmq.MessageConsumer.OnReceiveMessageHandler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private MessageConsumer mConsumer;
	private TextView mOutput;
	private String QUEUE_NAME = "";
	private String EXCHANGE_NAME = "amq.topic";
	private String ROUTING_KEY = "verordnung";
	public String message = "";
	private String name = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final EditText etv = (EditText) findViewById(R.id.editText);
		etv.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if ((arg2.getAction() == KeyEvent.ACTION_DOWN)
						&& (arg1 == KeyEvent.KEYCODE_ENTER)) {
					message = etv.getText().toString();
					new send().execute(message);
					etv.setText("");
					return true;
				}
				return false;
			}
		});
		

		mOutput = (TextView) findViewById(R.id.nachrichten);

		mConsumer = new MessageConsumer("192.168.2.118", "amq.topic", "topic");
		
		new consumerconnect().execute();
		
		mConsumer.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {

			public void onReceiveMessage(byte[] message) {
				String text = "";
				try {
					text = new String(message, "UTF8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				mOutput.append("\n" + text);
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

				channel.basicPublish(EXCHANGE_NAME, "verordnung", null,
						tempstr.getBytes());

				channel.close();

				connection.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	
	private class consumerconnect extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... Message) {
			try {
				mConsumer.connectToRabbitMQ();
				mConsumer.AddBinding("verordnung");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}
	
	@Override
	protected void onResume() {
		super.onPause();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}