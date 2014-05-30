package de.saxsys.android.projectiler.app.utils;

import android.content.Context;

import java.util.Date;
import java.util.List;

import de.saxsys.android.projectiler.app.backend.Projectiler;
import de.saxsys.android.projectiler.app.backend.UserDataStore;
import de.saxsys.android.projectiler.app.generatedmodel.Track;
import de.saxsys.projectiler.crawler.Booking;
import de.saxsys.projectiler.crawler.ConnectionException;
import de.saxsys.projectiler.crawler.CrawlingException;
import de.saxsys.projectiler.crawler.InvalidCredentialsException;

/**
 * Created by stefan.heinze on 19.05.2014.
 */
public class BusinessProcess {

    private static BusinessProcess INSTANCE;
    private UserDataStore dataStorage;
    private Projectiler projectiler;

    public static BusinessProcess getInstance(final Context context) {
        if (null == INSTANCE) {
            INSTANCE = new BusinessProcess(context);
        }
        return INSTANCE;
    }

    private BusinessProcess(final Context context) {
        projectiler = Projectiler.createDefaultProjectiler(context);
        dataStorage = UserDataStore.getInstance(context);
    }


    public String getUserName() {
        return projectiler.getUserName();
    }

    public boolean isCheckedIn() {
        return projectiler.isCheckedIn();
    }

    public Date checkin() {
        return projectiler.checkin();
    }

    public Date checkout(final String projectName) throws CrawlingException, IllegalStateException {

        Date checkout = null;

        checkout = projectiler.checkout(projectName);

        return checkout;
    }

    public Date checkout(final String projectName, final Date startDate, final Date endDate) throws CrawlingException, IllegalStateException {

        Date checkout = null;

        checkout = projectiler.checkout(projectName, startDate, endDate);

        return checkout;
    }


    public List<String> getProjectNames() throws ConnectionException, CrawlingException {
        return projectiler.getProjectNames();
    }

    public void saveCredentials(final String username, final String password, final boolean saveLogin)
            throws InvalidCredentialsException, ConnectionException, CrawlingException {
        projectiler.saveCredentials(username, password, saveLogin);
    }

    public void saveProjectName(final Context context, final String projectKey) {
        WidgetUtils.showProgressBarOnWidget(context, dataStorage);
        projectiler.saveProjectName(projectKey);
        WidgetUtils.hideProgressBarOnWidget(context, dataStorage);
    }

    public void resetStartTime() {
        dataStorage.setStartDate(null);
    }

    public String getStartDateAsString() {
        return dataStorage.getStartDateAsString();
    }

    public Date getStartDate() {
        return dataStorage.getStartDate();
    }

    public String getProjectName() {
        return dataStorage.getProjectName();
    }

    public boolean getAutoLogin() {
        return dataStorage.getAutoLogin();
    }

    public void setAutoLogin(boolean autoLogin) {
        dataStorage.setAutoLogin(autoLogin);
    }

    public void setWidgetLoading(boolean loading) {
        dataStorage.setWidgetLoading(loading);
    }

    public boolean isWidgetLoading() {
        return dataStorage.isWidgetLoading();
    }

    public void logout(final Context context) {
        projectiler.logout();
        WidgetUtils.refreshWidget(context);
    }

    public int getCurrentActiveProjectIndex(List<String> itemList) {
        return dataStorage.getCurrentActiveProjectIndex(itemList);
    }

    public void resetProject(final Context context, final boolean resetProjectName) {
        WidgetUtils.showProgressBarOnWidget(context, dataStorage);
        if(resetProjectName){
            saveProjectName(context, "");
        }
        resetStartTime();
        WidgetUtils.hideProgressBarOnWidget(context, dataStorage);
    }

    public void showProgressBarOnWidget(Context context) {
        WidgetUtils.showProgressBarOnWidget(context, dataStorage);
    }

    public void hideProgressBarOnWidget(Context context) {
        WidgetUtils.hideProgressBarOnWidget(context, dataStorage);
    }

    public List<Booking> getCurrentBookings() throws CrawlingException {
        return projectiler.getDailyReports();
    }


    public void checkoutAllTracks() throws CrawlingException {

        List<Track> tracks = dataStorage.getTracks();

        for (Track track : tracks) {

            dataStorage.deleteTrack(track);
            projectiler.checkoutTrack(track);
        }

    }

    public void saveComment(String comment) {
        dataStorage.saveComment(comment);
    }
}
