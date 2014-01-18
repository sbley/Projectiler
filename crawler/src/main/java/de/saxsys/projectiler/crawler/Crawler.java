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
	 */
	void clock(Credentials credentials, String projectName, Date start, Date end);

	/**
	 * Get a list of all available project names.
	 */
	List<String> getProjectNames(Credentials user);
}
