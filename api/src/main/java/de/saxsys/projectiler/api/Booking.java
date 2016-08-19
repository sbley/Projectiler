package de.saxsys.projectiler.api;

/**
 * A booking is a period of time booked for a project.
 * 
 * @author stefan.bley
 */
public class Booking {

    private final String projectId;
    private final String projectName;
    private final String startTime;
    private final String endTime;
    private final String comment;

    public Booking(String projectId, String projectName, String startTime, String endTime, String comment) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.comment = comment;
    }

    public String getProjectId() {
        return projectId;
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

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "" + startTime + "-" + endTime + " " + projectId + " " + projectName;
    }

}
