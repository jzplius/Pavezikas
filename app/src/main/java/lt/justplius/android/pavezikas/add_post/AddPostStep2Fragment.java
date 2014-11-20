package lt.justplius.android.pavezikas.add_post;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.ParseException;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.post.Post;
import lt.justplius.android.pavezikas.post.PostManager;

import static lt.justplius.android.pavezikas.post.PostUtils.getFormattedDate;
import static lt.justplius.android.pavezikas.post.PostUtils.getFormattedTime;

public class AddPostStep2Fragment extends Fragment  {

    private static final String TAG = "AddPostStep2Fragment";
    public static final int REQUEST_DIALOG_FRAGMENT_DATE = 0;
    public static final int REQUEST_DIALOG_FRAGMENT_TIME = 1;
    public static final String ARG_DIALOG_FRAGMENT_DATE = "lt.justplius.android.pavezikas.dialog_fragment_date";
    public static final String ARG_DIALOG_FRAGMENT_HOURS = "lt.justplius.android.pavezikas.dialog_fragment_hours";
    public static final String ARG_DIALOG_FRAGMENT_MINUTES = "lt.justplius.android.pavezikas.dialog_fragment_minutes";

    // Used to customize number pickers
    private static final int MAX_SEATS_NUMBER = 7;
    private static final int MIN_SEATS_NUMBER = 1;
    private static final int MAX_PRICE_NUMBER = 80;
    private static final int MIN_PRICE_NUMBER = 0;

    private AddPostStep2Callback mCallbacks;

    // Dialog fragments to be popped up on click
	private DialogFragment mDialogFragmentDate;
	private DialogFragment mDialogFragmentTime;

    private TextView mTextViewDate;
	private TextView mTextViewTime;
    private SeekBar mLeavingTimeSeekBar;
	private TextView mTextViewLeavingTimeFlexibleValue;
	private EditText mEditTextPhone;
	private EditText mEditTextMessage;
    private EditText mEditTextSeats;
    private EditText mEditTextPrice;

    private int mLeavingSeekBarHours = 0;
	private int mLeavingSeekBarMinutes = 30;
    private FragmentManager mFragmentManager;
    private Post mPost;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentManager = getActivity().getSupportFragmentManager();
        mPost = PostManager.getInstance(getActivity());

        // Date picker pop-up
        mDialogFragmentDate = new DatePickerFragment();
        mDialogFragmentDate.setTargetFragment(this, REQUEST_DIALOG_FRAGMENT_DATE);

        // Time picker pop-up
        mDialogFragmentTime = new TimePickerFragment();
        mDialogFragmentTime.setTargetFragment(this, REQUEST_DIALOG_FRAGMENT_TIME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
    	View view = inflater.inflate(R.layout.add_post_step2, container, false);
        Button buttonStep2Continue = (Button) view.findViewById(R.id.add_post_step2_button_continue);
        if (((AddPostActivity) getActivity()).getCurrentStep() == 3) {
            buttonStep2Continue.setVisibility(View.GONE);
        } else {
            buttonStep2Continue.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.onInformationSelected();
                    mPost.setPhone(mEditTextPhone.getText().toString());
                    mPost.setMessage(mEditTextMessage.getText().toString());
                }
            });
        }

        Button buttonDate = (Button) view.findViewById(R.id.add_post_step2_button_date);
        buttonDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogFragmentDate.show(mFragmentManager, "DialogFragmentDate");
            }
        });

        Button buttonTime = (Button) view.findViewById(R.id.add_post_step2_button_time);
        buttonTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogFragmentTime.show(mFragmentManager, "DialogFragmentTime");
            }
        });

    	mTextViewDate = (TextView) view.findViewById(R.id.add_post_step2_textView_date);
    	mTextViewTime = (TextView) view.findViewById(R.id.add_post_step2_textView_time);

        // Handle leaving time flexibility changes
        CheckBox checkBoxIsFlexible = (CheckBox) view.findViewById(R.id.add_post_step2_checkBox_flexibility);
        checkBoxIsFlexible.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mPost.setIsFlexible(isChecked);
                mLeavingTimeSeekBar.setEnabled(isChecked);
                mTextViewLeavingTimeFlexibleValue.setEnabled(isChecked);
                updateDateAndTime();
            }
        });
        checkBoxIsFlexible.setChecked(mPost.getIsFlexible());
        mTextViewLeavingTimeFlexibleValue = (TextView)view.findViewById(R.id.add_post_step2_textView_leaving_time_flexible);
    	mLeavingTimeSeekBar = (SeekBar) view.findViewById(R.id.add_post_step2_seekBar_leaving_time);
        mLeavingTimeSeekBar.setMax(12);
        mLeavingTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Count minutes and hours
                progress = progress * 30;
                mLeavingSeekBarHours = (int) Math.ceil(progress / 60);
                mLeavingSeekBarMinutes = progress - mLeavingSeekBarHours * 60;
                mPost.setTimeInterval(mLeavingSeekBarHours, mLeavingSeekBarMinutes);

                if (mLeavingSeekBarHours >= 1){
                    mTextViewLeavingTimeFlexibleValue.setText(" +- "
                            + mLeavingSeekBarHours
                            + getString(R.string.hours)
                            + " "
                            + mLeavingSeekBarMinutes
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
        mLeavingTimeSeekBar.setProgress(mPost.getLeavingSeekBarHours() * 2
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
        mEditTextSeats.addTextChangedListener( new TextWatcher() {
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
                    mEditTextSeats.setText(String.valueOf(MIN_SEATS_NUMBER));
                    mPost.setSeatsAvailable(String.valueOf(MIN_SEATS_NUMBER));
                } else {
                    mPost.setSeatsAvailable(String.valueOf(number));
                }
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
                    mEditTextPrice.setText(String.valueOf(MIN_PRICE_NUMBER));
                    mPost.setPrice(String.valueOf(MIN_PRICE_NUMBER));
                } else {
                    mPost.setPrice(String.valueOf(number));
                }
            }
        });
        mEditTextPrice.setText(mPost.getPrice());

        mEditTextPhone = (EditText) view.findViewById(R.id.add_post_step2_editText_phone);

        mEditTextMessage = (EditText) view.findViewById(R.id.add_post_step2_editText_message);

        updateDateAndTime();

        return view;
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

    public interface AddPostStep2Callback{
        public void onInformationSelected();
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

    @Override
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
    }
    
}