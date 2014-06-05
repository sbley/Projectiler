package de.saxsys.android.projectiler.app.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.droidparts.annotation.inject.InjectView;
import org.droidparts.concurrent.task.AsyncTaskResultListener;
import org.droidparts.fragment.support.v4.Fragment;

import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.saxsys.android.projectiler.app.MainActivity;
import de.saxsys.android.projectiler.app.R;
import de.saxsys.android.projectiler.app.asynctasks.StartAsyncTask;
import de.saxsys.android.projectiler.app.dialog.CommentDialog;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;

/**
 * Created by spaxx86 on 21.05.2014.
 */
public class TimeTrackingFragment extends Fragment implements View.OnClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_PROJECT_NAME = "project_name";
    private static final String ARG_START_VISIBLE = "start_visible";
    private static final String ARG_STOP_VISIBLE = "stop_visible";

    private String projectName;
    private boolean startVisible;
    private boolean stopVisible;
    private boolean isLoading;


    private BusinessProcess businessProcess;

    private View rootView;

    @InjectView(id = R.id.rl_container)
    private RelativeLayout rlContainer;
    @InjectView(id = R.id.rl_container_not_started)
    private RelativeLayout rlContainerNotStarted;
    @InjectView(id = R.id.tvProjectName)
    private TextView tvProject;
    @InjectView(id = R.id.rl_container_other_project_selected)
    private RelativeLayout rlOtherProject;
    @InjectView(id = R.id.chronometer)
    private Chronometer chronometer;
    @InjectView(id = R.id.tvOtherProjectSelected)
    private TextView tvOtherProjectSelected;
    @InjectView(id = R.id.btnStart, click = true)
    private Button btnStart;
    @InjectView(id = R.id.btnStop, click = true)
    private Button btnStop;
    @InjectView(id = R.id.btnReset, click = true)
    private Button btnReset;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment newInstance(String projectName, boolean startVisible, boolean stopVisible) {
        TimeTrackingFragment fragment = new TimeTrackingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT_NAME, projectName);
        args.putBoolean(ARG_START_VISIBLE, startVisible);
        args.putBoolean(ARG_STOP_VISIBLE, stopVisible);

        fragment.setArguments(args);
        return fragment;
    }

    public TimeTrackingFragment() {

    }


    @Override
    public View onCreateView(Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        businessProcess = BusinessProcess.getInstance(getActivity().getApplicationContext());
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        businessProcess = BusinessProcess.getInstance(getActivity().getApplicationContext());

        projectName = getArguments().getString(ARG_PROJECT_NAME);
        startVisible = getArguments().getBoolean(ARG_START_VISIBLE);
        stopVisible = getArguments().getBoolean(ARG_STOP_VISIBLE);

        initView();

    }

    private void setStartDateTextView(Date date) {

        if (date == null) {
            chronometer.setVisibility(View.INVISIBLE);
        } else {
            if (businessProcess.getProjectName().equals(projectName)) {
                chronometer.setVisibility(View.VISIBLE);
                long currentDatetime = System.currentTimeMillis();
                chronometer.setBase(SystemClock.elapsedRealtime() - (currentDatetime - date.getTime()));
                chronometer.start();

            } else {
                chronometer.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Fragment", "onResume");

        if (rootView != null) {

            String currentProjectName = businessProcess.getProjectName();

            if (currentProjectName.equals("")) {
                startVisible = true;
                stopVisible = false;
            } else if (currentProjectName.equals(projectName) && businessProcess.getStartDate() != null) {
                startVisible = false;
                stopVisible = true;
            } else {
                startVisible = true;
                stopVisible = false;
            }
            if (!isLoading) {
                initView();
            }
        }
    }

    private void initView() {

        if (projectName.equals("")) {
            tvProject.setVisibility(View.GONE);
            rlContainer.setVisibility(View.GONE);
            rlContainerNotStarted.setVisibility(View.VISIBLE);
            rlOtherProject.setVisibility(View.GONE);
            chronometer.setVisibility(View.INVISIBLE);
        } else if (businessProcess.getStartDate() != null && !businessProcess.getProjectName().equals(projectName)) {
            // ein anderes Projekt wurde schon gestartet
            rlContainer.setVisibility(View.GONE);
            rlContainerNotStarted.setVisibility(View.GONE);
            tvProject.setVisibility(View.GONE);
            chronometer.setVisibility(View.INVISIBLE);
            tvOtherProjectSelected = (TextView) rootView.findViewById(R.id.tvOtherProjectSelected);

            String text = String.format(getString(R.string.other_project_booked), businessProcess.getProjectName());

            tvOtherProjectSelected.setText(text);

            rlOtherProject.setVisibility(View.VISIBLE);

        } else {
            tvProject.setText(projectName);
            rlContainer.setVisibility(View.VISIBLE);
            rlContainerNotStarted.setVisibility(View.GONE);
            rlOtherProject.setVisibility(View.GONE);

            if (startVisible) {
                btnStart.setVisibility(View.VISIBLE);
            } else {
                btnStart.setVisibility(View.GONE);
            }

            if (stopVisible) {
                btnStop.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
            } else {
                btnStop.setVisibility(View.GONE);
                btnReset.setVisibility(View.GONE);
            }

            // ist ein startDate gesetzt?
            setStartDateTextView(businessProcess.getStartDate());
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getString(ARG_PROJECT_NAME));
    }

    @Override
    public void onClick(View view) {
        if (view == btnStart) {

            getActivity().setProgressBarIndeterminateVisibility(true);
            businessProcess.saveProjectName(getActivity().getApplicationContext(), projectName);
            new StartAsyncTask(getActivity().getApplicationContext(), startTaskResultListener).execute();

        } else if (view == btnStop) {

            btnStop.setEnabled(false);
            btnReset.setEnabled(false);
            CommentDialog dialog = new CommentDialog(stopTaskResultListener, backPressListener, projectName);
            dialog.show(getActivity().getFragmentManager(), "CommentDialog");

        } else if (view == btnReset) {

            businessProcess.resetProject(getActivity().getApplicationContext(), true);
            btnReset.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.GONE);
            setStartDateTextView(null);
            ((MainActivity) getActivity()).refreshNavigationDrawer("");

        }

    }

    private CommentDialog.OnBackPressListener backPressListener = new CommentDialog.OnBackPressListener() {
        @Override
        public void onBackPress() {
            btnStop.setEnabled(true);
            btnReset.setEnabled(true);
        }
    };

    private AsyncTaskResultListener<Date> startTaskResultListener = new AsyncTaskResultListener<Date>() {
        @Override
        public void onAsyncTaskSuccess(Date aDate) {
            getActivity().setProgressBarIndeterminateVisibility(false);

            // navigation Drawer aktualisieren
            ((MainActivity) getActivity()).refreshNavigationDrawer(projectName);

            btnReset.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.GONE);
            btnStop.setVisibility(View.VISIBLE);

            setStartDateTextView(aDate);

            WidgetUtils.refreshWidget(getActivity().getApplicationContext());
        }

        @Override
        public void onAsyncTaskFailure(Exception e) {

        }
    };

    private AsyncTaskResultListener<String> stopTaskResultListener = new AsyncTaskResultListener<String>() {

        @Override
        public void onAsyncTaskSuccess(String projectName) {
            getActivity().setProgressBarIndeterminateVisibility(false);

            businessProcess.resetStartTime();
            ((MainActivity) getActivity()).refreshNavigationDrawer("");

            btnReset.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.GONE);

            setStartDateTextView(null);

            WidgetUtils.refreshWidget(getActivity().getApplicationContext());

            Crouton.makeText(getActivity(), getString(R.string.time_booked_successfull), Style.INFO).show();

            btnStop.setEnabled(true);
            btnReset.setEnabled(true);
        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            getActivity().setProgressBarIndeterminateVisibility(false);

            if (e instanceof IllegalStateException) {
                Crouton.makeText(getActivity(), e.getMessage(), Style.ALERT).show();
            } else {
                businessProcess.resetStartTime();
                ((MainActivity) getActivity()).refreshNavigationDrawer("");

                btnReset.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.GONE);

                setStartDateTextView(null);

                WidgetUtils.refreshWidget(getActivity().getApplicationContext());

                Crouton.makeText(getActivity(), getString(R.string.time_booked_not_successfull), Style.ALERT).show();


                getActivity().supportInvalidateOptionsMenu();
            }

            btnStop.setEnabled(true);
            btnReset.setEnabled(true);
        }
    };
}
