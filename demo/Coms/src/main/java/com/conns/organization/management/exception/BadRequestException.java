package com.conns.organization.management.exception;

public class BadRequestException extends Exception{
	
	private static final long serialVersionUID = 1L;
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public BadRequestException(String message) {
		super(message);
		this.message = message;
	}

	public BadRequestException() {
		super();
	}

}
