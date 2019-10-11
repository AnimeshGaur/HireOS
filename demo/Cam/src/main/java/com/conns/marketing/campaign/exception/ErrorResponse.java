package com.conns.marketing.campaign.exception;

public class ErrorResponse {
	private String message;
	private int code;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return code;
	}

	public void setStatus(int status) {
		this.code = status;
	}
}
