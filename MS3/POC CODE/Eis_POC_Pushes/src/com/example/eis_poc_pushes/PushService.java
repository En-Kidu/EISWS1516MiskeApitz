package com.example.eis_poc_pushes;

import java.io.UnsupportedEncodingException;

import com.example.eis_poc_pushes.MessageConsumer.OnReceiveMessageHandler;
import com.example.eis_poc_rabbitmq.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class PushService extends Service {
	private NotificationManager mNM;
	private MessageConsumer mConsumer;
	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = R.string.local_service_started;

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class PushServiceBinder extends Binder {
		PushService getService() {
			return PushService.this;
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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

				showNotification("\n" + text);
			}
		});
		// Display a notification about us starting. We put an icon in the
		// status bar.
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		mNM.cancel(NOTIFICATION);

		// Tell the user we stopped.
		Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new PushServiceBinder();

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification(String text) {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(android.R.drawable.ic_notification_overlay) // the
																			// status
																			// icon
				.setTicker(text) // the status text
				.setWhen(System.currentTimeMillis()) // the time stamp
				.setContentTitle("Notification") // the label of the entry
				.setContentText(text) // the contents of the entry
				.setContentIntent(contentIntent) // The intent to send when the
													// entry is clicked
				.setGroup("group1")
				.setVibrate(new long[] { 250, 250 })
				.build();

		// Send the notification.
		mNM.notify(NOTIFICATION, notification);
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
}