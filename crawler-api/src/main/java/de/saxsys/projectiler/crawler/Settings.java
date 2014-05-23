package de.saxsys.projectiler.crawler;

import java.util.concurrent.TimeUnit;

/**
 * Projectile settings.
 * 
 * @author stefan.bley
 */
public class Settings {

	public String getProjectileUrl() {
		return System.getProperty("projectile.url", "https://pt.saxsys.de/projectile/start");
	}

	/** Time format used in Projectile */
	public String getTimeFormat() {
		return "HH:mm";
	}

	/** Connection timeout in millis */
	public int getTimeout() {
		return (int) TimeUnit.SECONDS.toMillis(10);
	}
}
