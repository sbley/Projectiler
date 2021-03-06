package de.saxsys.projectiler.ws.rest;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import de.saxsys.projectiler.api.Booking;
import de.saxsys.projectiler.api.ConnectionException;
import de.saxsys.projectiler.api.ProjectileClientApi;
import de.saxsys.projectiler.api.ProjectileApiException;
import de.saxsys.projectiler.api.Credentials;
import de.saxsys.projectiler.api.InvalidCredentialsException;
import de.saxsys.projectiler.api.NoBudgetException;
import de.saxsys.projectiler.api.Settings;
import de.saxsys.projectiler.ws.rest.dto.AddTimebitResponse;
import de.saxsys.projectiler.ws.rest.dto.GetTimebitResponse;
import de.saxsys.projectiler.ws.rest.dto.Job;
import de.saxsys.projectiler.ws.rest.dto.JobsResponse;
import de.saxsys.projectiler.ws.rest.dto.LoginCredentials;
import de.saxsys.projectiler.ws.rest.dto.Timebit;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Retrofit implementation of Crawler API.
 * <p>
 * Communicates through Projectile REST API.
 * </p>
 * 
 * @author stefan.bley
 */
public class RetrofitClient implements ProjectileClientApi {

    private static final Logger LOGGER = Logger.getLogger(RetrofitClient.class.getName());
    private final Retrofit retrofit;
    private String token;

    public RetrofitClient(final Settings settings) {
        OkHttpClient client = initHttpClient(settings);
        this.retrofit = new Retrofit.Builder().client(client)
                .baseUrl(HttpUrl.parse(settings.getProjectileUrl())
                        .newBuilder()
                        .addPathSegment("rest")
                        .addPathSegment("") // to get a trailing backslash
                        .build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private OkHttpClient initHttpClient(final Settings settings) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(settings.getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(settings.getTimeout(), TimeUnit.MILLISECONDS)
                .addInterceptor(new AuthorizationInterceptor(settings.getApplicationKey()))
                .addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY))
                .build();
        return client;
    }

    @Override
    public void checkCredentials(Credentials credentials) throws InvalidCredentialsException, ConnectionException {
        try {
            login(credentials);
        } catch (UnknownHostException e) {
            throw new ConnectionException(e);
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
    public List<String> getProjectNames(Credentials credentials) throws ConnectionException, ProjectileApiException {
        try {
            login(credentials);
            return readProjectNames();
        } catch (UnknownHostException e) {
            throw new ConnectionException(e);
        } catch (IOException e) {
            throw new ProjectileApiException("Error while retrieving project names.");
        }
    }

    @Override
    public void clock(Credentials credentials, String projectName, Date start, Date end, String comment)
            throws ConnectionException, ProjectileApiException {
        if (null == start || null == end || start.after(end)) {
            throw new ProjectileApiException("Time has not been clocked. Start time must be before end time.");
        }
        try {
            login(credentials);
            Job project = lookupProject(projectName);
            if (null == project) {
                throw new ProjectileApiException("Time has not been clocked. " + projectName + " is not a valid project.");
            } else {
                clockTime(start, end, project, comment);
            }
        } catch (UnknownHostException e) {
            throw new ConnectionException(e);
        } catch (IOException e) {
            throw new ProjectileApiException("Error while clocking time.");
        }
    }

    @Override
    public List<Booking> getDailyReport(Credentials credentials) throws ConnectionException, ProjectileApiException {
        try {
            login(credentials);
            return readDailyReport();
        } catch (UnknownHostException e) {
            throw new ConnectionException(e);
        } catch (IOException e) {
            throw new ProjectileApiException("Error while retrieving daily report.", e);
        }
    }

    private void login(Credentials credentials) throws IOException, InvalidCredentialsException {
        ProjectileRestApi service = retrofit.create(ProjectileRestApi.class);
        LoginCredentials body = new LoginCredentials(credentials.getUsername(), credentials.getPassword());
        Response<String> response = service.getToken(body).execute();
        if (response.isSuccessful()) {
            LOGGER.info("User " + credentials.getUsername() + " logged in.");
            token = response.body();
        } else if (404 == response.code()) {
            handle404NotFound();
        } else if (401 == response.code()) {
            throw new InvalidCredentialsException();
        }
    }

    private Job lookupProject(String projectName) throws IOException, ProjectileApiException {
        List<Job> projects = readProjects();
        for (Job project : projects) {
            if (projectName.equals(project.getProjectName())) {
                return project;
            }
        }
        return null;
    }

    private List<String> readProjectNames() throws IOException, ProjectileApiException {
        List<String> projectNames = new ArrayList<>();
        List<Job> projects = readProjects();
        for (Job project : projects) {
            projectNames.add(project.getProjectName());
        }
        LOGGER.info("Projects: " + projectNames);
        return projectNames;
    }

    private List<Job> readProjects() throws IOException, ProjectileApiException {
        ProjectileRestApi service = retrofit.create(ProjectileRestApi.class);
        Response<JobsResponse> response = service.getJobs().execute();
        if (response.isSuccessful()) {
            LOGGER.info("Projects read.");
            return response.body().getJobs();
        } else if (404 == response.code()) {
            handle404NotFound();
        } else if (401 == response.code()) {
            throw new InvalidCredentialsException();
        } else {
            LOGGER.severe(response.errorBody().string());
        }
        throw new IOException();
    }

    private Job readProject(String jobId) throws IOException, ProjectileApiException {
        ProjectileRestApi service = retrofit.create(ProjectileRestApi.class);
        Response<JobsResponse> response = service.getJob(jobId).execute();
        if (response.isSuccessful()) {
            LOGGER.info("Project " + jobId + " read.");
            JobsResponse body = response.body();
            if (body.getStatusCode().isError()) {
                handle404NotFound();
            } else {
                return body.getJobs().get(0);
            }
        } else if (404 == response.code()) {
            handle404NotFound();
        } else if (401 == response.code()) {
            throw new InvalidCredentialsException();
        } else {
            LOGGER.severe(response.errorBody().string());
        }
        throw new IOException();
    }

    private Object clockTime(Date start, Date end, Job project, String comment) throws ProjectileApiException, IOException {
        if (null == start || null == end || null == project) {
            LOGGER.warning("Time has not been clocked. Start time, end time or project missing.");
            throw new ProjectileApiException("Time has not been clocked. Start time, end time or project missing.");
        }

        ProjectileRestApi service = retrofit.create(ProjectileRestApi.class);
        Timebit timebit = new Timebit(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), formatTime(start),
                formatTime(end), project.getId(), comment);
        Response<AddTimebitResponse> response = service.addTimebit(timebit).execute();
        if (response.isSuccessful()) {
            AddTimebitResponse addTimebitResponse = response.body();
            if (addTimebitResponse.getStatusCode().isError()) {
                if (7 == addTimebitResponse.getStatusCode().getCodeNumber()) {
                    throw new NoBudgetException();
                } else {
                    throw new ProjectileApiException(addTimebitResponse.getMessage());
                }
            } else if (addTimebitResponse.getStatusCode().isWarning()) {
                LOGGER.warning("Time clocked with warning: " + addTimebitResponse.getMessage());
            }
            LOGGER.info("Time clocked for project '" + project.getProjectName() + "'.");
        } else if (404 == response.code()) {
            handle404NotFound();
        } else if (401 == response.code()) {
            throw new InvalidCredentialsException();
        }
        return null;
    }

    private List<Booking> readDailyReport() throws IOException, InvalidCredentialsException {
        Calendar today = Calendar.getInstance();
        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(today.getTime());
        ProjectileRestApi service = retrofit.create(ProjectileRestApi.class);
        Response<GetTimebitResponse> response = service.getTimebits(startDate, startDate).execute();
        if (response.isSuccessful()) {
            LOGGER.info("Daily report read.");
            List<Timebit> timebits = response.body().getTimebits();
            List<Booking> bookings = new ArrayList<>();
            for (Timebit timebit : timebits) {
                String projectName = "";
                try {
                    Job project = readProject(timebit.getJobId());
                    projectName = project.getProjectName();
                } catch (ProjectileApiException e) {
                    LOGGER.warning("Project name for job " + timebit.getJobId() + " could not be read.");
                }
                bookings.add(new Booking(timebit.getJobId(), projectName, timebit.getStarttime(), timebit.getEndtime(),
                        timebit.getNote()));
            }
            return bookings;
        } else if (404 == response.code()) {
            handle404NotFound();
        } else if (401 == response.code()) {
            throw new InvalidCredentialsException();
        } else {
            LOGGER.severe(response.errorBody().string());
        }
        throw new IOException();
    }

    private void handle404NotFound() throws ConnectException {
        throw new ConnectException("Invalid Projectile URL.");
    }

    /** Time formatted to Projectile format */
    private String formatTime(final Date time) {
        return new SimpleDateFormat("HH:mm").format(time);
    }

    private final class AuthorizationInterceptor implements Interceptor {
        private String applicationKey;

        private AuthorizationInterceptor(String applicationKey) {
            this.applicationKey = applicationKey;
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Builder requestBuilder = chain.request().newBuilder();
            requestBuilder.header("x-application-key", applicationKey);
            LOGGER.info("Access token: " + token);
            if (null != token) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }
            return chain.proceed(requestBuilder.build());
        }
    }
}
