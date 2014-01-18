package de.saxsys.projectiler.worker;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;
import de.saxsys.projectiler.crawler.Settings;
import de.saxsys.projectiler.crawler.selenium.SeleniumCrawler;

public class ClockTask extends Task<Boolean> {

	private final String projectKey;

	public ClockTask(final String projectKey) {
		this.projectKey = projectKey;
	}

	@Override
	protected Boolean call() throws Exception {
		try {
			final Projectiler projectiler = new Projectiler(new SeleniumCrawler(new Settings()));
			projectiler.checkout(projectKey);
		} catch (final Exception e) {
			e.printStackTrace();
			this.succeeded();
			return false;
		}
		this.succeeded();
		return true;
	}
}
