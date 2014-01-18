package de.saxsys.projectiler;

import javafx.concurrent.Task;
import de.saxsys.projectiler.selenium.SeleniumCrawler;
import de.saxsys.projectiler.selenium.Settings;

public class ProjectilerTask extends Task<Boolean> {

	@Override
	protected Boolean call() throws Exception {
		try {
			final Projectiler projectiler = new Projectiler(new SeleniumCrawler(new Settings()));
			System.out.println("Deine Projekte: " + projectiler.getProjectNames());
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
