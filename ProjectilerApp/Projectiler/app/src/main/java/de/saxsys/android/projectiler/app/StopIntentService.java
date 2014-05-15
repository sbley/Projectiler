package de.saxsys.android.projectiler.app;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import de.saxsys.android.projectiler.app.backend.Projectiler;
import de.saxsys.android.projectiler.app.crawler.CrawlingException;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class StopIntentService extends IntentService {

    private Projectiler projectiler;
    private RemoteViews views;

    public StopIntentService() {
        super("StopIntentService");
        projectiler = Projectiler.createDefaultProjectiler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("StopIntentService", "onHandleIntent");
        if (intent != null) {

            // zeige progressBar in Widget
            WidgetUtils.showProgressBarOnWidget(getApplicationContext(), projectiler);

            new StopAsyncTask().execute();
        }
    }

    private class StopAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {
                projectiler.checkout(getApplicationContext(), projectiler.getProjectName(getApplicationContext()));
            } catch (CrawlingException e) {
                e.printStackTrace();
                return e.getMessage();
            } catch (IllegalStateException e1) {
                return e1.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            WidgetUtils.hideProgressBarOnWidget(getApplicationContext(), projectiler);
        }
    }


}
