package de.saxsys.android.projectiler.app;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import de.saxsys.android.projectiler.app.crawler.CrawlingException;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;


public class ProjectilerIntentService extends IntentService {

    public static final String ACTION_START = "de.saxsys.android.businessProcess.app.action.START";
    public static final String ACTION_STOP = "de.saxsys.android.businessProcess.app.action.STOP";
    public static final String ACTION_RESET = "de.saxsys.android.businessProcess.app.action.RESET";

    private final BusinessProcess businessProcess;

    public ProjectilerIntentService() {
        super("TestIntentService");
        businessProcess = BusinessProcess.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TestIntentService", "");
        if (intent != null) {
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
        businessProcess.showProgressBarOnWidget(getApplicationContext());
        new StartAsyncTask().execute();
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStop() {
        businessProcess.showProgressBarOnWidget(getApplicationContext());
        new StopAsyncTask().execute();
    }

    private void handleActionReset() {
        businessProcess.resetProject(getApplicationContext());
    }

    // AsyncTasks
    private class StartAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            if(!businessProcess.getProjectName(getApplicationContext()).equals("")) {
                businessProcess.checkin(getApplicationContext());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            businessProcess.hideProgressBarOnWidget(getApplicationContext());

        }
    }

    private class StopAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {
                businessProcess.checkout(getApplicationContext(), businessProcess.getProjectName(getApplicationContext()));
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

            businessProcess.hideProgressBarOnWidget(getApplicationContext());
        }
    }


}
