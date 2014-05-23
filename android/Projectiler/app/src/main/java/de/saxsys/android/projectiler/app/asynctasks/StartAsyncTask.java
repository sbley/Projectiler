package de.saxsys.android.projectiler.app.asynctasks;

import android.content.Context;

import org.droidparts.concurrent.task.AsyncTask;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * Created by stefan.heinze on 21.05.2014.
 */
public class StartAsyncTask extends AsyncTask<Void, Void, Void> {

    BusinessProcess businessProcess;

    public StartAsyncTask(Context ctx, AsyncTaskResultListener<Void> resultListener) {
        super(ctx, resultListener);
        businessProcess = BusinessProcess.getInstance();
    }

    @Override
    protected Void onExecute(Void... voids) throws Exception {
        businessProcess.checkin(getContext());
        return null;
    }

}