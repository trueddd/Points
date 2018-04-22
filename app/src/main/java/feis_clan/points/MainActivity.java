package feis_clan.points;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
    private LinearLayout startButton;
    private LinearLayout statsButton;
    private LinearLayout exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton=(LinearLayout)findViewById(R.id.new_game_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new PreGameDialog();
                dialog.show(getFragmentManager(),"pregameDialog");
            }
        });

        statsButton=(LinearLayout)findViewById(R.id.stats_button);
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),R.string.stats_header,Toast.LENGTH_SHORT).show();
            }
        });

        exitButton=(LinearLayout)findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog=new ExitDialog();
                dialog.show(getFragmentManager(),"exitDialog");
            }
        });
    }
}
