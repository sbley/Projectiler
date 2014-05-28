package de.saxsys.android.projectiler.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import de.saxsys.android.projectiler.app.ProjectilerAppWidget;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ProjectNameIntentService extends IntentService {

    private BusinessProcess businessProcess;

    public ProjectNameIntentService() {
        super("ProjectNameIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            businessProcess = BusinessProcess.getInstance(getApplicationContext());
            String selectedProject = intent.getStringExtra(ProjectilerAppWidget.EXTRA_PROJECT_NAME);

            Log.i("ProjectNameIntentService", "selected Project " + selectedProject);
            businessProcess.saveProjectName(getApplicationContext(), selectedProject);
        }
    }

}
