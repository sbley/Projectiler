package de.saxsys.android.projectiler.app.asynctasks;

import android.content.Context;

import org.droidparts.concurrent.task.AsyncTask;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.Date;

import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * Created by stefan.heinze on 21.05.2014.
 */
public class StopAsyncTask extends AsyncTask<Void, Void, Void> {

    private BusinessProcess businessProcess;
    private String projectName;
    private Date startDate;
    private Date endDate;

    public StopAsyncTask(Context context, String projectName, Date startDate, Date endDate, AsyncTaskResultListener<Void> stopTaskResultListener) {
        super(context, stopTaskResultListener);
        businessProcess = BusinessProcess.getInstance(getContext());
        this.projectName = projectName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected Void onExecute(Void... voids) throws Exception {

        if(startDate == null){
            businessProcess.checkout(getContext(), projectName);
        }else{
            businessProcess.checkout(getContext(), projectName, startDate, endDate);
        }

        return null;
    }


}
