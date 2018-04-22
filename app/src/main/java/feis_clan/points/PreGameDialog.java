package feis_clan.points;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class PreGameDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pregame_dialog_view,null);
        final EditText heightEditText = (EditText)dialogView.findViewById(R.id.height_edit_text);
        final EditText widthEditText = (EditText)dialogView.findViewById(R.id.width_edit_text);
        builder.setView(dialogView)
                .setPositiveButton(R.string.pregame_dialog_start,
                        new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), GameActivity.class);
                        String h = heightEditText.getEditableText().toString();
                        String w = widthEditText.getEditableText().toString();
                        intent.putExtra("height", h);
                        intent.putExtra("width", w);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
