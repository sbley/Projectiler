package de.saxsys.android.projectiler.app.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by stefan.heinze on 25.05.2014.
 */
public class TimePreference extends DialogPreference implements
        TimePicker.OnTimeChangedListener {
    private String dateString;
    private String changedValueCanBeNull;
    private TimePicker datePicker;

    public TimePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        this.datePicker = new TimePicker(getContext());
        String time = getTime();

        String[] split = time.split(":");

        datePicker.setCurrentHour(Integer.parseInt(split[0]));
        datePicker.setCurrentMinute(Integer.parseInt(split[1]));
        return datePicker;
    }

    public String getTime() {
        return defaultValue();
    }

    public void setDate(String dateString) {
        this.dateString = dateString;
    }

    public static SimpleDateFormat formatter() {
        return new SimpleDateFormat("yyyy.MM.dd");
    }

    public static SimpleDateFormat summaryFormatter() {
        return new SimpleDateFormat("MMMM dd, yyyy");
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object def) {
        if (restoreValue) {
            this.dateString = getPersistedString(defaultValue());
            setTheDate(this.dateString);
        } else {
            boolean wasNull = this.dateString == null;
            setDate((String) def);
            if (!wasNull)
                persistDate(this.dateString);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        if (isPersistent())
            return super.onSaveInstanceState();
        else
            return new SavedState(super.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            setTheDate(((SavedState) state).dateValue);
        } else {
            SavedState s = (SavedState) state;
            super.onRestoreInstanceState(s.getSuperState());
            setTheDate(s.dateValue);
        }
    }

    @Override
    protected void onDialogClosed(boolean shouldSave) {
        if (shouldSave && this.changedValueCanBeNull != null) {
            setTheDate(this.changedValueCanBeNull);
            this.changedValueCanBeNull = null;
        }
    }

    private void setTheDate(String s) {
        setDate(s);
        persistDate(s);
    }

    private void persistDate(String s) {
        persistString(s);
        setSummary(s);
    }

    public static Calendar defaultCalendar() {
        return new GregorianCalendar(1970, 0, 1);
    }

    public static String defaultCalendarString() {
        return formatter().format(defaultCalendar().getTime());
    }

    private String defaultValue() {
        if (this.dateString == null)
            setDate(defaultCalendarString());
        return this.dateString;
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        datePicker.clearFocus();
        onTimeChanged(datePicker, datePicker.getCurrentHour(), datePicker.getCurrentMinute());
        onDialogClosed(which == DialogInterface.BUTTON1); // OK?
    }

    public static Calendar getDateFor(SharedPreferences preferences, String field) {
        Date date = stringToDate(preferences.getString(field,
                defaultCalendarString()));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    private static Date stringToDate(String dateString) {
        try {
            return formatter().parse(dateString);
        } catch (ParseException e) {
            return defaultCalendar().getTime();
        }
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hours, int minutes) {

        if(minutes < 10){
            this.changedValueCanBeNull = hours + ":0" + minutes;
        }else{
            this.changedValueCanBeNull = hours + ":" + minutes;
        }

    }

    private static class SavedState extends BaseSavedState {
        String dateValue;

        public SavedState(Parcel p) {
            super(p);
            dateValue = p.readString();
        }

        public SavedState(Parcelable p) {
            super(p);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(dateValue);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
