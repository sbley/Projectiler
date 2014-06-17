package de.saxsys.android.projectiler.app.dialog;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;

import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.Date;
import java.util.List;

import de.saxsys.android.projectiler.app.R;
import de.saxsys.android.projectiler.app.asynctasks.StopAsyncTask;
import de.saxsys.android.projectiler.app.backend.DateUtil;
import de.saxsys.android.projectiler.app.ui.adapter.CommentCompleteAdapter;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * Created by stefan.heinze on 29.05.2014.
 */
@SuppressLint("ValidFragment")
public class CommentDialog extends BaseDefaultDialogFragment {

    private static final String TAG = CommentDialog.class.getSimpleName();
    private TimePicker tpStart;
    private TimePicker tpStop;

    private AsyncTaskResultListener<String> stopTaskResultListener;
    private OnBackPressListener backPressListener;
    private String projectName;
    private BusinessProcess businessProcess;
    private boolean okClicked = false;
    private AutoCompleteTextView etComment;

    @SuppressLint("ValidFragment")
    public CommentDialog(final AsyncTaskResultListener<String> stopTaskResultListener, OnBackPressListener backPressListener, final String projectName){
        this.stopTaskResultListener = stopTaskResultListener;
        this.projectName = projectName;
        this.backPressListener = backPressListener;
    }

    @Override
    protected View getDialogView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_comment, parentContainer, false);

        tpStart = (TimePicker) view.findViewById(R.id.tpStart);
        tpStop = (TimePicker) view.findViewById(R.id.tpStop);
        etComment = (AutoCompleteTextView) view.findViewById(R.id.et_comment);

        businessProcess = BusinessProcess.getInstance(getActivity().getApplicationContext());
        List<String> comments = businessProcess.searchComments("");

        etComment.setAdapter(new CommentCompleteAdapter(getActivity(), comments));

        tpStart.setIs24HourView(true);
        tpStop.setIs24HourView(true);


        Date startDate = businessProcess.getStartDate();

        Date endDate = new Date(System.currentTimeMillis());

        DateUtil.setDatePicker(tpStart, startDate);
        DateUtil.setDatePicker(tpStop, endDate);

        return view;
    }




    @Override
    protected int getTitleId() {
        return R.string.comment_title;
    }

    @Override
    protected boolean hasNegativeButton() {
        return false;
    }

    @Override
    protected boolean hasPositiveButton() {
        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!okClicked){
            backPressListener.onBackPress();
        }
    }

    @Override
    protected void onClickPositiveButton() {
        super.onClickPositiveButton();
        getActivity().setProgressBarIndeterminateVisibility(true);

        businessProcess.saveComment(etComment.getText().toString());

        new StopAsyncTask(getActivity().getApplicationContext(), projectName, DateUtil.getDate(tpStart), DateUtil.getDate(tpStop), stopTaskResultListener).execute();

        okClicked = true;

    }

    public interface OnBackPressListener{
        public void onBackPress();
    }

}
