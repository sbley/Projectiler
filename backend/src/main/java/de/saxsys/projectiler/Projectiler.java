package de.saxsys.projectiler;

import java.util.Date;
import java.util.List;
import java.util.Random;

import de.saxsys.projectiler.crawler.Crawler;
import de.saxsys.projectiler.crawler.Credentials;
import de.saxsys.projectiler.crawler.Password;
import de.saxsys.projectiler.crawler.selenium.SeleniumCrawler;
import de.saxsys.projectiler.crawler.selenium.Settings;
import de.saxsys.projectiler.domain.User;

/**
 * Automatic time tracking in Projectile
 * 
 * @author stefan.bley
 */
public class Projectiler {

	private Credentials user;
	private Crawler crawler;

	public Projectiler(final User user, final Crawler crawler) {
		this.user = new Credentials(user.getUsername(), user.getPassword());
		this.crawler = crawler;
	}

	public void clock(final String projectName) {
		// TODO retrieve start time
		Date start = new Date(2017, 0, 1, 8, 30);
		Date end = new Date();
		crawler.clock(user, projectName, start, end);
	}

	public List<String> getProjectNames() {
		return crawler.getProjectNames(user);
	}

	public static void main(String[] args) {
		Projectiler projectiler = new Projectiler(new User("stefan.bley", Password.get()),
				new SeleniumCrawler(new Settings()));
		List<String> projectNames = projectiler.getProjectNames();
		projectiler.clock(projectNames.get(new Random().nextInt(projectNames.size() - 2) + 1));
	}
}
