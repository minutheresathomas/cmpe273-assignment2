package edu.sjsu.cmpe.library.message.broker.ptp;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.eclipse.jetty.util.log.Log;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.jersey.params.LongParam;

import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;

//import org.apache.activemq.transport.stomp.StompConnection;
public class Producer {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public void sendMessage(LongParam isbn, LibraryServiceConfiguration serviceConfig) throws JMSException
	{
		//LibraryServiceConfiguration serviceConfig = new LibraryServiceConfiguration();
		String user = env("APOLLO_USER", serviceConfig.getApolloUser());
		String password = env("APOLLO_PASSWORD", serviceConfig.getApolloPassword());
		String host = env("APOLLO_HOST", serviceConfig.getApolloHost());
		System.out.println("Service config port is : "+serviceConfig.getApolloPort());
		//int port = Integer.parseInt(env("APOLLO_PORT", serviceConfig.getApolloPort()));
		int port = Integer.parseInt(serviceConfig.getApolloPort());
		
		String queue = serviceConfig.getStompQueueName();
		String args[] = new String[] {};
		String destination = arg(args, 0, queue);
		
		StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
		factory.setBrokerURI("tcp://" + host + ":" + port);
		Connection connection = factory.createConnection(user, password);
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = new StompJmsDestination(destination);
		MessageProducer producer = session.createProducer(dest);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		
		
		String message = serviceConfig.getLibraryName()+":"+isbn;
		System.out.println("Message created is : "+message);
		TextMessage txtMsg = session.createTextMessage(message);
		txtMsg.setLongProperty("id", System.currentTimeMillis());
		System.out.println("Text message : " + txtMsg.toString());
		System.out.println("Message is : "+ message);
		log.debug("Text message : " + txtMsg.toString());
		log.debug("Message is : "+ message);
		producer.send(txtMsg);
		
		producer.send(session.createTextMessage("SHUTDOWN"));
		connection.close();
		System.out.println("Connection Closed...");
	}
	
	private static String env(String key, String defaultValue)
	{
		String val = System.getenv(key);
		if(val == null)
			return defaultValue;
		return val;
	}
	
	private static String arg(String[] args, int index, String defaultQueue)
	{
		if(index < args.length)
		{
			return args[index];
		}
		else
			return defaultQueue;
	}
}
