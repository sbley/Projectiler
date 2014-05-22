package de.saxsys.android.projectiler.app;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViewsService;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.saxsys.android.projectiler.app.crawler.CrawlingException;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * Created by stefan.heinze on 14.05.2014.
 */
public class WidgetService extends RemoteViewsService {
    private List<String> projectNames;
    private RemoteViewsService.RemoteViewsFactory ret = null;
    private Intent intent;
    private BusinessProcess businessProcess;
/*
* So pretty simple just defining the Adapter of the listview
* here Adapter is ListProvider
* */

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        this.intent = intent;
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        try {

            businessProcess = BusinessProcess.getInstance();

            businessProcess.showProgressBarOnWidget(getApplicationContext());

            ret = new GetProjectsAsyncTask().execute().get();

            businessProcess.hideProgressBarOnWidget(getApplicationContext());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ret;
    }


    private class GetProjectsAsyncTask extends AsyncTask<Void, Void, RemoteViewsService.RemoteViewsFactory> {

        @Override
        protected RemoteViewsService.RemoteViewsFactory doInBackground(Void... voids) {

            try {

                projectNames = businessProcess.getProjectNames(getApplicationContext());

                return (new ListProvider(getApplicationContext(), intent, projectNames));
            } catch (CrawlingException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}