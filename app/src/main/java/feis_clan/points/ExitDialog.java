package feis_clan.points;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class ExitDialog extends DialogFragment implements DialogInterface.OnClickListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.dialog_positive,this)
                .setNegativeButton(R.string.dialog_negative,this)
                .setTitle(R.string.exit_header)
                .setMessage(R.string.exit_dialog_message);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case Dialog.BUTTON_POSITIVE:
                getActivity().onBackPressed();
                break;
            default:
                dialog.dismiss();
                break;
        }
    }
}
