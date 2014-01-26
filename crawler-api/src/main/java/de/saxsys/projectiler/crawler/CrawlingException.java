package de.saxsys.projectiler.crawler;

public class CrawlingException extends RuntimeException {

	private static final long serialVersionUID = 7232508869516591946L;

	public CrawlingException(String message) {
		super(message);
	}

	public CrawlingException(String message, Throwable cause) {
		super(message, cause);
	}

}
