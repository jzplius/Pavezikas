package lt.justplius.android.pavezikas.add_post;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import lt.justplius.android.pavezikas.post.PostManager;

public class TimePickerDialogFragment extends DialogFragment
implements TimePickerDialog.OnTimeSetListener {
	
	@NonNull
    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time
		Calendar c = PostManager.getInstance(getActivity()).getCurrentCalendar();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(
                getActivity(),
                this,
                hour,
                minute,
                DateFormat.is24HourFormat(getActivity()));
	}
		
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Intent intent = new Intent();
        intent.putExtra(AddPostStep2Fragment.ARG_DIALOG_FRAGMENT_HOURS, hourOfDay);
        intent.putExtra(AddPostStep2Fragment.ARG_DIALOG_FRAGMENT_MINUTES, minute);
        getTargetFragment().onActivityResult(
                getTargetRequestCode(),
                Activity.RESULT_OK,
                intent);
        dismiss();
	}
}
