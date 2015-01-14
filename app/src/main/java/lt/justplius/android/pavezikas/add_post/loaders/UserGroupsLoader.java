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
import lt.justplius.android.pavezikas.add_post.Post;
import lt.justplius.android.pavezikas.common.DataLoader;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;
import lt.justplius.android.pavezikas.facebook.FacebookLoginFragment;

public class UserGroupsLoader extends DataLoader<String> {
    private static final String TAG = "UserGroupsLoader";

    public UserGroupsLoader(Context context) {
        super(context);
    }

    // Data loader in background thread
    @Override
    public String loadInBackground() {
        String userId = PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .getString(FacebookLoginFragment.PREF_FB_ID, "");
        String routeId = Post.getInstance().getRouteID();
        try {
            return new GetUserGroupsTask().execute(
                    new BasicNameValuePair("user_id", userId),
                    new BasicNameValuePair("route_id", routeId)
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "selectFromUserGroups.php error in downloading: ", e);
        }
        return null;
    }

    // Task to insert address to DB and retrieve it's id
    private class GetUserGroupsTask extends AsyncTask<NameValuePair, Void, String> {
        private String mUrl;

        @Override
        protected void onPreExecute() {
            mUrl = getContext().getString(R.string.url_select_from_user_groups);
        }

        @Override
        protected String doInBackground(NameValuePair... pair) {
            ArrayList<NameValuePair> pairs = new ArrayList<>();
            Collections.addAll(pairs, pair);
            return new HttpPostStringResponse(mUrl, pairs).returnJSON();
        }
    }
}
