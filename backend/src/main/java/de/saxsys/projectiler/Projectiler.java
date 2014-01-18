package de.saxsys.projectiler;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import de.saxsys.projectiler.crawler.Crawler;
import de.saxsys.projectiler.crawler.Credentials;
import de.saxsys.projectiler.crawler.Password;
import de.saxsys.projectiler.crawler.Settings;
import de.saxsys.projectiler.crawler.selenium.SeleniumCrawler;

/**
 * Automatic time tracking in Projectile
 * 
 * @author stefan.bley
 */
public class Projectiler {

	private static final Logger LOGGER = Logger.getLogger(Projectiler.class.getName());
	private final Credentials user;
	private final Crawler crawler;

	public static Projectiler createDefaultProjectiler() {
		return new Projectiler(UserDataStore.getInstance());
	}

	protected Projectiler(final UserDataStore store) {
		this(new Credentials(store.getUserName(), store.getPassword()), new SeleniumCrawler(
				new Settings()));
	}

	protected Projectiler(final Credentials credentials, final Crawler crawler) {
		user = credentials;
		this.crawler = crawler;
	}

	public void checkin() {
		Date startDate = new Date();
		UserDataStore.getInstance().setStartDate(startDate);
		LOGGER.info("Checked in at " + formatDate(startDate));
	}

	public int checkout(final String projectName) {
		UserDataStore store = UserDataStore.getInstance();
		final Date start = store.getStartDate();
		final Date end = new Date();
		crawler.clock(user, projectName, start, end);
		store.clearStartDate();
		store.save();
		LOGGER.info("Checked out at " + formatDate(end));
		return duration(start, end);
	}

	private int duration(final Date start, final Date end) {
		return (int) TimeUnit.MINUTES.convert(end.getTime() - start.getTime(),
				TimeUnit.MILLISECONDS);
	}

	private String formatDate(Date startDate) {
		return DateFormat.getDateInstance(DateFormat.SHORT).format(startDate);
	}

	public List<String> getProjectNames() {
		return crawler.getProjectNames(user);
	}

	public static void main(final String[] args) {
		final Projectiler projectiler = new Projectiler(new Credentials("stefan.bley",
				Password.get()), new SeleniumCrawler(new Settings()));
		final List<String> projectNames = projectiler.getProjectNames();
		projectiler.checkout(projectNames.get(new Random().nextInt(projectNames.size() - 2) + 1));
	}
}
