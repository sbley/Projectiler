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
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.saxsys.android.projectiler.app.backend.Projectiler;
import de.saxsys.android.projectiler.app.backend.UserDataStore;
import de.saxsys.android.projectiler.app.crawler.CrawlingException;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();



        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        setProgressBarIndeterminateVisibility(true);
        new GetProjectsAsyncTask().execute();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        String projectName = "";

        if(mNavigationDrawerFragment != null) {
            projectName = mNavigationDrawerFragment.getProjectName(position);
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getSupportFragmentManager();

            PlaceholderFragment fragment = new PlaceholderFragment();

            Log.i("Projekt", "gestartetes Projekt: " + UserDataStore.getInstance().getProjectName(getApplicationContext()) + " selectes Project: " + projectName);

            // beide Buttons sichtbar, weil kein aktives projekt
            if(UserDataStore.getInstance().getProjectName(getApplicationContext()).equals("")){
                fragment.setArguments(PlaceholderFragment.newInstance(projectName, true, false));

            }else if(UserDataStore.getInstance().getProjectName(getApplicationContext()).equals(projectName)){
                fragment.setArguments(PlaceholderFragment.newInstance(projectName, false, true));
            }else{
                fragment.setArguments(PlaceholderFragment.newInstance(projectName, false, false));
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
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.

            if(UserDataStore.getInstance().getAutoLogin(getApplicationContext())){
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
        } else if( id == R.id.action_logout){
            UserDataStore.getInstance().setAutoLogin(getApplicationContext(), false);
            finish();
        }else if( id == R.id.action_nfc){
            try {

                if(projectilerTag != null){

                    // nfc löschen
                    Log.d("", "write nfc");

                    write(NFC_KEY_WORD, projectilerTag);
                    Crouton.makeText(MainActivity.this, "NFC in Reichweite", Style.CONFIRM).show();
                }else{
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

        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            projectilerTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            Log.d("", "projectilerTag gesetzt");
            Crouton.makeText(MainActivity.this, "NFC in Reichweite", Style.CONFIRM).show();

            //Toast.makeText(this, "okay: " + mytag.toString(), Toast.LENGTH_LONG ).show();

            //new NdefReaderTask().execute(projectilerTag);

        }

    }

    private void write(String text, Tag tag) throws IOException, FormatException {

        NdefRecord[] records = { createRecord(text) };
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
        private TextView tvStartDateLabel;
        private TextView tvStartDate;

        private final Projectiler projectiler;

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
            projectiler = Projectiler.createDefaultProjectiler();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            projectName = getArguments().getString(ARG_PROJECT_NAME);
            startVisible = getArguments().getBoolean(ARG_START_VISIBLE);
            stopVisible = getArguments().getBoolean(ARG_STOP_VISIBLE);


            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            btnStart = (Button) rootView.findViewById(R.id.btnStart);
            btnStop = (Button) rootView.findViewById(R.id.btnStop);
            btnReset = (Button) rootView.findViewById(R.id.btnReset);
            tvStartDateLabel = (TextView) rootView.findViewById(R.id.tvStartDateLabel);
            tvStartDate = (TextView) rootView.findViewById(R.id.tvStartDate);

            textView.setText(projectName);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().setProgressBarIndeterminateVisibility(true);

                    projectiler.saveProjectName(getActivity().getApplicationContext(), projectName);

                    new StartAsyncTask().execute();

                }
            });

            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().setProgressBarIndeterminateVisibility(true);

                    new StopAsyncTask().execute();

                }
            });

            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    projectiler.saveProjectName(getActivity().getApplicationContext(), "");
                    projectiler.resetStartTime(getActivity().getApplicationContext());

                    btnReset.setEnabled(false);
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);

                    setStartDateTextView();

                    ((MainActivity) getActivity()).refreshNavigationDrawer("");

                }
            });


            btnStart.setEnabled(startVisible);
            btnStop.setEnabled(stopVisible);
            btnReset.setEnabled(stopVisible);


            // ist ein startDate gesetzt?
            setStartDateTextView();

            return rootView;
        }

        private void setStartDateTextView(){
            Date startDate = projectiler.getStartDate(getActivity().getApplicationContext());

            if(startDate == null){
                tvStartDateLabel.setVisibility(View.GONE);
                tvStartDate.setVisibility(View.GONE);
            }else{

                if(projectiler.getProjectName(getActivity().getApplicationContext()).equals(projectName)){
                    tvStartDateLabel.setVisibility(View.VISIBLE);
                    tvStartDate.setVisibility(View.VISIBLE);

                    tvStartDate.setText(projectiler.getStartDateAsString(getActivity().getApplicationContext()));
                }else{
                    tvStartDateLabel.setVisibility(View.GONE);
                }


            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getString(ARG_PROJECT_NAME));
        }


        private class StartAsyncTask extends AsyncTask<Void, Void, Void>{

            @Override
            protected Void doInBackground(Void... voids) {

                projectiler.checkin(getActivity().getApplicationContext());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                getActivity().setProgressBarIndeterminateVisibility(false);

                // navigation Drawer aktualisieren
                ((MainActivity)getActivity()).refreshNavigationDrawer(projectName);

                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnReset.setEnabled(true);

                setStartDateTextView();

            }
        }


        private class StopAsyncTask extends AsyncTask<Void, Void, String>{

            @Override
            protected String doInBackground(Void... voids) {

                try {
                    projectiler.checkout(getActivity().getApplicationContext(), projectName);
                } catch (CrawlingException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }catch (IllegalStateException e1){
                    return e1.getMessage();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);

                getActivity().setProgressBarIndeterminateVisibility(false);

                if(aVoid == null) {
                    ((MainActivity) getActivity()).refreshNavigationDrawer("");

                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);
                    btnReset.setEnabled(false);

                    setStartDateTextView();
                    Crouton.makeText(getActivity(), "Zeit wurd erfolgreich gebucht", Style.INFO).show();


                }else{
                    Crouton.makeText(getActivity(), aVoid, Style.ALERT).show();
                }
            }
        }

    }

    private void refreshNavigationDrawer(String projectName) {
        mNavigationDrawerFragment.setProjectActive(projectName);
    }


    private class GetProjectsAsyncTask extends AsyncTask<Void, Void, List<String>>{

        private Projectiler defaultProjectiler;

        @Override
        protected List<String> doInBackground(Void... voids) {

            defaultProjectiler = Projectiler.createDefaultProjectiler();
            try {

                List<String> projectNames = defaultProjectiler.getProjectNames(getApplicationContext());
                for(String projectName : projectNames){
                    Log.i("Projects: ", "" + projectName);
                }

                return projectNames;
            } catch (CrawlingException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<String> itemList) {
            super.onPostExecute(itemList);
            setProgressBarIndeterminateVisibility(false);

            // tritt auf bei autologin wenn das passwort geaendert wurde
            if(itemList != null){
                mNavigationDrawerFragment.setItems(itemList);
            }else {
                defaultProjectiler.setAutoLogin(getApplicationContext(), false);

                MainActivity.this.finish();

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
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        IntentFilter[] writeTagFilters = new IntentFilter[]{tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        Log.d("", "disableForegroundMode");

        nfcAdapter.disableForegroundDispatch(this);
    }

}
