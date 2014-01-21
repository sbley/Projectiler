package de.saxsys.projectiler;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import de.saxsys.projectiler.crawler.Crawler;
import de.saxsys.projectiler.crawler.Credentials;
import de.saxsys.projectiler.crawler.Password;
import de.saxsys.projectiler.crawler.Settings;
import de.saxsys.projectiler.crawler.selenium.SeleniumCrawler;

/**
 * Automatic time tracking in Projectile
 * 
 * @author stefan.bley
 */
public class Projectiler {

    final UserDataStore credentialStore = UserDataStore.getInstance();
    private static final Logger LOGGER = Logger.getLogger(Projectiler.class.getSimpleName());
    private final Crawler crawler;
    private boolean checkedIn;

    public static Projectiler createDefaultProjectiler() {
        return new Projectiler(new SeleniumCrawler(new Settings()));
    }

    protected Projectiler(final Crawler crawler) {
        this.crawler = crawler;
    }

    public void checkin() {
        final Date startDate = new Date();
        credentialStore.setStartDate(startDate);
        credentialStore.save();
        checkedIn = true;
        LOGGER.info("Checked in at " + formatDate(startDate));
    }

    public int checkout(final String projectName) {
        final UserDataStore store = UserDataStore.getInstance();
        final Date start = store.getStartDate();
        final Date end = new Date();
        crawler.clock(createCredentials(), projectName, start, end);
        store.clearStartDate();
        store.save();
        LOGGER.info("Checked out at " + formatDate(end));
        checkedIn = false;
        return duration(start, end);
    }

    private int duration(final Date start, final Date end) {
        return (int) TimeUnit.MINUTES.convert(end.getTime() - start.getTime(), TimeUnit.MILLISECONDS);
    }

    private String formatDate(final Date startDate) {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(startDate);
    }

    public List<String> getProjectNames() {
        return crawler.getProjectNames(createCredentials());
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public static void main(final String[] args) {
        final Projectiler projectiler = createDefaultProjectiler();
        UserDataStore.getInstance().setUserName("stefan.bley");
        UserDataStore.getInstance().setPassword(Password.get());
        projectiler.checkin();
        final List<String> projectNames = projectiler.getProjectNames();
        projectiler.checkout(projectNames.get(new Random().nextInt(projectNames.size() - 1)));
    }

    private Credentials createCredentials() {
        return new Credentials(credentialStore.getUserName(), credentialStore.getPassword());
    }

}
