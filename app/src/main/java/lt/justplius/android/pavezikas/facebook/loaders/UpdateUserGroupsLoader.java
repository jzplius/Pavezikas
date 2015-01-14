package lt.justplius.android.pavezikas.facebook.loaders;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.DataLoader;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;

public class UpdateUserGroupsLoader extends DataLoader<Void> {
    private static final String TAG = "UpdateUserGroupsLoader";
    private final ArrayList<NameValuePair> mPairs;

    public UpdateUserGroupsLoader(Context context, ArrayList<NameValuePair> pairs) {
        super(context);
        mPairs = pairs;
    }

    // Data loader in background thread
    @Override
    public Void loadInBackground() {
        try {
            //noinspection unchecked
            new UpdateUserGroupsTask().execute(mPairs).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "updateUserGroups.php error", e);
        }
        return null;
    }

    // Task to update user information on DB
    private class UpdateUserGroupsTask extends AsyncTask<ArrayList<NameValuePair>, Void, Void> {
        private String mUrl;

        protected void onPreExecute() {
            mUrl = getContext().getString(R.string.url_update_user_groups);
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<NameValuePair>... nvp) {
            new HttpPostStringResponse(mUrl, nvp[0]);
            return null;
        }
    }
}
