package de.saxsys.android.projectiler.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.droidparts.activity.support.v7.ActionBarActivity;
import org.droidparts.annotation.inject.InjectView;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.Date;
import java.util.List;

import de.saxsys.android.projectiler.app.asynctasks.GetProjectsAsyncTask;
import de.saxsys.android.projectiler.app.asynctasks.StartAsyncTask;
import de.saxsys.android.projectiler.app.asynctasks.StopAsyncTask;
import de.saxsys.android.projectiler.app.ui.adapter.ProjectListAdapter;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;
import de.saxsys.android.projectiler.app.utils.NotificationUtils;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;


public class NfcActivity extends ActionBarActivity {

    private final String TAG = NfcActivity.class.getSimpleName();

    @InjectView(id = R.id.progressBar)
    private ProgressBar progressBar;
    @InjectView(id = R.id.lvProjects)
    private ListView lvPorjects;

    private BusinessProcess businessProcess;

    @Override
    public void onPreInject() {
        setContentView(R.layout.activity_nfc);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        businessProcess = BusinessProcess.getInstance(getApplicationContext());

        if (!businessProcess.getAutoLogin()) {
            // Anzeige des Login
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

        } else if (businessProcess.getStartDate() == null) {
            // Projekteauswahl anzeigen
            progressBar.setVisibility(View.VISIBLE);
            new GetProjectsAsyncTask(getApplicationContext(), getProjectsListener).execute();

        } else {
            progressBar.setVisibility(View.VISIBLE);
            new StopAsyncTask(getApplicationContext(), businessProcess.getProjectName(), null, null, stopTaskListener).execute();

        }

    }

    private AsyncTaskResultListener<Date> startListener = new AsyncTaskResultListener<Date>() {
        @Override
        public void onAsyncTaskSuccess(Date date) {

            progressBar.setVisibility(View.GONE);

            String startTime = businessProcess.getStartDateAsString();

            String projectName = businessProcess.getProjectName();

            NotificationUtils.sendNotification(getApplicationContext(), 001, getString(R.string.start_tracking), getString(R.string.start_tracking_project, startTime, projectName));

            WidgetUtils.refreshWidget(getApplicationContext());
            finish();
        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    };
    private AsyncTaskResultListener<List<String>> getProjectsListener = new AsyncTaskResultListener<List<String>>() {
        @Override
        public void onAsyncTaskSuccess(List<String> itemList) {
            lvPorjects.setAdapter(new ProjectListAdapter(getApplicationContext(), itemList));

            progressBar.setVisibility(View.GONE);

            lvPorjects.setVisibility(View.VISIBLE);

            lvPorjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                    String projectName = (String) lvPorjects.getItemAtPosition(index);

                    businessProcess.saveProjectName(getApplicationContext(), projectName);

                    new StartAsyncTask(getApplicationContext(), startListener).execute();
                }
            });

        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            Log.e(TAG, e.getMessage(), e);
            finish();
        }
    };

    private AsyncTaskResultListener<String> stopTaskListener = new AsyncTaskResultListener<String>() {
        @Override
        public void onAsyncTaskSuccess(String projectName) {
            progressBar.setVisibility(View.GONE);

            NotificationUtils.sendNotification(getApplicationContext(), 002, getString(R.string.stopped_time_tracking), getString(R.string.project_booked, projectName));
            WidgetUtils.refreshWidget(getApplicationContext());
            NfcActivity.this.finish();
        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            Log.e(TAG, e.getMessage(), e);
            NotificationUtils.sendNotification(getApplicationContext(), 003, getString(R.string.error_stop_tracking), e.getMessage());
            finish();
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nfc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}