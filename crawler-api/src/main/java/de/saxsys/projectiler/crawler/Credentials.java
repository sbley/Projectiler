package de.saxsys.projectiler.crawler;

/**
 * Credentials to log into Projectile.
 * 
 * @author stefan.bley
 */
public final class Credentials {

	private final String username;
	private final String password;

	public Credentials(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
