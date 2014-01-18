package de.saxsys.projectiler.crawler.selenium;

public class Settings {

	public String getProjectileUrl() {
		return System.getProperty("projectile.url", "https://pt.saxsys.de/projectile/start");
	}

	public String getDriver() {
		return System.getProperty("driver", "htmlUnit");
	}

	public String getFirefoxBinaryPath() {
		return System.getProperty("firefox.path",
				"c:\\Program Files (x86)\\Internet\\Mozilla Firefox\\firefox.exe");
	}

	public String getTimeFormat() {
		return "HH:mm";
	}
}
