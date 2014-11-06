package lt.justplius.android.pavezikas.add_post;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.BackStackDoubleTapExit;
import lt.justplius.android.pavezikas.common.BaseTwoFragmentsActivity;
import lt.justplius.android.pavezikas.common.SlidingMenuUtils;
import lt.justplius.android.pavezikas.facebook_login.FacebookLoginFragment;
import lt.justplius.android.pavezikas.posts.PostDetailActivity;
import lt.justplius.android.pavezikas.posts.PostDetailFragment;
import lt.justplius.android.pavezikas.posts.PostsListFragment;

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
public class AddPostActivity extends BaseTwoFragmentsActivity
        implements PostsListFragment.Callbacks {

    private static final String TAG = "AddPostActivity";

    // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
    private boolean mTwoPane;

    @Override
    protected Fragment createFragment() {
        return new PostsListFragment();
    }

    @Override
    protected Fragment createDetailsFragment(int selectionId) {
        return PostDetailFragment.newInstance(selectionId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);

        // Set custom actionbar
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar);
        } else {
            Log.i(TAG, "Error in retrieving actionbar");
        }

        // Configure the SlidingMenu
        new SlidingMenuUtils(this).setSlidingMenu();

        // Set content to SlidingMenu's views
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        TextView textViewNameSurname = (TextView) findViewById(R.id.menu_list_name_surname);
        textViewNameSurname.setText(
                sp.getString(FacebookLoginFragment.PREF_FB_NAME_SURNAME, "Vardas Pavardė"));

        ProfilePictureView profilePictureView = (ProfilePictureView)
                findViewById(R.id.menu_list_profile_picture);
        profilePictureView.setProfileId(sp.getString(FacebookLoginFragment.PREF_FB_ID, "0"));

        RatingBar ratingBar = (RatingBar) findViewById(R.id.menu_list_rating_bar);
        ratingBar.setRating(sp.getFloat(FacebookLoginFragment.PREF_FB_RATING, 0));

        if (findViewById(R.id.fragment_details_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    /**
     * Callback method from {@link lt.justplius.android.pavezikas.posts.PostsListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int postId) {
        if (mTwoPane) {
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

    // Disable on back button pressed event by default
    // and exit from app on back button pressed twice
    @Override
    public void onBackPressed() {
        // If the button has been pressed twice go to the main screen of phone
        BackStackDoubleTapExit.BackStackDoubleTapExit(this);
    }

    public boolean isTwoPane(){
        return mTwoPane;
    }
}
