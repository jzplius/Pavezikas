package lt.justplius.android.pavezikas.facebook;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.display_posts.PostsListActivity;

import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_SELECT_USER_RATING;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_UPDATE_USER_GROUPS;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_UPDATE_USER_INFORMATION;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.getInstance;

public class FacebookLoginFragment extends Fragment
        implements LoaderManager.LoaderCallbacks {

    private static final String TAG = "FacebookLoginFragment";
    public static final String PREF_FB_ID = "FB_ID";
    public static final String PREF_FB_NAME_SURNAME = "FB_NAME_SURNAME";
    public static final String PREF_FB_RATING = "FB_RATING";
    private static final String ARG_USER_GROUPS = "user_groups";
    private static final java.lang.String ARG_USER_INFORMATION = "user_information";

    private UiLifecycleHelper mUiHelper;
    private SharedPreferences mSharedPreferences;
    private boolean mAreTasksFinished;

    public FacebookLoginFragment() {
        // Required empty public constructor
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private FacebookDialog.Callback mDialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.d(TAG, String.format("Error while logging in to facebook:  %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.d(TAG, "Successful login to Facebook");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAreTasksFinished = false;

        // Instantiate facebook lifecycle helper class
        mUiHelper = new UiLifecycleHelper(getActivity(), callback);
        mUiHelper.onCreate(savedInstanceState);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facebook_login, container, false);
        final LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(
                Arrays.asList("read_stream", "email", "user_groups", "user_birthday"));
        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @SuppressWarnings("unchecked")
            @Override
            public void onUserInfoFetched(GraphUser user) {
                Session session = Session.getActiveSession();
                // If authentication is successful update and retrieve
                // user data from DB and save information to shared preferences
                if (session != null && session.isOpened() && user != null && isVisible()) {

                    String id = user.getId();
                    String name_surname = user.getFirstName() + " " + user.getLastName();
                    String email = user.getProperty("email").toString();

                    mSharedPreferences.edit()
                            .putString(PREF_FB_ID, id)
                            .putString(PREF_FB_NAME_SURNAME, name_surname)
                            .apply();

                    ArrayList<NameValuePair> pairs = new ArrayList<>();
                    pairs.add(new BasicNameValuePair("id", id));
                    pairs.add(new BasicNameValuePair("name_surname", name_surname));
                    pairs.add(new BasicNameValuePair("email", email));

                    // Convert ArrayList<NVP> to it's String representation
                    String jsonString = new Gson().toJson(pairs);

                    // Update groups list
                    Bundle args = new Bundle();
                    args.putString(ARG_USER_INFORMATION, jsonString);
                    getActivity()
                            .getSupportLoaderManager()
                            .restartLoader(
                                    LOADER_UPDATE_USER_INFORMATION,
                                    args,
                                    FacebookLoginFragment.this);
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        mUiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUiHelper.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mUiHelper.onStop();
    }


    @SuppressWarnings("unchecked")
    private void onSessionStateChange(@SuppressWarnings("UnusedParameters") Session session,
                                      SessionState state, Exception exception) {
        if (exception instanceof FacebookOperationCanceledException ||
                exception instanceof FacebookAuthorizationException) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.facebook_failed_to_authenticate)
                    .setMessage(R.string.facebook_failed_to_authenticate)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
        if (state.isOpened()) {
            Log.i(TAG, "Facebook session is opened");
            // Determine if task has been created
            if (!mAreTasksFinished) {
                getActivity()
                        .getSupportLoaderManager()
                        .restartLoader(
                                LOADER_SELECT_USER_RATING,
                                null,
                                this);

                // Make the API call to get user groups
                new Request(
                        Session.getActiveSession(),
                        "/me/groups",
                        null,
                        HttpMethod.GET,
                        new Request.Callback() {
                            @Override
                            public void onCompleted(Response response) {
                                if (isVisible()) {
                                    // Retrieve Facebook user groups data from GraphObject
                                    try {
                                        // Data to be passed to DB
                                        ArrayList<NameValuePair> pairs = new ArrayList<>();
                                        NameValuePair pair;
                                        pair = new BasicNameValuePair("user_id",
                                                mSharedPreferences.getString(PREF_FB_ID, "0"));
                                        pairs.add(pair);

                                        // Data to contain response
                                        JSONObject group;
                                        JSONArray groups = response.getGraphObject()
                                                .getInnerJSONObject()
                                                .getJSONArray("data");
                                        for (int i = 0; i < groups.length(); i++) {
                                            group = groups.getJSONObject(i);
                                            // Get name and id of a group
                                            pair = new BasicNameValuePair("id[]", group.getString("id"));
                                            pairs.add(pair);
                                            pair = new BasicNameValuePair("name[]", group.getString("name"));
                                            pairs.add(pair);
                                        }
                                        // Convert ArrayList<NVP> to it's String representation
                                        String jsonString = new Gson().toJson(pairs);

                                        // Update groups list
                                        Bundle args = new Bundle();
                                        args.putString(ARG_USER_GROUPS, jsonString);
                                        getActivity()
                                                .getSupportLoaderManager()
                                                .restartLoader(
                                                        LOADER_UPDATE_USER_GROUPS,
                                                        args,
                                                        FacebookLoginFragment.this);

                                    } catch (JSONException e) {
                                        Log.e(TAG, "Error retrieving user groups: ", e);
                                    }
                                }
                            }
                        }
                ).executeAsync();
            }
        } else if (state.isClosed()) {
            Log.i(TAG, "Facebook session is closed");
            mAreTasksFinished = false;

            session.closeAndClearTokenInformation();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUiHelper.onActivityResult(requestCode, resultCode, data, mDialogCallback);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Type type;
        ArrayList<NameValuePair> pairs;
        switch (id) {
            case LOADER_UPDATE_USER_GROUPS:
                // Convert JSON representation to real object using GSON
                type = new TypeToken<ArrayList<BasicNameValuePair>>() {
                }.getType();
                pairs = new Gson().fromJson(args.getString(ARG_USER_GROUPS), type);
                return getInstance(getActivity())
                        .createUpdateUserGroupsLoader(pairs);
            case LOADER_UPDATE_USER_INFORMATION:
                // Convert JSON representation to real object using GSON
                type = new TypeToken<ArrayList<BasicNameValuePair>>() {
                }.getType();
                pairs = new Gson().fromJson(args.getString(ARG_USER_INFORMATION), type);
                return getInstance(getActivity())
                        .createUpdateUserInformationLoader(pairs);
            case LOADER_SELECT_USER_RATING:
                return getInstance(getActivity())
                        .createSelectUserRatingLoader();
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (isVisible()) {
            switch (loader.getId()) {
                case LOADER_SELECT_USER_RATING:
                    try {
                        JSONArray jsonArray = new JSONArray(data.toString());
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        mSharedPreferences
                                .edit()
                                .putFloat(PREF_FB_RATING, Float.valueOf(jsonObject.getString("rating")))
                                .apply();

                        // Start main activity without possibility to return to this activity
                        Intent intent = new Intent(getActivity(), PostsListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity()
                                .finish();

                        mAreTasksFinished = true;

                    } catch (JSONException e) {
                        Log.e(TAG, "Error retrieving user rating: ", e);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUiHelper.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mUiHelper.onDestroy();
    }
}
