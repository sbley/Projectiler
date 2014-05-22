package de.saxsys.android.projectiler.app.backend;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author stefan.bley
 */
public final class DateUtil {

	private DateUtil() {
	}

	/** Resets the seconds and millis to zero */
	public static Date resetSeconds(final Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	/** Formats a date to a short readable format */
	public static String formatShort(final Date date) {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
	}

    public static Date formatShort(final String date) {

        if(date.equals("")){
            return null;
        }

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

	/** Formats a date to hours and minutes */
	public static String formatHHmm(final Date date) {
		return new SimpleDateFormat("HHmm").format(date);
	}
}