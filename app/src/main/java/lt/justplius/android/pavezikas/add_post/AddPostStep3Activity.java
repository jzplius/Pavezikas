package lt.justplius.android.pavezikas.add_post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.add_post.events.DownloadFinishedEvent;
import lt.justplius.android.pavezikas.add_post.events.DownloadStartedEvent;
import lt.justplius.android.pavezikas.common.BaseVezikasSingleFragmentActivity;
import lt.justplius.android.pavezikas.mangers.BusManager;
import lt.justplius.android.pavezikas.mangers.PostLoadersManager;

import static lt.justplius.android.pavezikas.mangers.PostLoadersManager.LOADER_INSERT_POST;

/**
 * Contains single pane add post 3rd step fragment by implementing createFragment().
 * This activity allows "back and fourth" navigation.
 * Implements add post 3rd step fragment callbacks.
 */
public class AddPostStep3Activity extends BaseVezikasSingleFragmentActivity
        implements AddPostStep3Fragment.AddPostStep3Callback,
        LoaderManager.LoaderCallbacks{

    private static final String TAG = "AddPostStep3Activity";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_post;
    }

    @Override
    protected Fragment createFragment() {
        return new AddPostStep3Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RadioButton radioButton2 = (RadioButton) findViewById(R.id.post_insert_status_radioButton_2);
        radioButton2.setEnabled(true);
        RadioButton radioButton3 = (RadioButton) findViewById(R.id.post_insert_status_radioButton_3);
        radioButton3.setEnabled(true);

        Button buttonContinue = (Button) findViewById(R.id.post_insert_button);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                       onPostRouteSelected();
               }
           }
        );
        buttonContinue.setText(R.string.add_post);
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

    @Override
    public void onPostRouteSelected() {
        // Post an event, informing to start inserting post to DB
        BusManager
                .getInstance()
                .post(new DownloadStartedEvent(LOADER_INSERT_POST));

        getSupportLoaderManager()
                .restartLoader(LOADER_INSERT_POST, null, this);
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
    public void onBackPressed() {

        if (getSlidingMenu().isMenuShowing()) {
            getSlidingMenu().showContent();
        } else {
            finish();
        }
    }
}
