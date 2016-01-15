package com.example.mdks_client_pfleger.view;

import com.example.mdks_client_pfleger.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import controller.VerordnungsArrayAdapter;
import models.Applikationszeit;
import models.Medikationsplan;
import models.Verordnung;
import rabbitmq.MessageConsumer;
import rabbitmq.MessageConsumer.OnReceiveMessageHandler;

public class MedikationsplanActivity extends Activity {

	private MessageConsumer mConsumer;
	private String personalID = "personal1";
	private String EXCHANGE_NAME = "amq.topic";
	public String message = "";
	private JsonParser mParser;
	private Medikationsplan mPlan;
	private ListView mListeView;
	protected static final String ACTION_BROADCAST = "rabbitmq.publish";
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction() == ACTION_BROADCAST) {

				String text = intent.getExtras().getString("publish");
				//new send().execute("get");
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medikationsplan);
		setTitle("Medikationsplan");

		mParser = new JsonParser();

		mListeView = (ListView) findViewById(R.id.medikationsplan_list);

		mConsumer = new MessageConsumer("10.0.2.2", "amq.topic", "topic", "personal1.get");
		new consumerconnect().execute();
		mConsumer.setOnReceiveMessageHandler(new OnReceiveMessageHandler() {

			public void onReceiveMessage(byte[] message) {

				String text = "";
				String textPlain = null;
				textPlain = new String(message);
				JsonElement obj = mParser.parse(textPlain);
				JsonArray verordnungen = obj.getAsJsonArray();

				mPlan = new Medikationsplan(verordnungen);

				Log.v("JSON ", obj.toString());
				Log.v("JSONARRAY: ", verordnungen.toString());
				Log.v("textPlain ", textPlain);

				setPlan(mPlan);
			}

		});
		new send().execute("get");
		

	}

	private void setPlan(Medikationsplan mPlan) {
		ListView lw = (ListView) findViewById(R.id.medikationsplan_list);
		// get data from the table by the ListAdapter
		VerordnungsArrayAdapter adapter = new VerordnungsArrayAdapter(this, R.layout.liste_medikationsplan,
				mPlan.verordnungTimes, mPlan);

		lw.setAdapter(adapter);
	}

	private class send extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... Message) {

			try {

				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost("10.0.2.2");
				factory.setUsername("test");
				factory.setPassword("test");
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();
				String tempstr = "";
				for (int i = 0; i < Message.length; i++) {
					tempstr += Message[i];
				}

				if (tempstr.equals("get")) {
					JsonObject get = new JsonObject();
					get.addProperty("absender", personalID);
					get.addProperty("methode", "getMedikationsplanForStation");
					get.addProperty("routingKey", "get");
					JsonArray parameters = new JsonArray();
					JsonObject parameter = new JsonObject();
					parameter.addProperty("station", 1);
					// parameter.addProperty("patient", 1);
					parameters.add(parameter);
					get.add("parameters", parameters);
					Log.v("GETJSON ", get.toString());

					channel.basicPublish(EXCHANGE_NAME, "get", null, get.toString().getBytes());
					channel.close();
					connection.close();
				}

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
				mConsumer.AddBinding(personalID + ".get");
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
				mConsumer.dispose();
				mConsumer.Dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(ACTION_BROADCAST);

		getApplicationContext().registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		new consumerdisconnect().execute();
	}

}
