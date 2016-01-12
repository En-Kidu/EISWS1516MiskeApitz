package com.example.mdks_client_pfleger.view;

import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import com.example.mdks_client_pfleger.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
	private ListView mListeView;
	private JsonParser mParser;
	private Medikationsplan mPlan;
	private Verordnung mVerordnung;
	private Applikationszeit mAppzeit;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medikationsplan);
		setTitle("Medikationsplan");

		mParser = new JsonParser();

		mListeView = (ListView) findViewById(R.id.medikationsplan_list);

		mConsumer = new MessageConsumer("192.168.2.118", "amq.topic", "topic", "personal1");
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
				Log.v("JSONARRAY: ",verordnungen.toString());
				Log.v("textPlain ", textPlain); 
			}
		});
		new send().execute();

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
				for (int i = 0; i < Message.length; i++) {
					tempstr += Message[i];
				}
				
				JsonObject get = new JsonObject();
				get.addProperty("absender", personalID);
				get.addProperty("methode", "getMedikationsplanForStation");
				get.addProperty("routingKey", "get");
				JsonArray parameters = new JsonArray();
				JsonObject parameter = new JsonObject();
				parameter.addProperty("station", 1);
				parameter.addProperty("patient", 1);
				parameters.add(parameter);
				get.add("parameters", parameters);
				Log.v("GETJSON ", get.toString());
				
				
				channel.basicPublish(EXCHANGE_NAME, "get", null, get.toString().getBytes());
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
				mConsumer.AddBinding(personalID + ".get");
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
