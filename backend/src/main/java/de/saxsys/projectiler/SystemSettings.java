package de.saxsys.projectiler;

import de.saxsys.projectiler.api.Settings;

/**
 * Read settings from environment variables.
 * <dl>
 * <dt>projectile.url</dt>
 * <dd>Projectile URL, e.g. https://mydomain.com/projectile</dd>
 * <dt>projectile.apiKey</dt>
 * <dd>Projectile application key</dd>
 * </dl>
 * 
 * @author stefan.bley
 * @since 1.0.3
 */
public class SystemSettings extends Settings {

  @Override
  public String getProjectileUrl() {
    return System.getProperty("projectile.url");
  }

  @Override
  public String getApplicationKey() {
    return System.getProperty("projectile.apiKey");
  }

}
