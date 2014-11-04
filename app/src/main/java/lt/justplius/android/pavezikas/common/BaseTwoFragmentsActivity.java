package lt.justplius.android.pavezikas.common;

import android.support.v4.app.Fragment;

import lt.justplius.android.pavezikas.R;

import static lt.justplius.android.pavezikas.common.NetworkState.handleNoNetworkAvailable;
import static lt.justplius.android.pavezikas.common.NetworkState.sIsConnected;

public abstract class BaseTwoFragmentsActivity extends BaseSingleFragmentActivity {

    protected abstract Fragment createDetailsFragment(int selectionId);

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_post_list;
    }

    protected int setFragmentDetailsContainer() {
        return R.id.fragment_details_container;
    }

    // Inflate prepared to inflate Fragment
    protected void inflateDetailsFragment(int selectionId) {
        if (sIsConnected) {
            // Inflate newly prepared Fragment
            Fragment fragment = createDetailsFragment(selectionId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(setFragmentDetailsContainer(), fragment)
                    .commit();
        } else {
            handleNoNetworkAvailable(this);
        }
    }
}
