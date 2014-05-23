package de.saxsys.android.projectiler.app.utils;

import android.content.Context;

import java.util.Date;
import java.util.List;

import de.saxsys.android.projectiler.app.backend.Projectiler;
import de.saxsys.android.projectiler.app.crawler.ConnectionException;
import de.saxsys.android.projectiler.app.crawler.CrawlingException;
import de.saxsys.android.projectiler.app.crawler.InvalidCredentialsException;

/**
 * Created by stefan.heinze on 19.05.2014.
 */
public class BusinessProcess {


    private static BusinessProcess INSTANCE;

    public static BusinessProcess getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new BusinessProcess();
        }
        return INSTANCE;
    }

    private Projectiler projectiler;

    private BusinessProcess() {
        projectiler = Projectiler.createDefaultProjectiler();
    }


    public String getUserName(final Context context) {
        return projectiler.getUserName(context);
    }

    public boolean isCheckedIn(final Context context) {
        return projectiler.isCheckedIn(context);
    }

    public Date checkin(final Context context) {
        return projectiler.checkin(context);
    }

    public Date checkout(final Context context, final String projectName) throws CrawlingException {
        return projectiler.checkout(context, projectName);
    }

    public List<String> getProjectNames(final Context context) throws ConnectionException, CrawlingException {
        return projectiler.getProjectNames(context);
    }

    public void saveCredentials(final Context context, final String username, final String password, final boolean saveLogin)
            throws InvalidCredentialsException, ConnectionException, CrawlingException {
        projectiler.saveCredentials(context, username, password, saveLogin);
    }

    public void saveProjectName(final Context context, final String projectKey) {
        WidgetUtils.showProgressBarOnWidget(context, projectiler);
        projectiler.saveProjectName(context, projectKey);
        WidgetUtils.hideProgressBarOnWidget(context, projectiler);
    }

    public void resetStartTime(Context context) {
        projectiler.resetStartTime(context);
    }

    public String getStartDateAsString(final Context context) {
        return projectiler.getStartDateAsString(context);
    }

    public Date getStartDate(Context context) {
        return projectiler.getStartDate(context);
    }

    public String getProjectName(Context context) {
        return projectiler.getProjectName(context);
    }

    public boolean getAutoLogin(Context context) {
        return projectiler.getAutoLogin(context);
    }

    public void setAutoLogin(final Context context, boolean autoLogin) {
        projectiler.setAutoLogin(context, autoLogin);
    }

    public void setWidgetLoading(final Context context, boolean loading) {
        projectiler.setWidgetLoading(context, loading);
    }

    public boolean isWidgetLoading(Context context) {
        return projectiler.isWidgetLoading(context);
    }

    public void logout(final Context context) {
        projectiler.logout(context);
        WidgetUtils.refreshWidget(context);
    }

    public int getCurrentActiveProjectIndex(Context context, List<String> itemList) {
        return projectiler.getCurrentActiveProjectIndex(context, itemList);
    }

    public void resetProject(final Context context) {
        WidgetUtils.showProgressBarOnWidget(context, projectiler);
        saveProjectName(context, "");
        resetStartTime(context);
        WidgetUtils.hideProgressBarOnWidget(context, projectiler);
    }

    public void showProgressBarOnWidget(Context context) {
        WidgetUtils.showProgressBarOnWidget(context, projectiler);
    }

    public void hideProgressBarOnWidget(Context context) {
        WidgetUtils.hideProgressBarOnWidget(context, projectiler);
    }
}
