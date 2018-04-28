package feis_clan.points;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

public class GameActivity extends Activity {
    GridLayout grid;
    private boolean turn = false;
    private byte cells_status[][];
    private Stack<int[]> path = new Stack<>();
    private int redScore = 0;
    private int blueScore = 0;
    private int sideSize, square;
    private byte visited[][];
    private int sI, sJ;

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
            //maskDangerousPlaces();
            checkClosedField(cell_id[0],cell_id[1]);
            //unmask();
            recountScore();
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
        visited = getEmptyArray();
        path.clear();
        sI = startI;
        sJ = startJ;
        byte color = cells_status[startI][startJ];
        if(isPath(startI,startJ,color)){
            /*StringBuilder resLoop = new StringBuilder();
            while (!path.empty()){
                int last[] = path.pop();
                resLoop.append(Integer.toString(last[0]));
                resLoop.append(":");
                resLoop.append(Integer.toString(last[1]));
                resLoop.append(" ");
            }
            Log.i("Result loop",resLoop.toString());*/
            if(turn){
                redScore += capture((byte)2);
            }else
                blueScore += capture((byte)1);
        }
    }

    private void recountScore(){
        redScore = blueScore = 0;
        for(int i=0;i<sideSize;i++){
            for(int j=0;j<sideSize;j++){
                if(cells_status[i][j] == 1)
                    redScore++;
                if(cells_status[i][j] == 2)
                    blueScore++;
            }
        }
        ((TextView)findViewById(R.id.redScoreText)).setText(Integer.toString(redScore));
        ((TextView)findViewById(R.id.blueScoreText)).setText(Integer.toString(blueScore));
    }

    /**
     * | | | |*| |
     * | | |*| |*|
     * | |*|&| |*|
     * | | |*|*| |
     * | | | | | |
     */

    /**
     * TODO
     * чекать каждую точку на предмет ударения с соседними точками (в 4 направления) другого цвета
     */
    private int capture(byte colorToCapture){
        //2:1 3:2 2:3 1:2 2:1
        int count = 0;
        path.pop();
        ArrayList<int[]> list = new ArrayList<>(path);
        Log.i("qwe", Integer.toString(list.size()));
        //[3,2] [2,3] [1,2] [2,1]

        for(int i=0;i<sideSize;i++){
            for(int j=0;j<sideSize;j++){
                if(cells_status[i][j] == colorToCapture){
                    if(isSurrounded(list, i, j)){
                        Log.i("capturing point", Integer.toString(i)+":"+Integer.toString(j));
                        cells_status[i][j] = (byte)(colorToCapture == 1 ? 2 : 1);
                        ImageView point = (ImageView)findViewById(i*sideSize+j+1);
                        point.setImageResource((turn ? R.drawable.red_cell_10px : R.drawable.blue_cell_10px));
                        count++;
                    }else {
                        Log.i("q", "Point "+Integer.toString(i)+":"+Integer.toString(j)+" is not surrounded");
                    }
                }
            }
        }
        return count;
    }

    private boolean isSurrounded(ArrayList<int[]> wall, int curI, int curJ){
        if(isUpLocked(wall, curI, curJ, cells_status[curI][curJ])){
            Log.i("q", "Point "+Integer.toString(curI)+":"+Integer.toString(curJ)+" is upLocked");
            if(isLeftLocked(wall, curI, curJ, cells_status[curI][curJ])){
                Log.i("q", "Point "+Integer.toString(curI)+":"+Integer.toString(curJ)+" is leftLocked");
                if(isRightLocked(wall, curI, curJ, cells_status[curI][curJ])){
                    Log.i("q", "Point "+Integer.toString(curI)+":"+Integer.toString(curJ)+" is rightLocked");
                    return isDownLocked(wall, curI, curJ, cells_status[curI][curJ]);
                }
            }
        }
        return false;
    }

    private boolean isUpLocked(ArrayList<int[]> wall, int curI, int curJ, byte checkingPointColor) {
        byte isMove = isMovable(curI, curJ, 1);
        if(isMove == getOpposite(checkingPointColor) && wall.contains(new int[]{curI-1,curJ}))
            return true;
        if(isMove != -10){
            return isUpLocked(wall, curI-1, curJ, checkingPointColor);
        }else return false;
    }

    private boolean isLeftLocked(ArrayList<int[]> wall, int curI, int curJ, byte checkingPointColor){
        byte isMove = isMovable(curI, curJ, 3);
        if(isMove == getOpposite(checkingPointColor) && wall.contains(new int[]{curI,curJ-1}))
            return true;
        if(isMove != -10){
            return isLeftLocked(wall, curI, curJ-1, checkingPointColor);
        }else return false;
    }

    private boolean isRightLocked(ArrayList<int[]> wall, int curI, int curJ, byte checkingPointColor){
        byte isMove = isMovable(curI, curJ, 4);
        if(isMove == getOpposite(checkingPointColor) && wall.contains(new int[]{curI,curJ+1}))
            return true;
        if(isMove != -10){
            return isRightLocked(wall, curI, curJ+1, checkingPointColor);
        }else return false;
    }

    private boolean isDownLocked(ArrayList<int[]> wall, int curI, int curJ, byte checkingPointColor){
        byte isMove = isMovable(curI, curJ, 6);
        if(isMove == getOpposite(checkingPointColor) && wall.contains(new int[]{curI+1,curJ}))
            return true;
        if(isMove != -10){
            return isDownLocked(wall, curI+1, curJ, checkingPointColor);
        }else return false;
    }

    private void maskDangerousPlaces(){
        //**
        //*
        for(int i=0;i<sideSize-1;i++){
            for(int j=0;j<sideSize-1;j++){
                if(cells_status[i][j] == cells_status[i][j+1] && cells_status[i][j] == cells_status[i+1][j]
                        && cells_status[i][j] != 0){
                    cells_status[i][j] = (byte)(cells_status[i][j] == 1 ? -1 : -2);
                }
            }
        }
        //**
        // *
        for(int i=0;i<sideSize-1;i++){
            for(int j=0;j<sideSize-1;j++){
                if(cells_status[i][j] == cells_status[i][j+1] && cells_status[i][j] == cells_status[i+1][j+1]
                        && cells_status[i][j] != 0){
                    cells_status[i][j+1] = (byte)(cells_status[i][j] == 1 ? -1 : -2);
                }
            }
        }
        // *
        //**
        for(int i=0;i<sideSize-1;i++){
            for(int j=1;j<sideSize;j++){
                if(cells_status[i][j] == cells_status[i+1][j] && cells_status[i][j] == cells_status[i+1][j-1]
                        && cells_status[i][j] != 0){
                    cells_status[i][j] = (byte)(cells_status[i+1][j] == 1 ? -1 : -2);
                }
            }
        }
        //*
        //**
        for(int i=0;i<sideSize-1;i++){
            for(int j=0;j<sideSize-1;j++){
                if(cells_status[i][j] == cells_status[i+1][j] && cells_status[i][j] == cells_status[i+1][j+1]
                        && cells_status[i][j] != 0){
                    cells_status[i+1][j] = (byte)(cells_status[i+1][j] == 1 ? -1 : -2);
                }
            }
        }
    }

    private void unmask(){
        for(int i=0;i<sideSize;i++){
            for(int j=0;j<sideSize;j++){
                if(cells_status[i][j] == -1){
                    cells_status[i][j] = 1;
                }
                if(cells_status[i][j] == -2){
                    cells_status[i][j] = 2;
                }
            }
        }
    }

    private boolean isPath(int curI, int curJ, byte color){
        if(curI == sI && curJ == sJ){
            if(path.size() != 0){
                path.push(new int[]{curI,curJ});
                return true;
            }
        }
        //compare possible point and first element in stack
        visited[curI][curJ] = 1;
        if(isMovable(curI,curJ,0) == color){
            if(visited[curI-1][curJ-1] == 0 ||
                    (visited[curI-1][curJ-1] == 1 && path.firstElement()[0] == curI-1 && path.firstElement()[1] == curJ-1  &&
                        !(path.lastElement()[0] == curI-1 && path.lastElement()[1] == curJ-1))){
                path.push(new int[]{curI,curJ});
                if(isPath(curI-1,curJ-1,color)){
                    return true;
                }else
                    path.pop();
            }
        }
        if(isMovable(curI,curJ,1) == color){
            if(visited[curI-1][curJ] == 0 ||
                    (visited[curI-1][curJ] == 1 && path.firstElement()[0] == curI-1 && path.firstElement()[1] == curJ  &&
                            !(path.lastElement()[0] == curI-1 && path.lastElement()[1] == curJ))){
                path.push(new int[]{curI,curJ});
                if(isPath(curI-1,curJ,color)){
                    return true;
                }else
                    path.pop();
            }
        }
        if(isMovable(curI,curJ,2) == color){
            if(visited[curI-1][curJ+1] == 0 ||
                    (visited[curI-1][curJ+1] == 1 && path.firstElement()[0] == curI-1 && path.firstElement()[1] == curJ+1  &&
                            !(path.lastElement()[0] == curI-1 && path.lastElement()[1] == curJ+1))){
                path.push(new int[]{curI,curJ});
                if(isPath(curI-1,curJ+1,color)){
                    return true;
                }else
                    path.pop();
            }
        }
        if(isMovable(curI,curJ,3) == color){
            if(visited[curI][curJ-1] == 0 ||
                    (visited[curI][curJ-1] == 1 && path.firstElement()[0] == curI && path.firstElement()[1] == curJ-1  &&
                            !(path.lastElement()[0] == curI && path.lastElement()[1] == curJ-1))){
                path.push(new int[]{curI,curJ});
                if(isPath(curI,curJ-1,color)){
                    return true;
                }else
                    path.pop();
            }
        }
        if(isMovable(curI,curJ,4) == color){
            if(visited[curI][curJ+1] == 0 ||
                    (visited[curI][curJ+1] == 1 && path.firstElement()[0] == curI && path.firstElement()[1] == curJ+1  &&
                            !(path.lastElement()[0] == curI && path.lastElement()[1] == curJ+1))){
                path.push(new int[]{curI,curJ});
                if(isPath(curI,curJ+1,color)){
                    return true;
                }else
                    path.pop();
            }
        }
        if(isMovable(curI,curJ,5) == color){
            if(visited[curI+1][curJ-1] == 0 ||
                    (visited[curI+1][curJ-1] == 1 && path.firstElement()[0] == curI+1 && path.firstElement()[1] == curJ-1  &&
                            !(path.lastElement()[0] == curI+1 && path.lastElement()[1] == curJ-1))){
                path.push(new int[]{curI,curJ});
                if(isPath(curI+1,curJ-1,color)){
                    return true;
                }else
                    path.pop();
            }
        }
        if(isMovable(curI,curJ,6) == color){
            if(visited[curI+1][curJ] == 0 ||
                    (visited[curI+1][curJ] == 1 && path.firstElement()[0] == curI+1 && path.firstElement()[1] == curJ  &&
                            !(path.lastElement()[0] == curI+1 && path.lastElement()[1] == curJ))){
                path.push(new int[]{curI,curJ});
                if(isPath(curI+1,curJ,color)){
                    return true;
                }else
                    path.pop();
            }
        }
        if(isMovable(curI,curJ,7) == color){
            if(visited[curI+1][curJ+1] == 0 ||
                    (visited[curI+1][curJ+1] == 1 && path.firstElement()[0] == curI+1 && path.firstElement()[1] == curJ+1  &&
                            !(path.lastElement()[0] == curI+1 && path.lastElement()[1] == curJ+1))){
                path.push(new int[]{curI,curJ});
                if(isPath(curI+1,curJ+1,color)){
                    return true;
                }else
                    path.pop();
            }
        }
        visited[curI][curJ] = 2;
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
            if(i > 0 && j > 0)
                return cells_status[i-1][j-1];
            else return -10;
        }
        else if(direction == 1){
            if(i > 0)
                return cells_status[i-1][j];
            else return -10;
        }
        else if(direction == 2){
            if(i > 0 && j < sideSize-1)
                return cells_status[i-1][j+1];
            else return -10;
        }
        else if(direction == 3){
            if(j > 0)
                return cells_status[i][j-1];
            else return -10;
        }
        else if(direction == 4){
            if(j < sideSize-1)
                return cells_status[i][j+1];
            else return -10;
        }
        else if(direction == 5){
            if(i < sideSize-1 && j > 0)
                return cells_status[i+1][j-1];
            else return -10;
        }
        else if(direction == 6){
            if(i < sideSize-1)
                return cells_status[i+1][j];
            else return -10;
        }
        else {
            if(i < sideSize-1 && j < sideSize-1)
                return cells_status[i+1][j+1];
            else return -10;
        }
    }

    private byte getOpposite(byte status){
        return (byte)(status == 1 ? 2 : 1);
    }
}
