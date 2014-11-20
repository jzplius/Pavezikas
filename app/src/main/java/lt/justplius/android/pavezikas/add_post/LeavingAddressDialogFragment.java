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
import android.widget.EditText;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.post.Post;
import lt.justplius.android.pavezikas.post.PostManager;

public class LeavingAddressDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        // Use the Builder class dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final EditText editText = new EditText(context);
        Post post = PostManager.getInstance(context);
        if (post.isLeavingAddressSet()){
            editText.setText(post.getLeavingAddress());
        } else {
            editText.setText(", " + post.getLeavingAddress());
        }

        builder.setView(editText);
        builder.setMessage(R.string.set_leaving_address)
               .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       String address = editText.getText().toString();
                       Intent intent = new Intent();
                       intent.putExtra(AddPostStep3Fragment.ARG_LEAVING_ADDRESS, address);
                       getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                       dismiss();
                   }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       Intent intent = new Intent();
                       intent.putExtra(AddPostStep3Fragment.ARG_LEAVING_ADDRESS, "");
                       getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                       dismiss();
                   }
               });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
