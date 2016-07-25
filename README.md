Automatic time tracking for Projectile
======================================

Check in when you start working and select a project. Check out once you are finished.
Your time will automatically be inserted into your Projectile timesheet.

API Usage
---------

```java
// Configure API
public class ProductionSettings extends Settings {
    public String getProjectileUrl() {
        return "https://myprojectile.org/projectile";
    }
    public String getApplicationKey() {
        return "my-app-key";
    }
}
Settings settings = new ProductionSettings();
Crawler crawler = new RetrofitClient(settings);

// Use API
Credentials credentials = new Credentials("my-username", "my-password");
crawler.checkCredentials(credentials);
...

```