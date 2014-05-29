package de.saxsys.android.projectiler.app.dialog;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import org.droidparts.concurrent.task.AsyncTaskResultListener;

import de.saxsys.android.projectiler.app.R;
import de.saxsys.android.projectiler.app.asynctasks.StopAsyncTask;

/**
 * Created by stefan.heinze on 29.05.2014.
 */
@SuppressLint("ValidFragment")
public class CommentDialog extends BaseDefaultDialogFragment  {


    private AsyncTaskResultListener<Void> stopTaskResultListener;
    private String projectName;

    @SuppressLint("ValidFragment")
    public CommentDialog(final AsyncTaskResultListener<Void> stopTaskResultListener, final String projectName){
        this.stopTaskResultListener = stopTaskResultListener;
        this.projectName = projectName;
    }

    @Override
    protected View getDialogView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.dialog_comment, parentContainer, false);
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
    protected void onClickPositiveButton() {
        super.onClickPositiveButton();
        new StopAsyncTask(getActivity().getApplication(), projectName, stopTaskResultListener).execute();

    }
}
