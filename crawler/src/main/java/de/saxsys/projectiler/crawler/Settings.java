package de.saxsys.projectiler.crawler;

public class Settings {

	public String getProjectileUrl() {
		return System.getProperty("projectile.url", "https://pt.saxsys.de/projectile/start");
	}

	public String getTimeFormat() {
		return "HH:mm";
	}
}
