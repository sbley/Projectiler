package de.saxsys.projectiler.crawler.jsoup;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.saxsys.projectiler.crawler.Credentials;
import de.saxsys.projectiler.crawler.Password;
import de.saxsys.projectiler.crawler.Settings;

public class JSoupCrawlerTest {

	@Test
	public void getProjectName() throws Exception {
		Credentials credentials = new Credentials("stefan.bley", Password.get());
		final JSoupCrawler jSoupCrawler = new JSoupCrawler(new Settings());
		List<String> projectNames = jSoupCrawler.getProjectNames(credentials);
		jSoupCrawler.clock(credentials, projectNames.get(3), new Date(2014, 0, 1, 0, 30),
				new Date());
	}
}
