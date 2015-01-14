package lt.justplius.android.pavezikas.add_post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.add_post.events.DownloadFinishedEvent;
import lt.justplius.android.pavezikas.add_post.events.DownloadStartedEvent;
import lt.justplius.android.pavezikas.common.BaseVezikasTwoFragmentActivity;
import lt.justplius.android.pavezikas.mangers.BusManager;
import lt.justplius.android.pavezikas.mangers.DownloadsManager;
import lt.justplius.android.pavezikas.mangers.PostLoadersManager;

import static lt.justplius.android.pavezikas.common.NetworkStateUtils.handleNoNetworkAvailable;
import static lt.justplius.android.pavezikas.common.NetworkStateUtils.isConnected;
import static lt.justplius.android.pavezikas.common.NetworkStateUtils.sIsConnected;
import static lt.justplius.android.pavezikas.mangers.PostLoadersManager.LOADER_INSERT_POST;

/**
 * Contains single pane fragments by implementing createFragment(),
 * createDetailsFragment() fragments.
 * Fragments are selected according to current step.
 * This activity allows "back and fourth" navigation.
 * Implements add post fragments callbacks and loader callbacks.
 */
public class AddPostActivity extends BaseVezikasTwoFragmentActivity
implements AddPostStep1Fragment.AddPostStep1Callback,
        AddPostStep2Fragment.AddPostStep2Callback,
        AddPostStep3Fragment.AddPostStep3Callback,
        LoaderManager.LoaderCallbacks{
    private static final String ARG_CURRENT_STEP = "current_step";
    private static final String TAG = "AddPostActivity";
    private int mCurrentStep = 1;
    private RadioButton mRadioButton2;
    private RadioButton mRadioButton3;
    private Button mButtonContinue;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_post;
    }

    @Override
    protected Fragment createFragment() {
        switch (mCurrentStep) {
            case 2:
                return new AddPostStep2Fragment();
            case 3:
                return new AddPostStep3Fragment();
            default:
                return new AddPostStep1Fragment();
        }
    }

    @Override
    protected Fragment createDetailsFragment(int selectionId) {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrentStep = savedInstanceState.getInt(ARG_CURRENT_STEP);
        }
        super.onCreate(savedInstanceState);

        mRadioButton2 = (RadioButton) findViewById(R.id.post_insert_status_radioButton_2);
        if (mCurrentStep >= 2) {
            mRadioButton2.setEnabled(true);
        }
        mRadioButton3 = (RadioButton) findViewById(R.id.post_insert_status_radioButton_3);
        if (mCurrentStep >= 3) {
            mRadioButton3.setEnabled(true);
        }

        mButtonContinue = (Button) findViewById(R.id.post_insert_button);
        mButtonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCurrentStep) {
                    case 1:
                        onPostTypeSelected();
                        break;
                    case 2:
                        onInformationSelected();
                        break;
                    case 3:
                        onPostRouteSelected();
                        break;
                }
            }
        }
        );
        if (mCurrentStep >= 3) {
            mButtonContinue.setText(R.string.add_post);
        }
    }

    @Override
    protected int setActionBarLayoutResourceId() {
        return R.layout.actionbar_add_post;
    }

    // Inflate prepared to inflate Fragment
    @Override
    protected void inflateFragment() {
        if (sIsConnected) {
            FragmentManager fm = getSupportFragmentManager();
            // Inflate newly prepared Fragment
            // without checking if it has already been inflated
            Fragment fragment = createFragment();
            fm.beginTransaction()
                .replace(getFragmentContainerId(), fragment, TAG_FRAGMENT)
                .commit();
        } else {
            handleNoNetworkAvailable(this);
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

    private boolean isAllDataCorrect() {
        if (DownloadsManager.isDownloading()) {
            Toast.makeText(
                    AddPostActivity.this,
                    R.string.message_data_is_being_downloaded,
                    Toast.LENGTH_LONG)
                    .show();
            return false;
        } else if (Post.getInstance().getRouteID().equals("0")) {
            Toast.makeText(
                    AddPostActivity.this,
                    R.string.message_wrong_route_id,
                    Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    public int getCurrentStep() {
        return mCurrentStep;
    }

    public void setCurrentStep(int currentStep) {
        mCurrentStep = currentStep;
    }

    public RadioButton getRadioButton2() {
        return mRadioButton2;
    }

    public RadioButton getRadioButton3() {
        return mRadioButton3;
    }

    public Button getButtonContinue() {
        return mButtonContinue;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == LOADER_INSERT_POST) {
            return PostLoadersManager
                    .getInstance(this)
                    .createInsertPostLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (loader.getId() == LOADER_INSERT_POST) {
            try {
                JSONObject jsonObject = new JSONObject(data.toString());
                Post.getInstance().setPostId(jsonObject.getString("id"));
            } catch (JSONException e) {
                Log.e(TAG, "Error converting post insert result:", e);
                Post.getInstance().setPostId("0");
            }
            // Post an event, informing that user insert of post was completed
            BusManager
                    .getInstance()
                    .post(new DownloadFinishedEvent(LOADER_INSERT_POST));
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    @Override
    public void onPostTypeSelected() {
        if (isConnected(this)) {
            mCurrentStep = 2;
            inflateFragment();
            mRadioButton2.setEnabled(true);
        }
    }

    @Override
    public void onInformationSelected() {
        if (isConnected(this)){
            mCurrentStep = 3;
            inflateFragment();
            mRadioButton3.setEnabled(true);
            mButtonContinue.setText(R.string.add_post);
        }
    }

    @Override
    public void onPostRouteSelected() {
        if (isConnected(this) && isAllDataCorrect()) {
            // Post an event, informing to start inserting post to DB
            BusManager
                    .getInstance()
                    .post(new DownloadStartedEvent(LOADER_INSERT_POST));

            getSupportLoaderManager()
                    .restartLoader(LOADER_INSERT_POST, null, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_CURRENT_STEP, mCurrentStep);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (getSlidingMenu().isMenuShowing()) {
            getSlidingMenu().showContent();
        } else {
            if (isConnected(this)) {
                mCurrentStep--;
                switch (mCurrentStep) {
                    case 0:
                        finish();
                        break;
                    case 1:
                        inflateFragment();
                        mRadioButton3.setEnabled(false);
                        mRadioButton2.setEnabled(false);
                        break;
                    default:
                        inflateFragment();
                        mRadioButton3.setEnabled(false);
                        mButtonContinue.setText(R.string.next);
                        mButtonContinue.setEnabled(true);
                        break;
                }
            }
        }
    }
}
