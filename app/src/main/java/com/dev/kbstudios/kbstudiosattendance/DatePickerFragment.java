package com.dev.kbstudios.kbstudiosattendance;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.dev.kbstudios.kbstudiosattendance.AttendanceMenu;

import java.util.Calendar;

/**
 * Created by kbstudios on 21/9/17.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        AttendanceTabActivity attendanceMenu = (AttendanceTabActivity) getActivity();
        attendanceMenu.setYear(year);
        attendanceMenu.setMonth(month);
        attendanceMenu.setDay(day);
        attendanceMenu.gotoTime();
    }
}
