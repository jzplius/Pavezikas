package lt.justplius.android.pavezikas.posts;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.BaseSingleFragmentActivity;
import lt.justplius.android.pavezikas.common.SlidingMenuUtils;

/**
 * An activity representing a single Post detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PostsListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link PostDetailFragment}.
 */
public class PostDetailActivity extends BaseSingleFragmentActivity
        implements PostsListFragment.Callbacks {

    private static final String TAG = "PostDetailsActivity";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_details_container;
    }

    @Override
    protected Fragment createFragment() {
        return PostDetailFragment.newInstance(
                getIntent().getIntExtra(PostDetailFragment.ARG_POST_ID, 0));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(PostDetailFragment.ARG_POST_ID)) {
            // Configure the SlidingMenu
            new SlidingMenuUtils(this).configureSlidingMenu();

            // Show the Up button in the action bar.
            // Set custom actionbar
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(R.layout.actionbar);
            } else {
                Log.i(TAG, "Error in retrieving actionbar");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, PostsListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(int id) {
    }
}
