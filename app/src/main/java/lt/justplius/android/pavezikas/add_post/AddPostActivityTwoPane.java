package lt.justplius.android.pavezikas.add_post;

import android.support.v4.app.Fragment;

import lt.justplius.android.pavezikas.R;

import static lt.justplius.android.pavezikas.common.NetworkStateUtils.isConnected;

/**
 * Contains single-two pane fragments by implementing createFragment(),
 * createDetailsFragment() fragments.
 * Fragments are selected according to current step.
 * This activity allows "back and fourth" navigation.
 * Implements add post fragments callbacks and loader callbacks.
 */
public class AddPostActivityTwoPane extends AddPostActivity {

    @Override
    protected Fragment createFragment() {
        switch(getCurrentStep()) {
            case 3:
                return new AddPostStep2Fragment();
            default:
                return new AddPostStep1Fragment();
        }
    }

    @Override
    protected Fragment createDetailsFragment(int selectionId) {
        switch(getCurrentStep()) {
            case 3:
                return new AddPostStep3Fragment();
            default:
                return new AddPostStep2Fragment();
        }
    }

    @Override
    public void onPostTypeSelected() {
        if (getCurrentStep() != 2 && isConnected(this)) {
            setCurrentStep(2);
            inflateDetailsFragment(0);
            getRadioButton2().setEnabled(true);
        }
    }

    @Override
    public void onInformationSelected() {
        // This part is not reached on phones, it is reached only on tablets
        if (getCurrentStep() != 3 && isConnected(this)) {
            setCurrentStep(3);
            inflateFragment();
            inflateDetailsFragment(0);
            getRadioButton3().setEnabled(true);
            getButtonContinue().setText(R.string.add_post);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSlidingMenu().isMenuShowing()) {
            getSlidingMenu().showContent();
        } else {
            if (isConnected(this)) {
                setCurrentStep(getCurrentStep() - 1);
                switch (getCurrentStep()) {
                    case 0:
                        finish();
                        break;
                    case 1:
                        removeDetailsFragment();
                        getRadioButton3().setEnabled(false);
                        getRadioButton2().setEnabled(false);
                        break;
                    default:
                        removeDetailsFragment();
                        inflateFragment();
                        inflateDetailsFragment(0);
                        getRadioButton3().setEnabled(false);
                        getButtonContinue().setText(R.string.next);
                        getButtonContinue().setEnabled(true);
                        break;
                }
            }
        }
    }
}
