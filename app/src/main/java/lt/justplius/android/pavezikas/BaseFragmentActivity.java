package lt.justplius.android.pavezikas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import lt.justplius.android.pavezikas.common.BackStackDoubleTapExit;
import lt.justplius.android.pavezikas.common.NetworkState;

import static lt.justplius.android.pavezikas.common.NetworkState.handleNoNetworkAvailable;
import static lt.justplius.android.pavezikas.common.NetworkState.sIsConnected;

public abstract class BaseFragmentActivity extends FragmentActivity {

    protected abstract Fragment createFragment();
    protected abstract Fragment createDetailsFragment();

    protected int getLayoutResourceId(){
        return R.layout.activity_post_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        // First time determine if device has any available network connection
        NetworkState.isNetworkAvailable(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (sIsConnected) {
            // Inflate prepared Fragment, if it was not inflated in onCreate()
            inflateFragment();
        } else {
            handleNoNetworkAvailable(this);
        }
    }

    // Inflate prepared to inflate Fragment
    protected void inflateFragment() {
        FragmentManager fm = getSupportFragmentManager();
        // Commit check if container for Fragment exists and it is free
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            // Inflate newly prepared Fragment
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    // Inflate prepared to inflate Fragment
    protected void inflateDetailsFragment() {
        FragmentManager fm = getSupportFragmentManager();
        // Commit check if container for Fragment exists and it is free
        Fragment fragment = fm.findFragmentById(R.id.fragment_details_container);
        if (fragment == null) {
            // Inflate newly prepared Fragment
            fragment = createDetailsFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_details_container, fragment)
                    .commit();
        }
    }

    // Disable on back button pressed event by default
    // and exit from app on back button pressed twice
    @Override
    public void onBackPressed() {
        // If the button has been pressed twice go to the main screen of phone
        BackStackDoubleTapExit.BackStackDoubleTapExit(this);
    }
}
