package com.example.eis_poc;

import java.io.UnsupportedEncodingException;
import com.example.eis_poc.MessageConsumer.OnReceiveMessageHandler;
import com.example.eis_poc_rabbitmq.R;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Poc1RabbitMQ extends Activity {

	private MessageConsumer mConsumer;

	private TextView mOutput;

	private String EXCHANGE_NAME = "amq.topic";

	public String message = "";

	private Button mButtonSend;

	private EditText mEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_poc1);

		setTitle("POC 1 RabbitMQ");

		mEditText = (EditText) findViewById(R.id.POC1editText);

		mButtonSend = (Button) findViewById(R.id.POC1button1);

		mButtonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				message = mEditText.getText().toString();

				new send().execute(message);

				mEditText.setText("");

			}

		});

		mOutput = (TextView) findViewById(R.id.POC1nachrichten);

		mConsumer = new MessageConsumer("192.168.2.118", "amq.topic", "topic", "androidclient1");

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

				for (int i = 0; i < Message.length; i++) {

					tempstr += Message[i];

				}

				channel.basicPublish(EXCHANGE_NAME, "verordnung", null, tempstr.getBytes());

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
