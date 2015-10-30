package de.saxsys.projectiler.ws.rest.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JobsResponse {

  @SerializedName("StatusCode")
  private StatusCode statusCode;

  @SerializedName("Entries")
  private List<Job> entries = new ArrayList<>();

  public StatusCode getStatusCode() {
    return statusCode;
  }

  public List<Job> getJobs() {
    return entries;
  }

  public List<String> getProjectNames() {
    List<String> projectNames = new ArrayList<>();
    for (Job jobEntry : entries) {
      projectNames.add(jobEntry.getProjectName());
    }
    return projectNames;
  }
}
