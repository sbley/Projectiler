package de.saxsys.projectiler;

import java.util.List;

import de.saxsys.projectiler.domain.User;

public interface Crawler {

	/**
	 * Clock time for given user and project.
	 */
	void clock(User user, String projectName);

	/**
	 * Get a list of all available project names.
	 */
	List<String> getProjectNames(User user);
}
