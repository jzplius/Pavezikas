package lt.justplius.android.pavezikas.add_post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.BaseVezikasTwoFragmentActivity;

import static lt.justplius.android.pavezikas.common.NetworkState.handleNoNetworkAvailable;
import static lt.justplius.android.pavezikas.common.NetworkState.sIsConnected;

/**
 * An activity representing a list of Posts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link lt.justplius.android.pavezikas.posts.PostDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link lt.justplius.android.pavezikas.posts.PostsListFragment} and the item details
 * (if present) is a {@link lt.justplius.android.pavezikas.posts.PostDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link lt.justplius.android.pavezikas.posts.PostsListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class AddPostActivity extends BaseVezikasTwoFragmentActivity
implements AddPostStep1Fragment.AddPostStep1Callback,
        AddPostStep2Fragment.AddPostStep2Callback,
        AddPostStep3Fragment.AddPostStep3Callback{
    private static final String ARG_CURRENT_STEP = "current_step";
    private int mCurrentStep = 1;

    //TODO check if device is two pane


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentStep = savedInstanceState.getInt(ARG_CURRENT_STEP);
        }
    }

    @Override
    protected Fragment createFragment() {
        switch (mCurrentStep) {
            case 3:
                return new AddPostStep2Fragment();
            default:
                return new AddPostStep1Fragment();
        }
    }

    @Override
    protected Fragment createDetailsFragment(int selectionId) {
        switch (mCurrentStep) {
            case 3:
                return new AddPostStep3Fragment();
            default:
                return new AddPostStep2Fragment();
        }
    }

    @Override
    protected int setActionBarLayoutResourceId() {
        return R.layout.actionbar;
    }

    // Inflate prepared to inflate Fragment
    @Override
    protected void inflateFragment() {
        if (sIsConnected) {
            FragmentManager fm = getSupportFragmentManager();
            // Inflate newly prepared Fragment
            // without checking if it has already been inflated
            Fragment fragment = createFragment();
            fm.beginTransaction()
                .replace(getFragmentContainerId(), fragment)
                .commit();
        } else {
            handleNoNetworkAvailable(this);
        }
    }

    protected int getCurrentStep() {
        return mCurrentStep;
    }

    @Override
    public void onPostTypeSelected() {
        if (mCurrentStep != 2) {
            mCurrentStep = 2;
            inflateDetailsFragment(0);
        }
    }

    @Override
    public void onInformationSelected() {
        mCurrentStep = 3;
        inflateFragment();
        inflateDetailsFragment(0);
    }

    @Override
    public void onPostRouteSelected() {

    }

    @Override
    public void onBackPressed() {
        mCurrentStep--;
        switch (mCurrentStep) {
            case 0:
                finish();
                break;
            case 1:
                removeDetailsFragment();
                break;
            default:
                inflateFragment();
                inflateDetailsFragment(0);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_CURRENT_STEP, mCurrentStep);
        super.onSaveInstanceState(outState);
    }
}
