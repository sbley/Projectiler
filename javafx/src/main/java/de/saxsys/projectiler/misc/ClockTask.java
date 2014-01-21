package de.saxsys.projectiler.misc;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;

public class ClockTask extends Task<Boolean> {

	private final String projectKey;

	public ClockTask(final String projectKey) {
		this.projectKey = projectKey;
	}

	@Override
	protected Boolean call() throws Exception {
		try {
			final Projectiler projectiler = Projectiler.createDefaultProjectiler();
			projectiler.checkout(projectKey);
		} catch (final Exception e) {
			e.printStackTrace();
			this.succeeded();
			return false;
		}
		this.succeeded();
		return true;
	}
}
