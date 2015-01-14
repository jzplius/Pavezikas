package lt.justplius.android.pavezikas.add_post;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import lt.justplius.android.pavezikas.add_post.dialog_fragments.DroppingAddressDialogFragment;
import lt.justplius.android.pavezikas.add_post.dialog_fragments.LeavingAddressDialogFragment;
import lt.justplius.android.pavezikas.add_post.dialog_fragments.UserFacebookGroupsDialogFragment;
import lt.justplius.android.pavezikas.add_post.events.DownloadFinishedEvent;
import lt.justplius.android.pavezikas.add_post.events.DownloadStartedEvent;
import lt.justplius.android.pavezikas.display_posts.PostsListActivity;
import lt.justplius.android.pavezikas.facebook.FacebookLoginFragment;
import lt.justplius.android.pavezikas.mangers.BusManager;
import lt.justplius.android.pavezikas.mangers.DownloadsManager;

import static lt.justplius.android.pavezikas.common.NetworkStateUtils.isConnected;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_DROPPING_ADDRESS_ID;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_INSERT_POST;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_LEAVING_ADDRESS_ID;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_ROUTE_ID;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_USER_ROUTE_PAIRED_GROUPS;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.getInstance;

/**
 * Prepares route and facebook groups related information to insert.
 * Also checks if all data is correct before post insert to DB.
 */
public class AddPostStep3Fragment extends Fragment
        implements LoaderManager.LoaderCallbacks {
    private static final String TAG = "AddPostStep3Fragment";

    private static final int REQUEST_LEAVING_ADDRESS = 0;
    private static final int REQUEST_DROPPING_ADDRESS = 1;
    private static final int REQUEST_UPDATE_USER_ROUTE_PAIRED_GROUPS = 2;
    public static final String ARG_LEAVING_ADDRESS = "lt.justplius.android.pavezikas.leaving_address";
    public static final String ARG_DROPPING_ADDRESS = "lt.justplius.android.pavezikas.dropping_address";
    public static final String ARG_SELECTED_GROUPS = "lt.justplius.android.pavezikas.selected_groups";
    public static final String NVP_USER_ID = "user_id";
    public static final String NVP_ROUTE_ID = "route_id";
    private static final String NVP_GROUPS_IDS = "groupIds[]";

    //private AddPostStep3Callback mCallbacks;

    private TextView mTextViewLeavingAddress;
    private TextView mTextViewDroppingAddress;
    private Post mPost;

    private TextView mTextViewSelectedGroups;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mGroups;
    private Spinner mSpinnerLeavingCity;
    private Spinner mSpinnerDroppingCity;
    private ProgressBar mProgressBar;
    private Button mButtonFacebookGroups;
    private Button mButtonStep3Continue;
    private Spinner mListViewRoutePairedGroups;

    public interface AddPostStep3Callback{
        public void onPostRouteSelected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPost = Post.getInstance();

        getActivity()
                .getSupportLoaderManager()
                .initLoader(LOADER_USER_ROUTE_PAIRED_GROUPS, null, this);

        if (savedInstanceState != null) {
            mGroups = savedInstanceState.getStringArrayList(ARG_SELECTED_GROUPS);
        } else {
            mGroups = new ArrayList<>();
        }
        mAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.simple_list_item,
                mGroups);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.add_post_step3, container, false);

        /*
        Allows to softly animate child views change: hide / display
        route-paired groups, their title
         */
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.add_post_step3_linearLayout);
        LayoutTransition layoutTransition = layout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        Button buttonLeavingAddress = (Button) v.findViewById(R.id.add_post_step3_button_address_leaving);
        buttonLeavingAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected(getActivity())) {
                    // Leaving address pick-up
                    LeavingAddressDialogFragment leavingAddressPicker = new LeavingAddressDialogFragment();
                    leavingAddressPicker.setTargetFragment(AddPostStep3Fragment.this, REQUEST_LEAVING_ADDRESS);
                    leavingAddressPicker.show(getActivity().getSupportFragmentManager(), "LeavingPicker");
                }
            }
        });
        Button buttonDroppingAddress = (Button) v.findViewById(R.id.add_post_step3_button_address_destination);
        buttonDroppingAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected(getActivity())) {
                    DroppingAddressDialogFragment droppingAddressPicker = new DroppingAddressDialogFragment();
                    droppingAddressPicker.setTargetFragment(AddPostStep3Fragment.this, REQUEST_DROPPING_ADDRESS);
                    droppingAddressPicker.show(getActivity().getSupportFragmentManager(), "DroppingPicker");
                }
            }
        });

        mTextViewLeavingAddress = (TextView) v.findViewById(R.id.add_post_step3_textView_address_leaving);
        if (mPost.isLeavingAddressSet()) {
            mTextViewLeavingAddress.setText(mPost.getLeavingAddress());
            mTextViewLeavingAddress.setVisibility(View.VISIBLE);
        }
        mTextViewDroppingAddress = (TextView) v.findViewById(R.id.add_post_step3_textView_destination_address);
        mTextViewDroppingAddress.setText(mPost.getDroppingAddress());
        if (mPost.isDroppingAddressSet()) {
            mTextViewDroppingAddress.setText(mPost.getDroppingAddress());
            mTextViewDroppingAddress.setVisibility(View.VISIBLE);
        }

        // Populate leaving and dropping address spinner with string adapter
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.cities_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerLeavingCity = (Spinner) v.findViewById(R.id.add_post_step3_spinner_city_leaving);
        mSpinnerLeavingCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isConnected(getActivity())) {
                    mPost.setRouteCity(
                            0, mSpinnerLeavingCity.getItemAtPosition(position).toString(), getActivity());
                    // Calls onRouteIdRetrieved() when Loader downloads data
                } else {
                    // Revert selection
                    mSpinnerLeavingCity.setSelection(adapter.getPosition(mPost.getCity(0)));
                }
                mTextViewLeavingAddress.setText(mPost.getLeavingAddress());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        mSpinnerDroppingCity = (Spinner) v.findViewById(R.id.add_post_step3_spinner_city_destination);
        mSpinnerDroppingCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isConnected(getActivity())) {
                    mPost.setRouteCity(
                            1, mSpinnerDroppingCity.getItemAtPosition(position).toString(), getActivity());
                    // Calls onRouteIdRetrieved() when Loader downloads data
                } else {
                    // Revert selection
                    mSpinnerDroppingCity.setSelection(adapter.getPosition(mPost.getCity(1)));
                }
                mTextViewDroppingAddress.setText(mPost.getDroppingAddress());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        mSpinnerLeavingCity.setAdapter(adapter);
        mSpinnerLeavingCity.setSelection(adapter.getPosition(mPost.getCity(0)));
        mSpinnerDroppingCity.setAdapter(adapter);
        mSpinnerDroppingCity.setSelection(adapter.getPosition(mPost.getCity(1)));

        mButtonFacebookGroups = (Button) v.findViewById(R.id.add_post_step3_button_select_groups);
        mButtonFacebookGroups.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected(getActivity()) && isGroupsDataCorrect()) {
                    UserFacebookGroupsDialogFragment groupsDialogFragment = new UserFacebookGroupsDialogFragment();
                    groupsDialogFragment.setTargetFragment(
                            AddPostStep3Fragment.this,
                            REQUEST_UPDATE_USER_ROUTE_PAIRED_GROUPS);
                    groupsDialogFragment.show(getActivity().getSupportFragmentManager(), "GroupsDialogFragment");
                }
            }

            private boolean isGroupsDataCorrect() {
                if (DownloadsManager.isBeingDownloaded(LOADER_ROUTE_ID)
                        || DownloadsManager.isBeingDownloaded(LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS)) {
                    Toast.makeText(
                            getActivity(),
                            R.string.message_route_id_is_being_downloaded,
                            Toast.LENGTH_LONG)
                            .show();
                    return false;
                } else if (mPost.getRouteID().equals("0")) {
                    Toast.makeText(
                            getActivity(),
                            R.string.message_wrong_route_id,
                            Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                return true;
            }
        });

        mTextViewSelectedGroups = (TextView) v.findViewById(R.id.add_post_step3_textView_selected_groups);

        mListViewRoutePairedGroups = (Spinner) v.findViewById(R.id.add_post_step3_listView_groups);
        mListViewRoutePairedGroups.setAdapter(mAdapter);
        if (mGroups.size() > 0) {
            showRoutePairedGroups();
        } else if (!mPost.getRouteID().equals("0")) {
            updateRoutePairedGroupsView();
        }

        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.post_insert_status_progressBar);
        mProgressBar.getIndeterminateDrawable()
                .setColorFilter(
                        getResources().getColor(R.color.post_shadow),
                        android.graphics.PorterDuff.Mode.MULTIPLY);

        mButtonStep3Continue = (Button) getActivity().findViewById(R.id.post_insert_button);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusManager.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusManager.getInstance().unregister(this);
    }

    private void updateRoutePairedGroupsView() {
        // Get model
        if (!mPost.getRouteID().equals("0")) {
            //mProgressBar.setVisibility(View.VISIBLE);
            // Post an event, informing to start loading user Route-Paired groups
            BusManager
                    .getInstance()
                    .post(new DownloadStartedEvent(LOADER_USER_ROUTE_PAIRED_GROUPS));

            getActivity()
                    .getSupportLoaderManager()
                    .getLoader(LOADER_USER_ROUTE_PAIRED_GROUPS)
                    .forceLoad();
        }
    }

    /**
     * Call this method via Otto Event Bus.
     * Handles ProgressBar, Spinners, TextViews visibility.
      */
    @Subscribe
    public void onDownloadStarted (DownloadStartedEvent event) {
        switch (DownloadStartedEvent.sLoaderId) {
            case LOADER_LEAVING_ADDRESS_ID:
                mTextViewLeavingAddress.setVisibility(View.GONE);
                break;
            case LOADER_DROPPING_ADDRESS_ID:
                mTextViewDroppingAddress.setVisibility(View.GONE);
                break;
            case LOADER_ROUTE_ID:
                hideRoutePairedGroups();
                disableViewItems();
                break;
            case LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS:
                hideRoutePairedGroups();
                disableViewItems();
                break;
            default:
                break;
        }
        if (DownloadsManager.isDownloading()) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Call this method via Otto Event Bus.
     * Handles ProgressBar, Spinners, TextViews visibility.
     */
    @Subscribe
    public void onDownloadFinished(DownloadFinishedEvent event) {
        switch (DownloadFinishedEvent.sLoaderId) {
            case LOADER_LEAVING_ADDRESS_ID:
                if (mPost.isLeavingAddressSet()) {
                    mTextViewLeavingAddress.setText(mPost.getLeavingAddress());
                    mTextViewLeavingAddress.setVisibility(View.VISIBLE);
                } else {
                    mTextViewLeavingAddress.setVisibility(View.GONE);
                }
                break;
            case LOADER_DROPPING_ADDRESS_ID:
                if (mPost.isDroppingAddressSet()) {
                    mTextViewDroppingAddress.setText(mPost.getDroppingAddress());
                    mTextViewDroppingAddress.setVisibility(View.VISIBLE);
                } else {
                    mTextViewDroppingAddress.setVisibility(View.GONE);
                }
                break;
            case LOADER_ROUTE_ID:
                if (!mPost.getRouteID().equals("0")) {
                    updateRoutePairedGroupsView();
                } else {
                    hideRoutePairedGroups();
                    enableViewItems();
                }
                break;
            case LOADER_USER_ROUTE_PAIRED_GROUPS:
                if (mGroups.size() > 0) {
                    showRoutePairedGroups();
                }
                enableViewItems();
                break;
            case LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS:
                updateRoutePairedGroupsView();
                break;
            case LOADER_INSERT_POST:
                if (!mPost.getPostId().equals("0")) {
                    Toast.makeText(
                            getActivity(),
                            getString(R.string.successful_post_insert),
                            Toast.LENGTH_LONG)
                            .show();
                    Intent intent = new Intent(getActivity(), PostsListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(
                            getActivity(),
                            getString(R.string.unsuccessful_post_insert),
                            Toast.LENGTH_LONG)
                            .show();
                }
                break;
            default:
                break;
        }
        if (!DownloadsManager.isDownloading()) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void enableViewItems() {
        mSpinnerLeavingCity.setEnabled(true);
        mSpinnerDroppingCity.setEnabled(true);
        mButtonFacebookGroups.setEnabled(true);
        mButtonStep3Continue.setEnabled(true);
    }

    private void disableViewItems() {
        mSpinnerLeavingCity.setEnabled(false);
        mSpinnerDroppingCity.setEnabled(false);
        mButtonFacebookGroups.setEnabled(false);
        mButtonStep3Continue.setEnabled(false);
    }

    private void hideRoutePairedGroups(){
        mTextViewSelectedGroups.setVisibility(View.GONE);
        mListViewRoutePairedGroups.setVisibility(View.GONE);
    }

    private void showRoutePairedGroups() {
        mListViewRoutePairedGroups.setVisibility(View.VISIBLE);
        mTextViewSelectedGroups.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LEAVING_ADDRESS:
                    // Update model
                    mPost.setLeavingAddress(data.getStringExtra(ARG_LEAVING_ADDRESS), getActivity());
                    break;
                case REQUEST_DROPPING_ADDRESS:
                    // Update model
                    mPost.setDroppingAddress(data.getStringExtra(ARG_DROPPING_ADDRESS), getActivity());
                    break;
                case REQUEST_UPDATE_USER_ROUTE_PAIRED_GROUPS:
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

                    // Post an event, informing to start user Route-Paired groups list
                    BusManager
                            .getInstance()
                            .post(new DownloadStartedEvent(LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS));

                    getActivity()
                            .getSupportLoaderManager()
                            .restartLoader(LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS, args, this);
                    break;
            }
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case LOADER_USER_ROUTE_PAIRED_GROUPS:
                return
                        getInstance(getActivity())
                        .createUserRoutePairedGroupsLoader();
            case LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS:
                // Revert object from String using Gson
                Type type = new TypeToken<ArrayList<BasicNameValuePair>>(){}.getType();
                String gson = bundle.getString(ARG_SELECTED_GROUPS);
                ArrayList<NameValuePair> pairs =
                        new Gson().fromJson(gson, type);
                return
                        getInstance(getActivity())
                        .createUpdateUserRoutePairedGroupsLoader(pairs);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (isVisible()) {
            switch (loader.getId()) {
                case LOADER_USER_ROUTE_PAIRED_GROUPS:
                    mGroups.clear();
                    mAdapter.notifyDataSetChanged();
                    // Skip an un-appropriate request result ('0')
                    if (!data.equals("   \"0\"\n")) {
                        try {
                            JSONArray jsonArray = new JSONArray(data.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mGroups.add(jsonArray.getString(i));
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "Error converting route paired groups result:", e);
                        }
                        // Post an event, informing that user Route-Paired groups
                        // list finished loading
                        BusManager
                                .getInstance()
                                .post(new DownloadFinishedEvent(LOADER_USER_ROUTE_PAIRED_GROUPS));
                    }
                    break;
                case LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS:
                    // Post an event, informing that user Route-Paired groups
                    // list update has finished executing
                    BusManager
                            .getInstance()
                            .post(new DownloadFinishedEvent(LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS));
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(ARG_SELECTED_GROUPS, mGroups);
        super.onSaveInstanceState(outState);
    }

    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof AddPostStep3Callback)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (AddPostStep3Callback) activity;
    }*/

    @Override
    public void onDetach() {
        super.onDetach();

        mProgressBar.setVisibility(View.INVISIBLE);

        /*// Reset the active callbacks interface.
        mCallbacks = null;*/
    }
}