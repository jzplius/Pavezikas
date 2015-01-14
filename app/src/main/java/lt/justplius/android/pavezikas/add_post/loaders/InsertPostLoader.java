package lt.justplius.android.pavezikas.add_post.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.add_post.Post;
import lt.justplius.android.pavezikas.common.DataLoader;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;
import lt.justplius.android.pavezikas.facebook.FacebookLoginFragment;

public class InsertPostLoader extends DataLoader {
    private static final String TAG = "InsertPostLoader";

    public InsertPostLoader(Context context) {
        super(context);
    }

    // Data loader in background thread
    @Override
    public String loadInBackground() {
        try {
            return new InsertPostTask().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "insertToPost.php error inserting post to DB: ", e);
        }
        return null;
    }

    // Task to insert post to DB
    private class InsertPostTask extends AsyncTask<Void, Void, String> {
        private String mUrl;
        private ArrayList<NameValuePair> mPairs;

        protected void onPreExecute () {
            Post post = Post.getInstance();
            mUrl = getContext().getString(R.string.url_insert_to_post);

            // Prepare DB fields
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getContext());
            String dbUserId = p.getString(FacebookLoginFragment.PREF_FB_ID, "");
            mPairs = new ArrayList<>();
            mPairs.add(new BasicNameValuePair("user_id", dbUserId));
            mPairs.add(new BasicNameValuePair("route_id", post.getRouteID()));
            mPairs.add(new BasicNameValuePair("phone_id", post.getPhoneId()));
            mPairs.add(new BasicNameValuePair("seats_available",
                    post.getSeatsAvailable()));
            mPairs.add(new BasicNameValuePair("message_id", post.getMessageId()));
            mPairs.add(new BasicNameValuePair("leaving_date", post.getLeavingDate()));
            mPairs.add(new BasicNameValuePair("leaving_time_from", post.getLeavingTimeFrom()));
            mPairs.add(new BasicNameValuePair("leaving_time_to", post.getLeavingTimeTo()));
            mPairs.add(new BasicNameValuePair("price", post.getPrice()));
            mPairs.add(new BasicNameValuePair("leaving_address_id",
                    post.getLeavingAddressId()));
            mPairs.add(new BasicNameValuePair("dropping_address_id",
                    post.getDroppingAddress()));
            mPairs.add(new BasicNameValuePair("post_type",
                    String.valueOf(post.getPostType().charAt(0))));
        }

        @Override
        protected final String doInBackground(Void... pairs) {
            return new HttpPostStringResponse(mUrl, mPairs).returnJSON();
        }
    }
}
