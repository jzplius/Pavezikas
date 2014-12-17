package lt.justplius.android.pavezikas.add_post;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.add_post.events.RouteIdRetrievedEvent;
import lt.justplius.android.pavezikas.common.BaseTwoFragmentsActivity;
import lt.justplius.android.pavezikas.common.BusProvider;
import lt.justplius.android.pavezikas.facebook.FacebookLoginFragment;
import lt.justplius.android.pavezikas.post.Post;
import lt.justplius.android.pavezikas.post.PostManager;

import static lt.justplius.android.pavezikas.common.NetworkStateUtils.isConnected;
import static lt.justplius.android.pavezikas.post.PostLoaderManager.*;
import static lt.justplius.android.pavezikas.post.PostManager.getLoader;

public class AddPostStep3Fragment extends Fragment
        implements LoaderManager.LoaderCallbacks {
    private static final String TAG = "AddPostStep3Fragment";

    private static final int REQUEST_LEAVING_ADDRESS = 0;
    private static final int REQUEST_DROPPING_ADDRESS = 1;
    private static final int REQUEST_USER_GROUPS = 2;
    public static final String ARG_LEAVING_ADDRESS = "lt.justplius.android.pavezikas.leaving_address";
    public static final String ARG_DROPPING_ADDRESS = "lt.justplius.android.pavezikas.dropping_address";
    public static final String ARG_SELECTED_GROUPS = "lt.justplius.android.pavezikas.selected_groups";
    public static final String NVP_USER_ID = "user_id";
    public static final String NVP_ROUTE_ID = "route_id";
    private static final String NVP_GROUPS_IDS = "groupIds[]";

    private AddPostStep3Callback mCallbacks;

    private TextView mTextViewLeavingAddress;
    private TextView mTextViewDroppingAddress;
    private Spinner mSpinnerRoutePairedGroups;
    private Post mPost;

    private LeavingAddressDialogFragment mLeavingAddressPicker;
    private DroppingAddressDialogFragment mDroppingAddressPicker;
    private TextView mTextViewSelectedGroups;
    private ImageView mImageViewLineHorizontal6;
    private ImageView mImageViewLineHorizontal7;


    public interface AddPostStep3Callback{
        public void onPostRouteSelected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO check if asynchronous tasks aren't executed simultaneously
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
        mPost = PostManager.getPost(getActivity());

        getActivity()
                .getLoaderManager()
                .initLoader(LOADER_USER_ROUTE_PAIRED_GROUPS, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.add_post_step3, container, false);

        /*Button buttonStep3Continue = (Button) v.findViewById(R.id.add_post_step3_button_post);
        buttonStep3Continue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onPostRouteSelected();
            }
        });*/

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

        // Populate leaving and dropping address spinner with string adapter
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.cities_list, android.R.layout.simple_spinner_item);
        final Spinner spinnerLeavingCity = (Spinner) v.findViewById(R.id.add_post_step3_spinner_city_leaving);
        spinnerLeavingCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isConnected(getActivity())) {
                    mPost.setRouteCity(
                            0, spinnerLeavingCity.getItemAtPosition(position).toString());
                    // Calls onRouteIdRetrieved() when Loader downloads data
                } else {
                    // Revert selection
                    spinnerLeavingCity.setSelection(adapter.getPosition(mPost.getCity(0)));
                }
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
                if (isConnected(getActivity())) {
                    mPost.setRouteCity(
                            1, spinnerDroppingCity.getItemAtPosition(position).toString());
                    new Handler().postDelayed(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      updateRoutePairedGroupsView();
                                                  }
                                              },
                            1500);
                    // Calls onRouteIdRetrieved() when Loader downloads data
                } else {
                    // Revert selection
                    spinnerDroppingCity.setSelection(adapter.getPosition(mPost.getCity(1)));
                }
                mTextViewDroppingAddress.setText(mPost.getDroppingAddress());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
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

        mTextViewSelectedGroups = (TextView) v.findViewById(R.id.add_post_step3_textView_selected_groups);

        mSpinnerRoutePairedGroups = (Spinner) v.findViewById(R.id.add_post_step3_spinner_groups);

        mImageViewLineHorizontal6 = (ImageView) v.findViewById(R.id.add_post_step3_line_horizontal6);
        mImageViewLineHorizontal7 = (ImageView) v.findViewById(R.id.add_post_step3_line_horizontal7);
        return v;
    }

    @Override public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    private void updateRoutePairedGroupsView() {
        // Get model
        getActivity()
                .getLoaderManager()
                .getLoader(LOADER_USER_ROUTE_PAIRED_GROUPS)
                .forceLoad();
    }

    // Call this method via Otto Event Bus
    @Subscribe
    public void onRouteIdRetrieved(RouteIdRetrievedEvent event) {
        if (RouteIdRetrievedEvent.sRouteId == 0) {
            hideRoutePairedGroups();
        } else {
            updateRoutePairedGroupsView();
        }
    }

    private void hideRoutePairedGroups(){
        mSpinnerRoutePairedGroups.setAdapter(null);
        mSpinnerRoutePairedGroups.setVisibility(View.GONE);
        mTextViewSelectedGroups.setVisibility(View.GONE);
        mImageViewLineHorizontal6.setVisibility(View.GONE);
        mImageViewLineHorizontal7.setVisibility(View.GONE);
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
                        pairs.add(new BasicNameValuePair(NVP_GROUPS_IDS, groupId));
                    }

                    String userId = PreferenceManager
                            .getDefaultSharedPreferences(getActivity())
                            .getString(FacebookLoginFragment.PREF_FB_ID, "1");
                    pairs.add(new BasicNameValuePair(NVP_USER_ID, userId));
                    pairs.add(new BasicNameValuePair(NVP_ROUTE_ID, mPost.getRouteID()));
                    Bundle args = new Bundle();
                    String gson = new Gson().toJson(pairs);
                    // Convert to String using Gson
                    args.putString(ARG_SELECTED_GROUPS, gson);

                    getActivity()
                            .getLoaderManager()
                            .restartLoader(LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS, args, this);
                    break;
            }
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case LOADER_USER_ROUTE_PAIRED_GROUPS:
                return getLoader().createUserRoutePairedGroupsLoader(getActivity());
            case LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS:
                // Revert object from String using Gson
                Type type = new TypeToken<ArrayList<BasicNameValuePair>>(){}.getType();
                String gson = bundle.getString(ARG_SELECTED_GROUPS);
                ArrayList<NameValuePair> pairs =
                        new Gson().fromJson(gson, type);
                return getLoader().createUpdateUserRoutePairedGroupsLoader(
                        getActivity(),
                        pairs);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (isVisible()) {
            switch (loader.getId()) {
                case LOADER_USER_ROUTE_PAIRED_GROUPS:
                    ArrayList<String> groups = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(data.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            groups.add(jsonArray.getString(i));
                        }
                        if (groups.size() > 0) {
                            mSpinnerRoutePairedGroups.setVisibility(View.VISIBLE);
                            mTextViewSelectedGroups.setVisibility(View.VISIBLE);
                            mImageViewLineHorizontal6.setVisibility(View.VISIBLE);
                            mImageViewLineHorizontal7.setVisibility(View.VISIBLE);

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    groups);
                            mSpinnerRoutePairedGroups.setAdapter(adapter);
                        } else {
                            onLoaderReset(loader);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error converting route paired groups result:", e);
                    }
                    break;
                case LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS:
                    updateRoutePairedGroupsView();
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (isVisible()) {
            switch (loader.getId()) {
                case LOADER_USER_ROUTE_PAIRED_GROUPS:
                    hideRoutePairedGroups();
                    break;
                case LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS:
                    hideRoutePairedGroups();
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