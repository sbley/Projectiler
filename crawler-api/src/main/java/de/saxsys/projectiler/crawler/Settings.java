package de.saxsys.projectiler.crawler;

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
}
