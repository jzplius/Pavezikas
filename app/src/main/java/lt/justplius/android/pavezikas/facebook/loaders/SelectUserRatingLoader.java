package lt.justplius.android.pavezikas.facebook.loaders;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.DataLoader;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;

import static lt.justplius.android.pavezikas.facebook.FacebookLoginFragment.PREF_FB_ID;

public class SelectUserRatingLoader extends DataLoader<String> {
    private static final String TAG = "SelectUserRatingLoader";

    public SelectUserRatingLoader(Context context) {
        super(context);
    }

    // Data loader in background thread
    @Override
    public String loadInBackground() {
        try {
            //noinspection unchecked
            return  new SelectUserRatingTask().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "selectUserRating.php error", e);
        }
        return null;
    }

    // Retrieve latest user data from DB
    private class SelectUserRatingTask extends AsyncTask<Void, Void, String> {
        private String mUrl;

        protected void onPreExecute() {
            mUrl = getContext().getString(R.string.url_select_user_rating);
        }

        @Override
        protected final String doInBackground(Void... args) {
            String userId = PreferenceManager
                .getDefaultSharedPreferences(getContext())
                 .getString(PREF_FB_ID, "0");
            ArrayList<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("user_id",userId));
            return new HttpPostStringResponse(mUrl, pairs).returnJSON();
        }
    }
}
