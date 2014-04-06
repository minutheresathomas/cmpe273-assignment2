package edu.sjsu.cmpe.procurement.domain;

import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookOrder {
	
	@NotNull
	@JsonProperty
	private String id="47221";
	
	@JsonInclude(Include.NON_EMPTY)
	@JsonProperty
	private Set<Integer> order_book_isbns;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<Integer> getOrder_book_isbns() {
		return order_book_isbns;
	}

	public void setOrder_book_isbns(Set<Integer> order_book_isbns) {
		this.order_book_isbns = order_book_isbns;
	}
	
}
