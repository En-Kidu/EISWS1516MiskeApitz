package com.example.eis_poc;

import java.io.IOException;
import android.os.Handler;
import android.util.Log;
import com.rabbitmq.client.QueueingConsumer;

public class MessageConsumer extends IConnectToRabbitMQ {

	private String queuename;

	private String mQueue;

	private QueueingConsumer MySubscription;

	private byte[] mLastMessage;

	public Thread thread;

	public MessageConsumer(String server, String exchange, String exchangeType, String queue) {

		super(server, exchange, exchangeType);

		queuename = queue;

	}

	public interface OnReceiveMessageHandler {

		public void onReceiveMessage(byte[] message);

	};

	private OnReceiveMessageHandler mOnReceiveMessageHandler;

	public void setOnReceiveMessageHandler(OnReceiveMessageHandler handler) {

		mOnReceiveMessageHandler = handler;

	};

	private Handler mMessageHandler = new Handler();

	private Handler mConsumeHandler = new Handler();

	final Runnable mReturnMessage = new Runnable() {

		public void run() {

			mOnReceiveMessageHandler.onReceiveMessage(mLastMessage);

		}
	};

	final Runnable mConsumeRunner = new Runnable() {

		public void run() {

			Consume();

		}

	};

	@Override
	public boolean connectToRabbitMQ() {

		if (super.connectToRabbitMQ()) {

			try {

				mQueue = mModel.queueDeclare(queuename, true, false, false, null).getQueue();

				MySubscription = new QueueingConsumer(mModel);

				mModel.basicConsume(mQueue, false, MySubscription);

			} catch (IOException e) {

				e.printStackTrace();

				return false;

			}
			if (MyExchangeType == "fanout") {
				
				AddBinding("");
				
			}

			Running = true;
			
			mConsumeHandler.post(mConsumeRunner);

			return true;
			
		}
		
		return false;
		
	}

	public void AddBinding(String routingKey) {
		
		try {
			
			mModel.queueBind(mQueue, mExchange, routingKey);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}

	public void RemoveBinding(String routingKey) {
		
		try {
			
			mModel.queueUnbind(mQueue, mExchange, routingKey);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}

	private void Consume() {
		
		thread = new Thread() {

			@Override
			public void run() {
				
				while (Running) {
					
					QueueingConsumer.Delivery delivery;
					
					try {
						
						delivery = MySubscription.nextDelivery();
						
						mLastMessage = delivery.getBody();
						
						Log.v("mLastMessage ", mLastMessage.toString());
						
						mMessageHandler.post(mReturnMessage);
						
						try {
							
							mModel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
							
						} catch (IOException e) {
							
							e.printStackTrace();
							
						}
					} catch (InterruptedException ie) {
						
						ie.printStackTrace();
						
					}
					
				}
				
			}
			
		};
		
		thread.start();

	}

	public void dispose() {
		
		Running = false;
		
	}
	
}
