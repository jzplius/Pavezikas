package lt.justplius.android.pavezikas.add_post;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.BaseVezikasSingleFragmentActivity;

/**
 * Contains single pane add post 2rd step fragment by implementing createFragment().
 * This activity allows "back and fourth" navigation.
 * Implements add post 2rd step fragment callbacks.
 */
public class AddPostStep2Activity extends BaseVezikasSingleFragmentActivity
    implements AddPostStep2Fragment.AddPostStep2Callback{

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_post;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button buttonContinue = (Button) findViewById(R.id.post_insert_button);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(AddPostStep2Activity.this, AddPostStep3Activity.class);
                startActivity(detailIntent);
            }
        });

        RadioButton radioButton2 = (RadioButton) findViewById(R.id.post_insert_status_radioButton_2);
        radioButton2.setEnabled(true);
    }

    @Override
    protected Fragment createFragment() {
        return new AddPostStep2Fragment();
    }

    @Override
    public void onInformationSelected() {
        Intent detailIntent = new Intent(this, AddPostStep3Activity.class);
        startActivity(detailIntent);
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
