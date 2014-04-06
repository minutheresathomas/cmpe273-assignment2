package edu.sjsu.cmpe.procurement.jobs.ptp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjsu.cmpe.procurement.ApolloConfig;
import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import edu.sjsu.cmpe.procurement.domain.BookOrder;

public class Consumer {
	
	
	private static final Logger log = LoggerFactory.getLogger(Consumer.class);
	
	public static Set<Integer> getBookOrders() throws JMSException
	{
		Set<Integer> isbnList = new HashSet<Integer>();
		ApolloConfig serviceConfig = new ApolloConfig();
		String user = env("APOLLO_USER", serviceConfig.getUser());
		String password = env("APOLLO_PASSWORD", serviceConfig.getPassword());
		String host = env("APOLLO_HOST", serviceConfig.getHost());
		int port = Integer.parseInt(env("APOLLO_PORT", serviceConfig.getPort()));
		
//		String queue = serviceConfig.getStompQueueName();
//		String args[] = new String[] {};
//		String destination = arg(args, 0, queue);
		String destination = serviceConfig.getQueue();
		
		StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
		factory.setBrokerURI("tcp://" + host + ":" + port);
		Connection connection = factory.createConnection(user, password);
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = new StompJmsDestination(destination);
		MessageConsumer consumer = session.createConsumer(dest);
		long waitUntil = 5000; // wait for 5 sec
		while(true) {
		    Message msg = consumer.receive(waitUntil);
		    if( msg instanceof  TextMessage ) {
		           String body = ((TextMessage) msg).getText();
		           System.out.println("Received message = " + body);
		           if(!"SHUTDOWN".equals(body)) {
			           log.debug("Received message is : ", body);
			           String[] segments = body.split(":");
			           System.out.println("Segment 0 : "+segments[0]+ " : segment 1 : "+ segments[1]);
			           isbnList.add(Integer.parseInt(segments[1]));
			           //order.setOrder_book_isbns(isbnList);
		           }
		           else
		           {
		        	   System.out.println("exiting from the loop..");
		        	   break;
		           }
		    } else if (msg == null) {
		          System.out.println("No new messages. Existing due to timeout - " + waitUntil / 1000 + " sec");
		          break;
		    } else {
		         System.out.println("Unexpected message type: " + msg.getClass());
		    }
		} // end while loop
		connection.close();
		System.out.println("Done");
		return isbnList;
	}
	
	private static String env(String key, String defaultValue)
	{
		String val = System.getenv(key);
		if(val == null)
			return defaultValue;
		return val;
	}
	
//	private static String arg(String[] args, int index, String defaultQueue)
//	{
//		if(index < args.length)
//		{
//			return args[index];
//		}
//		else
//			return defaultQueue;
//	}
}
