package lt.justplius.android.pavezikas.add_post;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import lt.justplius.android.pavezikas.R;

import static lt.justplius.android.pavezikas.common.NetworkStateUtils.isConnected;

/**
 * Allows to select post type (driver / passenger) information.
 */
public class AddPostStep1Fragment extends Fragment  {
    private AddPostStep1Callback mCallbacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
    	View v = inflater.inflate(R.layout.add_post_step1, container, false);

        final RadioButton radioButtonDriver
                = (RadioButton) v.findViewById(R.id.add_post_step1_radioButton_driver);
        final RadioButton radioButtonPassenger
                = (RadioButton) v.findViewById(R.id.add_post_step1_radioButton_passenger);

        LinearLayout addDriverBackground = (LinearLayout)
                v.findViewById(R.id.add_post_step1_driverBackground);
        addDriverBackground.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isConnected(getActivity())) {
                    Post.getInstance().setPostType("driver");
                    mCallbacks.onPostTypeSelected();
                    radioButtonDriver.setChecked(true);
                    radioButtonPassenger.setChecked(false);
                }

            }

        });

        LinearLayout addPassengerBackground = (LinearLayout)
                v.findViewById(R.id.add_post_step1_passengerBackground);
        addPassengerBackground.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isConnected(getActivity())) {
                    Post.getInstance().setPostType("passenger");
                    mCallbacks.onPostTypeSelected();
                    radioButtonDriver.setChecked(false);
                    radioButtonPassenger.setChecked(true);
                }
            }

        });

        if (Post.getInstance().getPostType().equals("driver")) {
            radioButtonDriver.setChecked(true);
            radioButtonPassenger.setChecked(false);
        } else {
            radioButtonDriver.setChecked(false);
            radioButtonPassenger.setChecked(true);
        }

        return v;
    }

    public interface AddPostStep1Callback{
        public void onPostTypeSelected();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof AddPostStep1Callback)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (AddPostStep1Callback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface.
        mCallbacks = null;
    }
}