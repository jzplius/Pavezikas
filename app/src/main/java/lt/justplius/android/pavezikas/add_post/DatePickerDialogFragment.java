package lt.justplius.android.pavezikas.add_post;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import lt.justplius.android.pavezikas.post.PostManager;

public class DatePickerDialogFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {
	
	@NonNull
    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		// set min and max dates
		Calendar currentCalendar = Calendar.getInstance();
		long minDate = currentCalendar.getTimeInMillis();
        currentCalendar.add(Calendar.MONTH, 1);
		long maxDate = currentCalendar.getTimeInMillis();

		// Get currently selected date
		Calendar c = PostManager.getInstance(getActivity()).getCurrentCalendar();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		// Create a new instance of DatePickerDialog, set date bounds and return it
		DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
		DatePicker dp = dpd.getDatePicker();	
		dp.setMinDate(minDate);
		dp.setMaxDate(maxDate);

		return dpd;
	}
	
	public void onDateSet(DatePicker view, int year, int month, int day) {
        Long timeInMilliseconds = new GregorianCalendar(year, month, day).getTimeInMillis();
        Intent intent = new Intent();
        intent.putExtra(AddPostStep2Fragment.ARG_DIALOG_FRAGMENT_DATE, timeInMilliseconds);
        getTargetFragment().onActivityResult(
            getTargetRequestCode(),
            Activity.RESULT_OK,
            intent);
        dismiss();
    }
}
