package de.saxsys.projectiler.ws.rest.dto;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author stefan.bley
 */
public class Job {

  private String id;

  @SerializedName("bsmExtraFields")
  private BsmExtraFields bsmExtraFields;

  public String getId() {
    return id;
  }

  public String getProjectName() {
    return bsmExtraFields.getProjectName();
  }

  private class BsmExtraFields {
    @SerializedName("project_caption")
    private String projectName;

    public String getProjectName() {
      return projectName;
    }
  }
}