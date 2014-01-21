package de.saxsys.projectiler;

public class InvalidCredentialsException extends ProjectilerException {

	private static final long serialVersionUID = -2685439249041788899L;

	public InvalidCredentialsException() {
		super("Invalid username or password.");
	}
}
