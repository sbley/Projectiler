package de.saxsys.projectiler.concurrent;

import java.util.Date;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import de.saxsys.projectiler.InvalidCredentialsException;
import de.saxsys.projectiler.Notification;
import de.saxsys.projectiler.Projectiler;
import de.saxsys.projectiler.ProjectilerException;

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
            Notification.Notifier.INSTANCE.notifyWarning("Buchung abgebrochen", "Buchungsdauer war zu kurz.");
        } catch (final InvalidCredentialsException e) {
            e.printStackTrace();
            Notification.Notifier.INSTANCE.notifyError("Fehler beim buchen", "Logindaten sind falsch.");
        } catch (final ProjectilerException e) {
            e.printStackTrace();
            Notification.Notifier.INSTANCE.notifyError("Fehler beim buchen", "Haha.");
        }
        projectiler.saveProjectName(projectKey);

        this.succeeded();
        return checkout;
    }
}
