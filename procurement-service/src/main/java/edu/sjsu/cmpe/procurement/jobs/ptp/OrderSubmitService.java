package edu.sjsu.cmpe.procurement.jobs.ptp;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import edu.sjsu.cmpe.procurement.domain.BookOrder;

public class OrderSubmitService {
	private static Client client;
	
	public static void submitBookOrder(BookOrder order)
	{
		String publisherResponse;
		try {
			client = Client.create();
			WebResource resource = client.resource("http://54.193.56.218:9000/orders");
//			String request = "{\"id\" : \"47221\" , \"order_book_isbns\" : "
//								+ order.getOrder_book_isbns().toString()+"}";
			ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE).
					post(ClientResponse.class, order);
			System.out.println("Response is : "+response);
			System.out.println("##################################");
			if (response.getStatus() != 200) {
			    throw new RuntimeException("Failed : HTTP error code : "
			            + response.getStatus());
			}
			
			publisherResponse = response.getEntity(String.class);
			if((publisherResponse != null) && (!publisherResponse.isEmpty()))
				System.out.println("Response from the publisher is : "+publisherResponse);
			else
				System.out.println("Response from the publisher is null.");
		} catch (UniformInterfaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
