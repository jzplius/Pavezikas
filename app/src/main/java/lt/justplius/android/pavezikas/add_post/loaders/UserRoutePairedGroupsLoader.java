package lt.justplius.android.pavezikas.add_post.loaders;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.DataLoader;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;
import lt.justplius.android.pavezikas.facebook.FacebookLoginFragment;
import lt.justplius.android.pavezikas.post.PostManager;

public class UserRoutePairedGroupsLoader extends DataLoader<String> {
    private static final String TAG = "UserRoutePairedGroupsLoader";

    public UserRoutePairedGroupsLoader(Context context) {
        super(context);
    }

    // Data loader in background thread
    @Override
    public String loadInBackground() {
        String userId = PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .getString(FacebookLoginFragment.PREF_FB_ID, "");
        String routeId = PostManager.getPost(getContext()).getRouteID();
        try {
            return new SelectUserRoutePairedGroupsTask().execute(
                    new BasicNameValuePair("user_id", userId),
                    new BasicNameValuePair("route_id", routeId)
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "selectUserRoutePairedGroups.php error in downloading: ", e);
        }
        return null;
    }

    // Task to selected users' route and paired to it groups
    private class SelectUserRoutePairedGroupsTask extends AsyncTask<NameValuePair, Void, String> {
        private String mUrl;

        protected void onPreExecute () {
            mUrl = getContext().getString(R.string.url_select_user_route_paired_groups);
        }

        @Override
        protected String doInBackground(NameValuePair... pair) {
            // Add all passed pairs to ArrayList
            ArrayList<NameValuePair> pairs = new ArrayList<>();
            Collections.addAll(pairs, pair);
            return new HttpPostStringResponse(mUrl, pairs).returnJSON();
        }
    }
}
