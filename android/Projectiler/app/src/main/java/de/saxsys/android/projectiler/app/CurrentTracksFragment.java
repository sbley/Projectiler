package de.saxsys.android.projectiler.app;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.todddavies.components.progressbar.ProgressWheel;

import org.droidparts.annotation.inject.InjectView;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.List;

import de.saxsys.android.projectiler.app.asynctasks.GetDailyTrackAsyncTask;
import de.saxsys.projectiler.crawler.Booking;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link CurrentTracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CurrentTracksFragment extends org.droidparts.fragment.support.v4.Fragment {

    @InjectView(id = R.id.lvCurrentTracks)
    private ListView lvBooking;
    @InjectView(id = R.id.pw_spinner)
    private ProgressWheel progress;

    public static CurrentTracksFragment newInstance() {
        CurrentTracksFragment fragment = new CurrentTracksFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
    public CurrentTracksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_current_tracks, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progress.spin();
        new GetDailyTrackAsyncTask(getActivity().getApplicationContext(), getDailyTracksListener).execute();
    }

    private AsyncTaskResultListener<List<Booking>> getDailyTracksListener = new AsyncTaskResultListener<List<Booking>>() {

        @Override
        public void onAsyncTaskSuccess(List<Booking> bookings) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            lvBooking.setAdapter(new CurrentTrackAdapter(getActivity().getApplicationContext(), bookings));

            lvBooking.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);

        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            progress.setVisibility(View.GONE);

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                activity.getString(R.string.current_tracks));
    }
}
