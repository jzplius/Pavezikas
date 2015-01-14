package lt.justplius.android.pavezikas.add_post;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.ParseException;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.add_post.dialog_fragments.DatePickerDialogFragment;
import lt.justplius.android.pavezikas.add_post.dialog_fragments.TimePickerDialogFragment;
import lt.justplius.android.pavezikas.common.SoftKeyboardHandledLinearLayout;

import static lt.justplius.android.pavezikas.common.NetworkStateUtils.isConnected;
import static lt.justplius.android.pavezikas.common.PostUtils.getFormattedDate;
import static lt.justplius.android.pavezikas.common.PostUtils.getFormattedTime;

/**
 * Prepares post-related details information to insert to DB.
 */
public class AddPostStep2Fragment extends Fragment  {

    private static final String TAG = "AddPostStep2Fragment";
    public static final int REQUEST_DIALOG_FRAGMENT_DATE = 0;
    public static final int REQUEST_DIALOG_FRAGMENT_TIME = 1;
    public static final String ARG_DIALOG_FRAGMENT_DATE
            = "lt.justplius.android.pavezikas.dialog_fragment_date";
    public static final String ARG_DIALOG_FRAGMENT_HOURS
            = "lt.justplius.android.pavezikas.dialog_fragment_hours";
    public static final String ARG_DIALOG_FRAGMENT_MINUTES
            = "lt.justplius.android.pavezikas.dialog_fragment_minutes";

    // Used to customize number pickers
    private static final int MAX_SEATS_NUMBER = 7;
    private static final int MIN_SEATS_NUMBER = 1;
    private static final int MAX_PRICE_NUMBER = 80;
    private static final int MIN_PRICE_NUMBER = 0;

    //private AddPostStep2Callback mCallbacks;

    private TextView mTextViewDate;
	private TextView mTextViewTime;
    private TextView mTextViewLeavingTimeFlexibleValue;
    private EditText mEditTextSeats;
    private EditText mEditTextPrice;
    private EditText mEditTextPhone;
    private EditText mEditTextMessage;

    private int mLeavingSeekBarHours = 0;
	private int mLeavingSeekBarMinutes = 30;
    private Post mPost;

    public interface AddPostStep2Callback{
        public void onInformationSelected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPost = Post.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	View view = inflater.inflate(R.layout.add_post_step2, container, false);

        Button buttonDate = (Button) view.findViewById(R.id.add_post_step2_button_date);
        buttonDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Date picker pop-up
                DatePickerDialogFragment dialogFragmentDate = new DatePickerDialogFragment();
                dialogFragmentDate.setTargetFragment(AddPostStep2Fragment.this, REQUEST_DIALOG_FRAGMENT_DATE);
                dialogFragmentDate.show(getActivity().getSupportFragmentManager(), "DialogFragmentDate");
            }
        });

        Button buttonTime = (Button) view.findViewById(R.id.add_post_step2_button_time);
        buttonTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Time picker pop-up
                TimePickerDialogFragment dialogFragmentTime = new TimePickerDialogFragment();
                dialogFragmentTime.setTargetFragment(AddPostStep2Fragment.this, REQUEST_DIALOG_FRAGMENT_TIME);
                dialogFragmentTime.show(getActivity().getSupportFragmentManager(), "DialogFragmentTime");

            }
        });

    	mTextViewDate = (TextView) view.findViewById(R.id.add_post_step2_textView_date);
    	mTextViewTime = (TextView) view.findViewById(R.id.add_post_step2_textView_time);

        mTextViewLeavingTimeFlexibleValue = (TextView)view.findViewById(R.id.add_post_step2_textView_leaving_time_flexible);
        SeekBar leavingTimeSeekBar = (SeekBar) view.findViewById(R.id.add_post_step2_seekBar_leaving_time);
        leavingTimeSeekBar.setMax(11);
        leavingTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Prevent incrementation on screen rotations
                if (fromUser) {
                    // Count minutes and hours
                    progress = progress * 30 + 30;
                } else {
                    progress = mPost.getLeavingSeekBarHours() * 60 + mPost.getLeavingSeekBarMinutes();
                }

                mLeavingSeekBarHours = (int) Math.ceil(progress / 60);
                mLeavingSeekBarMinutes = progress - mLeavingSeekBarHours * 60;
                mPost.setTimeInterval(mLeavingSeekBarHours, mLeavingSeekBarMinutes);

                if (mLeavingSeekBarHours >= 1) {
                    mTextViewLeavingTimeFlexibleValue.setText(" +- "
                            + mLeavingSeekBarHours
                            + " "
                            + getString(R.string.hours)
                            + " "
                            + mLeavingSeekBarMinutes
                            + " "
                            + getString(R.string.minutes));
                } else {
                    mTextViewLeavingTimeFlexibleValue.setText(" +- "
                            + mLeavingSeekBarMinutes
                            + getString(R.string.minutes));
                }
                updateDateAndTime();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        leavingTimeSeekBar.setProgress(mPost.getLeavingSeekBarHours() * 2
                + mPost.getLeavingSeekBarMinutes() / 30);

        // Get references to included view elements
        View pickerSeats = view.findViewById(R.id.add_post_step2_picker_seats);
        Button buttonSeatsIncrease = (Button) pickerSeats.findViewById(R.id.button_number_picker_increase);
        buttonSeatsIncrease.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = readCurrentNumber(mEditTextSeats, MIN_SEATS_NUMBER);
                if (number >= MIN_SEATS_NUMBER && number < MAX_SEATS_NUMBER) {
                    number++;
                } else {
                    number = MIN_SEATS_NUMBER;
                }
                mEditTextSeats.setText(String.valueOf(number));
            }
        });
        Button buttonSeatsDecrease = (Button) pickerSeats.findViewById(R.id.button_number_picker_decrease);
        buttonSeatsDecrease.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = readCurrentNumber(mEditTextSeats, MIN_SEATS_NUMBER);
                if (number > MIN_SEATS_NUMBER) {
                    number--;
                }
                mEditTextSeats.setText(String.valueOf(number));
            }
        });
        mEditTextSeats = (EditText) ((ViewGroup) pickerSeats).getChildAt(1);
        mEditTextSeats.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = readCurrentNumber(mEditTextSeats, MIN_SEATS_NUMBER);
                if (number < MIN_SEATS_NUMBER || number > MAX_SEATS_NUMBER) {
                    number = MIN_SEATS_NUMBER;
                    mEditTextSeats.setText(String.valueOf(mPost.getSeatsAvailable()));
                }
                mPost.setSeatsAvailable(String.valueOf(number));
            }
        });
        mEditTextSeats.setText(mPost.getSeatsAvailable());

        // Get references to included view elements
        View pickerPrice = view.findViewById(R.id.add_post_step2_picker_price);
        Button buttonPriceIncrease = (Button) pickerPrice.findViewById(R.id.button_number_picker_increase);
        buttonPriceIncrease.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = readCurrentNumber(mEditTextPrice, MIN_PRICE_NUMBER);
                if (number >= MIN_PRICE_NUMBER && number < MAX_PRICE_NUMBER) {
                    number++;
                } else {
                    number = 0;
                }
                mEditTextPrice.setText(String.valueOf(number));
            }
        });
        Button buttonPriceDecrease = (Button) pickerPrice.findViewById(R.id.button_number_picker_decrease);
        buttonPriceDecrease.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = readCurrentNumber(mEditTextPrice, MIN_PRICE_NUMBER);
                if (number > MIN_PRICE_NUMBER) {
                    number--;
                }
                mEditTextPrice.setText(String.valueOf(number));
            }
        });
        mEditTextPrice = (EditText) ((ViewGroup) pickerPrice).getChildAt(1);
        mEditTextPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = readCurrentNumber(mEditTextPrice, MIN_PRICE_NUMBER);
                if (number < MIN_PRICE_NUMBER || number > MAX_PRICE_NUMBER) {
                    number = MIN_PRICE_NUMBER;
                    mEditTextPrice.setText(String.valueOf(mPost.getPrice()));
                }
                mPost.setPrice(String.valueOf(number));
            }
        });
        mEditTextPrice.setText(mPost.getPrice());

        mEditTextPhone = (EditText) view.findViewById(R.id.add_post_step2_editText_phone);
        mEditTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showOrHideSoftInputOnFocus();
                }
            }
        });
        if (!mPost.getPhone().equals("")) {
            mEditTextPhone.setText(mPost.getPhone());
        }

        mEditTextMessage = (EditText) view.findViewById(R.id.add_post_step2_editText_message);
        mEditTextMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showOrHideSoftInputOnFocus();
                }
            }
        });
        if (!mPost.getMessage().equals("")) {
            mEditTextMessage.setText(mPost.getMessage());
        }

        SoftKeyboardHandledLinearLayout linearLayout
                = (SoftKeyboardHandledLinearLayout) view.findViewById(R.id.add_post_step2_linearLayout);
        linearLayout.setOnSoftKeyboardVisibilityChangeListener(
                new SoftKeyboardHandledLinearLayout.SoftKeyboardVisibilityChangeListener() {
            @Override
            public void onSoftKeyboardShow() {
                // Do nothing, as not all shows are caught
            }

            @Override
            public void onSoftKeyboardHide() {
                // Save EditText information, because edit is finished
                mPost.setPhone(mEditTextPhone.getText().toString(), getActivity());
                mPost.setMessage(mEditTextMessage.getText().toString(), getActivity());
                // If fields are left empty supply values from model
                if (mEditTextSeats.getText().toString().equals("")) {
                    mEditTextSeats.setText(String.valueOf(mPost.getSeatsAvailable()));
                }
                if (mEditTextPrice.getText().toString().equals("")) {
                    mEditTextPrice.setText(String.valueOf(mPost.getPrice()));
                }
            }
        });

        updateDateAndTime();

        return view;
    }

    // Do not display keyboard, when internet connection is not present
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showOrHideSoftInputOnFocus() {
        //TODO test on pre-21 API devices
        if (!isConnected(getActivity())/* && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP*/) {
            mEditTextPhone.setShowSoftInputOnFocus(false);
            mEditTextMessage.setShowSoftInputOnFocus(false);
        } else {
            mEditTextPhone.setShowSoftInputOnFocus(true);
            mEditTextMessage.setShowSoftInputOnFocus(true);
        }
    }

    private int readCurrentNumber(EditText editText, int defaultValue) {
        try {
            return Integer.valueOf(editText.getText().toString());
        } catch (NumberFormatException e){
            return defaultValue;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Fragment is attached
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_DIALOG_FRAGMENT_DATE:
                    // Update model
                    long timeInMilliseconds = data.getExtras().getLong(ARG_DIALOG_FRAGMENT_DATE);
                    mPost.setDate(timeInMilliseconds);

                    // Update view
                    updateDateAndTime();
                    break;
                case REQUEST_DIALOG_FRAGMENT_TIME:
                    // Update model
                    int hours = data.getExtras().getInt(ARG_DIALOG_FRAGMENT_HOURS);
                    int minutes = data.getExtras().getInt(ARG_DIALOG_FRAGMENT_MINUTES);
                    mPost.setTime(hours, minutes);

                    // Update view
                    updateDateAndTime();
                    break;
            }
        }
    }


    private void updateDateAndTime(){
        try {
            mTextViewDate.setText(
                    getFormattedDate(getActivity(), mPost.getLeavingCalendarFrom().getTimeInMillis()));
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: ", e);
        }
        mTextViewTime.setText(getFormattedTime(
                getActivity(),
                mPost.getIsFlexible(),
                mPost.getLeavingCalendarFrom().getTimeInMillis(),
                mPost.getLeavingCalendarTo().getTimeInMillis()));
    }

    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof AddPostStep2Callback)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (AddPostStep2Callback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface.
        mCallbacks = null;
    }*/
}