package lt.justplius.android.pavezikas.add_post.dialog_fragments;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import lt.justplius.android.pavezikas.add_post.AddPostStep2Fragment;
import lt.justplius.android.pavezikas.add_post.Post;
import lt.justplius.android.pavezikas.common.TimePickerDialog;

public class TimePickerDialogFragment extends DialogFragment
implements TimePickerDialog.OnTimeSetListener,
        TimePickerDialog.OnDismissListener {

    private TimePicker mTimePicker;
    private TimePickerDialog mTimePickerDialog;

    @NonNull
    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Post.getInstance()
                        .getCurrentCalendar();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        mTimePickerDialog = new TimePickerDialog(
                getActivity(),
                this,
                hour,
                minute,
                DateFormat.is24HourFormat(getActivity()));

        return mTimePickerDialog;
	}

    @Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mTimePicker = view;
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
        // Indicates whether time has been selected
        if (mTimePickerDialog.isSelected() && mTimePicker != null) {
            Intent intent = new Intent();
            intent.putExtra(AddPostStep2Fragment.ARG_DIALOG_FRAGMENT_HOURS, mTimePicker.getCurrentHour());
            intent.putExtra(AddPostStep2Fragment.ARG_DIALOG_FRAGMENT_MINUTES, mTimePicker.getCurrentMinute());
            getTargetFragment().onActivityResult(
                    getTargetRequestCode(),
                    Activity.RESULT_OK,
                    intent);
        }
        super.onDismiss(dialog);
    }
}
