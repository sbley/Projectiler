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
    public static final String EXTRA_LIST_VIEW_ROW_NUMBER = "List_Row_Number";
    private ArrayList listItemList = new ArrayList();
    private Context context = null;
    private int appWidgetId;
    private List<String> projectNames;

    public ListProvider(Context context, Intent intent, List<String> projects) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem(projects);
    }

    private void populateListItem(List<String> projects) {
        this.projectNames = projects;
        for (int i = 0; i < projectNames.size(); i++) {
            List<String> listItem = new ArrayList<String>();

            listItem.add(projectNames.get(i));

            listItemList.add(listItem);



        }


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

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {

        Log.i("Listprovider", "getViewAt " + position);

        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.adapter_navigation_drawer);
        //ListItem listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.tv_project_name, projectNames.get(position));
/*
        Intent i = new Intent();
        Bundle extras = new Bundle();

        extras.putInt(EXTRA_LIST_VIEW_ROW_NUMBER, position);
        i.putExtras(extras);
        remoteView.setOnClickFillInIntent(R.id.rl_widget, i);
*/

        final Intent fillInIntent = new Intent();
        final Bundle extras = new Bundle();
        extras.putString(ProjectilerAppWidget.EXTRA_PROJECT_NAME, projectNames.get(position));
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


    private void notifyWidget(){
        Intent intent = new Intent(context, ProjectilerAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] appWidgetIds = {appWidgetId};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(intent);
    }

}
