package de.saxsys.projectiler;

import de.saxsys.projectiler.domain.User;

public interface Crawler {

	/**
	 * Clock time for given user and project
	 */
	public abstract void clock(User user, String projectName);

}
