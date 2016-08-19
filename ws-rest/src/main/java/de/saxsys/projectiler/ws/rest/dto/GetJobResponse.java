package de.saxsys.projectiler.ws.rest.dto;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author stefan.bley
 */
public class GetJobResponse {

  @SerializedName("StatusCode")
  private StatusCode statusCode;

  @SerializedName("Entries")
  private Job job;

  public StatusCode getStatusCode() {
    return statusCode;
  }

  public Job getJob() {
    return job;
  }

}
