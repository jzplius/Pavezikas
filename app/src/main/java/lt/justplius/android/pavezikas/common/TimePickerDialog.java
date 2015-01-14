package lt.justplius.android.pavezikas.common;

import android.content.Context;
import android.content.DialogInterface;

/**
 * This is extended TimePickerDialog to make a workaround to a bug
 * ('ok' button being called during screen rotations on some devices).
 * It indicates whether button 'ok' was really pressed (screen rotation
 * clicks are bypassed).
 */
public class TimePickerDialog extends android.app.TimePickerDialog {

    private boolean mIsSelected;

    public TimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
        mIsSelected = false;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        switch (which){
            case BUTTON_POSITIVE:
                setSelected(true);
                break;
            default:
                setSelected(false);
                break;
        }
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }
}
