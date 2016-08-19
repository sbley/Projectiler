package de.saxsys.projectiler.api;

/**
 * Exception to be thrown by Crawler implementations if credentials are invalid.
 * 
 * @author stefan.bley
 */
public final class InvalidCredentialsException extends ProjectileApiException {

	private static final long serialVersionUID = 2792697136523800724L;

	public InvalidCredentialsException() {
		super("Invalid username or password.");
	}
}
