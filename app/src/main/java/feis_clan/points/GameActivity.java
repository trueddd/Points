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
import java.util.Locale;
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

    /**
     * Fills the field (GridLayout) with ImageView's
     * and sets ID and OnClickListener for them.
     */
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

    /**
     * OnClickListener for points on the field.
     */
    private View.OnClickListener cellClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setPoint(v);
        }
    };

    /**
     * Generate short Toast when player tries to place his point on already occupied point.
     */
    private void filledPointToast(Context context){
        Toast.makeText(context,"There is a point already!",Toast.LENGTH_SHORT).show();
    }

    /**
     * Toggles the turn after one of players placed his point.
     */
    private void toggleTurn(){
        turn = !turn;
    }

    /**
     * Sets just placed point in matrix of points.
     * Runs methods to check captured zones.
     * @param view - view of just placed point.
     */
    private void setPoint(View view){
        int cell_id[] = getIDbyGridID(view.getId());
        if(cells_status[cell_id[0]][cell_id[1]] == 0){
            cells_status[cell_id[0]][cell_id[1]] = (byte)(turn ? 1 : 2);
            ImageView image=(ImageView)findViewById(view.getId());
            image.setImageResource(turn ? R.drawable.red_cell_10px : R.drawable.blue_cell_10px);
            checkClosedField(cell_id[0],cell_id[1]);
            recountScore();
            toggleTurn();
        }
        else {
            filledPointToast(getApplicationContext());
        }
    }

    /**
     * Gets (x,y) coordinates of point by ID of point in GridLayout.
     * @param id - ID of point in GridLayout.
     * @return array of coordinates (e.g. [3,2])
     */
    private int[] getIDbyGridID(int id){
        int cellID[] = {0, 0};
        while (id > sideSize){
            id = id-sideSize;
            cellID[0]++;
        }
        cellID[1] = id-1;
        return cellID;
    }

    /**
     * Checks if just placed point finishes any loop of points of the same color.
     * @param startI - coordinate y of just placed point.
     * @param startJ - coordinate x of just placed point.
     */
    private void checkClosedField(int startI, int startJ){
        visited = getEmptyArray();
        path.clear();
        sI = startI;
        sJ = startJ;
        byte color = cells_status[startI][startJ];
        if(isPath(startI,startJ,color)){
            if(turn){
                redScore += capture((byte)2);
            }else
                blueScore += capture((byte)1);
        }
    }

    /**
     * Recounts the score according the amount of points on the field.
     * Score directly depends on amount of points on the field.
     */
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
        ((TextView)findViewById(R.id.redScoreText)).setText(String.format(Locale.getDefault(),"%d", redScore));
        ((TextView)findViewById(R.id.blueScoreText)).setText(String.format(Locale.getDefault(),"%d", blueScore));
    }

    /**
     * Captures occupied points.
     * @param colorToCapture - color of points that can be captured.
     * @return - the amount of captured points.
     */
    private int capture(byte colorToCapture){
        int count = 0;
        path.pop();
        ArrayList<int[]> list = new ArrayList<>(path);
        wallToString(list);

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

    /**
     * Checks if the point with given coordinates is surrounded by points of opposite color.
     * @param wall - ArrayList of loop-forming points.
     * @param curI - y coordinate of current point.
     * @param curJ - x coordinate of current point.
     * @return true if point is surrounded, false - other way.
     */
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

    /**
     * Builds a string of points' coordinates and prints the string to Log.
     * @param wall - ArrayList of loop-forming points.
     */
    private void wallToString(ArrayList<int[]> wall){
        StringBuilder builder = new StringBuilder();
        for (int[] aWall : wall) {
            builder.append(aWall[0]);
            builder.append(":");
            builder.append(aWall[1]);
            builder.append(" ");
        }
        Log.i("wall", "The wall is "+builder.toString());
    }

    /**
     * 4 methods below check if point is locked from top/left/right/bottom by points from loop-forming array.
     * @param wall - loop-forming array of points.
     * @param curI - y coordinate of current point.
     * @param curJ - x coordinate of current point.
     * @param checkingPointColor - color of current point.
     * @return true if point is locked from top/left/right/bottom, false - other way.
     */
    private boolean isUpLocked(ArrayList<int[]> wall, int curI, int curJ, byte checkingPointColor) {
        byte isMove = isMovable(curI, curJ, 1);
        if (isMove == getOpposite(checkingPointColor) && isFromWall(wall, curI - 1, curJ)) {
            return true;
        }
        return isMove != -10 && isUpLocked(wall, curI - 1, curJ, checkingPointColor);
    }

    private boolean isLeftLocked(ArrayList<int[]> wall, int curI, int curJ, byte checkingPointColor) {
        byte isMove = isMovable(curI, curJ, 3);
        if (isMove == getOpposite(checkingPointColor) && isFromWall(wall, curI, curJ - 1))
            return true;
        return isMove != -10 && isLeftLocked(wall, curI, curJ - 1, checkingPointColor);
    }

    private boolean isRightLocked(ArrayList<int[]> wall, int curI, int curJ, byte checkingPointColor) {
        byte isMove = isMovable(curI, curJ, 4);
        if (isMove == getOpposite(checkingPointColor) && isFromWall(wall, curI, curJ + 1))
            return true;
        return isMove != -10 && isRightLocked(wall, curI, curJ + 1, checkingPointColor);
    }

    private boolean isDownLocked(ArrayList<int[]> wall, int curI, int curJ, byte checkingPointColor) {
        byte isMove = isMovable(curI, curJ, 6);
        if (isMove == getOpposite(checkingPointColor) && isFromWall(wall, curI + 1, curJ))
            return true;
        return isMove != -10 && isDownLocked(wall, curI + 1, curJ, checkingPointColor);
    }

    /**
     * Checks if given point belongs to given array of points.
     * @param wall - ArrayList of points.
     * @param i - y coordinate of given point.
     * @param j - x coordinate of given point.
     * @return true if point belongs to wall, false - other way.
     */
    private boolean isFromWall(ArrayList<int[]> wall, int i, int j){
        for(int[] point : wall){
            if(point[0] == i && point[1] == j){
                return true;
            }
        }
        return false;
    }

//    private void maskDangerousPlaces(){
//        //**
//        //*
//        for(int i=0;i<sideSize-1;i++){
//            for(int j=0;j<sideSize-1;j++){
//                if(cells_status[i][j] == cells_status[i][j+1] && cells_status[i][j] == cells_status[i+1][j]
//                        && cells_status[i][j] != 0){
//                    cells_status[i][j] = (byte)(cells_status[i][j] == 1 ? -1 : -2);
//                }
//            }
//        }
//        //**
//        // *
//        for(int i=0;i<sideSize-1;i++){
//            for(int j=0;j<sideSize-1;j++){
//                if(cells_status[i][j] == cells_status[i][j+1] && cells_status[i][j] == cells_status[i+1][j+1]
//                        && cells_status[i][j] != 0){
//                    cells_status[i][j+1] = (byte)(cells_status[i][j] == 1 ? -1 : -2);
//                }
//            }
//        }
//        // *
//        //**
//        for(int i=0;i<sideSize-1;i++){
//            for(int j=1;j<sideSize;j++){
//                if(cells_status[i][j] == cells_status[i+1][j] && cells_status[i][j] == cells_status[i+1][j-1]
//                        && cells_status[i][j] != 0){
//                    cells_status[i][j] = (byte)(cells_status[i+1][j] == 1 ? -1 : -2);
//                }
//            }
//        }
//        //*
//        //**
//        for(int i=0;i<sideSize-1;i++){
//            for(int j=0;j<sideSize-1;j++){
//                if(cells_status[i][j] == cells_status[i+1][j] && cells_status[i][j] == cells_status[i+1][j+1]
//                        && cells_status[i][j] != 0){
//                    cells_status[i+1][j] = (byte)(cells_status[i+1][j] == 1 ? -1 : -2);
//                }
//            }
//        }
//    }
//
//    private void unmask(){
//        for(int i=0;i<sideSize;i++){
//            for(int j=0;j<sideSize;j++){
//                if(cells_status[i][j] == -1){
//                    cells_status[i][j] = 1;
//                }
//                if(cells_status[i][j] == -2){
//                    cells_status[i][j] = 2;
//                }
//            }
//        }
//    }

    /**
     * Finds the loop starting from given point.
     * @param curI - y coordinate of given point.
     * @param curJ - x coordinate of given point.
     * @param color - color of given point.
     * @return true if the path was found, false - other way.
     */
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
     * Checks if the move can be performed to the given direction from given point.
     * @param i - y coordinate of given point.
     * @param j - x coordinate of given point.
     * @param direction:
     * 0 - top-left   1 - top   2 - top-right
     * 3 - left   4 - right
     * 5 - bottom-left   6 - bottom   7 - bottom-right
     * @return color of point placed to the given direction from given point,
     * returns -10 if bounds of field reached.
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
