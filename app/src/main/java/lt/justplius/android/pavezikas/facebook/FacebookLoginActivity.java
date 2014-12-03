package lt.justplius.android.pavezikas.facebook;

import android.support.v4.app.Fragment;

import lt.justplius.android.pavezikas.common.BackStackDoubleTapExit;
import lt.justplius.android.pavezikas.common.BaseSingleFragmentActivity;

public class FacebookLoginActivity extends BaseSingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new FacebookLoginFragment();
    }

    // Disable on back button pressed event by default
    // and exit from app on back button pressed twice
    @Override
    public void onBackPressed() {
        // If the button has been pressed twice go to the main screen of phone
        BackStackDoubleTapExit.BackStackDoubleTapExit(this);
    }
}
