package de.saxsys.projectiler.concurrent;

import java.util.Date;

import javafx.concurrent.Task;
import de.saxsys.projectiler.InvalidCredentialsException;
import de.saxsys.projectiler.Notification;
import de.saxsys.projectiler.Projectiler;
import de.saxsys.projectiler.ProjectilerException;

public class CheckOutTask extends Task<Date> {

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
            checkout = projectiler.checkout(projectKey);
        } catch (final IllegalStateException e) {
            Notification.Notifier.INSTANCE.notifyWarning("Fehler beim buchen",
                    "Es dürfen keine Buchungen unter einer Minute angelegt werden.");
            e.printStackTrace();
        } catch (final InvalidCredentialsException e) {
            Notification.Notifier.INSTANCE.notifyWarning("Fehler beim buchen", "Logindaten sind falsch.");
            e.printStackTrace();
        } catch (final ProjectilerException e) {
            Notification.Notifier.INSTANCE.notifyWarning("Fehler beim buchen", "Haha.");
            e.printStackTrace();
        }
        projectiler.saveProjectName(projectKey);

        this.succeeded();
        return checkout;
    }
}
