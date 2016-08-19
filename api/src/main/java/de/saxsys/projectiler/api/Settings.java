package de.saxsys.projectiler.api;

import java.util.concurrent.TimeUnit;

/**
 * Projectile settings.
 * 
 * @author stefan.bley
 */
public abstract class Settings {

  public abstract String getProjectileUrl();

  public abstract String getApplicationKey();

  /** Connection timeout in millis */
  public int getTimeout() {
    return (int) TimeUnit.SECONDS.toMillis(10);
  }
}
