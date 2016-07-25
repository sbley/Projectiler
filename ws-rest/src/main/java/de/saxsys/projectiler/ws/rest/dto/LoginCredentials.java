package de.saxsys.projectiler.ws.rest.dto;

/**
 * 
 * @author stefan.bley
 */
public class LoginCredentials {

  private String username;
  private String password;

  public LoginCredentials(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
