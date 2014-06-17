package de.saxsys.android.projectiler.app.ui.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.todddavies.components.progressbar.ProgressWheel;

import org.droidparts.annotation.inject.InjectView;
import org.droidparts.concurrent.task.AsyncTaskResultListener;
import org.droidparts.fragment.support.v4.Fragment;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.saxsys.android.projectiler.app.R;
import de.saxsys.android.projectiler.app.asynctasks.GetProjectsAsyncTask;
import de.saxsys.android.projectiler.app.ui.adapter.NavigationDrawerAdapter;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements View.OnClickListener {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private List<String> itemList;
    private BusinessProcess businessProcess;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    @InjectView(id = R.id.lvProjects)
    private ExpandableListView mDrawerListView;
    @InjectView(id = R.id.rl_refresh)
    private RelativeLayout rlRefresh;
    @InjectView(id = R.id.ibRefresh, click = true)
    private ImageButton ibRefresh;
    @InjectView(id = R.id.pw_spinner)
    private ProgressWheel progress;

    public NavigationDrawerFragment() {
    }

    private AsyncTaskResultListener<List<String>> getProjectsListener = new AsyncTaskResultListener<List<String>>() {
        @Override
        public void onAsyncTaskSuccess(List<String> itemList) {
            progress.setVisibility(View.GONE);
            setItems(itemList);

            if (businessProcess.getProjectName().equals("")) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = TimeTrackingFragment.newInstance("", false, true);

                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }
        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            progress.setVisibility(View.GONE);
            Crouton.makeText(getActivity(), getString(R.string.no_connection_to_server), Style.ALERT).show();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = TimeTrackingFragment.newInstance("", false, true);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

            setItems(null);
        }
    };

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);

        businessProcess = BusinessProcess.getInstance(getActivity().getApplicationContext());

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }else{
            progress.setVisibility(View.VISIBLE);
            progress.spin();
            new GetProjectsAsyncTask(getActivity().getApplicationContext(), false, getProjectsListener).execute();
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public View onCreateView(Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_logout) {
            return true;
        }else if(item.getItemId() == R.id.action_refresh_Projects){
            getActivity().setProgressBarIndeterminateVisibility(true);
            progress.setVisibility(View.VISIBLE);
            progress.spin();
            rlRefresh.setVisibility(View.GONE);
            mDrawerListView.setVisibility(View.GONE);

            new GetProjectsAsyncTask(getActivity().getApplication(),true, refreshClickListener).execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(itemList != null){
            int currentActiveIndex = businessProcess.getCurrentActiveProjectIndex(itemList);

            mDrawerListView.setAdapter(new NavigationDrawerAdapter(getActivity().getApplicationContext(), itemList, currentActiveIndex));
        }

    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    public void setItems(final List<String> itemList) {

        if(itemList == null){

            mDrawerListView.setVisibility(View.GONE);
            rlRefresh.setVisibility(View.VISIBLE);

        }else{

            mDrawerListView.setVisibility(View.VISIBLE);
            rlRefresh.setVisibility(View.GONE);

            this.itemList = itemList;

            final int currentActiveIndex = businessProcess.getCurrentActiveProjectIndex(itemList);

            mDrawerListView.setAdapter(new NavigationDrawerAdapter(getActivity().getApplicationContext(), itemList, currentActiveIndex));
            mDrawerListView.setGroupIndicator(null);

            mDrawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                    if(groupPosition == 0){

                        Fragment currentTracksFragment = CurrentTracksFragment.newInstance();

                        fragmentManager.beginTransaction()
                                .replace(R.id.container, currentTracksFragment)
                                .commit();
                        mDrawerLayout.closeDrawers();

                    }else if (groupPosition == 1){

                        Fragment fragment = TimeTrackingFragment.newInstance(itemList.get(childPosition), false, true);

                        fragmentManager.beginTransaction()
                                .replace(R.id.container, fragment)
                                .commit();

                        mDrawerLayout.closeDrawers();
                    }

                    return false;
                }
            });
        }



    }

    public String getProjectName(int position) {
        if(itemList == null){
            return null;
        }
        return itemList.get(position);
    }

    public void setProjectActive(String projectName) {
        List<String> items = itemList;

        if(items != null){
            int currentActiveIndex = -1;

            for(int i = 0; i < items.size(); i++){
                if(projectName.equals(items.get(i))){
                    currentActiveIndex = i;
                }
            }
            mDrawerListView.setAdapter(new NavigationDrawerAdapter(getActivity().getApplicationContext(), items, currentActiveIndex));
        }

    }

    @Override
    public void onClick(View view) {
        if( view == ibRefresh){
            getActivity().setProgressBarIndeterminateVisibility(true);
            progress.setVisibility(View.VISIBLE);
            progress.spin();
            rlRefresh.setVisibility(View.GONE);
            new GetProjectsAsyncTask(getActivity().getApplication(), false, refreshClickListener).execute();
        }
    }

    private AsyncTaskResultListener<List<String>> refreshClickListener = new AsyncTaskResultListener<List<String>>() {
        @Override
        public void onAsyncTaskSuccess(List<String> items) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            progress.setVisibility(View.GONE);
            setItems(items);
        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            Crouton.makeText(getActivity(), getString(R.string.no_connection_to_server), Style.ALERT).show();
            progress.setVisibility(View.GONE);
            getActivity().setProgressBarIndeterminateVisibility(false);
            setItems(null);
        }
    };

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
