package de.saxsys.android.projectiler.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.todddavies.components.progressbar.ProgressWheel;

import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.List;

import de.saxsys.android.projectiler.app.asynctasks.GetProjectsAsyncTask;
import de.saxsys.android.projectiler.app.ui.adapter.ProjectListAdapter;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;


public class SelectProjectPopup extends Activity {


    private ProgressWheel progressBar;
    private ListView lvPorjects;
    private BusinessProcess businessProcess;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_project_popup);


        progressBar = (ProgressWheel) findViewById(R.id.progressBar);

        lvPorjects = (ListView) findViewById(R.id.lvProjects);


        businessProcess = BusinessProcess.getInstance(getApplicationContext());

        progressBar.setVisibility(View.VISIBLE);
        progressBar.spin();
        new GetProjectsAsyncTask(getApplicationContext(), getProjectsResultListener).execute();


    }

    private AsyncTaskResultListener<List<String>> getProjectsResultListener = new AsyncTaskResultListener<List<String>>() {
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

                    WidgetUtils.refreshWidget(getApplicationContext());

                    finish();
                }
            });

        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            finish();
        }
    };

}
