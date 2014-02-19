package de.saxsys.projectiler.crawler.selenium;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.saxsys.projectiler.crawler.Crawler;
import de.saxsys.projectiler.crawler.CrawlingException;
import de.saxsys.projectiler.crawler.Credentials;
import de.saxsys.projectiler.crawler.InvalidCredentialsException;
import de.saxsys.projectiler.crawler.Settings;

/**
 * Interacts with Projectile via Selenium Web Driver.
 * 
 * @author stefan.bley
 */
public class SeleniumCrawler implements Crawler {

    private static final Logger LOGGER = Logger.getLogger(SeleniumCrawler.class.getSimpleName());
    private WebDriver driver;
    private final Settings settings;
    private final SeleniumSettings seleniumSettings = new SeleniumSettings();

    public SeleniumCrawler(final Settings settings) {
        this.settings = settings;
    }

    @Override
    public void checkCredentials(final Credentials user) throws CrawlingException {
        initDriver();
        try {
            login(user);
            logout();
        } catch (final WebDriverException e) {
            throw new CrawlingException("Error while checking credentials.", e);
        } finally {
            releaseDriver();
        }
    }

    @Override
    public List<String> getProjectNames(final Credentials user) throws CrawlingException {
        initDriver();
        try {
            login(user);
            openTimeTracker();
            return readProjectNames();
        } catch (final WebDriverException e) {
            throw new CrawlingException("Error while retrieving project names.", e);
        } finally {
            try {
                logout();
            } catch (final WebDriverException e) {
                throw new CrawlingException("Error while retrieving project names.", e);
            } finally {
                releaseDriver();
            }
        }
    }

    @Override
    public void clock(final Credentials user, final String projectName, final Date start, final Date end)
        throws CrawlingException {
        initDriver();
        try {
            login(user);
            openTimeTracker();
            clockTime(start, end, projectName);
        } catch (final WebDriverException e) {
            throw new CrawlingException("Error while clocking time.", e);
        } finally {
            try {
                logout();
            } catch (final WebDriverException e) {
                throw new CrawlingException("Error while clocking time.", e);
            } finally {
                releaseDriver();
            }
        }
    }

    private void initDriver() {
        if ("firefox".equals(seleniumSettings.getDriver())) {
            this.driver = firefoxDriver();
        } else {
            // headless driver
            this.driver = htmlUnitDriver();
        }
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
    }

    private void login(final Credentials user) throws InvalidCredentialsException {
        driver.get(settings.getProjectileUrl());
        driver.findElement(By.name("login")).sendKeys(user.getUsername());
        final WebElement txtPassword = driver.findElement(By.name("password"));
        txtPassword.sendKeys(user.getPassword());
        txtPassword.submit();
        try {
            driver.findElement(By.name("password"));
            throw new InvalidCredentialsException();
        } catch (final NoSuchElementException e) {
            LOGGER.info("User " + user.getUsername() + " logged in.");
        }
    }

    private void openTimeTracker() {
        final WebElement btnIntro =
                (new WebDriverWait(driver, 2)).until(presenceOfElementLocated(By
                        .cssSelector("input[name$='BUTTON.intro']")));
        btnIntro.click();
        final WebElement selectTimeTracker =
                (new WebDriverWait(driver, 2)).until(presenceOfElementLocated(By
                        .cssSelector("select[id$='Field_TimeTracker']")));
        new Select(selectTimeTracker).selectByVisibleText("heute");
        driver.findElement(By.cssSelector("input[title=TimeTracker]")).click();
        LOGGER.info("Timetracker opened.");
    }

    private List<String> readProjectNames() {
        final List<String> projectNames = new ArrayList<>();
        final List<WebElement> options = driver.findElements(By.cssSelector("select[id$='NewWhat_0_0'] option"));
        for (final WebElement option : options) {
            final String text = option.getText();
            if (!text.trim().isEmpty()) {
                projectNames.add(text);
            }
        }
        LOGGER.info("Project names read.");
        return projectNames;
    }

    /**
     * @param projectName can be substring of the project name but must be identify only one project
     */
    private void clockTime(final Date start, final Date end, final String projectName) {
        driver.findElement(By.cssSelector("input.rw[id$='NewFrom_0_0']")).sendKeys(formatTime(start));
        driver.findElement(By.cssSelector("input.rw[id$='NewTo_0_0']")).sendKeys(formatTime(end));
        final WebElement selProject = driver.findElement(By.cssSelector("select[id$='NewWhat_0_0']"));
        final WebElement option = selProject.findElement(By.xpath(".//option[contains(., '" + projectName + "')]"));
        String selectedProjectValue = option.getAttribute("value");
        option.click();
        driver.findElement(By.cssSelector("input[title='Änderungen speichern']")).click();
        try {
            final WebElement btnConfirmOverwrite =
                    driver.findElement(By.cssSelector("input[name$='ForceOverwriteYes']"));
            btnConfirmOverwrite.click();
            LOGGER.info("Overwrite confirmed.");
        } catch (final NoSuchElementException e) {
        } finally {
            // verify time has been clocked
            String inputStartId =
                    driver.findElement(By.cssSelector("input.rw[id*='Field_Start'][value='" + formatTime(start) + "']"))
                            .getAttribute("id");
            String inputEndId = inputStartId.replace("Start", "End");
            driver.findElement(By.cssSelector("input.rw[id='" + inputEndId + "'][value='" + formatTime(end) + "']"));
            String inputWhatId = inputStartId.replace("Start", "What");
            driver.findElement(By.cssSelector("select[id='" + inputWhatId + "'] option[selected][value='"
                    + selectedProjectValue + "']"));
            LOGGER.info("Time clocked for project '" + projectName + "'.");
        }
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
        try {
            return new FirefoxDriver();
        } catch (WebDriverException e) {
            return new FirefoxDriver(new FirefoxBinary(new File(seleniumSettings.getFirefoxBinaryPath())), null);
        }
    }
}
