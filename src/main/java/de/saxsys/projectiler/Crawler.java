package de.saxsys.projectiler;

import de.saxsys.projectiler.domain.User;

public interface Crawler {

	public abstract void clock(User user, String projectName);

}
