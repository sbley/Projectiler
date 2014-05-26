package de.saxsys.android.projectiler.app.asynctasks;

import android.content.Context;

import org.droidparts.concurrent.task.AsyncTask;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * Created by stefan.heinze on 25.05.2014.
 */
public class UploadAllTracksAsyncTask extends AsyncTask {

    private BusinessProcess businessProcess;

    public UploadAllTracksAsyncTask(Context ctx, AsyncTaskResultListener resultListener) {
        super(ctx, resultListener);
        businessProcess = BusinessProcess.getInstance(ctx);
    }

    @Override
    protected Object onExecute(Object[] objects) throws Exception {
        businessProcess.checkoutAllTracks(getContext());
        return null;
    }
}
