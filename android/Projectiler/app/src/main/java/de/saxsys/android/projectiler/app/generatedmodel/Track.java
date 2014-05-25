package de.saxsys.android.projectiler.app.generatedmodel;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table TRACK.
 */
public class Track {

    private Long id;
    /** Not-null value. */
    private String projectName;
    /** Not-null value. */
    private String startdDate;
    /** Not-null value. */
    private String endDate;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Track() {
    }

    public Track(Long id) {
        this.id = id;
    }

    public Track(Long id, String projectName, String startdDate, String endDate) {
        this.id = id;
        this.projectName = projectName;
        this.startdDate = startdDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getProjectName() {
        return projectName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /** Not-null value. */
    public String getStartdDate() {
        return startdDate;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setStartdDate(String startdDate) {
        this.startdDate = startdDate;
    }

    /** Not-null value. */
    public String getEndDate() {
        return endDate;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
