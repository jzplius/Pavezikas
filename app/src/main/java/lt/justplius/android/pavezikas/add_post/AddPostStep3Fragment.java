package lt.justplius.android.pavezikas.add_post;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.BaseTwoFragmentsActivity;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;
import lt.justplius.android.pavezikas.common.NetworkStateUtils;
import lt.justplius.android.pavezikas.facebook.FacebookLoginFragment;
import lt.justplius.android.pavezikas.post.Post;
import lt.justplius.android.pavezikas.post.PostManager;

import static lt.justplius.android.pavezikas.common.NetworkStateUtils.isConnected;

public class AddPostStep3Fragment extends Fragment  {
    private static final String TAG = "AddPostStep3Fragment";

    private static final int REQUEST_LEAVING_ADDRESS = 0;
    private static final int REQUEST_DROPPING_ADDRESS = 1;
    private static final int REQUEST_USER_GROUPS = 2;
    public static final String ARG_LEAVING_ADDRESS = "lt.justplius.android.pavezikas.leaving_address";
    public static final String ARG_DROPPING_ADDRESS = "lt.justplius.android.pavezikas.dropping_address";
    public static final String ARG_SELECTED_GROUPS = "lt.justplius.android.pavezikas.selected_groups";

    private AddPostStep3Callback mCallbacks;

    private TextView mTextViewLeavingAddress;
    private TextView mTextViewDroppingAddress;
    private ListView mListViewRoutePairedGroups;
    private Post mPost;

    private LeavingAddressDialogFragment mLeavingAddressPicker;
    private DroppingAddressDialogFragment mDroppingAddressPicker;


    public interface AddPostStep3Callback{
        public void onPostRouteSelected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO get static model
        // TODO get dynamic model
        // TODO update model
        // if user did not select two equal cities
        // TODO download data in background thread
        // TODO show view
        // TODO update view
        // TODO check for network connectivity
        // TODO check if fragment is attached to activity
        // TODO implement upStop/onResume, onPause/OnResume handling and release of resources
        // TODO implement screen rotation
        // TODO implement back pressed action
        // TODO implement single-dual pane view display
        // TODO implement UNIT tests
        // TODO implement integration tests

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
                if (isConnected(getActivity())) {
                    mLeavingAddressPicker.show(getActivity().getSupportFragmentManager(), "LeavingPicker");
                }
            }
        });
        Button buttonDroppingAddress = (Button) v.findViewById(R.id.add_post_step3_button_address_destination);
        buttonDroppingAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected(getActivity())) {
                    mDroppingAddressPicker.show(getActivity().getSupportFragmentManager(), "DroppingPicker");
                }
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateRoutePairedGroupsView();
                    }
                },
                500);
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
                new Handler().postDelayed(new Runnable() {
                                              @Override
                                              public void run() {
                                                  updateRoutePairedGroupsView();
                                              }
                                          },
                        500);
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

        Button buttonFacebookGroups = (Button) v.findViewById(R.id.add_post_step3_button_select_groups);
        buttonFacebookGroups.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected(getActivity())) {
                    UserGroupsDialogFragment groupsDialogFragment = new UserGroupsDialogFragment();
                    groupsDialogFragment.setTargetFragment(
                            getFragmentManager().findFragmentByTag(BaseTwoFragmentsActivity.TAG_DETAILS_FRAGMENT),
                            REQUEST_USER_GROUPS);
                    groupsDialogFragment.show(getActivity().getSupportFragmentManager(), "GroupsDialogFragment");
                }
            }
        });

        mListViewRoutePairedGroups = (ListView) v.findViewById(R.id.add_post_step3_listView_groups);
        updateRoutePairedGroupsView();

        return v;
    }

    private void updateRoutePairedGroupsView() {
        String userId = PreferenceManager
                .getDefaultSharedPreferences(getActivity()).getString(FacebookLoginFragment.PREF_FB_ID, "");
        new SelectUserRoutePairedGroupsTask().execute(
                new BasicNameValuePair("user_id", userId),
                new BasicNameValuePair("route_id", mPost.getRouteID())
        );
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
                case REQUEST_USER_GROUPS:
                    // Update model
                    ArrayList<String> groups = data.getStringArrayListExtra(ARG_SELECTED_GROUPS);
                    ArrayList<NameValuePair> pairs = new ArrayList<>();
                    for (String groupId : groups) {
                        pairs.add(new BasicNameValuePair("groupIds[]", groupId));
                    }
                    String userId = PreferenceManager
                            .getDefaultSharedPreferences(getActivity())
                            .getString(FacebookLoginFragment.PREF_FB_ID, "");
                    pairs.add(new BasicNameValuePair("user_id", userId));
                    pairs.add(new BasicNameValuePair("route_id", mPost.getRouteID()));
                    //noinspection unchecked
                    new UpdateUserRoutePairedGroupsTask().execute(pairs);

                    // UpdateView
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

    //TODO move to background thread and to separate class
    // Task to update users' route and groups in DB
    @SuppressWarnings("unchecked")
    private class UpdateUserRoutePairedGroupsTask extends AsyncTask<ArrayList<NameValuePair>, Void, Void> {
        private String mUrl;

        protected void onPreExecute () {
            mUrl = getActivity().getString(R.string.url_update_user_route_paired_groups);
        }

        @Override
        protected Void doInBackground(ArrayList<NameValuePair>... pairs) {
            new HttpPostStringResponse(mUrl, pairs[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
           updateRoutePairedGroupsView();
        }
    }


    //TODO move to background thread and to separate class
    // Task to selected users' route and paired to it groups
    @SuppressWarnings("unchecked")
    private class SelectUserRoutePairedGroupsTask extends AsyncTask<NameValuePair, Void, String> {
        private String mUrl;

        protected void onPreExecute () {
            mUrl = getActivity().getString(R.string.url_select_user_route_paired_groups);
        }

        @Override
        protected String doInBackground(NameValuePair... pair) {
            // Add all passed pairs to ArrayList
            ArrayList<NameValuePair> pairs = new ArrayList<>();
            Collections.addAll(pairs, pair);
            return new HttpPostStringResponse(mUrl, pairs).returnJSON();
        }

        @Override
        protected void onPostExecute(String result) {
            ArrayList<String> groups = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    groups.add(jsonArray.getString(i));
                }
                mListViewRoutePairedGroups.setAdapter(
                    new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, groups));
            } catch (JSONException e) {
                Log.e(TAG, "Error converting route paired groups result:", e);
            }
        }
    }
}