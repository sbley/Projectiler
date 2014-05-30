package de.saxsys.android.projectiler.app.asynctasks;

import android.content.Context;

import org.droidparts.concurrent.task.AsyncTask;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.List;

import de.saxsys.android.projectiler.app.utils.BusinessProcess;
import de.saxsys.projectiler.crawler.Booking;

/**
 * Created by stefan.heinze on 23.05.2014.
 */
public class GetDailyTrackAsyncTask extends AsyncTask<Void, Void, List<Booking>> {

    private BusinessProcess businessProcess;

    public GetDailyTrackAsyncTask(Context ctx, AsyncTaskResultListener<List<Booking>> resultListener) {
        super(ctx, resultListener);
        businessProcess = BusinessProcess.getInstance(getContext());
    }

    @Override
    protected List<Booking> onExecute(Void... voids) throws Exception {
        return businessProcess.getCurrentBookings();
    }
}
