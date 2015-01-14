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
public class MessageIdLoader extends DataLoader<String> {

    private static final String TAG = "MessageIdLoader";
    private final String mMessage;

    public MessageIdLoader(Context context, String message) {
        super(context);
        mMessage = message;
    }

    @Override
    public String loadInBackground() {
        try {
            return new GetMessageIdTask()
                    .execute(mMessage)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "selectFromMessage.php error in downloading: ", e);
        }
        return null;
    }

    // Task to insert message to DB and retrieve it's id
    private class GetMessageIdTask extends AsyncTask<String, Void, String> {

        private ArrayList<NameValuePair> mPairs;
        private String mUrl;

        protected void onPreExecute() {
            mPairs = new ArrayList<>();
            mUrl = getContext().getString(R.string.url_select_from_message);
        }

        @Override
        protected String doInBackground(String... params) {
            // Check whether such message exists, if it does not exist insert
            //  new one into db and return its' ID
            mPairs.add(new BasicNameValuePair("message", params[0]));
            return new HttpPostStringResponse(mUrl, mPairs).returnJSON();
        }
    }
}
