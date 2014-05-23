package de.saxsys.android.projectiler.app;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link CurrentTracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CurrentTracksFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_tracks, container, false);
    }


}
