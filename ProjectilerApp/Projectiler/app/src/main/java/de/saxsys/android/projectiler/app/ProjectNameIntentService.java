package de.saxsys.android.projectiler.app;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import de.saxsys.android.projectiler.app.backend.Projectiler;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ProjectNameIntentService extends IntentService {

    private Projectiler projectiler;

    public ProjectNameIntentService() {
        super("ProjectNameIntentService");
        Log.i("ProjectNameIntentService", "ProjectNameIntentService");
        projectiler = Projectiler.createDefaultProjectiler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("ProjectNameIntentService", "onHandleIntent");
        if (intent != null) {

            String selectedProject = intent.getStringExtra(ProjectilerAppWidget.EXTRA_PROJECT_NAME);

            Log.i("ProjectNameIntentService", "selected Project " + selectedProject);

            WidgetUtils.showProgressBarOnWidget(getApplicationContext(), projectiler);

            projectiler.saveProjectName(getApplicationContext(), selectedProject);

            WidgetUtils.hideProgressBarOnWidget(getApplicationContext(), projectiler);


        }
    }

}
