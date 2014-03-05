package de.saxsys.projectiler.concurrent;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;
import de.saxsys.projectiler.crawler.ConnectionException;
import de.saxsys.projectiler.crawler.InvalidCredentialsException;
import de.saxsys.projectiler.misc.Notification;

public class LoginTask extends Task<Boolean> {

    private static final Logger LOGGER = Logger.getLogger(LoginTask.class.getSimpleName());

    private final Projectiler projectiler;
    private final String username;
    private final String password;

    public LoginTask(final Projectiler projectiler, final String username, final String password) {
        this.projectiler = projectiler;
        this.username = username;
        this.password = password;
    }

    @Override
    protected Boolean call() {
        LOGGER.info("Try to log in");
        try {
            projectiler.saveCredentials(username, password);
        } catch (InvalidCredentialsException e) {
            logError("Falsche Logindaten.", e);
            return false;
        } catch (ConnectionException e) {
            logError("Projectile kann nicht erreicht werden.", e);
            return false;
        } catch (Exception e) {
            logError("Unbekannter Fehler aufgetreten.", e);
            return false;
        }

        this.succeeded();
        return true;
    }

    private void logError(String error, Throwable e) {
        LOGGER.log(Level.SEVERE, "Error during login", e);
        Notification.Notifier.INSTANCE.notifyError("Fehler beim Login", error);
    }
}
