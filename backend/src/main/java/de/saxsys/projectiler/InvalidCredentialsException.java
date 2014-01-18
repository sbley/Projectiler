package de.saxsys.projectiler;

public class InvalidCredentialsException extends RuntimeException {

	private static final long serialVersionUID = 2792697136523800724L;

	public InvalidCredentialsException() {
		super("Invalid username or password.");
	}
}
