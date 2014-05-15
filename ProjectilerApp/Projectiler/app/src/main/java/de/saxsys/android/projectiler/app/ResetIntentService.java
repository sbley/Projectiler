package de.saxsys.android.projectiler.app;

import android.app.IntentService;
import android.content.Intent;

import de.saxsys.android.projectiler.app.backend.Projectiler;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ResetIntentService extends IntentService {

    private Projectiler projectiler;

    public ResetIntentService() {
        super("ResetIntentService");
        projectiler = Projectiler.createDefaultProjectiler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            WidgetUtils.showProgressBarOnWidget(getApplicationContext(), projectiler);

            projectiler.saveProjectName(getApplicationContext(), "");
            projectiler.resetStartTime(getApplicationContext());

            WidgetUtils.hideProgressBarOnWidget(getApplicationContext(), projectiler);
        }
    }

}
