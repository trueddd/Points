package feis_clan.points;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity{
    private int redScore=0;
    private int blueScore=0;
    //private Time timeSpent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.small_field);
    }
}
