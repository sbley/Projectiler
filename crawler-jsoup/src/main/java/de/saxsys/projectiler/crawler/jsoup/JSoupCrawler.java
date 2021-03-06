package de.saxsys.projectiler.crawler.jsoup;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.saxsys.projectiler.crawler.Booking;
import de.saxsys.projectiler.crawler.ConnectionException;
import de.saxsys.projectiler.crawler.Crawler;
import de.saxsys.projectiler.crawler.CrawlingException;
import de.saxsys.projectiler.crawler.Credentials;
import de.saxsys.projectiler.crawler.InvalidCredentialsException;
import de.saxsys.projectiler.crawler.NoBudgetException;
import de.saxsys.projectiler.crawler.OverlapException;
import de.saxsys.projectiler.crawler.Settings;

/**
 * Interacts with Projectile via JSoup.
 * 
 * @author stefan.bley
 * @see <a href="http://jsoup.org">http://jsoup.org</a>
 */
public class JSoupCrawler implements Crawler {

    private static final Logger LOGGER = Logger.getLogger(JSoupCrawler.class.getSimpleName());
    private static final String JSESSIONID = "JSESSIONID";
    /** select box value for 'Heute' */
    private static final String TODAY = "4";
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
            Document startPage = login(credentials);
            logout(startPage);
        } catch (final UnknownHostException e) {
            throw new ConnectionException(e);
        } catch (final IOException e) {
            throw new CrawlingException("Error while checking credentials.", e);
        }
    }

    @Override
    public List<String> getProjectNames(final Credentials credentials) throws CrawlingException {
        try {
            Document startPage = login(credentials);
            Document ttPage = openTimeTracker(startPage);

            List<String> projectNames = readProjectNames(ttPage);

            logout(ttPage);
            return projectNames;
        } catch (final UnknownHostException e) {
            throw new ConnectionException(e);
        } catch (final IOException e) {
            throw new CrawlingException("Error while retrieving project names.", e);
        }
    }

    @Override
    public void clock(final Credentials credentials, final String projectName, final Date start, final Date end,
            final String comment) throws CrawlingException {
        try {
            Document startPage = login(credentials);
            Document ttPage = openTimeTracker(startPage);

            clockTime(start, end, projectName, comment, ttPage);

            logout(ttPage);
        } catch (final UnknownHostException e) {
            throw new ConnectionException(e);
        } catch (final IOException e) {
            throw new CrawlingException("Error while clocking time.", e);
        }
    }

    @Override
    public List<Booking> getDailyReport(final Credentials credentials) throws ConnectionException, CrawlingException {
        try {
            Document startPage = login(credentials);
            Document ttPage = openTimeTracker(startPage);

            List<Booking> bookings = readDailyReport(ttPage);

            logout(ttPage);
            return bookings;
        } catch (final UnknownHostException e) {
            throw new ConnectionException(e);
        } catch (final IOException e) {
            throw new CrawlingException("Error while retrieving daily report.", e);
        }
    }

    /**
     * Login to Projectile and return the cookies containing the session ID.
     * 
     * @throws InvalidCredentialsException
     *             if the credentials are wrong
     */
    private Document login(final Credentials cred) throws IOException, InvalidCredentialsException {
        Response response = jsoupConnection().data("login", cred.getUsername())
                .data("password", cred.getPassword())
                .data("jsenabled", "0")
                .data("external.loginOK.x", "8")
                .data("external.loginOK.y", "8")
                .execute();
        Document startPage = response.parse();
        if (startPage.getElementsByAttributeValue("name", "password").isEmpty()) {
            String sessionId = response.cookie(JSESSIONID);
            LOGGER.info("User " + cred.getUsername() + " logged in with session ID " + sessionId + ".");
            saveTaid(startPage);
            cookies = response.cookies();
            return startPage;
        } else {
            throw new InvalidCredentialsException();
        }
    }

    private Document openTimeTracker(final Document startPage) throws IOException {
        Document currentPage = startPage;
        boolean autostart = !startPage.select("input[name$=.Field.0.Autostart]").isEmpty();
        if (autostart) {
            currentPage = openIntroPage(currentPage);
        }
        Document introPage = openStandardIntroPage(currentPage);

        String today = formatToday();
        Response response = jsoupConnection().cookies(cookies)
                .data("taid", taid)
                .data("CurrentFocusField", "0")
                .data("CurrentDraggable", "0")
                .data("CurrentDropTraget", "0")
                .data(introPage.select("input[name$=.Button=TimeTracker1").first().attr("name") + ".x", "8")
                .data(introPage.select("input[name$=.Button=TimeTracker1").first().attr("name") + ".y", "8")
                .data(introPage.select("input[name$=.val.BsmHiddenViewField]").first().attr("name"), "1")
                .data(introPage.select("select[name$=.Field.0.Field_TimeTracker").first().attr("name"), TODAY)
                .data(introPage.select("input[name$=.Field.0.Field_TimeTrackerDate:0").first().attr("name"), today)
                .data(introPage.select("input[name$=.Field.0.Field_TimeTrackerDate2:0").first().attr("name"), today)
                .execute();
        Document ttPage = response.parse();
        saveTaid(ttPage);
        return ttPage;
    }

    private Document openIntroPage(Document startPage) throws IOException {
        Response response = jsoupConnection().cookies(cookies)
                .data("taid", taid)
                .data("CurrentFocusField", "0")
                .data("CurrentDraggable", "0")
                .data("CurrentDropTraget", "0")
                .data(startPage.select("input[name$=.BUTTON.intro").first().attr("name") + ".x", "8")
                .data(startPage.select("input[name$=.BUTTON.intro").first().attr("name") + ".y", "8")
                .data(startPage.select("input[name$=.val.BsmHiddenViewField]").first().attr("name"), "1")
                .execute();
        Document introPage = response.parse();
        saveTaid(introPage);
        return introPage;
    }

    private Document openStandardIntroPage(final Document startPage) throws IOException {
        Response response = jsoupConnection().cookies(cookies)
                .data("taid", taid)
                .data("CurrentFocusField", "0")
                .data("CurrentDraggable", "0")
                .data("CurrentDropTraget", "0")
                .data(startPage.select("input[name$=.BUTTON.SelectTab.0").first().attr("name"), "0")
                .data(startPage.select("input[name$=.val.BsmHiddenViewField]").first().attr("name"), "1")
                .execute();
        Document ttPage = response.parse();
        saveTaid(ttPage);
        return ttPage;
    }

    private List<String> readProjectNames(final Document timeTrackerPage) throws IOException, CrawlingException {
        List<String> projectNames = new ArrayList<String>();
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

    private void clockTime(final Date start, final Date end, final String projectName, final String comment,
            final Document ttPage) throws IOException, CrawlingException {
        String optionValue = null;
        Elements options = ttPage.select("select[id$=NewWhat_0_0] option");
        for (Element option : options) {
            if (projectName.equals(option.text())) {
                optionValue = option.val();
                break;
            }
        }
        String nullSafeComment = (null == comment) ? "" : comment;
        final Response response = jsoupConnection().cookies(cookies)
                .data("taid", taid)
                .data("CurrentFocusField", "name")
                .data("CurrentDraggable", "0")
                .data("CurrentDropTraget", "0")
                .data(ttPage.select("input[name$=.val.BsmHiddenViewField]").first().attr("name"), "1")
                .data(ttPage.select("input[name$=+0+0__ButtonTable_]").first().attr("name") + ".x", "8")
                .data(ttPage.select("input[name$=+0+0__ButtonTable_]").first().attr("name") + ".y", "8")
                .data(ttPage.select("select[id$=.Field.0.TimeTrackerConfirmationAction").first().id(), "-1")
                .data(ttPage.select("input[id$=Field.0.Begin:0]").first().id(), formatToday())
                .data(ttPage.select("input.rw[id$=NewFrom_0_0]").first().id(), formatTime(start))
                .data(ttPage.select("input.rw[id$=NewTo_0_0]").first().id(), formatTime(end))
                .data(ttPage.select("input.rw[id$=NewTime_0_0]").first().id(), "")
                .data(ttPage.select("select[id$=NewWhat_0_0]").first().id(), optionValue)
                .data(ttPage.select("input.rw[id$=NewNote_0_0]").first().id(), nullSafeComment)
                .execute();
        Document document = response.parse();
        saveTaid(document);

        // verify time has been clocked
        if (!document.select("td:contains(wird überschrieben)").isEmpty()) {
            LOGGER.info("Booking overlaps an existing booking.");
            throw new OverlapException();
        }
        if (!document.select("td:contains(darf keine weitere Zeit)").isEmpty()) {
            LOGGER.info("No budget left for project '" + projectName + "'.");
            throw new NoBudgetException();
        }
        boolean clocked = false;
        try {
            String inputStartId = document.select("input.rw[id*=Field_Start][value=" + formatTime(start) + "]")
                    .first()
                    .id();
            String inputEndId = inputStartId.replace("Start", "End");
            clocked = !document.select("input.rw[id=" + inputEndId + "][value=" + formatTime(end) + "]").isEmpty();
            String inputWhatId = inputStartId.replace("Start", "What");
            clocked &= !document.select("select[id=" + inputWhatId + "] option[selected][value=" + optionValue + "]")
                    .isEmpty();
            clocked &= document.select("img[src*=error_i1_16.gif]").isEmpty();
        } catch (NullPointerException e) {
            clocked = false;
        } finally {
            if (!clocked) {
                LOGGER.info("Time has not been clocked for project '" + projectName + "'.");
                throw new CrawlingException("Time has not been clocked.");
            }
        }
        LOGGER.info("Time clocked for project '" + projectName + "'.");
    }

    private List<Booking> readDailyReport(final Document timeTrackerPage) {
        List<Booking> bookings = new ArrayList<Booking>();

        final Elements inputsStart = timeTrackerPage.select("input.rw[id*=Field_Start]");
        for (Element inputStart : inputsStart) {
            String inputStartId = inputStart.id();
            String startTime = inputStart.val();
            String inputEndId = inputStartId.replace("Start", "End");
            String endTime = timeTrackerPage.select("input.rw[id=" + inputEndId + "]").first().val();
            String inputWhatId = inputStartId.replace("Start", "What");
            String projectName = timeTrackerPage.select("select[id=" + inputWhatId + "] option[selected]")
                    .first()
                    .text();
            bookings.add(new Booking(projectName, startTime, endTime));
        }

        LOGGER.info("Daily report read.");
        return bookings;
    }

    private void logout(final Document page) throws IOException {
        Response execute = jsoupConnection().cookies(cookies)
                .data("taid", taid)
                .data(page.select("input[name$=L.BUTTON.logout]").first().attr("name") + ".x", "8")
                .data(page.select("input[name$=L.BUTTON.logout]").first().attr("name") + ".y", "8")
                .execute();
        final Elements select = execute.parse().select("td:contains(korrekt beendet)");
        if (select.isEmpty()) {
            LOGGER.severe("Error while logging out.");
        } else {
            LOGGER.info("User logged out.");
        }
    }

    /**
     * Creates a JSoup connection to Projectile with method POST and given timeout
     */
    private Connection jsoupConnection() {
        return Jsoup.connect(settings.getProjectileUrl()).timeout(settings.getTimeout()).method(Method.POST);
    }

    /** Reads the transaction ID from the response and stores it to a field */
    private void saveTaid(final Document page) {
        taid = page.select("input[name=taid]").val();
        LOGGER.finer("TAID: " + taid);
    }

    /** Time formatted to Projectile format */
    private String formatTime(final Date time) {
        return new SimpleDateFormat("HH:mm").format(time);
    }

    /** Current date formatted as dd.MM.yyyy */
    private String formatToday() {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    }
}
