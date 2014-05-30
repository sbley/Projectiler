package de.saxsys.android.projectiler.app;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.todddavies.components.progressbar.ProgressWheel;

import org.droidparts.activity.Activity;
import org.droidparts.annotation.inject.InjectView;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.List;

import de.saxsys.android.projectiler.app.asynctasks.GetProjectsAsyncTask;
import de.saxsys.android.projectiler.app.ui.adapter.ProjectListAdapter;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;


public class SelectProjectPopup extends Activity {

    @InjectView(id = R.id.progressBar)
    private ProgressWheel progressBar;
    @InjectView(id = R.id.lvProjects)
    private ListView lvProjects;

    private BusinessProcess businessProcess;


    @Override
    public void onPreInject() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_project_popup);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        businessProcess = BusinessProcess.getInstance(getApplicationContext());

        if(businessProcess.getStartDate() != null){
            finish();
        }

        progressBar.setVisibility(View.VISIBLE);
        progressBar.spin();

        new GetProjectsAsyncTask(getApplicationContext(), getProjectsResultListener).execute();

    }

    private AsyncTaskResultListener<List<String>> getProjectsResultListener = new AsyncTaskResultListener<List<String>>() {
        @Override
        public void onAsyncTaskSuccess(List<String> itemList) {
            lvProjects.setAdapter(new ProjectListAdapter(getApplicationContext(), itemList));
            lvProjects.setVisibility(View.VISIBLE);
            lvProjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                    String projectName = (String) lvProjects.getItemAtPosition(index);

                    businessProcess.saveProjectName(getApplicationContext(), projectName);

                    WidgetUtils.refreshWidget(getApplicationContext());

                    finish();
                }
            });

            progressBar.setVisibility(View.GONE);

        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            finish();
        }
    };

}
