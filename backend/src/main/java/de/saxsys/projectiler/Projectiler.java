package de.saxsys.projectiler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import de.saxsys.projectiler.crawler.Crawler;
import de.saxsys.projectiler.crawler.Credentials;
import de.saxsys.projectiler.crawler.Password;
import de.saxsys.projectiler.crawler.Settings;
import de.saxsys.projectiler.crawler.selenium.SeleniumCrawler;

/**
 * Automatic time tracking in Projectile.
 * 
 * @author stefan.bley
 */
public class Projectiler {

	private static final Logger LOGGER = Logger.getLogger(Projectiler.class.getSimpleName());
	private final UserDataStore dataStore = UserDataStore.getInstance();
	private final Crawler crawler;

	public static Projectiler createDefaultProjectiler() {
		return new Projectiler(new SeleniumCrawler(new Settings()));
	}

	protected Projectiler(final Crawler crawler) {
		this.crawler = crawler;
	}

	public boolean isCheckedIn() {
		return dataStore.isCheckedIn();
	}

	/**
	 * Invoke a checkin, i.e. start a working period.
	 * 
	 * @return checkin date
	 * @throws ProjectilerException
	 *             in case backend operations fail
	 */
	public Date checkin() throws ProjectilerException {
		final Date start = new Date();
		dataStore.setStartDate(start);
		dataStore.save();
		LOGGER.info("Checked in at " + formatDate(start));
		return start;
	}

	/**
	 * Invoke a checkout, i.e. finish a working period.
	 * 
	 * @param projectName
	 *            project to clock your time to
	 * @throws ProjectilerException
	 *             in case backend operations fail
	 * @throws IllegalArgumentException
	 *             if invoked while not being checked in or if work time is less
	 *             than one minute
	 */
	public Date checkout(final String projectName) throws ProjectilerException,
			IllegalArgumentException {
		if (!isCheckedIn()) {
			throw new IllegalArgumentException("Must be checked in before checking out.");
		}

		final Date start = dataStore.getStartDate();
		final Date end = new Date();
		if (formatHHmm(start).equals(formatHHmm(end))) {
			throw new IllegalArgumentException("Work time must be at least 1 minute.");
		}

		try {
			crawler.clock(createCredentials(), projectName, start, end);
			dataStore.clearStartDate();
			dataStore.save();
			LOGGER.info("Checked out at " + formatDate(end));
			return end;
		} catch (de.saxsys.projectiler.crawler.InvalidCredentialsException e) {
			throw new InvalidCredentialsException();
		} catch (Exception e) {
			throw new ProjectilerException(e.getMessage(), e);
		}
	}

	/**
	 * Retrieve available project names.
	 * 
	 * @return list of project names or empty list
	 * @throws ProjectilerException
	 *             in case backend operations fail
	 */
	public List<String> getProjectNames() throws ProjectilerException {
		return crawler.getProjectNames(createCredentials());
	}

	public void saveCredentials(final String username, final String password) {
		dataStore.setCredentials(username, password);
		dataStore.save();
	}

	public void saveProjectName(final String projectKey) {
		dataStore.setProjectName(projectKey);
		dataStore.save();
	}

	private String formatDate(final Date date) {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
	}

	private String formatHHmm(final Date date) {
		return new SimpleDateFormat("HHmm").format(date);
	}

	private Credentials createCredentials() {
		return new Credentials(dataStore.getUserName(), dataStore.getPassword());
	}

	public static void main(final String[] args) throws ProjectilerException {
		// common use case sequence
		final Projectiler projectiler = createDefaultProjectiler();
		projectiler.saveCredentials("stefan.bley", Password.get());
		projectiler.checkin();
		final List<String> projectNames = projectiler.getProjectNames();
		projectiler.checkout(projectNames.get(new Random().nextInt(projectNames.size() - 1)));
	}
}
