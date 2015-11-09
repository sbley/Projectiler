package de.saxsys.projectiler.ws.rest.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetTimebitResponse {

  @SerializedName("StatusCode")
  private StatusCode statusCode;

  @SerializedName("Entries")
  private List<Timebit> entries = new ArrayList<>();

  public StatusCode getStatusCode() {
    return statusCode;
  }

  public List<Timebit> getTimebits() {
    return entries;
  }
}
