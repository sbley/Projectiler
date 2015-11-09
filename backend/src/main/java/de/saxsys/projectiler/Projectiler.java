package de.saxsys.projectiler;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import de.saxsys.projectiler.crawler.ConnectionException;
import de.saxsys.projectiler.crawler.Crawler;
import de.saxsys.projectiler.crawler.CrawlingException;
import de.saxsys.projectiler.crawler.Credentials;
import de.saxsys.projectiler.crawler.InvalidCredentialsException;
import de.saxsys.projectiler.ws.rest.RetrofitClient;

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
    return new Projectiler(new RetrofitClient(new SystemSettings()));
  }

  protected Projectiler(final Crawler crawler) {
    this.crawler = crawler;
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
    final Date start = DateUtil.resetSeconds(new Date());
    dataStore.setStartDate(start);
    dataStore.save();
    LOGGER.info("Checked in at " + DateUtil.formatShort(start));
    return start;
  }

  /**
   * Invoke a checkout, i.e. finish a working period.
   * 
   * @param projectName project to clock your time to
   * @param comment optional comment
   * @throws CrawlingException
   * @throws ConnectionException if connection to Projectile fails
   * @throws ProjectilerException in case backend operations fail
   * @throws IllegalStateException if invoked while not being checked in or if work time is less than one minute
   */
  public Date checkout(final String projectName, final String comment) throws CrawlingException {
    if (!isCheckedIn()) {
      throw new IllegalStateException("Must be checked in before checking out.");
    }

    final Date start = dataStore.getStartDate();
    final Date end = new Date();
    if (DateUtil.formatHHmm(start).equals(DateUtil.formatHHmm(end))) {
      dataStore.clearStartDate();
      dataStore.save();
      throw new IllegalStateException("Work time must be at least 1 minute.");
    }

    try {
      crawler.clock(createCredentials(), projectName, start, end, comment);
    } finally {
      dataStore.clearStartDate();
      dataStore.save();
    }
    LOGGER.info("Checked out at " + DateUtil.formatShort(end));
    return end;
  }

  /**
   * Retrieve available project names.
   * 
   * @return list of project names or empty list
   * @throws ConnectionException if connection to Projectile fails
   * @throws CrawlingException if an error occoures in the crawles
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
   * @throws ConnectionException if connection to Projectile fails
   * @throws CrawlingException
   */
  public void saveCredentials(final String username, final String password)
    throws InvalidCredentialsException, ConnectionException, CrawlingException {
    crawler.checkCredentials(new Credentials(username, password));
    dataStore.setCredentials(username, password);
    dataStore.save();
  }

  /** Save the project name to the userdata store. */
  public void saveProjectName(final String projectKey) {
    dataStore.setProjectName(projectKey);
    dataStore.save();
  }

  private Credentials createCredentials() {
    return new Credentials(dataStore.getUserName(), dataStore.getPassword());
  }
}
