package edu.sjsu.cmpe.library.message.broker.pubsub;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.status.StatusChecker;
import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.domain.Book.Status;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;

public class Listener {
	
	private LibraryServiceConfiguration serviceConfig;
	private BookRepositoryInterface bookRepository;
	
	public Listener(LibraryServiceConfiguration serviceConfig, 
    		 BookRepositoryInterface bookRepository)
	{
		this.serviceConfig = serviceConfig;
		this.bookRepository = bookRepository;
	}
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public void listenFromTopic() throws JMSException, MalformedURLException
	{
		String user = env("APOLLO_USER", serviceConfig.getApolloUser());
		String password = env("APOLLO_PASSWORD", serviceConfig.getApolloPassword());
		String host = env("APOLLO_HOST", serviceConfig.getApolloHost());
		System.out.println("Service config port is : "+serviceConfig.getApolloPort());
		int port = Integer.parseInt(serviceConfig.getApolloPort());
		
		String library = serviceConfig.getLibraryName();
		String topic = serviceConfig.getStompTopicName();
		System.out.println("topic is : "+topic);
		String destination = topic;
		System.out.println("destination is : "+ destination);
		
		StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
		factory.setBrokerURI("tcp://" + host + ":" + port);
		Connection connection = factory.createConnection(user, password);
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = new StompJmsDestination(destination);
		MessageConsumer consumer = session.createConsumer(dest);
		while(true) {
			Message msg = consumer.receive();
		    if( msg instanceof  TextMessage || msg instanceof StompJmsMessage) {
		           String body = ((TextMessage) msg).getText();
		           System.out.println("------------------------------------------------");
		           System.out.println("Received message = " + body);
		           if(!"SHUTDOWN".equals(body)) {
			           log.debug("Received message is : ", body);
			           // Process the received message and add it to the hashMap
			           processMessage(body);
			           System.out.println("===========================");
			           System.out.println("from topic : "+topic);
			           System.out.println("by library : "+library);
			           System.out.println("===========================");
			           System.out.println("------------------------------------------------");
		           }
		           else
		           {
		        	   System.out.println("exiting from the loop..");
		        	   break;
		           }
		    } else {
		         System.out.println("Unexpected message type: " + msg.getClass());
		    }
		} // end while loop
		connection.close();
		System.out.println("Done");
	}
	
	private String env(String key, String defaultValue)
	{
		String val = System.getenv(key);
		if(val == null)
			return defaultValue;
		return val;
	}
	
	private String arg(String[] args, int index, String defaultQueue)
	{
		if(index < args.length)
		{
			return args[index];
		}
		else
			return defaultQueue;
	}
	
	private void processMessage(String body) throws MalformedURLException
	{
		String[] segments = body.split(":",4);
		Book newBook = new Book();

     	   System.out.println("Segment: " + segments);
     	   // Iterate through hash map to check the isbn alreay exists or not
     	   if((!segments[0].isEmpty()) && (!segments[1].isEmpty()) && (!segments[2].isEmpty()) 
     			   && (!segments[3].isEmpty()))
     	   {
     		   long isbn = Long.parseLong(segments[0]);
     		   Book book = bookRepository.getBookByISBN(isbn);
     		   if((book != null) && (!("available").equals(book.getStatus())))
     		   {
     			   book.setStatus(Status.available);
     			   System.out.println("Status of book with ISBN "+isbn+" is set to available");
     		   }
     		   else
     		   {
     			   newBook.setIsbn(isbn);
     			   newBook.setTitle(segments[1]);
     			   newBook.setCategory(segments[2]);
     			   //URL url = new URL((segments[3])+":"+(segments[4]));
     			  URL url = new URL(segments[3]);
     			   newBook.setCoverimage(url);
     			   bookRepository.saveBook(newBook);
     			   System.out.println("A new book with isbn "+isbn+ " has been added!");
     			   
     		   }
     	   }
    }
}
