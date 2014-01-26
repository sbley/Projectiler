package de.saxsys.projectiler.crawler.selenium;

public class SeleniumSettings {

	public String getDriver() {
		return System.getProperty("driver", "htmlUnit");
	}

	public String getFirefoxBinaryPath() {
		return System.getProperty("firefox.path",
				"c:\\Program Files (x86)\\Internet\\Mozilla Firefox\\firefox.exe");
	}

}
