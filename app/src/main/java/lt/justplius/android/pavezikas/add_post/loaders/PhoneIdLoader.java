package lt.justplius.android.pavezikas.add_post.loaders;

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

/**
 * Created by JUSTPLIUS on 2014.12.15.
 */
public class PhoneIdLoader extends DataLoader<String> {

    private static final String TAG = "PhoneIdLoader";
    private final String mPhone;

    public PhoneIdLoader(Context context, String route) {
        super(context);
        mPhone = route;
    }

    @Override
    public String loadInBackground() {
        try {
            return new GetPhoneIdTask()
                    .execute(mPhone)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "selectFromRoute.php error in downloading: ", e);
        }
        return null;
    }

    // Task to insert route to DB and retrieve it's id
    private class GetPhoneIdTask extends AsyncTask<String, Void, String> {

        private ArrayList<NameValuePair> mPairs;
        private String mUrl;

        protected void onPreExecute() {
            mPairs = new ArrayList<>();
            mUrl = getContext().getString(R.string.url_select_from_phone);
        }

        @Override
        protected String doInBackground(String... params) {
            // Check whether such phone exists, if it does not exist insert
            //  new one into db and return its' ID
            mPairs.add(new BasicNameValuePair("phone", params[0]));
            return new HttpPostStringResponse(mUrl, mPairs).returnJSON();
        }
    }
}
