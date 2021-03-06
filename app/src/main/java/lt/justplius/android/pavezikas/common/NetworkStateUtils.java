package lt.justplius.android.pavezikas.common;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import lt.justplius.android.pavezikas.R;

/**
 * This is a static class for usage in these situations:
 * <p/>
 * - isNetworkAvailable(): performing a runtime network check on main
 * thread indicating whether phone is connected or not
 * <p/>
 * - hasActiveInternetConnection(): additionally checks if network
 * connection is active
 * <p/>
 * - CheckIfDeviceIsConnectedTask(): runs hasActiveInternetConnection()
 * in asynchronous task
 * <p/>
 * - handleIfNoNetworkAvailable(): invokes NetworkUnavailableActivity if
 * network is not present at runtime
 */
public class NetworkStateUtils {
    // Static members for usage of connection state handling
    public static boolean sIsConnected = false;
    public static boolean sIsConnectionBeingHandled = false;
    public static boolean sShouldInflateFragment = false;

    // Check if device has any available network connection
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        sIsConnected = activeNetworkInfo != null;
        return sIsConnected;
    }

    // Check if device is connected to active internet connection
    public static boolean hasActiveInternetConnection(Context context) {
        final String TAG = "NetworkStateUtils.java"; // Remove for release app

        if (isNetworkAvailable(context)) {
            Log.d(TAG, "Network available"); // Remove for release app
            try {
                // Connect to google server to check if network is active
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                Log.d(TAG, "Network is active"); // Remove for release app

                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.d(TAG, "Network is inactive", e); // Remove for release app
            }
        } else {
            Log.d(TAG, "No network available"); // Remove for release app
        }

        return false;
    }

    // Connection state change handler asynchronous task
    private static class CheckIfDeviceIsConnectedTask extends AsyncTask<Context, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Context... params) {
            return hasActiveInternetConnection(params[0]);
        }

        //save connection status state on static member
        protected void onPostExecute(Boolean result) {
            sIsConnected = result;
        }
    }

    // Convenience method checking if device is connected to
    // internet. It creates and executes a single network test
    // asynchronous task, based on it's context
    public static void checkIfDeviceIsConnected(Context context) {
        //check is committed in asynchronous task
        CheckIfDeviceIsConnectedTask cidict = new CheckIfDeviceIsConnectedTask();
        //execute asynchronous task
        cidict.execute(context);
    }

    // Method responsible for invoking NetworkUnavailableActivity if
    // network is not present at runtime
    public static void handleNoNetworkAvailable(Context context) {

        if (!sIsConnectionBeingHandled) {
            // Static variable for avoiding simultaneous invoking of
            // NetworkUnavailableActivity from different fragments
            // while inflating few of them at start
            sIsConnectionBeingHandled = true;
            // Start new activity when internet connection is not present
            Intent intent = new Intent(
                context.getApplicationContext(),
                NetworkUnavailableActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /** Method that returns isConnected value and if needed informs
     * with toast about no connection being present, used in
     * network-sensitive actions pre-check */
    public static boolean isConnected(Context context) {
        if (!sIsConnected) {
            Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        }
        return sIsConnected;
    }
}
