package edu.sjsu.cmpe.procurement.jobs.pubsub;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;




import com.fasterxml.jackson.databind.JsonSerializer;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import edu.sjsu.cmpe.procurement.domain.Book;
import edu.sjsu.cmpe.procurement.domain.BooksDto;

public class OrderPullService {
	private static Client client;
	public static BooksDto pullBookOrder()
	{
		//JSONParser parser = new JSONParser();
		//ArrayList<Book> publisherBookResponse = new ArrayList<Book>();
		BooksDto publisherBookResponse = new BooksDto();
		try {
			client = Client.create();
			WebResource resource = client.resource("http://54.193.56.218:9000/orders/47221");
			ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE).
					get(ClientResponse.class);
			System.out.println("Response is : "+response);
			
			if (response.getStatus() != 200) {
			    throw new RuntimeException("Failed : HTTP error code : "
			            + response.getStatus());
			}
			
			String publisherResponse = response.getEntity(String.class);
			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON( publisherResponse );
			JSONArray shippedBooks = (JSONArray) jsonObject.getJSONArray("shipped_books");
			
			for(int i=0 ; i<shippedBooks.size() ; i++)
			{
				String bookString = shippedBooks.getString(i);
				System.out.println("individual object item is  : "+bookString);
				JSONObject shippedBook = (JSONObject) JSONSerializer.toJSON( bookString );
				Book book = new Book();
				book.setCategory(shippedBook.getString("category"));
				book.setCoverimage(shippedBook.getString("coverimage"));
				book.setIsbn(shippedBook.getLong("isbn"));
				book.setTitle(shippedBook.getString("title"));
				publisherBookResponse.setShipped_books(book);
			}

			if((response != null))
				System.out.println("Response from the publisher is : "+response);
			else
				System.out.println("Response from the publisher is null.");
		} catch (UniformInterfaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return publisherBookResponse;
	}
}
