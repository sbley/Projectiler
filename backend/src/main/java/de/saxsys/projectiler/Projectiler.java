package de.saxsys.projectiler;

import de.saxsys.projectiler.domain.Password;
import de.saxsys.projectiler.domain.User;
import de.saxsys.projectiler.selenium.SeleniumCrawler;
import de.saxsys.projectiler.selenium.Settings;

/**
 * Automatic time tracking in Projectile
 * 
 * @author stefan.bley
 */
public class Projectiler {

	private User user = new User("stefan.bley", Password.get());;
	private String projectName = "10-12/13";
	private Crawler crawler;

	public Projectiler(final Crawler crawler) {
		this.crawler = crawler;
	}

	public void clock() {
		crawler.clock(user, projectName);
	}

	public static void main(String[] args) {
		new Projectiler(new SeleniumCrawler(new Settings())).clock();
	}
}
