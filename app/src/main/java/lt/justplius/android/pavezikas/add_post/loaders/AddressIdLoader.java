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

public class AddressIdLoader extends DataLoader<String> {
    private static final String TAG = "AddressIdLoader";
    private final String mAddress;

    public AddressIdLoader(Context context, String address) {
        super(context);
        mAddress = address;
    }

    // Data loader in background thread
    @Override
    public String loadInBackground() {
        try {
            return new GetAddressIdTask().execute(mAddress).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "selectFromAddress.php error in downloading", e);
        }
        return null;
    }

    // Task to insert address to DB and retrieve it's id
    private class GetAddressIdTask extends AsyncTask<String, Void, String> {
        private ArrayList<NameValuePair> mPairs;
        private String mUrl;

        protected void onPreExecute () {
            mPairs = new ArrayList<>();
            mUrl = getContext().getString(R.string.url_select_from_address);
        }

        @Override
        protected String doInBackground(String... params) {
            mPairs.add(new BasicNameValuePair("address", params[0]));
            return new HttpPostStringResponse(mUrl, mPairs).returnJSON();
        }
    }
}
