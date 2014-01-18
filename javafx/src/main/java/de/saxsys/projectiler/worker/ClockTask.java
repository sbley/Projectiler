package de.saxsys.projectiler.worker;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;
import de.saxsys.projectiler.crawler.selenium.SeleniumCrawler;
import de.saxsys.projectiler.crawler.selenium.Settings;
import de.saxsys.projectiler.domain.User;

public class ClockTask extends Task<Boolean> {

	private final String projectKey;
	private final String password;
	private final String userName;

	public ClockTask(final String userName, final String password, final String projectKey) {
		this.userName = userName;
		this.password = password;
		this.projectKey = projectKey;
	}

	@Override
	protected Boolean call() throws Exception {
		try {
			final Projectiler projectiler = new Projectiler(new User(userName, password),
					new SeleniumCrawler(new Settings()));
			projectiler.clock(projectKey);
		} catch (final Exception e) {
			e.printStackTrace();
			this.succeeded();
			return false;
		}
		this.succeeded();
		return true;
	}
}
