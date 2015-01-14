package lt.justplius.android.pavezikas.display_posts;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

import lt.justplius.android.pavezikas.common.BaseVezikasTwoFragmentActivity;
import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.mangers.BusManager;
import lt.justplius.android.pavezikas.display_posts.events.DownloadPostsEvent;

/**
 * An activity representing a list of Posts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link lt.justplius.android.pavezikas.display_posts.PostDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link lt.justplius.android.pavezikas.display_posts.PostsListFragment} and the item details
 * (if present) is a {@link lt.justplius.android.pavezikas.display_posts.PostDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link lt.justplius.android.pavezikas.display_posts.PostsListFragment.Callbacks} interface
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
        return R.layout.actionbar_posts;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure activity-specific action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            View view = actionBar.getCustomView();

            if (view != null) {
                // OnClick show menu
                ImageButton imageButtonMenu = (ImageButton) view.findViewById(R.id.action_bar_main_menu);
                imageButtonMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSlidingMenu().showMenu();
                    }
                });

                // OnClick reload posts
                final ImageButton imageButtonRefresh = (ImageButton) view.findViewById(R.id.action_bar_refresh);
                imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Post an event, informing that action was clicked
                        BusManager.getInstance().post(new DownloadPostsEvent());
                        // Animate rotation for 1 second
                        RotateAnimation rotateAnimation = new RotateAnimation(
                                0,
                                359,
                                Animation.RELATIVE_TO_SELF,
                                0.5f,
                                Animation.RELATIVE_TO_SELF,
                                0.5f);
                        rotateAnimation.setRepeatMode(Animation.RESTART);
                        rotateAnimation.setDuration(500);
                        imageButtonRefresh.startAnimation(rotateAnimation);
                    }
                });

                // OnClick do nothing
                ImageButton imageButtonHome = (ImageButton) view.findViewById(R.id.action_bar_home);
                imageButtonHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BusManager.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusManager.getInstance().unregister(this);
    }

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
