package lt.justplius.android.pavezikas.add_post.loaders;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.add_post.Post;
import lt.justplius.android.pavezikas.common.DataLoader;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;

/**
 * Created by JUSTPLIUS on 2014.12.15.
 */
public class RouteIdLoader extends DataLoader<String> {

    private static final String TAG = "RunIdLoader";

    public RouteIdLoader(Context context) {
        super(context);
    }

    @Override
    public String loadInBackground() {
        try {
            return new GetRouteIdTask()
                    .execute(Post.getInstance().getRoute())
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "selectFromRoute.php error in downloading: ", e);
            return null;
        }
    }

    // Task to insert route to DB and retrieve it's id
    private class GetRouteIdTask extends AsyncTask<String, Void, String> {

        private ArrayList<NameValuePair> mPairs;
        private String mUrl;

        protected void onPreExecute() {
            mPairs = new ArrayList<>();
            mUrl = getContext().getString(R.string.url_select_from_route);
        }

        @Override
        protected String doInBackground(String... params) {
            // Check whether such route exists, if it does not,
            // insert new one into DB and retrieve it's ID
            mPairs.add(new BasicNameValuePair("route", params[0]));
            return new HttpPostStringResponse(mUrl, mPairs).returnJSON();
        }
    }
}
