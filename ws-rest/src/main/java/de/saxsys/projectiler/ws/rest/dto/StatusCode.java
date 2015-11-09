package de.saxsys.projectiler.ws.rest.dto;

import com.google.gson.annotations.SerializedName;

public class StatusCode {

  @SerializedName("CodeNumber")
  private int codeNumber;
  @SerializedName("Name")
  private String name;
  @SerializedName("IsError")
  private boolean error;
  @SerializedName("IsWarning")
  private boolean warning;
  @SerializedName("Message")
  private String message;

  public int getCodeNumber() {
    return codeNumber;
  }

  public String getName() {
    return name;
  }

  public boolean isError() {
    return error;
  }

  public boolean isWarning() {
    return warning;
  }

  public String getMessage() {
    return message;
  }

}
