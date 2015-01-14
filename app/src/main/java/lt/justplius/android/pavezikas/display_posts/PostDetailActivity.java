package lt.justplius.android.pavezikas.display_posts;

import android.support.v4.app.Fragment;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.BaseVezikasSingleFragmentActivity;

/**
 * An activity representing a single Post detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PostsListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link PostDetailFragment}.
 */
public class PostDetailActivity extends BaseVezikasSingleFragmentActivity
        implements PostsListFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return PostDetailFragment.newInstance(
                getIntent().getIntExtra(PostDetailFragment.ARG_POST_ID, 0));
    }

    @Override
    public void onItemSelected(int id) {
    }

    @Override
    public void onBackPressed() {
        if (getSlidingMenu().isMenuShowing()) {
            getSlidingMenu().showContent();
        } else {
            finish();
        }
    }
}
