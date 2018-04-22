package feis_clan.points;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class GameActivity extends Activity {
    GridLayout grid;
    private boolean turn = false;
    private int cells_status[];
    //private int redScore = 0;
    //private int blueScore = 0;
    private int height, width;

    //private Time timeSpent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        height = Integer.parseInt(getIntent().getStringExtra("height"));
        width = Integer.parseInt(getIntent().getStringExtra("width"));

        setContentView(R.layout.battlefield);
        grid = (GridLayout) findViewById(R.id.grid);

        int cell_IDs[] = new int[height*width];
        cell_IDs = markupField();
        cells_status = new int[height*width];
        for(int i=0;i<height*width;i++)
            cells_status[i] = 0;
    }

    private int[] markupField() {
        grid.setColumnCount(width);
        int cell_IDs[] = new int[height*width];
        for (int i = 0; i < height*width; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.empty_cell_10px);
            imageView.setId(i);
            cell_IDs[i] = imageView.getId();
            imageView.setOnClickListener(cellClick);
            grid.addView(imageView);
        }
        return cell_IDs;
    }

    private View.OnClickListener cellClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //makeShortToast(getApplicationContext(),Integer.toString(v.getId()));
            setPoint(v);
        }
    };

    private void makeShortToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    private void toggleTurn(){
        turn = !turn;
    }

    private void setPoint(View view){
        if(cells_status[view.getId()] == 0){
            cells_status[view.getId()] = (turn ? 1 : 2);
            ImageView image=(ImageView)findViewById(view.getId());
            image.setImageResource(turn ? R.drawable.red_cell_10px : R.drawable.blue_cell_10px);
            toggleTurn();
        }
        else {
            makeShortToast(getApplicationContext(), "There is a point already!");
        }
    }
}
