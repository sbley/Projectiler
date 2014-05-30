package de.saxsys.android.projectiler.app.backend;

import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import de.saxsys.android.projectiler.app.db.DataProvider;
import de.saxsys.android.projectiler.app.generatedmodel.Track;
import de.saxsys.projectiler.crawler.Booking;
import de.saxsys.projectiler.crawler.ConnectionException;
import de.saxsys.projectiler.crawler.Crawler;
import de.saxsys.projectiler.crawler.CrawlingException;
import de.saxsys.projectiler.crawler.Credentials;
import de.saxsys.projectiler.crawler.InvalidCredentialsException;
import de.saxsys.projectiler.crawler.Settings;
import de.saxsys.projectiler.crawler.jsoup.JSoupCrawler;


/**
 * Automatic time tracking in Projectile.
 *
 * @author stefan.bley
 */
public class Projectiler {

    private static final Logger LOGGER = Logger.getLogger(Projectiler.class.getSimpleName());
    private final UserDataStore dataStore = UserDataStore.getInstance();
    private final Crawler crawler;
    private DataProvider dataProvider;

    public static Projectiler createDefaultProjectiler(final Context context) {
        return new Projectiler(new JSoupCrawler(new Settings()), context);
    }

    protected Projectiler(final Crawler crawler, final Context context) {
        this.crawler = crawler;
        dataProvider = new DataProvider(context);
    }

    public String getUserName(final Context context) {
        return dataStore.getUserName(context);
    }

    public boolean isCheckedIn(final Context context) {
        return dataStore.isCheckedIn(context);
    }

    /**
     * Invoke a checkin, i.e. start a working period.
     *
     * @return checkin date
     */
    public Date checkin(final Context context) {
        final Date start = new Date();
        dataStore.setStartDate(context, start);
        LOGGER.info("Checked in at " + DateUtil.formatShort(start));
        return start;
    }

    public Date checkout(final Context context, final String projectName) throws CrawlingException {
        final Date start = dataStore.getStartDate(context);
        final Date end = new Date();

        return checkout(context, projectName, start, end);
    }

    public Date checkout(Context context, String projectName, Date startDate, Date endDate) throws CrawlingException  {

        startDate = DateUtil.resetSeconds(startDate);
        endDate = DateUtil.resetSeconds(endDate);

        if (!isCheckedIn(context)) {
            throw new IllegalStateException("Must be checked in before checking out.");
        }

        if (DateUtil.formatHHmm(startDate).equals(DateUtil.formatHHmm(endDate))) {
            throw new IllegalStateException("Work time must be at least 1 minute.");
        }

        try {
            Log.i("Projectiler", "clock with comment " + dataStore.getComment(context));
            crawler.clock(createCredentials(context), projectName, startDate, endDate, dataStore.getComment(context));
        } catch (CrawlingException e) {
            Track track = new Track();
            track.setProjectName(projectName);
            track.setTimestamp(new Date());
            track.setStartdDate(startDate);
            track.setEndDate(endDate);
            dataProvider.saveTrack(track);

            throw e;
        } finally {
            dataStore.clearStartDate(context);
            dataStore.setProjectName(context, "");
            dataStore.deleteComment(context);
        }
        LOGGER.info("Checked out at " + DateUtil.formatShort(endDate));
        return endDate;
    }

    /**
     * Retrieve available project names.
     *
     * @return list of project names or empty list
     * @throws ConnectionException if connection to Projectile fails
     * @throws CrawlingException   if an error occoures in the crawles
     */
    public List<String> getProjectNames(final Context context) throws ConnectionException, CrawlingException {
        return crawler.getProjectNames(createCredentials(context));
    }

    /**
     * Verify credentials and save them to the userdata store.
     *
     * @param username Projectile username
     * @param password Projectile password
     * @throws InvalidCredentialsException if credentials are invalid
     * @throws ConnectionException         if connection to Projectile fails
     * @throws CrawlingException
     */
    public void saveCredentials(final Context context, final String username, final String password, final boolean saveLogin)
            throws InvalidCredentialsException, ConnectionException, CrawlingException {
        crawler.checkCredentials(new Credentials(username, password));
        dataStore.setCredentials(context, username, password);
        dataStore.setAutoLogin(context, saveLogin);

    }

    /**
     * Save the project name to the userdata store.
     */
    public void saveProjectName(final Context context, final String projectKey) {
        dataStore.setProjectName(context, projectKey);
    }

    private Credentials createCredentials(final Context context) {
        return new Credentials(dataStore.getUserName(context), dataStore.getPassword(context));
    }

    public void resetStartTime(Context context) {
        dataStore.setStartDate(context, null);
    }

    public String getStartDateAsString(final Context context) {
        return dataStore.getStartDateAsString(context);
    }

    public Date getStartDate(Context context) {
        return dataStore.getStartDate(context);
    }

    public String getProjectName(Context context) {
        return dataStore.getProjectName(context);
    }

    public boolean getAutoLogin(Context context) {
        return dataStore.getAutoLogin(context);
    }

    public void setAutoLogin(final Context context, boolean autoLogin) {
        dataStore.setAutoLogin(context, autoLogin);
    }

    public void setWidgetLoading(final Context context, boolean loading) {
        dataStore.setWidgetLoading(context, loading);
    }

    public boolean isWidgetLoading(Context context) {
        return dataStore.isWidgetLoading(context);
    }

    public void logout(final Context context) {
        dataStore.setAutoLogin(context, false);
        dataStore.setCredentials(context, "", "");
        dataStore.setProjectName(context, "");
    }

    public int getCurrentActiveProjectIndex(Context context, List<String> itemList) {
        return dataStore.getCurrentActiveProjectIndex(context, itemList);
    }

    public List<Booking> getDailyReports(final Context context) throws CrawlingException {
        return crawler.getDailyReport(createCredentials(context));
    }

    public void checkoutTrack(final Context context, Track track) throws CrawlingException {

        try {
            crawler.clock(createCredentials(context), track.getProjectName(), track.getStartdDate(), track.getEndDate(), dataStore.getComment(context));
        } catch (CrawlingException e) {
            dataProvider.saveTrack(track);
            throw e;
        }
    }

    public void saveComment(Context context, String comment) {
        dataStore.saveComment(context, comment);
    }

}
