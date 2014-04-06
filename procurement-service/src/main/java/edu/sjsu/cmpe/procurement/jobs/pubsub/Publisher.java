package edu.sjsu.cmpe.procurement.jobs.pubsub;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjsu.cmpe.procurement.ApolloConfig;
import edu.sjsu.cmpe.procurement.domain.Book;
import edu.sjsu.cmpe.procurement.domain.BooksDto;

public class Publisher {
	
	private final static Logger log = LoggerFactory.getLogger(Publisher.class);
	
	public static void publishBook(BooksDto books) throws JMSException
	{
		ApolloConfig serviceConfig = new ApolloConfig();
		String user = env("APOLLO_USER", serviceConfig.getUser());
		String password = env("APOLLO_PASSWORD", serviceConfig.getPassword());
		String host = env("APOLLO_HOST", serviceConfig.getHost());
		int port = Integer.parseInt(env("APOLLO_PORT", serviceConfig.getPort()));
		// Get Topic Destination
		
		StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
		factory.setBrokerURI("tcp://" + host + ":" + port);
		Connection connection = factory.createConnection(user, password);
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		/**
		 * Generate the message for each topic
		 *  	/topic/{last-5-digit-of-sjsu-id}.book.*
		 *  	/topic/{last-5-digit-of-sjsu-id}.book.computer
		 */
	
		//generate the message to be delivered
		
		for(int i=0 ; i < books.getShipped_books().size() ; i++)
		{
			System.out.println("********************************");
			System.out.println("Book category is : "+books.getShipped_books().get(i).getCategory().toLowerCase());
			
			Book book = books.getShipped_books().get(i);
			if("computer".equalsIgnoreCase(book.getCategory()))
			{
				String destinationB = serviceConfig.getTopic() + book.getCategory().toLowerCase();
				System.out.println("Topic destination : "+destinationB);
				Destination dest = new StompJmsDestination(destinationB);
				MessageProducer producer = session.createProducer(dest);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				
				StringBuilder sb = new StringBuilder();
				
				sb.append(String.valueOf(book.getIsbn()));
				sb.append(":").append(book.getTitle());
				sb.append(":").append(book.getCategory().toLowerCase());
				sb.append(":").append(book.getCoverimage());
				String message = sb.toString();
				
				TextMessage msg = session.createTextMessage(message);
				msg.setLongProperty("id", System.currentTimeMillis());
				System.out.println("Message is : "+ message);
				System.out.println("TO topic is : "+ destinationB);
//				log.debug("Text message : " , msg.toString());
//				log.debug("Message is : ", message);
				producer.send(msg);
			}
			
			else if("comics".equalsIgnoreCase(book.getCategory()))
			{
				String destination = serviceConfig.getTopic() + book.getCategory() ;
				System.out.println("Topic destination : "+destination);
				Destination dest = new StompJmsDestination(destination);
				MessageProducer producer = session.createProducer(dest);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				
				StringBuilder sb = new StringBuilder();
				
				sb.append(String.valueOf(book.getIsbn()));
				sb.append(":").append(book.getTitle());
				sb.append(":").append(book.getCategory().toLowerCase());
				sb.append(":").append(book.getCoverimage());
				String message = sb.toString();
				
				TextMessage msg = session.createTextMessage(message);
				msg.setLongProperty("id", System.currentTimeMillis());
				System.out.println("Message is : "+ message);
				System.out.println("TO topic is : "+ destination);
	//			log.debug("Text message : " , msg.toString());
	//			log.debug("Message is : ", message);
				producer.send(msg);
			}
			
			else if("management".equalsIgnoreCase(book.getCategory()))
			{
				String destination = serviceConfig.getTopic() + book.getCategory() ;
				System.out.println("Topic destination : "+destination);
				Destination dest = new StompJmsDestination(destination);
				MessageProducer producer = session.createProducer(dest);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				
				StringBuilder sb = new StringBuilder();
				
				sb.append(String.valueOf(book.getIsbn()));
				sb.append(":").append(book.getTitle());
				sb.append(":").append(book.getCategory().toLowerCase());
				sb.append(":").append(book.getCoverimage());
				String message = sb.toString();
				
				TextMessage msg = session.createTextMessage(message);
				msg.setLongProperty("id", System.currentTimeMillis());
				System.out.println("Message is : "+ message);
				System.out.println("TO topic is : "+ destination);
	//			log.debug("Text message : " , msg.toString());
	//			log.debug("Message is : ", message);
				producer.send(msg);
			}
			
			else if("selfimprovement".equalsIgnoreCase(book.getCategory()))
			{
				String destination = serviceConfig.getTopic() + book.getCategory() ;
				System.out.println("Topic destination : "+destination);
				Destination dest = new StompJmsDestination(destination);
				MessageProducer producer = session.createProducer(dest);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				
				StringBuilder sb = new StringBuilder();
				
				sb.append(String.valueOf(book.getIsbn()));
				sb.append(":").append(book.getTitle());
				sb.append(":").append(book.getCategory().toLowerCase());
				sb.append(":").append(book.getCoverimage());
				String message = sb.toString();
				
				TextMessage msg = session.createTextMessage(message);
				msg.setLongProperty("id", System.currentTimeMillis());
				System.out.println("Message is : "+ message);
				System.out.println("TO topic is : "+ destination);
	//			log.debug("Text message : " , msg.toString());
	//			log.debug("Message is : ", message);
				producer.send(msg);
			}
		}
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

}
