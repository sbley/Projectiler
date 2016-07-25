package de.saxsys.projectiler.ws.rest.dto;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author stefan.bley
 *
 */
public class AddTimebitResponse {

  @SerializedName("StatusCode")
  private StatusCode statusCode;

  @SerializedName("Message")
  private String message;

  public StatusCode getStatusCode() {
    return statusCode;
  }

  public String getMessage() {
    return message;
  }
}
