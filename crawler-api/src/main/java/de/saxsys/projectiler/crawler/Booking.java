package de.saxsys.projectiler.crawler;

/**
 * A booking is a period of time booked for a project.
 * 
 * @author stefan.bley
 */
public class Booking {

	private final String projectName;
	private final String startTime;
	private final String endTime;

	public Booking(String projectName, String startTime, String endTime) {
		this.projectName = projectName;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	@Override
	public String toString() {
		return "" + startTime + " - " + endTime + " " + projectName;
	}

}
