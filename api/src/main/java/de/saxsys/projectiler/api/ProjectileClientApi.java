package de.saxsys.projectiler.api;

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
public interface ProjectileClientApi {

	/**
	 * Verifies the credentials are valid.
	 * 
	 * @throws InvalidCredentialsException
	 *             if the credentials are invalid
	 * @throws ConnectionException
	 *             if connection to Projectile cannot be established
	 * @throws ProjectileApiException
	 *             in case the crawling fails
	 */
	void checkCredentials(Credentials credentials) throws InvalidCredentialsException,
			ConnectionException, ProjectileApiException;

	/**
	 * Get a list of all available project names.
	 * 
	 * @throws ConnectionException
	 *             if connection to Projectile cannot be established
	 * @throws ProjectileApiException
	 *             in case the crawling fails
	 * @return project names or empty list
	 */
	List<String> getProjectNames(Credentials credentials) throws ConnectionException,
			ProjectileApiException;

	/**
	 * Clock time for given user and project.
	 * <p>
	 * Implementations should verify the time has been clocked as part of this
	 * method.
	 * </p>
	 * 
	 * @param start
	 *            start time (required)
	 * @param end
	 *            end time (required, must be after start time)
	 * @param comment
	 *            (optional)
	 * @throws ConnectionException
	 *             if connection to Projectile cannot be established
	 * @throws ProjectileApiException
	 *             in case the crawling fails
	 */
	void clock(Credentials credentials, String projectName, Date start, Date end, String comment)
			throws ConnectionException, ProjectileApiException;

	/**
	 * Get a list of clocked times for the current day and user.
	 * 
	 * @return list of clocked times or empty list
	 * @throws ConnectionException
	 *             if connection to Projectile cannot be established
	 * @throws ProjectileApiException
	 *             in case the crawling fails
	 */
	List<Booking> getDailyReport(Credentials credentials) throws ConnectionException,
			ProjectileApiException;

}
