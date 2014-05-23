package de.saxsys.android.projectiler.app.crawler;

import java.util.Date;
import java.util.List;

/**
 * Implementing classes interact with Projectile.
 * <p>
 * Implementations should not change the user's settings in Projectile and
 * should always logout properly.
 * </p>
 * 
 * @author stefan.bley
 */
public interface Crawler {

	/**
	 * Verifies the credentials are valid.
	 * 
	 * @throws InvalidCredentialsException
	 *             if the credentials are invalid
	 * @throws ConnectionException
	 *             if connection to Projectile cannot be established
	 * @throws CrawlingException
	 *             in case the crawling fails
	 */
	void checkCredentials(Credentials credentials) throws InvalidCredentialsException,
			ConnectionException, CrawlingException;

	/**
	 * Get a list of all available project names.
	 * 
	 * @throws ConnectionException
	 *             if connection to Projectile cannot be established
	 * @throws CrawlingException
	 *             in case the crawling fails
	 * @return project names or empty list
	 */
	List<String> getProjectNames(Credentials credentials) throws ConnectionException,
			CrawlingException;

	/**
	 * Clock time for given user and project.
	 * <p>
	 * Implementations should verify the time has been clocked as part of this
	 * method.
	 * </p>
	 * 
	 * @throws ConnectionException
	 *             if connection to Projectile cannot be established
	 * @throws CrawlingException
	 *             in case the crawling fails
	 */
	void clock(Credentials credentials, String projectName, Date start, Date end)
			throws ConnectionException, CrawlingException;
}
