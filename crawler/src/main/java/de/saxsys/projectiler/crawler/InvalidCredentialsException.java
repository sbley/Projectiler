package de.saxsys.projectiler.crawler;

public class InvalidCredentialsException extends CrawlingException {

	private static final long serialVersionUID = 2792697136523800724L;

	public InvalidCredentialsException() {
		super("Invalid username or password.");
	}
}
