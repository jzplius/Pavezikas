package lt.justplius.android.pavezikas.common;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.facebook.FacebookLoginFragment;

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
public abstract class BaseVezikasTwoFragmentActivity extends BaseTwoFragmentsActivity {

    private static final String TAG = "AddPostActivity";

    // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
    private boolean mTwoPane;

    protected abstract int setActionBarLayoutResourceId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);

        // Set custom actionbar
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(setActionBarLayoutResourceId());
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
