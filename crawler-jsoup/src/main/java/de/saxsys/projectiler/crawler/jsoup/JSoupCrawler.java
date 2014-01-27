package de.saxsys.projectiler.crawler.jsoup;

import java.io.IOException;
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

	public JSoupCrawler(final Settings settings) {
		this.settings = settings;
	}

	@Override
	public void checkCredentials(final Credentials credentials) throws CrawlingException {
		try {
			Map<String, String> cookies = login(credentials);
			logout(cookies);
		} catch (final IOException e) {
			throw new CrawlingException("Error while checking credentials.", e);
		}
	}

	@Override
	public void clock(final Credentials credentials, final String projectName, final Date start,
			final Date end) throws CrawlingException {
		try {
			Map<String, String> cookies = login(credentials);
			Response response = openTimeTracker(cookies);

			clockTime(start, end, projectName, cookies, response.parse());

			logout(cookies);
		} catch (final IOException e) {
			throw new CrawlingException("Error while clocking time.", e);
		}
	}

	@Override
	public List<String> getProjectNames(final Credentials credentials) throws CrawlingException {
		try {
			Map<String, String> cookies = login(credentials);
			Response response = openTimeTracker(cookies);

			final List<String> projectNames = readProjectNames(response.parse());

			logout(cookies);
			return projectNames;
		} catch (IOException e) {
			throw new CrawlingException("Error while retrieving project names.", e);
		}
	}

	/**
	 * Login to Projectile and return the cookies contaning the session ID.
	 */
	private Map<String, String> login(final Credentials cred) throws IOException {
		Response response = Jsoup.connect(settings.getProjectileUrl()).method(Method.POST)
				.data("login", cred.getUsername()).data("password", cred.getPassword())
				.data("jsenabled", "0").data("external.loginOK.x", "8")
				.data("external.loginOK.y", "8").execute();
		Document startPage = response.parse();
		if (startPage.getElementsByAttributeValue("name", "password").isEmpty()) {
			String sessionId = response.cookie(JSESSIONID);
			LOGGER.info("User " + cred.getUsername() + " logged in with session ID " + sessionId
					+ ".");
			return response.cookies();
		} else {
			throw new InvalidCredentialsException();
		}
	}

	private Response openTimeTracker(final Map<String, String> cookies) throws IOException {
		String today = formatToday();
		Response response = Jsoup.connect(settings.getProjectileUrl()).method(Method.POST)
				.cookies(cookies).data("taid", "2").data("Id_34L.act.MenuSelect_showintro", "0")
				.data("CurrentFocusField", "0").data("CurrentDraggable", "0")
				.data("CurrentDropTraget", "0").data("Id_15L.FIELD.queryLogic", "ALL")
				.data("Id_15L.FIELD.doctype", "-1").data("Id_14L.val.Invisible", "0")
				.data("Id_18.val.BsmHiddenViewField", "1").data("Id_20.Field.0.WordQueryType", "0")
				.data("Id_20.Field.0.Category", "-1").data("Id_20.Field.0.DocumentType", "-1")
				.data("Id_20.Field.0.Field_TimeTracker", "4")
				.data("Id_20.Field.0.Field_TimeTrackerDate:0", today)
				.data("Id_20.Field.0.Field_TimeTrackerDate2:0", today).execute();
		return response;
	}

	private List<String> readProjectNames(Document timeTrackerPage) throws IOException {
		List<String> projectNames = new ArrayList<>();
		Elements options = timeTrackerPage.select("select[id$=NewWhat_0_0] option");
		for (Element option : options) {
			String text = option.text();
			if (!text.trim().isEmpty()) {
				projectNames.add(text);
			}
		}
		LOGGER.info("Project names read.");
		return projectNames;
	}

	private void clockTime(final Date start, final Date end, final String projectName,
			final Map<String, String> cookies, final Document ttPage) throws IOException {
		String optionValue = null;
		Elements options = ttPage.select("select[id$=NewWhat_0_0] option");
		for (Element option : options) {
			if (projectName.equals(option.text())) {
				optionValue = option.val();
				break;
			}
		}
		Jsoup.connect(settings.getProjectileUrl())
				.method(Method.POST)
				.cookies(cookies)
				.data("taid", "2")
				.data("CurrentFocusField", "name")
				.data("CurrentDraggable", "0")
				.data("CurrentDropTraget", "0")
				.data("Id_15L.FIELD.queryLogic", "ALL")
				.data("Id_15L.FIELD.doctype", "-1")
				.data("Id_14L.val.Invisible", "0")
				.data(ttPage.select("input[name$=.val.BsmHiddenViewField]").first().attr("name"),
						"1")
				.data(ttPage.select("input[name$=+0+0__ButtonTable_]").first().attr("name") + ".x",
						"8")
				.data(ttPage.select("input[name$=+0+0__ButtonTable_]").first().attr("name") + ".y",
						"8")
				.data(ttPage.select("select[id$=.Field.0.TimeTrackerConfirmationAction").first()
						.id(), "-1")
				.data(ttPage.select("input[id$=Field.0.Begin:0]").first().id(), formatToday())
				.data(ttPage.select("input.rw[id$=NewFrom_0_0]").first().id(), formatTime(start))
				.data(ttPage.select("input.rw[id$=NewTo_0_0]").first().id(), formatTime(end))
				.data(ttPage.select("input.rw[id$=NewTime_0_0]").first().id(), "")
				.data(ttPage.select("select[id$=NewWhat_0_0]").first().id(), optionValue)
				.data(ttPage.select("input.rw[id$=NewNote_0_0]").first().id(), "").execute();
		LOGGER.info("Time clocked for project '" + projectName + "'.");
	}

	private void logout(final Map<String, String> cookies) throws IOException {
		Jsoup.connect(settings.getProjectileUrl()).method(Method.POST).cookies(cookies)
				.data("Id_15L.BUTTON.logout.x", "3").data("Id_15L.BUTTON.logout.y", "9").execute();
		LOGGER.info("User logged out.");

	}

	private String formatTime(final Date time) {
		return new SimpleDateFormat(settings.getTimeFormat()).format(time);
	}

	/** current date formatted as dd.MM.yyyy */
	private String formatToday() {
		return new SimpleDateFormat("dd.MM.yyyy").format(new Date());
	}
}
