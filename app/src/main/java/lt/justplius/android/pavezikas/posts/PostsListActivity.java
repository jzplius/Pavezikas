package lt.justplius.android.pavezikas.posts;

import android.content.Intent;
import android.support.v4.app.Fragment;

import lt.justplius.android.pavezikas.common.BaseVezikasTwoFragmentActivity;
import lt.justplius.android.pavezikas.R;

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
public class PostsListActivity extends BaseVezikasTwoFragmentActivity
        implements PostsListFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new PostsListFragment();
    }

    @Override
    protected Fragment createDetailsFragment(int selectionId) {
        return PostDetailFragment.newInstance(selectionId);
    }

    @Override
    protected int setActionBarLayoutResourceId() {
        return R.layout.actionbar;
    }

    /**
     * Callback method from {@link PostsListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int postId) {
        if (isTwoPane()) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            inflateDetailsFragment(postId);
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, PostDetailActivity.class);
            detailIntent.putExtra(PostDetailFragment.ARG_POST_ID, postId);
            startActivity(detailIntent);
        }
    }
}
