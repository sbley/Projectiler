package de.saxsys.android.projectiler.app;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefan.heinze on 14.05.2014.
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private final String TAG = ListProvider.class.getSimpleName();

    public static final String EXTRA_LIST_VIEW_ROW_NUMBER = "List_Row_Number";
    private ArrayList<String> listItemList = new ArrayList<String>();
    private Context context = null;
    private int appWidgetId;

    public ListProvider(Context context, Intent intent, List<String> projects) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        Log.d(TAG, "show " + projects.size() + " Projects");
        listItemList.addAll(projects);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public RemoteViews getViewAt(int position) {

        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.adapter_navigation_drawer);
        remoteView.setTextViewText(R.id.tv_project_name, listItemList.get(position));

        final Intent fillInIntent = new Intent();
        final Bundle extras = new Bundle();
        extras.putString(ProjectilerAppWidget.EXTRA_PROJECT_NAME, listItemList.get(position));
        fillInIntent.putExtras(extras);
        remoteView.setOnClickFillInIntent(R.id.rl_widget, fillInIntent);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

}
