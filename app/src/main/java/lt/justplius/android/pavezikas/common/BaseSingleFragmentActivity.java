package lt.justplius.android.pavezikas.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import lt.justplius.android.pavezikas.R;

import static lt.justplius.android.pavezikas.common.NetworkState.handleNoNetworkAvailable;
import static lt.justplius.android.pavezikas.common.NetworkState.sIsConnected;

public abstract class BaseSingleFragmentActivity extends FragmentActivity {

    protected abstract Fragment createFragment();

    protected int getLayoutResourceId(){
        return R.layout.activity_post_singlepane;
    }

    protected int getFragmentContainerId(){
        return R.id.fragment_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        // First time determine if device has any available network connection
        NetworkState.isNetworkAvailable(this);
        // Inflate prepared Fragment, if it has network available
        inflateFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Inflate prepared Fragment, if it was not inflated in onCreate()
        // after no network situation handled
        inflateFragment();
    }

    // Inflate prepared to inflate Fragment
    protected void inflateFragment() {
        if (sIsConnected) {
            FragmentManager fm = getSupportFragmentManager();
            // Commit check if container for Fragment exists and it is free
            Fragment fragment = fm.findFragmentById(getFragmentContainerId());
            if (fragment == null) {
                // Inflate newly prepared Fragment
                fragment = createFragment();
                fm.beginTransaction()
                        .replace(getFragmentContainerId(), fragment)
                        .commit();
            }
        } else {
            handleNoNetworkAvailable(this);
        }
    }
}