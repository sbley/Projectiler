package de.saxsys.projectiler.crawler;

import java.util.Date;
import java.util.List;

/**
 * Implementing classes interact with Projectile.
 * 
 * @author stefan.bley
 */
public interface Crawler {

	/**
	 * Clock time for given user and project.
	 * 
	 * @throws CrawlingException
	 *             in case the crawling fails
	 */
	void clock(Credentials credentials, String projectName, Date start, Date end)
			throws CrawlingException;

	/**
	 * Get a list of all available project names.
	 * 
	 * @throws CrawlingException
	 *             in case the crawling fails
	 */
	List<String> getProjectNames(Credentials user) throws CrawlingException;
}
