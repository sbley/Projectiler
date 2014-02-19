package de.saxsys.projectiler.concurrent;

import java.util.Date;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;
import de.saxsys.projectiler.crawler.InvalidCredentialsException;
import de.saxsys.projectiler.misc.Notification;

public class CheckOutTask extends Task<Date> {

    private static final Logger LOGGER = Logger.getLogger(CheckOutTask.class.getSimpleName());

    private final String projectKey;
    private final Projectiler projectiler;

    public CheckOutTask(final Projectiler projectiler, final String projectKey) {
        this.projectiler = projectiler;
        this.projectKey = projectKey;
    }

    @Override
    protected Date call() {
        Date checkout = null;
        try {
            LOGGER.info("Perform Checkout from GUI");
            checkout = projectiler.checkout(projectKey);
            Notification.Notifier.INSTANCE.notifySuccess("Buchung erfolgreich", "Buchung im Projectile durchgeführt.");

        } catch (final IllegalStateException e) {
            e.printStackTrace();
            logError("Die Buchungsdauer war zu kurz (mind. 1 Minute).");
        } catch (final InvalidCredentialsException e) {
            e.printStackTrace();
            logError("Logindaten sind falsch.");
        } catch (final Exception e) {
            e.printStackTrace();
            logError("Unbekannter Fehler aufgetreten.");
        }
        projectiler.saveProjectName(projectKey);

        this.succeeded();
        return checkout;
    }

    private void logError(String error) {
        Notification.Notifier.INSTANCE.notifyError("Fehler beim Buchen", error);
    }
}
