package feis_clan.points;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

public class GameActivity extends Activity {
    GridLayout grid;
    private boolean turn = false;
    private byte cells_status[][];
    //private int redScore = 0;
    //private int blueScore = 0;
    private int sideSize, square;
    //private boolean finishedPath = false;
    private byte visited[][];

    //private Time timeSpent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sideSize = getIntent().getIntExtra("side_size", 5);
        square = sideSize*sideSize;

        setContentView(R.layout.battlefield);
        grid = (GridLayout) findViewById(R.id.grid);

        markupField();
        cells_status = getEmptyArray();
    }

    private void markupField() {
        grid.setColumnCount(sideSize);
        for (int i = 0; i < square; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.empty_cell_10px);
            imageView.setId(i+1);
            imageView.setOnClickListener(cellClick);
            grid.addView(imageView);
        }
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
        int cell_id[] = getIDbyGridID(view.getId());
        if(cells_status[cell_id[0]][cell_id[1]] == 0){
            cells_status[cell_id[0]][cell_id[1]] = (byte)(turn ? 1 : 2);
            ImageView image=(ImageView)findViewById(view.getId());
            image.setImageResource(turn ? R.drawable.red_cell_10px : R.drawable.blue_cell_10px);
            checkClosedField(cell_id[0],cell_id[1]);
            toggleTurn();
        }
        else {
            makeShortToast(getApplicationContext(), "There is a point already!");
        }
    }

    private int[] getIDbyGridID(int id){
        int cellID[] = {0, 0};
        while (id > sideSize){
            id = id-sideSize;
            cellID[0]++;
        }
        cellID[1] = id-1;
        return cellID;
    }

    private void checkClosedField(int startI, int startJ){
        /*boolean visited[][] = new boolean[sideSize][sideSize];
        for(int i=0;i<visited.length;i++)
            for(int j=0;j<visited[i].length;j++)
                visited[i][j] = false;*/
        /*for(int i=0;i<sideSize;i++){
            for(int j=0;j<sideSize;j++){
                if(cells_status[i][j] == colourToCheck){

                }
            }
        }*/
        visited = getEmptyArray();
        visited[startI][startJ] = 2;
        if(isPath(startI,startJ,cells_status[startI][startJ]))
            Toast.makeText(getApplicationContext(),"YEAH!!!",Toast.LENGTH_SHORT).show();
        //finishedPath = false;
        //if(getPath(startI,startJ,cells_status[startI][startJ],visited/*,new Stack<int[]>()*/))
        //    Toast.makeText(getApplicationContext(), "Gotcha!",
        //            Toast.LENGTH_SHORT).show();
        /*while(!path.empty()){
            int point[] = path.pop();
            Toast.makeText(getApplicationContext(),
                    Integer.toString(point[0])+":"+Integer.toString(point[1]),
                    Toast.LENGTH_SHORT).show();
        }*/
    }

    private boolean isPath(int curI, int curJ, byte color){

        if(visited[curI][curJ] == 2 && isFieldPassed(visited))
            return true;
        visited[curI][curJ]=1;
        if(isMovable(curI,curJ,0) == color){
            if(visited[curI-1][curJ-1] != 1){
                if(isPath(curI-1,curJ-1,color))
                    return true;
            }
        }
        if(isMovable(curI,curJ,2) == color){
            if(visited[curI-1][curJ+1] != 1){
                if(isPath(curI-1,curJ+1,color))
                    return true;
            }
        }
        if(isMovable(curI,curJ,5) == color){
            if(visited[curI+1][curJ-1] != 1){
                if(isPath(curI+1,curJ-1,color))
                    return true;
            }
        }
        if(isMovable(curI,curJ,7) == color){
            if(visited[curI+1][curJ+1] != 1){
                return isPath(curI + 1, curJ + 1, color);
            }
        }
        return false;
    }

    private boolean getPath(int curI, int curJ,
                                 byte colour, byte visited[][]/*, Stack<int[]> path*/){
        if(visited[curI][curJ] == 2 && isFieldPassed(visited)) {
            //finishedPath = true;
            return true;
        }
        visited[curI][curJ] = 1;
        //path.push(new int[]{curI, curJ});
        /*if(path.size()>1 && path.firstElement() == path.lastElement()) {
            //Toast.makeText(getApplicationContext(), "Gotcha!", Toast.LENGTH_SHORT).show();
            return path;
        }*/
        if(isMovable(curI,curJ,0) == colour){
            if(visited[curI-1][curJ-1] != 1){
                /*path = getPath(curI-1,curJ-1,colour,visited,path);
                if(finishedPath)
                    return path;*/
                if(getPath(curI-1,curJ-1,colour,visited))
                    return true;
            }
        }
        if(isMovable(curI,curJ,1) == colour){
            if(visited[curI-1][curJ] != 1){
                /*path = getPath(curI-1,curJ,colour,visited,path);
                if(finishedPath)
                    return path;*/
                if(getPath(curI-1,curJ,colour,visited))
                    return true;
            }
        }
        if(isMovable(curI,curJ,2) == colour){
            if(visited[curI-1][curJ+1] != 1){
                /*path = getPath(curI-1,curJ+1,colour,visited,path);
                if(finishedPath)
                    return path;*/
                if(getPath(curI-1,curJ+1,colour,visited))
                    return true;
            }
        }
        if(isMovable(curI,curJ,3) == colour){
            if(visited[curI][curJ-1] != 1){
                /*path = getPath(curI,curJ-1,colour,visited,path);
                if(finishedPath)
                    return path;*/
                if(getPath(curI,curJ-1,colour,visited))
                    return true;
            }
        }
        if(isMovable(curI,curJ,4) == colour){
            if(visited[curI][curJ+1] != 1){
                /*path = getPath(curI,curJ+1,colour,visited,path);
                if(finishedPath)
                    return path;*/
                if(getPath(curI,curJ+1,colour,visited))
                    return true;
            }
        }
        if(isMovable(curI,curJ,5) == colour){
            if(visited[curI+1][curJ-1] != 1){
                /*path = getPath(curI+1,curJ-1,colour,visited,path);
                if(finishedPath)
                    return path;*/
                if(getPath(curI+1,curJ-1,colour,visited))
                    return true;
            }
        }
        if(isMovable(curI,curJ,6) == colour){
            if(visited[curI+1][curJ] != 1){
                /*path = getPath(curI+1,curJ,colour,visited,path);
                if(finishedPath)
                    return path;*/
                if(getPath(curI+1,curJ,colour,visited))
                    return true;
            }
        }
        if(isMovable(curI,curJ,7) == colour){
            if(visited[curI+1][curJ+1] != 1){
                /*path = getPath(curI+1,curJ+1,colour,visited,path);
                if(finishedPath)
                    return path;*/
                if(getPath(curI+1,curJ+1,colour,visited))
                    return true;
            }
        }
        //path.pop();
        return false;
    }

    private byte[][] getEmptyArray(){
        byte arr[][] = new byte[sideSize][sideSize];
        for(int i=0;i<sideSize;i++)
            for(int j=0;j<sideSize;j++)
                arr[i][j] = 0;
        return arr;
    }

    /**
     * direction:
     * 0 - top-left   1 - top   2 - top-right
     * 3 - left   4 - right
     * 5 - bottom-left   6 - bottom   7 - bottom-right
     */
    private byte isMovable(int i, int j, int direction){
        if(direction == 0){
            if(i != 0 && j!= 0)
                return cells_status[i-1][j-1];
            else return 0;
        }
        else if(direction == 1){
            if(i != 0)
                return cells_status[i-1][j];
            else return 0;
        }
        else if(direction == 2){
            if(i != 0 && j != sideSize-1)
                return cells_status[i-1][j+1];
            else return 0;
        }
        else if(direction == 3){
            if(j != 0)
                return cells_status[i][j-1];
            else return 0;
        }
        else if(direction == 4){
            if(j != sideSize-1)
                return cells_status[i][j+1];
            else return 0;
        }
        else if(direction == 5){
            if(i != sideSize-1 && j != 0)
                return cells_status[i+1][j-1];
            else return 0;
        }
        else if(direction == 6){
            if(i != sideSize-1)
                return cells_status[i+1][j];
            else return 0;
        }
        else {
            if(i != sideSize-1 && j != sideSize-1)
                return cells_status[i+1][j+1];
            else return 0;
        }
    }

    private boolean isFieldPassed(byte visited[][]){
        for(int i=0;i<sideSize;i++){
            for(int j=0;j<sideSize;j++){
                if(visited[i][j] == 1){
                    return true;
                }
            }
        }
        return false;
    }
}
