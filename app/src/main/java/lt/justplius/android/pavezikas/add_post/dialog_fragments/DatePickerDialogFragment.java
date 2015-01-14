package lt.justplius.android.pavezikas.add_post.dialog_fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import lt.justplius.android.pavezikas.add_post.AddPostStep2Fragment;
import lt.justplius.android.pavezikas.add_post.Post;
import lt.justplius.android.pavezikas.common.DatePickerDialog;

public class DatePickerDialogFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {

    private DatePicker mDatePicker;
    private DatePickerDialog mDatePickerDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            // set min and max dates
            Calendar currentCalendar = Calendar.getInstance();
            long minDate = currentCalendar.getTimeInMillis();
            currentCalendar.add(Calendar.MONTH, 2);
            long maxDate = currentCalendar.getTimeInMillis();

            // Get currently selected date
            Calendar c = Post.getInstance().getCurrentCalendar();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog, set date bounds and return it
            mDatePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            DatePicker dp = mDatePickerDialog.getDatePicker();
            dp.setMinDate(minDate);
            dp.setMaxDate(maxDate);


        return mDatePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mDatePicker = view;
    }

    // Handle of compatibility issue bug
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    // Return value to target fragment. This is a workaround to
    // 'ok' button being called during screen rotations on some devices.
    @Override
    public void onDismiss(DialogInterface dialog) {
        // Indicates whether date has been selected
        if (mDatePickerDialog.isSelected() && mDatePicker != null) {
            Long timeInMilliseconds
                    = new GregorianCalendar(mDatePicker.getYear(),
                    mDatePicker.getMonth(),
                    mDatePicker.getDayOfMonth())
                    .getTimeInMillis();
            Intent intent = new Intent();
            intent.putExtra(AddPostStep2Fragment.ARG_DIALOG_FRAGMENT_DATE, timeInMilliseconds);
            getTargetFragment().onActivityResult(
                    getTargetRequestCode(),
                    Activity.RESULT_OK,
                    intent);
        }
        super.onDismiss(dialog);
    }
}
