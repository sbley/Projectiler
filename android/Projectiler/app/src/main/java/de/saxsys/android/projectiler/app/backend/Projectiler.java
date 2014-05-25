package de.saxsys.android.projectiler.app.backend;

import android.content.Context;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

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

    public static Projectiler createDefaultProjectiler() {
        return new Projectiler(new JSoupCrawler(new Settings()));
    }

    protected Projectiler(final Crawler crawler) {
        this.crawler = crawler;
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
        // TODO #2 round start date to HH:mm:00 [SB]
        final Date start = DateUtil.resetSeconds(new Date());
        dataStore.setStartDate(context, start);
        LOGGER.info("Checked in at " + DateUtil.formatShort(start));
        return start;
    }

    public Date checkout(final Context context, final String projectName) throws CrawlingException {
        if (!isCheckedIn(context)) {
            throw new IllegalStateException("Must be checked in before checking out.");
        }

        final Date start = dataStore.getStartDate(context);
        final Date end = new Date();
        if (DateUtil.formatHHmm(start).equals(DateUtil.formatHHmm(end))) {
            //dataStore.clearStartDate(context);
            throw new IllegalStateException("Work time must be at least 1 minute.");
        }

        try {
            crawler.clock(createCredentials(context), projectName, start, end);
        } finally {
            dataStore.clearStartDate(context);
            dataStore.setProjectName(context, "");
        }
        LOGGER.info("Checked out at " + DateUtil.formatShort(end));
        return end;
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
        crawler.clock(createCredentials(context), track.getProjectName(), DateUtil.formatShort(track.getStartdDate()), DateUtil.formatShort(track.getEndDate()));
    }
}
