package lt.justplius.android.pavezikas.add_post;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.post.Post;
import lt.justplius.android.pavezikas.post.PostManager;

public class AddPostStep3Fragment extends Fragment  {
    private static final int REQUEST_LEAVING_ADDRESS = 0;
    private static final int REQUEST_DROPPING_ADDRESS = 1;
    public static final String ARG_LEAVING_ADDRESS = "lt.justplius.android.pavezikas.leaving_address";
    public static final String ARG_DROPPING_ADDRESS = "lt.justplius.android.pavezikas.dropping_address";
    private AddPostStep3Callback mCallbacks;
    private LeavingAddressDialogFragment mLeavingAddressPicker;
    private TextView mTextViewLeavingAddress;
    private TextView mTextViewDroppingAddress;
    private DroppingAddressDialogFragment mDroppingAddressPicker;
    private Post mPost;

    public interface AddPostStep3Callback{
        public void onPostRouteSelected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Leaving address pick-up
        mLeavingAddressPicker = new LeavingAddressDialogFragment();
        mLeavingAddressPicker.setTargetFragment(this, REQUEST_LEAVING_ADDRESS);
        mDroppingAddressPicker = new DroppingAddressDialogFragment();
        mDroppingAddressPicker.setTargetFragment(this, REQUEST_DROPPING_ADDRESS);

        mPost = PostManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.add_post_step3, container, false);

        Button buttonStep3Continue = (Button) v.findViewById(R.id.add_post_step3_button_post);
        buttonStep3Continue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onPostRouteSelected();
            }
        });

        Button buttonLeavingAddress = (Button) v.findViewById(R.id.add_post_step3_button_address_leaving);
        buttonLeavingAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeavingAddressPicker.show(getActivity().getSupportFragmentManager(), "LeavingPicker");
            }
        });
        Button buttonDroppingAddress = (Button) v.findViewById(R.id.add_post_step3_button_address_destination);
        buttonDroppingAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDroppingAddressPicker.show(getActivity().getSupportFragmentManager(), "DroppingPicker");
            }
        });

        mTextViewLeavingAddress = (TextView) v.findViewById(R.id.add_post_step3_textView_address_leaving);
        mTextViewLeavingAddress.setText(mPost.getLeavingAddress());
        mTextViewDroppingAddress = (TextView) v.findViewById(R.id.add_post_step3_textView_destination_address);
        mTextViewDroppingAddress.setText(mPost.getDroppingAddress());

        final Spinner spinnerLeavingCity = (Spinner) v.findViewById(R.id.add_post_step3_spinner_city_leaving);
        spinnerLeavingCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                mPost.setRouteCity(
                        0, spinnerLeavingCity.getItemAtPosition(position).toString());
                mTextViewLeavingAddress.setText(mPost.getLeavingAddress());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
        final Spinner spinnerDroppingCity = (Spinner) v.findViewById(R.id.add_post_step3_spinner_city_destination);
        spinnerDroppingCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                mPost.setRouteCity(
                        1, spinnerDroppingCity.getItemAtPosition(position).toString());
                mTextViewDroppingAddress.setText(mPost.getDroppingAddress());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });

        // Populate leaving and dropping address spinner with string adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.cities_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLeavingCity.setAdapter(adapter);
        spinnerLeavingCity.setSelection(adapter.getPosition(mPost.getCity(0)));
        spinnerDroppingCity.setAdapter(adapter);
        spinnerDroppingCity.setSelection(adapter.getPosition(mPost.getCity(1)));
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LEAVING_ADDRESS:
                    // Update model
                    mPost.setLeavingAddress(data.getStringExtra(ARG_LEAVING_ADDRESS));
                    // Update view
                    mTextViewLeavingAddress.setText(mPost.getLeavingAddress());
                    break;
                case REQUEST_DROPPING_ADDRESS:
                    // Update model
                    mPost.setDroppingAddress(data.getStringExtra(ARG_DROPPING_ADDRESS));
                    // Update view
                    mTextViewDroppingAddress.setText(mPost.getDroppingAddress());
                    break;
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof AddPostStep3Callback)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (AddPostStep3Callback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface.
        mCallbacks = null;
    }
}