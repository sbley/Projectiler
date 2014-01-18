package de.saxsys.projectiler.crawler.selenium;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.saxsys.projectiler.crawler.Crawler;
import de.saxsys.projectiler.crawler.Credentials;
import de.saxsys.projectiler.crawler.InvalidCredentialsException;
import de.saxsys.projectiler.crawler.Settings;

public class SeleniumCrawler implements Crawler {

	private static final Logger LOGGER = Logger.getLogger(SeleniumCrawler.class.getName());
	private WebDriver driver;
	private Settings settings;
	private SeleniumSettings seleniumSettings;

	public SeleniumCrawler(Settings settings) {
		this.settings = settings;
	}

	@Override
	public void clock(final Credentials user, final String projectName, final Date start,
			final Date end) {

		initDriver();
		login(user);
		openTimeTracker();

		clockTime(start, end, projectName);

		logout();
		releaseDriver();
	}

	@Override
	public List<String> getProjectNames(final Credentials user) {

		initDriver();
		login(user);
		openTimeTracker();

		List<String> projectNames = readProjectNames();

		logout();
		releaseDriver();
		return projectNames;
	}

	private void initDriver() {
		if ("firefox".equals(seleniumSettings.getDriver())) {
			this.driver = firefoxDriver();
		} else {
			// headless driver
			this.driver = htmlUnitDriver();
		}
	}

	private void login(Credentials user) {
		driver.get(settings.getProjectileUrl());
		driver.findElement(By.name("login")).sendKeys(user.getUsername());
		WebElement txtPassword = driver.findElement(By.name("password"));
		txtPassword.sendKeys(user.getPassword());
		txtPassword.submit();
		try {
			driver.findElement(By.name("password"));
			throw new InvalidCredentialsException();
		} catch (NoSuchElementException e) {
			LOGGER.info("User " + user.getUsername() + " logged in.");
		}
	}

	private void openTimeTracker() {
		WebElement btnIntro = (new WebDriverWait(driver, 2)).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector("input[name$='BUTTON.intro']")));
		btnIntro.click();
		new Select(driver.findElement(By.cssSelector("select[id$='Field_TimeTracker']")))
				.selectByVisibleText("heute");
		driver.findElement(By.cssSelector("input[title=TimeTracker]")).click();
		LOGGER.info("Timetracker opened.");
	}

	/**
	 * @param projectName
	 *            can be substring of the project name but must be identify only
	 *            one project
	 */
	private void clockTime(final Date start, final Date end, final String projectName) {
		driver.findElement(By.cssSelector("input.rw[id$='NewFrom_0_0']")).sendKeys(
				formatTime(start));
		driver.findElement(By.cssSelector("input.rw[id$='NewTo_0_0']")).sendKeys(formatTime(end));
		WebElement selProject = driver.findElement(By.cssSelector("select[id$='NewWhat_0_0']"));
		selProject.findElement(By.xpath(".//option[contains(., '" + projectName + "')]")).click();
		driver.findElement(By.cssSelector("input[title='Änderungen speichern']")).click();
		try {
			WebElement btnConfirmOverwrite = driver.findElement(By
					.cssSelector("input[name$='ForceOverwriteYes']"));
			btnConfirmOverwrite.click();
			LOGGER.info("Overwrite confirmed.");
		} catch (NoSuchElementException e) {
		} finally {
			LOGGER.info("Time clocked for project '" + projectName + "'.");
		}
	}

	private List<String> readProjectNames() {
		List<String> projectNames = new ArrayList<>();
		List<WebElement> options = driver.findElements(By
				.cssSelector("select[id$='NewWhat_0_0'] option"));
		for (WebElement option : options) {
			String text = option.getText();
			if (!text.trim().isEmpty()) {
				projectNames.add(text);
			}
		}
		LOGGER.info("Project names read.");
		return projectNames;
	}

	private void logout() {
		driver.findElement(By.cssSelector("input[title=Abmelden]")).click();
		LOGGER.info("User logged out.");
	}

	private void releaseDriver() {
		driver.quit();
	}

	private CharSequence formatTime(final Date time) {
		return new SimpleDateFormat(settings.getTimeFormat()).format(time);
	}

	// headless driver
	private WebDriver htmlUnitDriver() {
		return new HtmlUnitDriver(false);
	}

	private WebDriver firefoxDriver() {
		return new FirefoxDriver(new FirefoxBinary(
				new File(seleniumSettings.getFirefoxBinaryPath())), null);
	}
}
