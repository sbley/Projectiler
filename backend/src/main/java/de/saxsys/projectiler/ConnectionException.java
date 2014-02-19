package de.saxsys.projectiler;

public class ConnectionException extends ProjectilerException {

	private static final long serialVersionUID = -6633352843142873730L;

	public ConnectionException(Throwable cause) {
		super("Unable to connect to Projectile", cause);
	}

}
