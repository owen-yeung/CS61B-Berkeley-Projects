package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author Owen
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }
    /** Helper for tilt()
     *  Returns the y-coordinate of the merge candidate
     *  Returns 0 if there is no merge candidate (i.e. all tiles above are empty)
     *  Assumes a merge candidate exists (i.e. rest of column is nonempty)
     *  Assumes we are not on the top row (i.e. y is not 3)*/
    public static int findMergeCandidateRow(Board b, int x, int y) {
        // Iterate over the y-coordinate, starting from the tile above
        for (int i = y+1;i<b.size();i=i+1){
            // If tile is empty, move up to the next tile in the column
            if (b.tile(x,i) == null){
                continue;
            } else{
                //If nonempty, return the current y-coordinate
//                System.out.println("Merge candidate found at y="+i);
                return i;
            }
        }
        return 0;
    }
    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;
        this.board.setViewingPerspective(side);

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.

        // Iterate across columns
        for (int x = 0; x<this.board.size(); x=x+1){
            int highestMerge = 4;
            // Iterate down the column, starting with y=2
            for (int y = 2; y>=0; y=y-1){
                // Grab the current tile
                Tile t = this.board.tile(x,y);
//                // Test Code
//                if (t != null) {
//                    this.board.move(x, y + 1, t);
//                    changed = true;
//                }
                // If current tile is empty, move on to the next tile
                if (t == null){
                    continue;
                }

                // Find the y-coordinate of the merge candidate
                int cy = findMergeCandidateRow(this.board,x,y);
                // If there is no merge candidate, move directly to the edge
                if (cy==0){
                    this.board.move(x,this.board.size()-1,t);
                    changed = true;
                }
                // If we can merge, move onto the merge candidate and update the score
                else if (t.value() == this.board.tile(x,cy).value() && cy < highestMerge){
//                    System.out.println("Merge approved: Moving " + t.value() + " to " + this.board.tile(x,cy).value());
                    this.score = this.score + t.value()*2;
                    this.board.move(x,cy,t);
                    changed = true;
                    highestMerge = cy;
                }
                // If we cannot merge, move to the tile directly below the merge candidate
                else {
                    if (cy-1==y){
                        continue;
                    }
                    this.board.move(x,cy-1,t);
                    changed = true;
                }
            }
        }
        this.board.setViewingPerspective(Side.NORTH);

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        for (int y = 0; y < b.size(); y=y+1){
            for (int x=0;x<b.size();x=x+1){
                if (b.tile(x,y)==null){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        for (int y = 0; y < b.size(); y=y+1){
            for (int x=0;x<b.size();x=x+1){
                if (b.tile(x,y) == null){
                    continue;
                }
                if (b.tile(x,y).value()==MAX_PIECE){
                    return true;
                }
            }
        }
        return false;
    }
    /** Helper for atLeastOneMoveExists:
     * Returns true if an adjacent tile has the same value
     * NOTE: Do not input an empty coordinate
     */
    public static boolean equalsAdjacent(Board b,int x, int y){
        int val = b.tile(x,y).value();
        if (x>0 && b.tile(x-1,y).value()==val) {
            return true;
        } else if (x<b.size()-1 && b.tile(x+1,y).value()==val){
            return true;
        } else if (y>0 && b.tile(x,y-1).value()==val) {
            return true;
        } else if (y<b.size()-1 && b.tile(x,y+1).value()==val){
            return true;
        }
        return false;
    }
    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */

    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        if (emptySpaceExists(b)){
            return true;
        }
        for (int y = 0; y < b.size(); y=y+1){
            for (int x=0;x<b.size();x=x+1){
                if (equalsAdjacent(b,x,y)){
                    return true;
                }
            }
        }
        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
