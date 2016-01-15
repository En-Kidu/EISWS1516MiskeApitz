package rabbitmq;

import java.io.UnsupportedEncodingException;

import com.example.mdks_client_pfleger.MainActivity;
import com.example.mdks_client_pfleger.R;

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
import rabbitmq.MessageConsumer.OnReceiveMessageHandler;

public class PushService extends Service {

	protected static final String ACTION_BROADCAST = "rabbitmq.publish";
	private NotificationManager mNM;
	private MessageConsumer mConsumer;
	private int NOTIFICATION = R.string.local_service_started;
	private final IBinder mBinder = new PushServiceBinder();
	
	public class PushServiceBinder extends Binder {
		PushService getService() {
			return PushService.this;
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Toast.makeText(this, "Service gestartet", Toast.LENGTH_SHORT).show();
		mConsumer = new MessageConsumer("10.0.2.2", "amq.topic", "topic", "personal1");
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
				Intent broadcastIntent = new Intent(ACTION_BROADCAST);
				broadcastIntent.putExtra("publish", text);
				getApplicationContext().sendBroadcast(broadcastIntent);
			}
		});

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		mNM.cancel(NOTIFICATION);
		Toast.makeText(this, "Service gestoppt", Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private void showNotification(String text) {
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(android.R.drawable.ic_notification_overlay).setTicker(text)
				.setWhen(System.currentTimeMillis()).setContentTitle("Notification").setContentText(text)
				.setContentIntent(contentIntent).setGroup("group1").setVibrate(new long[] { 250, 250 }).build();

		mNM.notify(NOTIFICATION, notification);
	}

	private class consumerconnect extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... Message) {
			try {
				mConsumer.connectToRabbitMQ();
				//TODO BIND ROUTING KEYS

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

}