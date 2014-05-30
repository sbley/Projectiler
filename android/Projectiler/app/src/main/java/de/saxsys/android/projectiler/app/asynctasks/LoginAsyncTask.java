package de.saxsys.android.projectiler.app.asynctasks;

import android.content.Context;

import org.droidparts.concurrent.task.AsyncTask;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * Created by stefan.heinze on 28.05.2014.
 */
public class LoginAsyncTask extends AsyncTask<Void, Void, Void> {

    private final String username;
    private final String password;
    private final boolean saveLogin;
    private BusinessProcess businessProcess;

    public LoginAsyncTask(Context ctx, final String username, final String password, final boolean saveLogin, AsyncTaskResultListener<Void> resultListener) {
        super(ctx, resultListener);
        this.username = username;
        this.password = password;
        this.saveLogin = saveLogin;
        businessProcess = BusinessProcess.getInstance(ctx);
    }

    @Override
    protected Void onExecute(Void... voids) throws Exception {
        businessProcess.saveCredentials(username, password, saveLogin);
        return null;
    }
}
