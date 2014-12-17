package lt.justplius.android.pavezikas.add_post;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.TextView;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.post.Post;
import lt.justplius.android.pavezikas.post.PostManager;

public class DroppingAddressDialogFragment extends DialogFragment {
    private AlertDialog mDialog;
    private TextView mEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);

        // Prevent from recreation during device rotations
        if (mEditText == null) {
            mEditText= new EditText(getActivity());
            final Post post = PostManager.getPost(getActivity());
            if (post.isDroppingAddressSet()){
                mEditText.setText(post.getDroppingAddress());
            } else {
                mEditText.setText(", " + post.getDroppingAddress());
            }

            // Use the Builder class for dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(mEditText);
            builder.setMessage(R.string.set_dropping_address)
                   .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           String address = mEditText.getText().toString();
                           Intent intent = new Intent();
                           intent.putExtra(AddPostStep3Fragment.ARG_DROPPING_ADDRESS, address);
                           getTargetFragment()
                                   .onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                           dismiss();
                       }
                   })
                   .setNegativeButton(R.string.revert, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           Intent intent = new Intent();
                           intent.putExtra(AddPostStep3Fragment.ARG_DROPPING_ADDRESS, "");
                           getTargetFragment()
                                   .onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                           dismiss();
                       }
                   });
            // Create the AlertDialog object and return it
            mDialog = builder.create();
        }

        return mDialog;
    }

    // Handle of compatibility issue bug
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
