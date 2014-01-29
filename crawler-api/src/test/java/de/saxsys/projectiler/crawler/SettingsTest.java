package de.saxsys.projectiler.crawler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SettingsTest {

	@Test
	public void getProjectileUrl_returnsDefaultUrl() throws Exception {
		assertThat(new Settings().getProjectileUrl(), is("https://pt.saxsys.de/projectile/start"));
	}

	@Test
	public void getProjectileUrl_returnsSystemPropertyIfSet() throws Exception {
		String previous = System.setProperty("projectile.url", "http://localhost/pt");

		assertThat(new Settings().getProjectileUrl(), is("http://localhost/pt"));

		// reset
		if (null == previous)
			System.clearProperty("projectile.url");
		else
			System.setProperty("projectile.url", previous);
	}
}
