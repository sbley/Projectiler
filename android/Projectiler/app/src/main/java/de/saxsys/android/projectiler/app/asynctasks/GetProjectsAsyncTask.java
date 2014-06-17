package de.saxsys.android.projectiler.app.asynctasks;

import android.content.Context;

import org.droidparts.concurrent.task.AsyncTask;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.List;

import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * Created by stefan.heinze on 25.05.2014.
 */
public class GetProjectsAsyncTask extends AsyncTask<Void, Void, List<String>> {

    private BusinessProcess businessProcess;
    private boolean loadFromServer;

    public GetProjectsAsyncTask(Context ctx, boolean loadFromServer, AsyncTaskResultListener<List<String>> resultListener) {
        super(ctx, resultListener);
        businessProcess = BusinessProcess.getInstance(getContext());
        this.loadFromServer = loadFromServer;
    }

    @Override
    protected List<String> onExecute(Void... voids) throws Exception {
        return businessProcess.getProjectNames(loadFromServer);
    }
}
