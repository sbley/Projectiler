package de.saxsys.android.projectiler.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.Date;

import de.saxsys.android.projectiler.app.R;
import de.saxsys.android.projectiler.app.asynctasks.StartAsyncTask;
import de.saxsys.android.projectiler.app.asynctasks.StopAsyncTask;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;
import de.saxsys.android.projectiler.app.utils.NotificationUtils;


public class ProjectilerBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = ProjectilerBroadcastReceiver.class.getSimpleName();

    public static final String ACTION_START = "de.saxsys.android.businessProcess.app.action.START";
    public static final String ACTION_STOP = "de.saxsys.android.businessProcess.app.action.STOP";
    public static final String ACTION_RESET = "de.saxsys.android.businessProcess.app.action.RESET";

    public static final String EXTRAS_START_DATE = "de.saxsys.android.businessProcess.app.extras.START_DATE";
    public static final String EXTRAS_END_DATE = "de.saxsys.android.businessProcess.app.extras.END_DATE";

    private BusinessProcess businessProcess;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "");
        this.context = context;
        businessProcess = BusinessProcess.getInstance(context);
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                handleActionStart(context);
            } else if (ACTION_STOP.equals(action)) {
                Date startDate = null;
                Date endDate = null;

                long startLong = intent.getLongExtra(EXTRAS_START_DATE, 0);
                long endLong = intent.getLongExtra(EXTRAS_END_DATE, 0);

                if(startLong != 0){
                    startDate = new Date(startLong);
                }
                if(endLong != 0){
                    endDate = new Date(endLong);
                }else{
                    endDate = new Date();
                }

                handleActionStop(context, startDate, endDate);
            }else if (ACTION_RESET.equals(action)) {
                handleActionReset(context);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStart(final Context context) {

        if(!businessProcess.getProjectName().equals("")) {
            businessProcess.showProgressBarOnWidget(context);
            new StartAsyncTask(context, startTaskResultListener).execute();
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     * @param startDate
     * @param endDate
     */
    private void handleActionStop(final Context context, Date startDate, Date endDate) {
        businessProcess.showProgressBarOnWidget(context);
        new StopAsyncTask(context, businessProcess.getProjectName(), startDate, endDate, stopTaskResultListener).execute();
    }

    private void handleActionReset(final Context context) {
        businessProcess.resetProject(context, false);
    }

    private AsyncTaskResultListener<Date> startTaskResultListener = new AsyncTaskResultListener<Date>() {
        @Override
        public void onAsyncTaskSuccess(Date aDate) {
            businessProcess.hideProgressBarOnWidget(context);
        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            businessProcess.hideProgressBarOnWidget(context);
        }
    };

    private AsyncTaskResultListener<String> stopTaskResultListener = new AsyncTaskResultListener<String>() {
        @Override
        public void onAsyncTaskSuccess(String projectName) {
            businessProcess.hideProgressBarOnWidget(context);
        }

        @Override
        public void onAsyncTaskFailure(Exception e) {

            if(e instanceof IllegalStateException){
                // notification schicken
                NotificationUtils.sendNotification(context, 111, context.getString(R.string.error_stop_tracking), e.getMessage());
            }

            businessProcess.hideProgressBarOnWidget(context);
        }
    };
}
