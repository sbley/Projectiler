package de.saxsys.projectiler.worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;

public class ProjectTask extends Task<List<String>> {

	public ProjectTask() {
	}

	@Override
	protected List<String> call() throws Exception {
		Projectiler projectiler = null;
		List<String> projectNames = Collections.emptyList();
		try {
			projectiler = Projectiler.createDefaultProjectiler();
			projectNames = projectiler.getProjectNames();
		} catch (final Exception e) {
			e.printStackTrace();
			this.succeeded();
			return new ArrayList<>();
		}
		this.succeeded();
		return projectNames;
	}

}
