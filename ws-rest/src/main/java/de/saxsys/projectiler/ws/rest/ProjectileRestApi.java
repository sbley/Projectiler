package de.saxsys.projectiler.ws.rest;

import de.saxsys.projectiler.ws.rest.dto.AddTimebitResponse;
import de.saxsys.projectiler.ws.rest.dto.GetTimebitResponse;
import de.saxsys.projectiler.ws.rest.dto.LoginCredentials;
import de.saxsys.projectiler.ws.rest.dto.Timebit;
import de.saxsys.projectiler.ws.rest.dto.JobsResponse;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Retrofit definition of the Projectile REST API.
 * 
 * @author stefan.bley
 */
public interface ProjectileRestApi {

  /**
   * Authenticate with credentials to get a authentication token.
   * 
   * @param body credentials
   * @return JWT token
   */
  @Headers("Accept: text/plain")
  @POST("token/0")
  Call<String> getToken(@Body LoginCredentials body);

  @Headers("Accept: application/json")
  @GET("api/json/0/jobs?bsmExtraFields=project_caption")
  Call<JobsResponse> getJobs();

  @Headers({"Accept: application/json", "Content-Type: application/json" })
  @POST("api/json/0/timebits")
  Call<AddTimebitResponse> addTimebit(@Body Timebit timebit);

  @Headers("Accept: application/json")
  @GET("api/json/0/timebits?showMine=true")
  Call<GetTimebitResponse> getTimebits(@Query("startDate") String startDate,
      @Query("endDate") String endDate);
}
