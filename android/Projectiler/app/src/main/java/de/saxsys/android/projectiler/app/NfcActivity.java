package de.saxsys.android.projectiler.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.todddavies.components.progressbar.ProgressWheel;

import org.droidparts.activity.support.v7.ActionBarActivity;
import org.droidparts.annotation.inject.InjectView;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.Date;
import java.util.List;

import de.saxsys.android.projectiler.app.asynctasks.GetProjectsAsyncTask;
import de.saxsys.android.projectiler.app.asynctasks.StartAsyncTask;
import de.saxsys.android.projectiler.app.dialog.CommentDialog;
import de.saxsys.android.projectiler.app.ui.adapter.ProjectListAdapter;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;
import de.saxsys.android.projectiler.app.utils.NotificationUtils;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;


public class NfcActivity extends ActionBarActivity {

    private final String TAG = NfcActivity.class.getSimpleName();

    @InjectView(id = R.id.progressBar)
    private ProgressWheel progressBar;
    @InjectView(id = R.id.lvProjects)
    private PullToRefreshListView lvProjects;

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
            progressBar.spin();

            lvProjects.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.spin();
                    lvProjects.setVisibility(View.GONE);
                    new GetProjectsAsyncTask(getApplicationContext(), true, getProjectsListener).execute();

                }
            });

            new GetProjectsAsyncTask(getApplicationContext(), false, getProjectsListener).execute();

        } else {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.spin();
            lvProjects.setVisibility(View.GONE);
            CommentDialog dialog = new CommentDialog(stopTaskListener, backPressListener, businessProcess.getProjectName());
            dialog.show(getFragmentManager(), "CommentDialog");

        }

    }
    private CommentDialog.OnBackPressListener backPressListener = new CommentDialog.OnBackPressListener() {
        @Override
        public void onBackPress() {
            finish();
        }
    };

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
            lvProjects.setAdapter(new ProjectListAdapter(getApplicationContext(), itemList));
            lvProjects.onRefreshComplete();

            progressBar.setVisibility(View.GONE);

            lvProjects.setVisibility(View.VISIBLE);

            lvProjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                    String projectName = (String) adapterView.getItemAtPosition(index);

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