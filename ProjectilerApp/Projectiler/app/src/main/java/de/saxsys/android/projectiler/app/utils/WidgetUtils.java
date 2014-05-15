package de.saxsys.android.projectiler.app.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import de.saxsys.android.projectiler.app.ProjectilerAppWidget;
import de.saxsys.android.projectiler.app.backend.Projectiler;

/**
 * Created by stefan.heinze on 15.05.2014.
 */
public class WidgetUtils {


    public static void refreshWidget(final Context context){
        Intent widgetIntent = new Intent(context, ProjectilerAppWidget.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, ProjectilerAppWidget.class));
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(widgetIntent);
    }


    public static void showProgressBarOnWidget(final Context context, final Projectiler projectiler){
        projectiler.setWidgetLoading(context, true);
        refreshWidget(context);
    }

    public static void hideProgressBarOnWidget(final Context context, final Projectiler projectiler){
        projectiler.setWidgetLoading(context, false);
        refreshWidget(context);
    }
}
