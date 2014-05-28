package de.saxsys.android.projectiler.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Date;

import de.saxsys.android.projectiler.app.service.ProjectilerIntentService;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;


/**
 * Implementation of App Widget functionality.
 */
public class ProjectilerAppWidget extends AppWidgetProvider {

    private static final String CLICK_ACTION = "de.saxsys.android.projectiler.app.widget.CLICK";
    public static final String EXTRA_PROJECT_NAME = "de.saxsys.android.projectiler.app.widget.PROJECT_NAME";
    private static final String SHOW_PROJECT_POPUP_DIALOG_ACTION = "de.saxsys.android.projectiler.app.widget.showprojectpopup";


    private static BusinessProcess businessProcess;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        businessProcess = BusinessProcess.getInstance(context);
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
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


        Intent popupIntent = new Intent(context, ProjectilerAppWidget.class);
        popupIntent.setAction(SHOW_PROJECT_POPUP_DIALOG_ACTION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, popupIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.tv_current_project, pendingIntent);

        String projectName = businessProcess.getProjectName(context);

        if (projectName.equals("")) {
            views.setTextViewText(R.id.tv_current_project, context.getString(R.string.please_choose_a_project));
        } else {
            views.setTextViewText(R.id.tv_current_project, projectName);
        }

        boolean isLoading = businessProcess.isWidgetLoading(context);

        if (isLoading) {
            views.setViewVisibility(R.id.progressBar, View.VISIBLE);

            views.setViewVisibility(R.id.buttonReset, View.GONE);
            views.setViewVisibility(R.id.buttonStart, View.GONE);
            views.setViewVisibility(R.id.buttonStop, View.GONE);

        } else {
            views.setViewVisibility(R.id.progressBar, View.GONE);
            // ist der nutzer eingelogged?
            if (!businessProcess.getAutoLogin(context)) {


                Intent loginIntent = new Intent(context, LoginActivity.class);
                PendingIntent loginPendingIntent = PendingIntent.getActivity(context, 0, loginIntent, 0);
                views.setOnClickPendingIntent(R.id.buttonLogin, loginPendingIntent);

                views.setViewVisibility(R.id.rl_widget_login, View.VISIBLE);
                views.setViewVisibility(R.id.ll_widget_content, View.GONE);

            } else {

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

                Date startDate = businessProcess.getStartDate(context);
                // ist gestartet
                if (startDate != null) {

                    long currentDatetime = System.currentTimeMillis();
                    views.setChronometer(R.id.chronometer, SystemClock.elapsedRealtime() - (currentDatetime - startDate.getTime()), null, true);

                    views.setViewVisibility(R.id.chronometer, View.VISIBLE);

                    views.setViewVisibility(R.id.buttonStart, View.GONE);
                    views.setViewVisibility(R.id.buttonStop, View.VISIBLE);
                    views.setViewVisibility(R.id.buttonReset, View.VISIBLE);

                } else {

                    views.setViewVisibility(R.id.buttonStart, View.VISIBLE);
                    views.setViewVisibility(R.id.buttonStop, View.GONE);
                    views.setViewVisibility(R.id.buttonReset, View.GONE);

                }

            }
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(SHOW_PROJECT_POPUP_DIALOG_ACTION)) {
            
            Intent popUpIntent = new Intent(context, SelectProjectPopup.class);
            popUpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(popUpIntent);

        }

        super.onReceive(context, intent);
    }
}


