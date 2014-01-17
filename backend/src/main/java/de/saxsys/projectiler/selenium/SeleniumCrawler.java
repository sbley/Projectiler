package de.saxsys.projectiler.selenium;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;

import de.saxsys.projectiler.Crawler;
import de.saxsys.projectiler.domain.User;

public class SeleniumCrawler implements Crawler {

	private WebDriver driver;
	private Settings settings;

	public SeleniumCrawler(Settings settings) {
		this.settings = settings;
	}

	@Override
	public void clock(final User user, final String projectName) {

		initDriver();

		login(user);

		openTimeTracker();

		clockTime(projectName);

		logout();

		releaseDriver();
	}

	private void initDriver() {
		if ("firefox".equals(settings.getDriver())) {
			this.driver = firefoxDriver();
		} else {
			// headless driver
			this.driver = htmlUnitDriver();
		}
	}

	protected void login(User user) {
		driver.get(settings.getProjectileUrl());
		driver.findElement(By.name("login")).sendKeys(user.getUsername());
		WebElement txtPassword = driver.findElement(By.name("password"));
		txtPassword.sendKeys(user.getPassword());
		txtPassword.submit();
	}

	protected void openTimeTracker() {
		new Select(driver.findElement(By.cssSelector("select[id$='Field_TimeTracker']")))
				.selectByVisibleText("heute");
		driver.findElement(By.cssSelector("input[title=TimeTracker]")).click();
	}

	/**
	 * @param projectName
	 *            can be substring of the project name but must be identify only
	 *            one project
	 */
	protected void clockTime(String projectName) {
		driver.findElement(By.cssSelector("input.rw[id$='NewFrom_0_0']")).sendKeys("08:30");
		driver.findElement(By.cssSelector("input.rw[id$='NewTo_0_0']")).sendKeys(currentTime());
		WebElement selProject = driver.findElement(By.cssSelector("select[id$='NewWhat_0_0']"));
		selProject.findElement(By.xpath(".//option[contains(., '" + projectName + "')]")).click();
		driver.findElement(By.cssSelector("input[title='Änderungen speichern']")).click();
		try {
			WebElement btnConfirmOverwrite = driver.findElement(By
					.cssSelector("input[name$='ForceOverwriteYes']"));
			btnConfirmOverwrite.click();
		} catch (NoSuchElementException e) {
		}
	}

	private void logout() {
		driver.findElement(By.cssSelector("input[title=Abmelden]")).click();
	}

	private void releaseDriver() {
		driver.quit();
	}

	private CharSequence currentTime() {
		return new SimpleDateFormat(settings.getTimeFormat()).format(new Date());
	}

	// headless driver
	private WebDriver htmlUnitDriver() {
		return new HtmlUnitDriver();
	}

	private WebDriver firefoxDriver() {
		return new FirefoxDriver(new FirefoxBinary(new File(settings.getFirefoxBinaryPath())), null);
	}

}
