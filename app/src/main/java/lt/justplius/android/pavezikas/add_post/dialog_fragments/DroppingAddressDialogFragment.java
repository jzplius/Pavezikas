package lt.justplius.android.pavezikas.add_post.dialog_fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.add_post.AddPostStep3Fragment;
import lt.justplius.android.pavezikas.add_post.Post;

public class DroppingAddressDialogFragment extends DialogFragment {
    private String mAddress;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);

        // Retrieve saved value during device rotations
        if (mAddress == null) {
            Post post = Post.getInstance();
            if (post.isDroppingAddressSet()) {
                mAddress = post.getDroppingAddress();
            }
        }

        final EditText editText= new EditText(getActivity());
        editText.setText(mAddress);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAddress = s.toString();
            }
        });

        // Use the Builder class for dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(editText);
        builder.setMessage(R.string.set_dropping_address)
               .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       String address = editText.getText().toString();
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
        return builder.create();
    }

    // Handle of compatibility issue bug
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
