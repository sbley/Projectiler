package de.saxsys.projectiler.misc;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;

public class CheckOutTask extends Task<Boolean> {

    private final String projectKey;
    private final Projectiler projectiler;

    public CheckOutTask(final Projectiler projectiler, final String projectKey) {
        this.projectiler = projectiler;
        this.projectKey = projectKey;
    }

    @Override
    protected Boolean call() throws Exception {
        try {
            projectiler.checkout(projectKey);
        } catch (final Exception e) {
            e.printStackTrace();
            this.succeeded();
            return false;
        }
        this.succeeded();
        return true;
    }
}
