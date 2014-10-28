package lt.justplius.android.pavezikas;

import android.support.v4.app.Fragment;

public class FacebookLoginActivity extends BaseFragmentActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected Fragment createFragment() {
        return new FacebookLoginFragment();
    }

    // Not used in single Fragment layouts, only used in two pane layouts
    @Override
    protected Fragment createDetailsFragment() {
        return null;
    }
}
