package lt.justplius.android.pavezikas.facebook.loaders;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.DataLoader;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;

public class UpdateUserInformationLoader extends DataLoader<Void> {
    private static final String TAG = "UpdateUserInformationLoader";
    private final ArrayList<NameValuePair> mPairs;

    public UpdateUserInformationLoader(Context context, ArrayList<NameValuePair> pairs) {
        super(context);
        mPairs = pairs;
    }

    // Data loader in background thread
    @Override
    public Void loadInBackground() {
        try {
            //noinspection unchecked
            new UpdateUserInformationTask().execute(mPairs).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "updateUser.php error", e);
        }
        return null;
    }

    // Update user information on DB
    private class UpdateUserInformationTask extends AsyncTask<ArrayList<NameValuePair>, Void, Void> {
        private String mUrl;

        protected void onPreExecute() {
            mUrl = getContext().getString(R.string.url_update_user);
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<NameValuePair>... nvp) {
            new HttpPostStringResponse(mUrl, nvp[0]);
            return null;
        }
    }
}
