package com.example.mdks_client_pfleger.view;

import java.io.IOException;

import com.example.mdks_client_pfleger.R;
import com.rabbitmq.client.*;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

public class Dispenser extends Activity {

	public String mServer = "10.0.2.2";
	public String mExchange = "amq.topic";
	public String mExchangeTyp = "topic";
	private String mQueuename = "personal1.get";
	private Connection mConnection;
	private Channel mChannel;
	private Object mQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dispenser);
		setTitle("Dispenser");

		new consumerconnect().execute();

	}

	private class consumerconnect extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... Message) {
			try {

				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost(mServer);
				factory.setUsername("test");
				factory.setPassword("test");

				mConnection = factory.newConnection();
				mChannel = mConnection.createChannel();
				mChannel.exchangeDeclare(mExchange, mExchangeTyp, true);
				mQueue = mChannel.queueDeclare(mQueuename, true, false, false, null).getQueue();

				Consumer consumer = new DefaultConsumer(mChannel) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
							byte[] body) throws IOException {
						String message = new String(body, "UTF-8");
						System.out.println(" [x] Received '" + message + "'");
					}
				};

				mChannel.basicConsume(mQueuename, true, consumer);
				new consumerdisconnect().execute();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}
	
	private class consumerdisconnect extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... Message) {
			try {
				mChannel.abort();
				mConnection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
