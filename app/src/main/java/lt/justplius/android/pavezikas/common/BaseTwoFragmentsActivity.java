package lt.justplius.android.pavezikas.common;

import android.support.v4.app.Fragment;

import lt.justplius.android.pavezikas.R;

import static lt.justplius.android.pavezikas.common.NetworkStateUtils.handleNoNetworkAvailable;
import static lt.justplius.android.pavezikas.common.NetworkStateUtils.sIsConnected;

public abstract class BaseTwoFragmentsActivity extends BaseSingleFragmentActivity {

    public static final String TAG_DETAILS_FRAGMENT = "DetailsFragment";

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
                    .replace(setFragmentDetailsContainer(), fragment, TAG_DETAILS_FRAGMENT)
                    .commit();
        } else {
            handleNoNetworkAvailable(this);
        }
    }

    // Remove details Fragment from FragmentManager
    protected void removeDetailsFragment() {
        Fragment fragment
                = getSupportFragmentManager().findFragmentById(setFragmentDetailsContainer());
        getSupportFragmentManager()
            .beginTransaction()
            .remove(fragment)
            .commit();
    }
}
