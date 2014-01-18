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

	private User user = new User("stefan.bley", Password.get());;
	private Crawler crawler;

	public Projectiler(final Crawler crawler) {
		this.crawler = crawler;
	}

	public void clock(String projectName) {
		crawler.clock(user, projectName);
	}

	public List<String> getProjectNames() {
		return crawler.getProjectNames(user);
	}

	public static void main(String[] args) {
		Projectiler projectiler = new Projectiler(new SeleniumCrawler(new Settings()));
		List<String> projectNames = projectiler.getProjectNames();
		projectiler.clock(projectNames.get(new Random().nextInt(projectNames.size() - 1)) + 1);
	}
}
