package lt.justplius.android.pavezikas.add_post;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.facebook.FacebookGroup;

public class UserGroupsDialogFragment
        extends DialogFragment
    implements LoaderManager.LoaderCallbacks<String>{
    private static final String TAG = "FacebookGroupsDialogFragment";
    private static final int LOADER_USER_GROUPS = 0;

    private ListView mListView;
    private ArrayList<String> mSelectedGroups;
    private AlertDialog mDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);

        // Prevent recreation on screen orientation changes
        if (mListView == null) {
            // Prepare model
            mListView = new ListView(getActivity());
            mSelectedGroups = new ArrayList<>();

            // Get model
            getActivity()
                    .getSupportLoaderManager()
                    .initLoader(LOADER_USER_GROUPS, null, this)
                    .forceLoad();

            // Use the Builder class for dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(mListView);
            builder.setMessage(R.string.set_selected_groups)
                    .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Update model
                            Intent intent = new Intent();
                            intent.putExtra(AddPostStep3Fragment.ARG_SELECTED_GROUPS, mSelectedGroups);
                            getTargetFragment()
                                    .onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                            dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getTargetFragment()
                                    .onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                            dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            mDialog = builder.create();
        }

        return mDialog;
    }

    private class FacebookGroupsAdapter extends ArrayAdapter<FacebookGroup> {
        private final ArrayList<FacebookGroup> mGroups;

        public FacebookGroupsAdapter(Context context, ArrayList<FacebookGroup> groups) {
            super(context, 0, groups);
            mGroups = groups;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(mGroups.get(position).getGroupName());
            checkBox.setChecked(mGroups.get(position).isChecked());
            // Add selected group to selected list
            if (mGroups.get(position).isChecked()) {
                mSelectedGroups.add(mGroups.get(position).getGroupId());
            }
            // On change add or remove group from selected list
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mSelectedGroups.add(mGroups.get(position).getGroupId());
                    } else {
                        mSelectedGroups.remove(mGroups.get(position).getGroupId());
                    }
                }
            });
            return checkBox;
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new UserGroupsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String result) {
        try {
            ArrayList<FacebookGroup> groups = new ArrayList<>();
            JSONArray results = new JSONArray(result);
            for (int i = 0; i < results.length(); i++) {
                JSONObject group = results.getJSONObject(i);
                String groupId = group.getString("group_id");
                String groupName = group.getString("group_name");
                boolean isSelected = group.getString("is_selected").equals("1");
                groups.add(new FacebookGroup(groupId, isSelected, groupName));
            }
            FacebookGroupsAdapter adapter = new FacebookGroupsAdapter(getActivity(), groups);
            mListView.setAdapter(adapter);
        } catch (JSONException e) {
            Log.e(TAG, "selectFromUserGroups.php error: ", e);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        mListView.setAdapter(null);
    }

    // Handle of compatibility issue bug
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
