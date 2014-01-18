package de.saxsys.projectiler;

import java.util.Date;
import java.util.List;
import java.util.Random;

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

    private final Credentials user;
    private final Crawler crawler;

    public Projectiler(final Crawler crawler) {
        final UserDataStore store = UserDataStore.getInstance();
        this.user = new Credentials(store.getUserName(), store.getPassword());
        this.crawler = crawler;
    }

    protected Projectiler(final Credentials credentials, final Crawler crawler) {
        user = credentials;
        this.crawler = crawler;
    }

    public void clock(final String projectName) {
        // TODO retrieve start time
        final Date start = new Date(2017, 0, 1, 8, 30);
        final Date end = new Date();
        crawler.clock(user, projectName, start, end);
    }

    public List<String> getProjectNames() {
        return crawler.getProjectNames(user);
    }

    public static void main(final String[] args) {
        final Projectiler projectiler =
                new Projectiler(new Credentials("stefan.bley", Password.get()), new SeleniumCrawler(new Settings()));
        final List<String> projectNames = projectiler.getProjectNames();
        projectiler.clock(projectNames.get(new Random().nextInt(projectNames.size() - 2) + 1));
    }
}
