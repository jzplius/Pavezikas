package lt.justplius.android.pavezikas.facebook;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;
import lt.justplius.android.pavezikas.display_posts.PostsListActivity;

public class FacebookLoginFragment extends Fragment {
    private static final String TAG = "FacebookLogin";
    public static final String PREF_FB_ID = "FB_ID";
    public static final String PREF_FB_NAME_SURNAME = "FB_NAME_SURNAME";
    public static final String PREF_FB_RATING = "FB_RATING";

    private UiLifecycleHelper mUiHelper;
    private SelectUserRatingTask mSelectUserRatingTask = null;
    private SharedPreferences mSharedPreferences;

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
            Log.d(TAG, String.format("Error in login: %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.d(TAG, "Successful login!");
        }
    };

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
            Log.i(TAG, "Logged in...");
            // Determine if task has been created
            if (mSelectUserRatingTask == null) {
                mSelectUserRatingTask = new SelectUserRatingTask();
                ArrayList<NameValuePair> nvp = new ArrayList<> ();
                nvp.add(new BasicNameValuePair("id", mSharedPreferences.getString(PREF_FB_ID, "0")));
                mSelectUserRatingTask.execute(nvp);

                // Make the API call to get user groups
                new Request(
                        Session.getActiveSession(),
                        "/me/groups",
                        null,
                        HttpMethod.GET,
                        new Request.Callback() {
                            @Override
                            public void onCompleted(Response response) {
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
                                    // Update groups list
                                    new UpdateUserGroupsTask().execute(pairs);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "Error retrieving user groups: " + e.toString());
                                }
                            }
                        }
                ).executeAsync();
            }
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            mSelectUserRatingTask = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate facebook lifecycle helper class
        mUiHelper = new UiLifecycleHelper(getActivity(), callback);
        mUiHelper.onCreate(savedInstanceState);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facebook_login, container, false);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
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
                if (session != null && session.isOpened() && user != null) {

                    String id = user.getId();
                    String name_surname = user.getFirstName() + " " + user.getLastName();
                    String email = user.getProperty("email").toString();

                    mSharedPreferences.edit()
                            .putString(PREF_FB_ID, id)
                            .putString(PREF_FB_NAME_SURNAME, name_surname)
                            .apply();

                    ArrayList<NameValuePair> nvp = new ArrayList<>();
                    nvp.add(new BasicNameValuePair("id", id));
                    nvp.add(new BasicNameValuePair("name_surname", name_surname));
                    nvp.add(new BasicNameValuePair("email", email));
                    new UpdateUserInformationTask().execute(nvp);
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

    @SuppressWarnings("NullableProblems")
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUiHelper.onActivityResult(requestCode, resultCode, data, mDialogCallback);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUiHelper.onDestroy();
    }

    // Update user information on DB
    private class UpdateUserInformationTask extends AsyncTask<ArrayList<NameValuePair>, Void, Void> {
        private String mUrl;

        protected void onPreExecute () {
            mUrl = getString(R.string.url_update_user);
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<NameValuePair>... nvp) {
            new HttpPostStringResponse(mUrl, nvp[0]);
            return null;
        }
    }

    // Retrieve latest user data from DB
    private class SelectUserRatingTask extends AsyncTask<ArrayList<NameValuePair>, Void, String> {
        private String mUrl;

        protected void onPreExecute () {
            mUrl = getString(R.string.url_select_user_rating);
        }

        @SafeVarargs
        @Override
        protected final String doInBackground(ArrayList<NameValuePair>... nvp) {
            return new HttpPostStringResponse(mUrl, nvp[0]).returnJSON();
        }

        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                mSharedPreferences
                        .edit()
                        .putFloat(PREF_FB_RATING, Float.valueOf(jsonObject.getString("rating")))
                        .apply();

                // Start main activity without possibility to return to this activity
                Intent intent = new Intent(getActivity(), PostsListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();

            } catch (JSONException e) {
                Log.e(TAG, "Error retrieving user rating: " + e.toString());
            }
        }
    }

    // TODO download in background thread
    // Update user information on DB
    private class UpdateUserGroupsTask extends AsyncTask<ArrayList<NameValuePair>, Void, Void> {
        private String mUrl;

        protected void onPreExecute () {
            mUrl = getString(R.string.url_update_user_groups);
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<NameValuePair>... nvp) {
            new HttpPostStringResponse(mUrl, nvp[0]);
            return null;
        }
    }
}
