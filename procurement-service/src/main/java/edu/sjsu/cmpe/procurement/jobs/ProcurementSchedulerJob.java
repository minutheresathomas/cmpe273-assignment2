package edu.sjsu.cmpe.procurement.jobs;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.procurement.ProcurementService;
import edu.sjsu.cmpe.procurement.domain.Book;
import edu.sjsu.cmpe.procurement.domain.BookOrder;
import edu.sjsu.cmpe.procurement.domain.BooksDto;
import edu.sjsu.cmpe.procurement.jobs.ptp.Consumer;
import edu.sjsu.cmpe.procurement.jobs.ptp.OrderSubmitService;
import edu.sjsu.cmpe.procurement.jobs.pubsub.OrderPullService;
import edu.sjsu.cmpe.procurement.jobs.pubsub.Publisher;

/**
 * This job will run at every 5 second.
 */
@Every("5mn")
public class ProcurementSchedulerJob extends Job {
    private final Logger log = LoggerFactory.getLogger(getClass()); 

    @Override
    public void doJob() {
	    
    	Set<Integer> isbnList = new HashSet<Integer>();
	    BookOrder order = new BookOrder();
	    BooksDto shippedBooks = new BooksDto();
	    
		String strResponse = ProcurementService.jerseyClient.resource(
			"http://ip.jsontest.com/").get(String.class);
		log.debug("Response from jsontest.com: {}", strResponse);
		try
		{
			/**
			 * Do job to consume the book orders from the Apollo queue
			 */
			isbnList = Consumer.getBookOrders();
			System.out.println("IsbnList : "+isbnList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		/**
		 * Do Job to Submit the book orders from queue to the publisher
		 */
		try {
			if((isbnList!=null) && !isbnList.isEmpty())
			{
				System.out.println("IsbnList in job : "+isbnList);
				order.setOrder_book_isbns(isbnList);
				OrderSubmitService.submitBookOrder(order);
			}
		} 
		catch (Exception e2) 
		{
			e2.printStackTrace();
		}
		
		/**
		 * Do job to Pull the shipped books from the Publisher
		 */
		try {
			shippedBooks = OrderPullService.pullBookOrder();
			System.out.println("Shipped books( in job ) : " + shippedBooks);
		} 
		catch (Exception e1) {
			e1.printStackTrace();
		}
		
		/**
		 * Do job to publish the shipped books to the topic
		 */
		try {
			if((shippedBooks != null) && (!shippedBooks.getShipped_books().isEmpty()))
				Publisher.publishBook(shippedBooks);
		} 
		catch (JMSException e) {
			e.printStackTrace();
		}
    }
    
}
