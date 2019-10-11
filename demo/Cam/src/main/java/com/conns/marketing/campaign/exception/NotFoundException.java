package com.conns.marketing.campaign.exception;

public class NotFoundException extends Exception{

	private static final long serialVersionUID = 1L;
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public NotFoundException(String message) {
		super(message);
		this.message = message;
	}

	public NotFoundException() {
		super();
	}
	
}
