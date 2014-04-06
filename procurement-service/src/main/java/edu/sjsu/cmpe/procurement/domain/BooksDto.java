package edu.sjsu.cmpe.procurement.domain;

import java.util.ArrayList;

public class BooksDto {
	
	private ArrayList<Book> shipped_books;
	
	public BooksDto()
	{
		shipped_books = new ArrayList<Book>();
	}

	public ArrayList<Book> getShipped_books() {
		return shipped_books;
	}

	public void setShipped_books(Book book) {
		this.shipped_books.add(book);
	}

}
