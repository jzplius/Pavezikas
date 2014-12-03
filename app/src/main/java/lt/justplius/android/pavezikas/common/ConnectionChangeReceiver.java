package lt.justplius.android.pavezikas.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class responds to internet state changes. When informed about state change it
 * asynchronously executes checkIfDeviceIsConnected(), to check devices' new state. *
 * checkIfDeviceIsConnected determines network state changes, such as "Network available",
 * "Network is active", "Network is inactive", "No network available".
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
    // Handle the network state change event by asynchronously executing checkIfDeviceIsConnected()
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkStateUtils.checkIfDeviceIsConnected(context);
    }
}
