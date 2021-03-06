package lt.justplius.android.pavezikas.common;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import lt.justplius.android.pavezikas.R;

public abstract class BaseVezikasSingleFragmentActivity extends BaseSingleFragmentActivity {
    private static final String TAG = "BaseVezikasSingleFragmentActivity";

    private SlidingMenu mSlidingMenu;

    protected int setActionBarLayoutResourceId() {
        return R.layout.actionbar_details;
    }

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
        mSlidingMenu = new SlidingMenuUtils(this).configureSlidingMenu();

        // Configure activity-specific action bar
        actionBar = getActionBar();
        if (actionBar != null) {
            View view = actionBar.getCustomView();

            if (view != null) {
                // OnClick show menu
                ImageButton imageButtonMenu = (ImageButton) view.findViewById(R.id.action_bar_main_menu);
                imageButtonMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSlidingMenu().toggle();
                    }
                });

                // OnClick return to previous fragment
                ImageButton imageButtonHome = (ImageButton) view.findViewById(R.id.action_bar_home);
                imageButtonHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        }
    }

    // Show menu on 'menu' key press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SETTINGS || keyCode == KeyEvent.KEYCODE_MENU) {
            mSlidingMenu.toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Disable on back button pressed event by default
    // and exit from app on back button pressed twice
    @Override
    public void onBackPressed() {
        // If the button has been pressed twice go to the main screen of phone
        if (mSlidingMenu.isMenuShowing()) {
            mSlidingMenu.showContent();
        } else {
            BackStackDoubleTapExit.BackStackDoubleTapExit(this);
        }
    }

    public SlidingMenu getSlidingMenu(){
        return mSlidingMenu;
    }
}
