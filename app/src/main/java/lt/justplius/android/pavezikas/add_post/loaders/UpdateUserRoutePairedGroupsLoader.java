package lt.justplius.android.pavezikas.add_post.loaders;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.DataLoader;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;

public class UpdateUserRoutePairedGroupsLoader extends DataLoader {

    private ArrayList<NameValuePair> mPairs;

    public UpdateUserRoutePairedGroupsLoader(Context context, ArrayList<NameValuePair> pairs) {
        super(context);
        mPairs = pairs;
    }

    // Data loader in background thread
    @Override
    public Void loadInBackground() {
        //noinspection unchecked
        new UpdateUserRoutePairedGroupsTask().execute(
                mPairs);
        return null;
    }

    // Task to update users' route and groups in DB
    private class UpdateUserRoutePairedGroupsTask extends AsyncTask<ArrayList<NameValuePair>, Void, Void> {
        private String mUrl;

        protected void onPreExecute () {
            mUrl = getContext().getString(R.string.url_update_user_route_paired_groups);
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<NameValuePair>... pairs) {
            new HttpPostStringResponse(mUrl, pairs[0]);
            return null;
        }
    }
}
