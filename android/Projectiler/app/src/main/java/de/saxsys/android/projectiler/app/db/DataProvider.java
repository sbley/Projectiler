package de.saxsys.android.projectiler.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.saxsys.android.projectiler.app.generatedmodel.DaoMaster;
import de.saxsys.android.projectiler.app.generatedmodel.DaoSession;
import de.saxsys.android.projectiler.app.generatedmodel.Track;
import de.saxsys.android.projectiler.app.generatedmodel.TrackDao;

/**
 * Created by stefan.heinze on 11.04.14.
 */
public class DataProvider {

    private static final String DB_NAME = "projectiler-db";

    private final Context context;
    private SQLiteDatabase db;
    private TrackDao trackDao;


    public DataProvider(final Context context) {
        this.context = context;

        SQLiteDatabase.CursorFactory cursorFactory = null;

        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, cursorFactory);

        db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        DaoSession daoSession = daoMaster.newSession();

        trackDao = daoSession.getTrackDao();

    }

    public void saveTrack(final Track track) {
        trackDao.insertOrReplace(track);
    }

    public void deleteTrack(final Track track) {
        trackDao.delete(track);
    }

    public List<Track> getTracks() {
        List<Track> tracks = trackDao.loadAll();
        List<Track> ret = new ArrayList<Track>();

        for (Track track : tracks) {

            if (isToday(track.getTimestamp())) {
                ret.add(track);
            }
        }
        return ret;
    }

    private boolean isToday(Date timestamp) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);

        Calendar calToday = Calendar.getInstance();
        calToday.setTime(new Date());

        int dayToday = calToday.get(Calendar.DAY_OF_MONTH);
        int monthToday = calToday.get(Calendar.MONTH);
        int yearToday = calToday.get(Calendar.YEAR);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);


        if (day == dayToday && month == monthToday && year == yearToday) {
            return true;
        }

        return false;
    }

}
