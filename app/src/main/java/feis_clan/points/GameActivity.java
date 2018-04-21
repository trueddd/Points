package feis_clan.points;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class GameActivity extends Activity{
    GridLayout grid;
    private int redScore=0;
    private int blueScore=0;
    //private Time timeSpent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.small_field);

        grid=(GridLayout)findViewById(R.id.grid);
    }

    private void markupField(){
        
    }
}
