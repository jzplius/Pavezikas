package lt.justplius.android.pavezikas.common;

import android.app.Activity;
import android.content.Context;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import lt.justplius.android.pavezikas.R;

/**
 * Created by JUSTPLIUS on 2014.11.03.
 */
public class SlidingMenuUtils {
    Activity mActivity;
    public SlidingMenuUtils(Activity activity) {
        mActivity = activity;
    }

    public void setSlidingMenu(){
        // Configure the SlidingMenu
        SlidingMenu menu = new SlidingMenu(mActivity);
        menu.setMode(SlidingMenu.RIGHT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadowright);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.5f);
        menu.attachToActivity(mActivity, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.menu_list);
    }
}
