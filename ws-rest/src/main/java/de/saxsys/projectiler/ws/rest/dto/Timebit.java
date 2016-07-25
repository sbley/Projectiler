package de.saxsys.projectiler.ws.rest.dto;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author stefan.bley
 */
public class Timebit {

  private static final String JOBS_TYPE = "JOBS";

  @SerializedName("date")
  private String day;
  @SerializedName("start")
  private String starttime;
  @SerializedName("end")
  private String endtime;
  @SerializedName("type")
  private String type;
  @SerializedName("job")
  private String jobId;
  @SerializedName("note")
  private String note;

  public Timebit(String day, String starttime, String endtime, String jobId, String note) {
    this.day = day;
    this.starttime = starttime;
    this.endtime = endtime;
    this.type = JOBS_TYPE;
    this.jobId = jobId;
    this.note = note;
  }

  public String getStarttime() {
    return starttime;
  }

  public String getEndtime() {
    return endtime;
  }

  public String getJobId() {
    return jobId;
  }
}
