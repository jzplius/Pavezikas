package lt.justplius.android.pavezikas.common;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.facebook.FacebookLoginActivity;
import lt.justplius.android.pavezikas.facebook.FacebookLoginFragment;
import lt.justplius.android.pavezikas.display_posts.PostsListActivity;

import static lt.justplius.android.pavezikas.facebook.FacebookLoginFragment.PREF_FB_NAME_SURNAME;

/**
 * Created by JUSTPLIUS on 2014.11.03.
 */
public class SlidingMenuUtils {
    Activity mActivity;
    public SlidingMenuUtils(Activity activity) {
        mActivity = activity;
    }

    public SlidingMenu configureSlidingMenu(){
        // Configure the SlidingMenu
        SlidingMenu menu = new SlidingMenu(mActivity);
        menu.setMode(SlidingMenu.RIGHT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowDrawable(R.drawable.shadowright);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setBehindWidthRes(R.dimen.slidingmenu_behind_width);
        menu.setFadeEnabled(true);
        menu.setFadeDegree(0.5f);
        menu.attachToActivity(mActivity, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.menu_list);

        configureView();

        return menu;
    }

    // Reference and initialize views
    private void configureView() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);

        TextView textViewNameSurname = (TextView) mActivity.findViewById(R.id.menu_list_name_surname);
        textViewNameSurname.setText(
                sp.getString(PREF_FB_NAME_SURNAME, mActivity.getString(R.string.name_surname)));

        ProfilePictureView profilePictureView = (ProfilePictureView)
                mActivity.findViewById(R.id.menu_list_profile_picture);
        profilePictureView.setProfileId(sp.getString(FacebookLoginFragment.PREF_FB_ID, "0"));

        // On logout return to Facebook Login activity
        LoginButton loginButton = (LoginButton) mActivity.findViewById(R.id.menu_list_button_login);
        loginButton.setSessionStatusCallback(new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState sessionState, Exception e) {
                if (session.isClosed()) {
                    session.closeAndClearTokenInformation();

                    Intent intent = new Intent(mActivity, FacebookLoginActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.finish();
                }
            }
        });

        RatingBar ratingBar = (RatingBar) mActivity.findViewById(R.id.menu_list_rating_bar);
        ratingBar.setRating(sp.getFloat(FacebookLoginFragment.PREF_FB_RATING, 0));

        LinearLayout linearLayoutPosts = (LinearLayout) mActivity.findViewById(R.id.menu_list_link_posts);
        linearLayoutPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, PostsListActivity.class);
                mActivity.startActivity(intent);
            }
        });
    }
}
