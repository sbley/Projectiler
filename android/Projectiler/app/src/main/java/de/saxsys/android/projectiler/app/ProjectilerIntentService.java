package de.saxsys.android.projectiler.app;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.droidparts.concurrent.task.AsyncTaskResultListener;

import de.saxsys.android.projectiler.app.asynctasks.StartAsyncTask;
import de.saxsys.android.projectiler.app.asynctasks.StopAsyncTask;
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

        if(!businessProcess.getProjectName(getApplicationContext()).equals("")) {
            businessProcess.showProgressBarOnWidget(getApplicationContext());
            new StartAsyncTask(getApplicationContext(), startTaskResultListener).execute();
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStop() {
        businessProcess.showProgressBarOnWidget(getApplicationContext());
        new StopAsyncTask(getApplicationContext(), businessProcess.getProjectName(getApplicationContext()), stopTaskResultListener).execute();
    }

    private void handleActionReset() {
        businessProcess.resetProject(getApplicationContext());
    }


    private AsyncTaskResultListener<Void> startTaskResultListener = new AsyncTaskResultListener<Void>() {
        @Override
        public void onAsyncTaskSuccess(Void aVoid) {
            businessProcess.hideProgressBarOnWidget(getApplicationContext());
        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            businessProcess.hideProgressBarOnWidget(getApplicationContext());
        }
    };

    private AsyncTaskResultListener<Void> stopTaskResultListener = new AsyncTaskResultListener<Void>() {
        @Override
        public void onAsyncTaskSuccess(Void aVoid) {
            businessProcess.hideProgressBarOnWidget(getApplicationContext());
        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            businessProcess.hideProgressBarOnWidget(getApplicationContext());
        }
    };


}
