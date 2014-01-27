package de.saxsys.projectiler.concurrent;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;

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
    protected Boolean call() throws Exception {
        try {
            LOGGER.info("Try to log in");
            projectiler.saveCredentials(username, password);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login", e);
            return false;
        }
        this.succeeded();
        return true;
    }
}
