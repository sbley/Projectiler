package de.saxsys.android.projectiler.app;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;

import de.saxsys.android.projectiler.app.backend.Projectiler;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class StartIntentService extends IntentService {

    private Projectiler projectiler;

    public StartIntentService() {
        super("StartIntentService");
        projectiler = Projectiler.createDefaultProjectiler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            WidgetUtils.showProgressBarOnWidget(getApplicationContext(), projectiler);
            new StartAsyncTask().execute();


        }
    }

    private class StartAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            if(!projectiler.getProjectName(getApplicationContext()).equals("")) {
                projectiler.checkin(getApplicationContext());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            WidgetUtils.hideProgressBarOnWidget(getApplicationContext(), projectiler);

        }
    }

}
