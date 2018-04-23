package feis_clan.points;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class PreGameDialog extends DialogFragment {
    /**
     * seekbar - value
     * 0 - 5
     * 1 - 10
     * 2 - 15
     * 3 - 20
     */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pregame_dialog_view,null);
        SeekBar slider = (SeekBar)dialogView.findViewById(R.id.field_size_slider);
        final TextView currentSize = (TextView)dialogView.findViewById(R.id.pregame_dialog_current_size_text);
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentSize.setText(Integer.toString((progress+1)*5));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        builder.setView(dialogView)
                .setPositiveButton(R.string.pregame_dialog_start,
                        new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), GameActivity.class);
                        intent.putExtra("side_size", Integer.parseInt(currentSize.getText().toString()));
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
