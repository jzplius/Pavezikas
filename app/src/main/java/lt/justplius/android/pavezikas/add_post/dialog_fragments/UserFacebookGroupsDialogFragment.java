package lt.justplius.android.pavezikas.add_post.dialog_fragments;

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
import lt.justplius.android.pavezikas.add_post.AddPostStep3Fragment;
import lt.justplius.android.pavezikas.facebook.FacebookGroup;
import lt.justplius.android.pavezikas.mangers.LoadersManager;

import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_USER_GROUPS;

public class UserFacebookGroupsDialogFragment
        extends DialogFragment
    implements LoaderManager.LoaderCallbacks<String>{
    private static final String TAG = "FacebookGroupsDialogFragment";

    ListView mListView;
    private ArrayList<String> mSelectedGroups;
    private FacebookGroupsAdapter mAdapter;
    private int mFirstVisiblePosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity()
                .getSupportLoaderManager()
                .restartLoader(LOADER_USER_GROUPS, null, this)
                .forceLoad();

        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Prepare model
        mListView = new ListView(getActivity());
        // Retrieve data on screen rotation
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.setSelectionFromTop(mFirstVisiblePosition, 0);
        }

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
        return builder.create();
    }

    private class FacebookGroupsAdapter extends ArrayAdapter<FacebookGroup> {

        public FacebookGroupsAdapter(Context context, ArrayList<FacebookGroup> groups) {
            super(context, 0, groups);
            mSelectedGroups = new ArrayList<>();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(getItem(position).getGroupName());
            checkBox.setChecked(getItem(position).isChecked());
            // Add selected group to selected list
            if (getItem(position).isChecked() && !mSelectedGroups.contains(getItem(position).getGroupId())) {
                mSelectedGroups.add(getItem(position).getGroupId());
            }
            // On change add or remove group from selected list
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mSelectedGroups.add(getItem(position).getGroupId());
                    } else {
                        mSelectedGroups.remove(getItem(position).getGroupId());
                    }
                    mAdapter.getItem(position).setChecked(isChecked);
                }
            });
            return checkBox;
        }
    }

    @Override
    public android.support.v4.content.Loader<String> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_USER_GROUPS) {
            return LoadersManager
                    .getInstance(getActivity())
                    .createUserGroupsLoader();
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String> loader, String result) {
        if (getDialog() != null
                && getDialog().isShowing()
                && loader.getId() == LOADER_USER_GROUPS) {
            try {
                ArrayList<FacebookGroup> groups = new ArrayList<>();
                JSONArray results = new JSONArray(result);
                for (int i = 0; i < results.length(); i++) {
                    JSONObject group = results.getJSONObject(i);
                    String groupId = group.getString("group_id");
                    String groupName = group.getString("group_name");
                    boolean isSelected = !group.getString("is_selected").equals("0");
                    groups.add(new FacebookGroup(groupId, isSelected, groupName));
                }
                mAdapter = new FacebookGroupsAdapter(getActivity(), groups);
                mListView.setAdapter(mAdapter);
            } catch (JSONException e) {
                Log.e(TAG, "selectFromUserGroups.php error: ", e);
            }
        }
    }

    @Override
    public void onLoaderReset( android.support.v4.content.Loader<String> loader) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save currently first visible ListView item position
        mFirstVisiblePosition = mListView.getFirstVisiblePosition();
    }

    // Handle of compatibility issue bug
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
