package de.saxsys.android.projectiler.app;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import de.saxsys.android.projectiler.app.backend.Projectiler;
import de.saxsys.android.projectiler.app.crawler.CrawlingException;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;


public class ProjectilerIntentService extends IntentService {

    public static final String ACTION_START = "de.saxsys.android.projectiler.app.action.START";
    public static final String ACTION_STOP = "de.saxsys.android.projectiler.app.action.STOP";
    public static final String ACTION_RESET = "de.saxsys.android.projectiler.app.action.RESET";

    private final Projectiler projectiler;

    public ProjectilerIntentService() {
        super("TestIntentService");
        projectiler = Projectiler.createDefaultProjectiler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TestIntentService", "");
        if (intent != null) {
            WidgetUtils.showProgressBarOnWidget(getApplicationContext(), projectiler);
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                handleActionStart();
            } else if (ACTION_STOP.equals(action)) {
                handleActionStop();
            }else if (ACTION_RESET.equals(action)) {
                handleActionReset();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStart() {
        new StartAsyncTask().execute();
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStop() {
        new StopAsyncTask().execute();
    }

    private void handleActionReset() {
        WidgetUtils.showProgressBarOnWidget(getApplicationContext(), projectiler);

        projectiler.saveProjectName(getApplicationContext(), "");
        projectiler.resetStartTime(getApplicationContext());

        WidgetUtils.hideProgressBarOnWidget(getApplicationContext(), projectiler);
    }

    // AsyncTasks
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
