package de.saxsys.android.projectiler.app.backend;


import android.content.Context;

import java.util.ArrayList;
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
    private final UserDataStore dataStore;
    private final Crawler crawler;

    public static Projectiler createDefaultProjectiler(final Context context) {
        return new Projectiler(new JSoupCrawler(new Settings()), context);
    }

    protected Projectiler(final Crawler crawler, final Context context) {
        this.crawler = crawler;
        dataStore = UserDataStore.getInstance(context);
    }

    public String getUserName() {
        return dataStore.getUserName();
    }

    public boolean isCheckedIn() {
        return dataStore.isCheckedIn();
    }

    /**
     * Invoke a checkin, i.e. start a working period.
     *
     * @return checkin date
     */
    public Date checkin() {
        final Date start = new Date();
        dataStore.setStartDate(start);
        LOGGER.info("Checked in at " + start);
        return start;
    }

    public Date checkout(final String projectName) throws CrawlingException {
        final Date start = dataStore.getStartDate();
        final Date end = new Date();

        return checkout(projectName, start, end);
    }

    public Date checkout(String projectName, Date startDate, Date endDate) throws CrawlingException  {

        startDate = DateUtil.resetSeconds(startDate);
        endDate = DateUtil.resetSeconds(endDate);

        if (!isCheckedIn()) {
            throw new IllegalStateException("Must be checked in before checking out.");
        }

        if (DateUtil.formatHHmm(startDate).equals(DateUtil.formatHHmm(endDate))) {
            throw new IllegalStateException("Work time must be at least 1 minute.");
        }

        try {
            crawler.clock(createCredentials(), projectName, startDate, endDate, dataStore.getComment());
        } catch (CrawlingException e) {
            Track track = new Track();
            track.setProjectName(projectName);
            track.setTimestamp(new Date());
            track.setStartdDate(startDate);
            track.setEndDate(endDate);
            dataStore.saveTrack(track);

            throw e;
        } finally {
            dataStore.clearStartDate();
            dataStore.deleteComment();
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
    public List<String> getProjectNames() throws ConnectionException, CrawlingException {
        return crawler.getProjectNames(createCredentials());
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
    public void saveCredentials(final String username, final String password, final boolean saveLogin)
            throws InvalidCredentialsException, ConnectionException, CrawlingException {
        crawler.checkCredentials(new Credentials(username, password));
        dataStore.setCredentials(username, password);
        dataStore.setAutoLogin(saveLogin);

    }

    /**
     * Save the project name to the userdata store.
     */
    public void saveProjectName(final String projectKey) {
        dataStore.setProjectName(projectKey);
    }

    private Credentials createCredentials() {
        return new Credentials(dataStore.getUserName(), dataStore.getPassword());
    }


    public void logout() {
        dataStore.setAutoLogin(false);
        dataStore.setCredentials("", "");
        dataStore.setProjectName("");
        dataStore.savePorjectNames(new ArrayList<String>());
    }

    public List<Booking> getDailyReports() throws CrawlingException {
        return crawler.getDailyReport(createCredentials());
    }

    public void checkoutTrack(Track track) throws CrawlingException {

        try {
            crawler.clock(createCredentials(), track.getProjectName(), track.getStartdDate(), track.getEndDate(), dataStore.getComment());
        } catch (CrawlingException e) {
            dataStore.saveTrack(track);
            throw e;
        }
    }

}
