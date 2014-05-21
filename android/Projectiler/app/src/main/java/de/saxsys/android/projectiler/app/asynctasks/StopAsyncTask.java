package de.saxsys.android.projectiler.app.asynctasks;

import android.content.Context;

import org.droidparts.concurrent.task.AsyncTask;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * Created by stefan.heinze on 21.05.2014.
 */
public class StopAsyncTask extends AsyncTask<Void, Void, Void> {

    private BusinessProcess businessProcess;
    private String projectName;

    public StopAsyncTask(Context ctx, String projectName, AsyncTaskResultListener<Void> resultListener) {
        super(ctx, resultListener);
        businessProcess = BusinessProcess.getInstance();
        this.projectName = projectName;
    }

    @Override
    protected Void onExecute(Void... voids) throws Exception {
        businessProcess.checkout(getContext(), projectName);
        return null;
    }


}
