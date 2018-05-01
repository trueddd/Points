package feis_clan.points;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class PostGameDialog extends DialogFragment {
    public static PostGameDialog newInstance(int redScore, int blueScore, int redCaptured, int blueCaptured){
        PostGameDialog fragment = new PostGameDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("redScore", redScore);
        bundle.putInt("blueScore", blueScore);
        bundle.putInt("redCaptured", redCaptured);
        bundle.putInt("blueCaptured", blueCaptured);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.postgame_dialog_view, null);

        int red = getArguments().getInt("redScore");
        int blue = getArguments().getInt("blueScore");
        int redCap = getArguments().getInt("redCaptured");
        int blueCap = getArguments().getInt("blueCaptured");

        int result = 0;

        TextView redScore = view.findViewById(R.id.red_score);
        redScore.setText(String.format(Locale.getDefault(), "%d", red));

        TextView blueScore = view.findViewById(R.id.blue_score);
        blueScore.setText(String.format(Locale.getDefault(), "%d", blue));

        TextView redCaptured = view.findViewById(R.id.blue_captures);
        redCaptured.setText(String.format(Locale.getDefault(), "%d", redCap));

        TextView blueCaptured = view.findViewById(R.id.red_captures);
        blueCaptured.setText(String.format(Locale.getDefault(), "%d", blueCap));

        ImageView redCup = view.findViewById(R.id.red_cup_place);
        ImageView blueCup = view.findViewById(R.id.blue_cup_place);
        if((red - blue == 1 || blue - red == 1) && redCap == blueCap){
            redCup.setImageResource(R.drawable.hands_30);
            blueCup.setImageResource(R.drawable.hands_30);
            result = 0;
        }else if(red > blue){
            redCup.setImageResource(R.drawable.cup_30);
            result = 1;
        }else if(blue > red){
            blueCup.setImageResource(R.drawable.cup_30);
            result = 2;
        }else {
            redCup.setImageResource(R.drawable.hands_30);
            blueCup.setImageResource(R.drawable.hands_30);
            result = 0;
        }
        updateStats(result, red, blue, redCap, blueCap);

        builder.setView(view)
                .setPositiveButton(R.string.postgame_dialog_finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().onBackPressed();
                    }
                });
        return builder.create();
    }

    private void updateStats(int result, int redScore, int blueScore, int redCaptured, int blueCaptured){
        SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("GAMES_PLAYED", preferences.getInt("GAMES_PLAYED", 0) + 1);
        if(result == 1){
            editor.putInt("RED_WINS", preferences.getInt("RED_WINS", 0) + 1);
        }else if(result == 2){
            editor.putInt("BLUE_WINS", preferences.getInt("BLUE_WINS", 0) + 1);
        }else {
            editor.putInt("DRAWS", preferences.getInt("DRAWS", 0) + 1);
        }
        editor.putInt("RED_CAPTURED", preferences.getInt("RED_CAPTURED", 0) + redCaptured);
        editor.putInt("BLUE_CAPTURED", preferences.getInt("BLUE_CAPTURED", 0) + blueCaptured);
        if(preferences.getInt("MAX_SCORE", 0) < redScore){
            editor.putInt("MAX_SCORE", redScore);
        }
        if(preferences.getInt("MAX_SCORE", 0) < blueScore){
            editor.putInt("MAX_SCORE", blueScore);
        }
        if(preferences.getInt("MAX_CAPTURED", 0) < redCaptured){
            editor.putInt("MAX_CAPTURED", redCaptured);
        }
        if(preferences.getInt("MAX_CAPTURED", 0) < blueCaptured){
            editor.putInt("MAX_CAPTURED", blueCaptured);
        }
        editor.apply();
    }
}
