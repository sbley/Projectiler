package de.saxsys.projectiler;

import java.util.List;
import java.util.Random;

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

	private User user;
	private Crawler crawler;

	public Projectiler(final User user, final Crawler crawler) {
		this.user = user;
		this.crawler = crawler;
	}

	public void clock(final String projectName) {
		crawler.clock(user, projectName);
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
