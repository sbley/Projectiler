package de.saxsys.projectiler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import de.saxsys.projectiler.crawler.Crawler;
import de.saxsys.projectiler.crawler.CrawlingException;
import de.saxsys.projectiler.crawler.Credentials;
import de.saxsys.projectiler.crawler.Settings;
import de.saxsys.projectiler.crawler.jsoup.JSoupCrawler;

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
		return new Projectiler(new JSoupCrawler(new Settings()));
	}

	protected Projectiler(final Crawler crawler) {
		this.crawler = crawler;
	}

	public String getUserName() {
		return dataStore.getUserName();
	}

	public boolean isCheckedIn() {
		return dataStore.isCheckedIn();
	}

	/**
	 * Invoke a checkin, i.e. start a working period.
	 * 
	 * @return checkin date
	 */
	public Date checkin() {
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
	 * @throws ConnectionException
	 *             if connection to Projectile fails
	 * @throws ProjectilerException
	 *             in case backend operations fail
	 * @throws IllegalStateException
	 *             if invoked while not being checked in or if work time is less
	 *             than one minute
	 */
	public Date checkout(final String projectName) throws ConnectionException,
			ProjectilerException, IllegalStateException {
		if (!isCheckedIn()) {
			throw new IllegalStateException("Must be checked in before checking out.");
		}

		final Date start = dataStore.getStartDate();
		final Date end = new Date();
		if (formatHHmm(start).equals(formatHHmm(end))) {
			dataStore.clearStartDate();
			dataStore.save();
			throw new IllegalStateException("Work time must be at least 1 minute.");
		}

		try {
			crawler.clock(createCredentials(), projectName, start, end);
			dataStore.clearStartDate();
			dataStore.save();
			LOGGER.info("Checked out at " + formatDate(end));
			return end;
		} catch (final de.saxsys.projectiler.crawler.ConnectionException e) {
			throw new ConnectionException(e);
		} catch (final CrawlingException e) {
			throw new ProjectilerException(e.getMessage(), e);
		}
	}

	/**
	 * Retrieve available project names.
	 * 
	 * @return list of project names or empty list
	 * @throws ConnectionException
	 *             if connection to Projectile fails
	 * @throws ProjectilerException
	 *             in case backend operations fail
	 */
	public List<String> getProjectNames() throws ConnectionException, ProjectilerException {
		try {
			return crawler.getProjectNames(createCredentials());
		} catch (final de.saxsys.projectiler.crawler.ConnectionException e) {
			throw new ConnectionException(e);
		} catch (final CrawlingException e) {
			throw new ProjectilerException(e.getMessage(), e);
		}
	}

	/**
	 * Verify credentials and save them to the userdata store.
	 * 
	 * @param username
	 *            Projectile username
	 * @param password
	 *            Projectile password
	 * @throws ConnectionException
	 *             if connection to Projectile fails
	 * @throws InvalidCredentialsException
	 *             if credentials are invalid
	 * @throws ProjectilerException
	 *             in case backend operations fail
	 */
	public void saveCredentials(final String username, final String password)
			throws InvalidCredentialsException, ConnectionException, ProjectilerException {
		try {
			crawler.checkCredentials(new Credentials(username, password));
			dataStore.setCredentials(username, password);
			dataStore.save();
		} catch (final de.saxsys.projectiler.crawler.InvalidCredentialsException e) {
			throw new InvalidCredentialsException();
		} catch (final de.saxsys.projectiler.crawler.ConnectionException e) {
			throw new ConnectionException(e);
		} catch (final CrawlingException e) {
			throw new ProjectilerException(e.getMessage(), e);
		}
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
}
