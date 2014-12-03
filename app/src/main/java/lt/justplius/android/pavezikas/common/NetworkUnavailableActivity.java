package lt.justplius.android.pavezikas.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import lt.justplius.android.pavezikas.R;

import static lt.justplius.android.pavezikas.common.NetworkStateUtils.*;

/**
 * This activity shows a view that reminds to turn on internet connection.
 * If internet is present user pushes button to return to previous activity.
 */
public class NetworkUnavailableActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_network_unavailable);

        // Get instances of view objects
        Button buttonCheckConnection = (Button) findViewById(R.id.button_check_connection);

        // Handle responses to events on view objects
        buttonCheckConnection.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Return to previous activity if internet connection is present
                if (sIsConnected) {
                    sIsConnectionBeingHandled = false;
                    finish();
                }
            }

        });
    }

    // Disable on back button pressed event by default
    // and exit from app on back button pressed twice
    @Override
    public void onBackPressed() {
        // If the button has been pressed twice go to the main screen of phone
        BackStackDoubleTapExit.BackStackDoubleTapExit(this);
    }
}
