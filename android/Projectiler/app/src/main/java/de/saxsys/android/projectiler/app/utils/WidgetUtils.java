package de.saxsys.android.projectiler.app.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import de.saxsys.android.projectiler.app.ProjectilerAppWidget;
import de.saxsys.android.projectiler.app.backend.UserDataStore;

/**
 * Created by stefan.heinze on 15.05.2014.
 */
public class WidgetUtils {


    public static void refreshWidget(final Context context){
        new RefreshWidgetAsyncTask(context).execute();
    }


    public static void showProgressBarOnWidget(final Context context, final UserDataStore dataStore){
        dataStore.setWidgetLoading(true);
        refreshWidget(context);
    }

    public static void hideProgressBarOnWidget(final Context context, final UserDataStore dataStore){
        dataStore.setWidgetLoading(false);
        refreshWidget(context);
    }



    private static class RefreshWidgetAsyncTask extends AsyncTask<Void, Void, Void>{

        private final Context context;

        public RefreshWidgetAsyncTask(final Context context){
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Intent widgetIntent = new Intent(context, ProjectilerAppWidget.class);
            widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, ProjectilerAppWidget.class));
            widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            context.sendBroadcast(widgetIntent);

            return null;
        }
    }
}
