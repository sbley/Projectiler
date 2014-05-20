package de.saxsys.android.projectiler.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.saxsys.android.projectiler.app.crawler.CrawlingException;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String NFC_KEY_WORD = "de.saxsys.android.projectile.app";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Tag projectilerTag;
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] writeTagFilters;
    private Menu menu;
    private BusinessProcess businessProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        businessProcess = BusinessProcess.getInstance();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        setProgressBarIndeterminateVisibility(true);
        new GetProjectsAsyncTask().execute();


        // gibt es bereits ein laufendes Projekt?
        if (businessProcess.getStartDate(getApplicationContext()) != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            PlaceholderFragment fragment = new PlaceholderFragment();

            fragment.setArguments(PlaceholderFragment.newInstance(businessProcess.getProjectName(getApplicationContext()), false, true));

            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();


        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        String projectName = "";

        if (mNavigationDrawerFragment != null) {
            projectName = mNavigationDrawerFragment.getProjectName(position);
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getSupportFragmentManager();

            PlaceholderFragment fragment = new PlaceholderFragment();

            String currentProjectName = businessProcess.getProjectName(getApplicationContext());

            Log.i("Projekt", "gestartetes Projekt: " + currentProjectName + " selectes Project: " + projectName);

            // beide Buttons sichtbar, weil kein aktives projekt
            if (currentProjectName.equals("")) {
                fragment.setArguments(PlaceholderFragment.newInstance(projectName, true, false));

            } else if (currentProjectName.equals(projectName) && businessProcess.getStartDate(getApplicationContext()) != null) {
                fragment.setArguments(PlaceholderFragment.newInstance(projectName, false, true));
            } else {
                fragment.setArguments(PlaceholderFragment.newInstance(projectName, true, false));
            }


            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

        }
    }

    public void onSectionAttached(String number) {

        mTitle = number;

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);

        if(mTitle.equals("")){
            mTitle = getString(R.string.app_name);
        }

        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;

        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.

            if (businessProcess.getAutoLogin(getApplicationContext())) {
                getMenuInflater().inflate(R.menu.main, menu);
            }
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {

            businessProcess.logout(getApplicationContext());

            finish();
        } else if (id == R.id.action_nfc) {
            try {

                if (projectilerTag != null) {

                    // nfc löschen
                    Log.d("", "write nfc");

                    write(NFC_KEY_WORD, projectilerTag);
                    Crouton.makeText(MainActivity.this, getString(R.string.ncf_in_range), Style.CONFIRM).show();
                } else {
                    // bitte NFC hinlegen
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("", "onNewIntent");

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            projectilerTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            Log.d("", "projectilerTag gesetzt");
            Crouton.makeText(MainActivity.this, getString(R.string.ncf_in_range), Style.CONFIRM).show();

            MenuItem item = menu.findItem(R.id.action_nfc);
            if (item == null) {
                getMenuInflater().inflate(R.menu.nfcitem, menu);
            }


        }

    }

    private void write(String text, Tag tag) throws IOException, FormatException {

        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {

        //create the message in according with the standard
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;

        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return recordNFC;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
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
        private Button btnStart;
        private Button btnStop;
        private Button btnReset;

        private final BusinessProcess businessProcess;
        private View rootView;
        private RelativeLayout rlContainer;
        private RelativeLayout rlContainerNotStarted;
        private TextView tvProject;
        private RelativeLayout rlOtherProject;
        private Chronometer chronometer;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Bundle newInstance(String projectName, boolean startVisible, boolean stopVisible) {
            Bundle args = new Bundle();
            args.putString(ARG_PROJECT_NAME, projectName);
            args.putBoolean(ARG_START_VISIBLE, startVisible);
            args.putBoolean(ARG_STOP_VISIBLE, stopVisible);
            return args;
        }

        public PlaceholderFragment() {
            businessProcess = BusinessProcess.getInstance();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            projectName = getArguments().getString(ARG_PROJECT_NAME);
            startVisible = getArguments().getBoolean(ARG_START_VISIBLE);
            stopVisible = getArguments().getBoolean(ARG_STOP_VISIBLE);


            initView(rootView);

            return rootView;
        }

        private void setStartDateTextView() {
            Date startDate = businessProcess.getStartDate(getActivity().getApplicationContext());

            if (startDate == null) {
                chronometer.setVisibility(View.INVISIBLE);
            } else {

                if (businessProcess.getProjectName(getActivity().getApplicationContext()).equals(projectName)) {
                    chronometer.setVisibility(View.VISIBLE);
                    long currentDatetime = System.currentTimeMillis();
                    chronometer.setBase(SystemClock.elapsedRealtime() - (currentDatetime - startDate.getTime()));
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

                String currentProjectName = businessProcess.getProjectName(getActivity().getApplicationContext());

                if (currentProjectName.equals("")) {
                    startVisible = true;
                    stopVisible = false;
                } else if (currentProjectName.equals(projectName) && businessProcess.getStartDate(getActivity().getApplicationContext()) != null) {
                    startVisible = false;
                    stopVisible = true;
                } else {
                    startVisible = true;
                    stopVisible = false;
                }

                initView(rootView);

            }
        }

        private void initView(View rootView) {
            tvProject = (TextView) rootView.findViewById(R.id.section_label);
            btnStart = (Button) rootView.findViewById(R.id.btnStart);
            btnStop = (Button) rootView.findViewById(R.id.btnStop);
            btnReset = (Button) rootView.findViewById(R.id.btnReset);
            chronometer = (Chronometer) rootView.findViewById(R.id.chronometer);
            rlContainer = (RelativeLayout) rootView.findViewById(R.id.rl_container);
            rlContainerNotStarted = (RelativeLayout) rootView.findViewById(R.id.rl_container_not_started);
            rlOtherProject = (RelativeLayout) rootView.findViewById(R.id.rl_container_other_project_selected);



            if (projectName.equals("")) {
                tvProject.setVisibility(View.GONE);
                rlContainer.setVisibility(View.GONE);
                rlContainerNotStarted.setVisibility(View.VISIBLE);
                rlOtherProject.setVisibility(View.GONE);
                chronometer.setVisibility(View.INVISIBLE);
            }else if(businessProcess.getStartDate(getActivity().getApplicationContext()) != null && !businessProcess.getProjectName(getActivity().getApplicationContext()).equals(projectName)){
                // ein anderes Projekt wurde schon gestartet
                rlContainer.setVisibility(View.GONE);
                rlContainerNotStarted.setVisibility(View.GONE);
                tvProject.setVisibility(View.GONE);
                chronometer.setVisibility(View.INVISIBLE);
                TextView tvOtherProjectSelected = (TextView) rootView.findViewById(R.id.tvOtherProjectSelected);

                String text = String.format(getString(R.string.other_project_booked), businessProcess.getProjectName(getActivity().getApplicationContext()));

                tvOtherProjectSelected.setText(text);



                rlOtherProject.setVisibility(View.VISIBLE);

            }else{
                tvProject.setText(projectName);
                rlContainer.setVisibility(View.VISIBLE);
                rlContainerNotStarted.setVisibility(View.GONE);
                rlOtherProject.setVisibility(View.GONE);

                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().setProgressBarIndeterminateVisibility(true);

                        businessProcess.saveProjectName(getActivity().getApplicationContext(), projectName);

                        new StartAsyncTask().execute();

                    }
                });

                btnStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().setProgressBarIndeterminateVisibility(true);
                        btnStop.setEnabled(false);

                        new StopAsyncTask().execute();

                    }
                });

                btnReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        businessProcess.resetProject(getActivity().getApplicationContext());

                        btnReset.setVisibility(View.GONE);
                        btnStart.setVisibility(View.VISIBLE);
                        btnStop.setVisibility(View.GONE);

                        setStartDateTextView();

                        ((MainActivity) getActivity()).refreshNavigationDrawer("");

                    }
                });

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
                setStartDateTextView();
            }


        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getString(ARG_PROJECT_NAME));
        }


        private class StartAsyncTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                businessProcess.checkin(getActivity().getApplicationContext());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                getActivity().setProgressBarIndeterminateVisibility(false);

                // navigation Drawer aktualisieren
                ((MainActivity) getActivity()).refreshNavigationDrawer(projectName);

                btnReset.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);

                setStartDateTextView();

                WidgetUtils.refreshWidget(getActivity().getApplicationContext());

            }
        }

        private class StopAsyncTask extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {

                try {
                    businessProcess.checkout(getActivity().getApplicationContext(), projectName);
                } catch (CrawlingException e) {
                    e.printStackTrace();
                    return e.getMessage();
                } catch (IllegalStateException e1) {
                    return e1.getMessage();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);

                getActivity().setProgressBarIndeterminateVisibility(false);

                if (aVoid == null) {
                    businessProcess.resetStartTime(getActivity().getApplicationContext());
                    ((MainActivity) getActivity()).refreshNavigationDrawer("");

                    btnReset.setVisibility(View.GONE);
                    btnStart.setVisibility(View.VISIBLE);
                    btnStop.setVisibility(View.GONE);



                    setStartDateTextView();

                    WidgetUtils.refreshWidget(getActivity().getApplicationContext());

                    Crouton.makeText(getActivity(), getString(R.string.time_booked_successfull), Style.INFO).show();

                } else {
                    Crouton.makeText(getActivity(), aVoid, Style.ALERT).show();
                }
                btnStop.setEnabled(true);
            }
        }

    }

    private void refreshNavigationDrawer(String projectName) {

        if (businessProcess.getStartDate(getApplicationContext()) != null) {
            mNavigationDrawerFragment.setProjectActive(projectName);
        } else {
            mNavigationDrawerFragment.setProjectActive("");
        }
    }


    private class GetProjectsAsyncTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            try {

                List<String> projectNames = businessProcess.getProjectNames(getApplicationContext());

                return projectNames;
            } catch (CrawlingException e) {
                // TODO: hier muss unterschieden werden, ob die user credentials nicht mehr stimmen oder kein empfang ist

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<String> itemList) {
            super.onPostExecute(itemList);
            setProgressBarIndeterminateVisibility(false);

            // tritt auf bei autologin wenn das passwort geaendert wurde
            if (itemList != null) {
                mNavigationDrawerFragment.setItems(itemList);

                if(businessProcess.getProjectName(getApplicationContext()).equals("")){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    PlaceholderFragment fragment = new PlaceholderFragment();

                    fragment.setArguments(PlaceholderFragment.newInstance("", false, true));

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment)
                            .commit();
                }
            }else{
                Crouton.makeText(MainActivity.this, getString(R.string.no_connection_to_server), Style.ALERT).show();
            }


        }
    }


    public void onResume() {
        super.onResume();
        enableForegroundMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundMode();
    }

    public void enableForegroundMode() {
        Log.d("", "enableForegroundMode");

        // foreground mode gives the current active application priority for reading scanned tags

        if (nfcAdapter != null) {
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
            IntentFilter[] writeTagFilters = new IntentFilter[]{tagDetected};
            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
        }

    }

    public void disableForegroundMode() {
        Log.d("", "disableForegroundMode");
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

}
