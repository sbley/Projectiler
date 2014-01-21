package de.saxsys.projectiler.misc;

import java.util.Date;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;
import de.saxsys.projectiler.UserDataStore;

public class CheckOutTask extends Task<Date> {

    private final String projectKey;
    private final Projectiler projectiler;

    public CheckOutTask(final Projectiler projectiler, final String projectKey) {
        this.projectiler = projectiler;
        this.projectKey = projectKey;
    }

    @Override
    protected Date call() throws Exception {
        Date checkout = null;
        try {
            checkout = projectiler.checkout(projectKey);
            final UserDataStore instance = UserDataStore.getInstance();
            instance.setProjectName(projectKey);
            instance.save();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        this.succeeded();
        return checkout;
    }
}
