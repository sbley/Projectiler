package de.saxsys.projectiler.crawler.jsoup;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.saxsys.projectiler.crawler.ConnectionException;
import de.saxsys.projectiler.crawler.Crawler;
import de.saxsys.projectiler.crawler.CrawlingException;
import de.saxsys.projectiler.crawler.Credentials;
import de.saxsys.projectiler.crawler.InvalidCredentialsException;
import de.saxsys.projectiler.crawler.Settings;

/**
 * Interacts with Projectile via JSoup.
 * 
 * @author stefan.bley
 * @see {@link http://jsoup.org}
 */
public class JSoupCrawler implements Crawler {

    private static final Logger LOGGER = Logger.getLogger(JSoupCrawler.class.getSimpleName());
    private static final String JSESSIONID = "JSESSIONID";
    private final Settings settings;

    /** transaction id */
    private String taid;
    private Map<String, String> cookies;

    public JSoupCrawler(final Settings settings) {
        this.settings = settings;
    }

    @Override
    public void checkCredentials(final Credentials credentials) throws CrawlingException {
        try {
            login(credentials);
            logout();
        } catch (final UnknownHostException e) {
            throw new ConnectionException(e);
        } catch (final IOException e) {
            throw new CrawlingException("Error while checking credentials.", e);
        }
    }

    @Override
    public List<String> getProjectNames(final Credentials credentials) throws CrawlingException {
        try {
            login(credentials);
            Document ttPage = openTimeTracker();

            List<String> projectNames = readProjectNames(ttPage);

            logout();
            return projectNames;
        } catch (final UnknownHostException e) {
            throw new ConnectionException(e);
        } catch (final IOException e) {
            throw new CrawlingException("Error while retrieving project names.", e);
        }
    }

    @Override
    public void clock(final Credentials credentials, final String projectName, final Date start, final Date end)
        throws CrawlingException {
        try {
            login(credentials);
            Document ttPage = openTimeTracker();

            clockTime(start, end, projectName, ttPage);

            logout();
        } catch (final UnknownHostException e) {
            throw new ConnectionException(e);
        } catch (final IOException e) {
            throw new CrawlingException("Error while clocking time.", e);
        }
    }

    /**
     * Login to Projectile and return the cookies containing the session ID.
     * 
     * @throws InvalidCredentialsException if the credentials are wrong
     */
    private void login(final Credentials cred) throws IOException, InvalidCredentialsException {
        Response response =
                Jsoup.connect(settings.getProjectileUrl()).method(Method.POST).data("login", cred.getUsername())
                        .data("password", cred.getPassword()).data("jsenabled", "0").data("external.loginOK.x", "8")
                        .data("external.loginOK.y", "8").execute();
        Document startPage = response.parse();
        if (startPage.getElementsByAttributeValue("name", "password").isEmpty()) {
            String sessionId = response.cookie(JSESSIONID);
            LOGGER.info("User " + cred.getUsername() + " logged in with session ID " + sessionId + ".");
            saveTaid(startPage);
            cookies = response.cookies();
        } else {
            throw new InvalidCredentialsException();
        }
    }

    private Document openTimeTracker() throws IOException {
        String today = formatToday();
        Response response =
                Jsoup.connect(settings.getProjectileUrl()).method(Method.POST).cookies(cookies).data("taid", taid)
                        .data("CurrentFocusField", "0").data("CurrentDraggable", "0").data("CurrentDropTraget", "0")
                        .data("Id_14L.BUTTON.TimeTracker.x", "8").data("Id_14L.BUTTON.TimeTracker.y", "8")
                        .data("Id_17.val.BsmHiddenViewField", "1").data("Id_19.Field.0.DocumentType", "-1")
                        .data("Id_19.Field.0.Field_TimeTracker", "4")
                        .data("Id_19.Field.0.Field_TimeTrackerDate:0", today)
                        .data("Id_19.Field.0.Field_TimeTrackerDate2:0", today).execute();
        Document ttPage = response.parse();
        saveTaid(ttPage);
        return ttPage;
    }

    private List<String> readProjectNames(Document timeTrackerPage) throws IOException, CrawlingException {
        List<String> projectNames = new ArrayList<>();
        Elements options = timeTrackerPage.select("select[id$=NewWhat_0_0] option");
        if (options.isEmpty()) {
            throw new CrawlingException("No projects found.");
        }
        for (Element option : options) {
            String text = option.text();
            if (!text.trim().isEmpty()) {
                projectNames.add(text);
            }
        }
        LOGGER.info("Project names read.");
        return projectNames;
    }

    private void clockTime(final Date start, final Date end, final String projectName, final Document ttPage)
        throws IOException {
        String optionValue = null;
        Elements options = ttPage.select("select[id$=NewWhat_0_0] option");
        for (Element option : options) {
            if (projectName.equals(option.text())) {
                optionValue = option.val();
                break;
            }
        }
        Response response =
                Jsoup.connect(settings.getProjectileUrl()).method(Method.POST).cookies(cookies).data("taid", taid)
                        .data("CurrentFocusField", "name").data("CurrentDraggable", "0").data("CurrentDropTraget", "0")
                        .data(ttPage.select("input[name$=.val.BsmHiddenViewField]").first().attr("name"), "1")
                        .data(ttPage.select("input[name$=+0+0__ButtonTable_]").first().attr("name") + ".x", "8")
                        .data(ttPage.select("input[name$=+0+0__ButtonTable_]").first().attr("name") + ".y", "8")
                        .data(ttPage.select("select[id$=.Field.0.TimeTrackerConfirmationAction").first().id(), "-1")
                        .data(ttPage.select("input[id$=Field.0.Begin:0]").first().id(), formatToday())
                        .data(ttPage.select("input.rw[id$=NewFrom_0_0]").first().id(), formatTime(start))
                        .data(ttPage.select("input.rw[id$=NewTo_0_0]").first().id(), formatTime(end))
                        .data(ttPage.select("input.rw[id$=NewTime_0_0]").first().id(), "")
                        .data(ttPage.select("select[id$=NewWhat_0_0]").first().id(), optionValue)
                        .data(ttPage.select("input.rw[id$=NewNote_0_0]").first().id(), "").execute();
        saveTaid(response.parse());
        LOGGER.info("Time clocked for project '" + projectName + "'.");
    }

    private void logout() throws IOException {
        Response execute =
                Jsoup.connect(settings.getProjectileUrl()).method(Method.POST).cookies(cookies).data("taid", taid)
                        .data("Id_14L.BUTTON.logout.x", "3").data("Id_14L.BUTTON.logout.y", "9").execute();
        final Elements select = execute.parse().select("td:contains(korrekt beendet)");
        if (select.isEmpty()) {
            LOGGER.severe("Error while logging out.");
        } else {
            LOGGER.info("User logged out.");
        }
    }

    /** Reads the transaction ID from the response and stores it to a field */
    private void saveTaid(final Document page) {
        taid = page.select("input[name=taid]").val();
        LOGGER.finer("TAID: " + taid);
    }

    /** Time formatted to Projectile format */
    private String formatTime(final Date time) {
        return new SimpleDateFormat(settings.getTimeFormat()).format(time);
    }

    /** Current date formatted as dd.MM.yyyy */
    private String formatToday() {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    }
}
