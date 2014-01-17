package de.saxsys.projectiler.domain;

public class User {

	private String username;
	private CharSequence password;

	public User(String username, CharSequence password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public CharSequence getPassword() {
		return password;
	}

}
