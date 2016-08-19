package de.saxsys.projectiler.crawler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CredentialsTest {

	@Test
	public void constructor_assignsToFields() throws Exception {
		assertThat(new Credentials("username", "password").getUsername(), is("username"));
		assertThat(new Credentials("username", "password").getPassword(), is("password"));
	}
}
