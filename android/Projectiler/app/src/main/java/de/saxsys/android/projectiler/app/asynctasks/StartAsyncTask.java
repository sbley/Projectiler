package de.saxsys.android.projectiler.app.asynctasks;

import android.content.Context;

import org.droidparts.concurrent.task.AsyncTask;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.Date;

import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * Created by stefan.heinze on 21.05.2014.
 */
public class StartAsyncTask extends AsyncTask<Void, Void, Date> {

    BusinessProcess businessProcess;

    public StartAsyncTask(Context ctx, AsyncTaskResultListener<Date> resultListener) {
        super(ctx, resultListener);
        businessProcess = BusinessProcess.getInstance(getContext());
    }

    @Override
    protected Date onExecute(Void... voids) throws Exception {
        return businessProcess.checkin();
    }

}