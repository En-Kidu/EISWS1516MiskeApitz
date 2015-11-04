package com.example.eis_poc_rabbitmq;

import java.io.IOException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class IConnectToRabbitMQ {
	  public String mServer;
    public String mExchange;

    protected Channel mModel = null;
    protected Connection  mConnection;

    protected boolean Running ;

    protected  String MyExchangeType ;

    public IConnectToRabbitMQ(String server, String exchange, String exchangeType)
    {
  	  mServer = server;
  	  mExchange = exchange;
        MyExchangeType = exchangeType;
    }

    public void Dispose()
    {
        Running = false;

			try {
				if (mConnection!=null)
					mConnection.close();
				if (mModel != null)
					mModel.abort();
			} catch (IOException e) {
				e.printStackTrace();
			}

    }

    public boolean connectToRabbitMQ()
    {
  	  if(mModel!= null && mModel.isOpen() )
  		  return true;
        try
        {
      	  ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(mServer);
            connectionFactory.setUsername("test");
            connectionFactory.setPassword("test");
            mConnection = connectionFactory.newConnection();
            mModel = mConnection.createChannel();
            mModel.exchangeDeclare(mExchange, MyExchangeType, true);

            return true;
        }
        catch (Exception e)
        {
      	  e.printStackTrace();
            return false;
        }
    }
}