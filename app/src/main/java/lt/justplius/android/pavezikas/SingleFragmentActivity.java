package lt.justplius.android.pavezikas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by JUSTPLIUS on 2014.08.26.
 */
public abstract class SingleFragmentActivity extends FragmentActivity {
    protected abstract Fragment createFragment();

    protected int getLayoutResourceId(){
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            // Commit check if container for Fragment exists and it is free
            Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
            if (fragment == null) {
                fragment = createFragment();
                fm.beginTransaction()
                        .add(R.id.fragmentContainer, fragment)
                        .commit();
            }
        }
    }
}
