package de.saxsys.android.projectiler.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Date;

import de.saxsys.android.projectiler.app.backend.Projectiler;
import de.saxsys.android.projectiler.app.backend.UserDataStore;


/**
 * Implementation of App Widget functionality.
 */
public class ProjectilerAppWidget extends AppWidgetProvider {

    private static final String CLICK_ACTION = "de.saxsys.android.projectiler.app.widget.CLICK";
    public static final String EXTRA_PROJECT_NAME = "de.saxsys.android.projectiler.app.widget.PROJECT_NAME";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        Log.i("ProjectilerAppWidget", "updateAppWidget");

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.projectiler_app_widget);

        Projectiler projectiler = Projectiler.createDefaultProjectiler();


        // ist der nutzer eingelogged?
        if(UserDataStore.getInstance().getUserName(context).equals("")){

            Intent loginIntent = new Intent(context, LoginActivity.class);
            PendingIntent loginPendingIntent = PendingIntent.getActivity(context, 0, loginIntent, 0);
            views.setOnClickPendingIntent(R.id.buttonLogin, loginPendingIntent);

            views.setViewVisibility(R.id.rl_widget_login, View.VISIBLE);
            views.setViewVisibility(R.id.ll_widget_content, View.GONE);


        }else{

            views.setViewVisibility(R.id.rl_widget_login, View.GONE);
            views.setViewVisibility(R.id.ll_widget_content, View.VISIBLE);

            Intent intent = new Intent(context, ProjectilerIntentService.class);
            intent.setAction(ProjectilerIntentService.ACTION_START);
            PendingIntent startPendingIntent = PendingIntent.getService(context, 0, intent, 0);
            intent.setAction(ProjectilerIntentService.ACTION_STOP);
            PendingIntent stopPendingIntent = PendingIntent.getService(context, 0, intent, 0);
            intent.setAction(ProjectilerIntentService.ACTION_RESET);
            PendingIntent resetPendingIntent = PendingIntent.getService(context, 0, intent, 0);

            views.setOnClickPendingIntent(R.id.buttonReset, resetPendingIntent);
            views.setOnClickPendingIntent(R.id.buttonStop, stopPendingIntent);
            views.setOnClickPendingIntent(R.id.buttonStart, startPendingIntent);


            String currentProject = projectiler.getProjectName(context);
            views.setTextViewText(R.id.tv_current_project, currentProject);

            Date startDate = projectiler.getStartDate(context);
            // ist gestartet
            if(startDate != null){

                long currentDatetime = System.currentTimeMillis();
                views.setChronometer(R.id.chronometer, SystemClock.elapsedRealtime() - (currentDatetime - startDate.getTime()), null, true);

                views.setViewVisibility(R.id.chronometer, View.VISIBLE);

                views.setViewVisibility(R.id.rlWidget_left, View.GONE);
                views.setViewVisibility(R.id.buttonStart, View.GONE);
                views.setViewVisibility(R.id.buttonStop, View.VISIBLE);
                views.setViewVisibility(R.id.buttonReset, View.VISIBLE);

            }else{
                views.setChronometer(R.id.chronometer, SystemClock.elapsedRealtime(), null, false);
                //RemoteViews Service needed to provide adapter for ListView
                Intent svcIntent = new Intent(context, WidgetService.class);
                //passing app widget id to that RemoteViews Service
                svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                //setting a unique Uri to the intent
                //don't know its purpose to me right now
                svcIntent.setData(Uri.parse(
                        svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

                views.setRemoteAdapter(R.id.lvProjectsWidget, svcIntent);

                views.setViewVisibility(R.id.chronometer, View.GONE);



                final Intent onClickIntent = new Intent(context, ProjectNameIntentService.class);
                onClickIntent.setAction(ProjectilerAppWidget.CLICK_ACTION);
                onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
                final PendingIntent onClickPendingIntent = PendingIntent.getService(context, 0,
                        onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setPendingIntentTemplate(R.id.lvProjectsWidget, onClickPendingIntent);



                views.setViewVisibility(R.id.rlWidget_left, View.VISIBLE);
                views.setViewVisibility(R.id.buttonStart, View.VISIBLE);
                views.setViewVisibility(R.id.buttonStop, View.GONE);
                views.setViewVisibility(R.id.buttonReset, View.GONE);

            }


            boolean isLoading = projectiler.isWidgetLoading(context);

            if(isLoading){
                views.setViewVisibility(R.id.progressBarWidget, View.VISIBLE);
            }else{
                views.setViewVisibility(R.id.progressBarWidget, View.GONE);
            }
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


