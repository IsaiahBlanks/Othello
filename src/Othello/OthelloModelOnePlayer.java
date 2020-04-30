package Othello;

import java.util.ArrayList;
import java.util.Arrays;

public class OthelloModelOnePlayer implements OthelloMain.OthelloModelInterface {
    private SquareStates[][] othelloBoard;
    private boolean whoseTurn;
    private int whiteScore;
    private int blackScore;

    public OthelloModelOnePlayer() {
        setupBoard();
        //flag to set false when there is no valid move remaining
        whoseTurn = false; //false for white, true for black
    }

    public boolean getCurrentTurn() {
        return whoseTurn;
    }

    private void switchPlayer() {
        whoseTurn = !whoseTurn;
    }

    private void setupBoard() {
        othelloBoard = new SquareStates[8][8];
        for (SquareStates[] squareStates : othelloBoard) {
            Arrays.fill(squareStates, SquareStates.EMPTY);
        }
        othelloBoard[3][3] = SquareStates.WHITE;
        othelloBoard[4][4] = SquareStates.WHITE;
        othelloBoard[3][4] = SquareStates.BLACK;
        othelloBoard[4][3] = SquareStates.BLACK;
    }

    public boolean turn(int[] move) throws IllegalArgumentException {
        boolean movesLeft = checkForMovesLeft();
        if(!movesLeft) {
            throw new IllegalStateException("Game is Over!");
        }
        boolean validMove = checkMove(move);
        if(!validMove) {
            throw new IllegalArgumentException("Not a valid move!");
        }
        return true;
    }

    public String updateScore() {
        whiteScore = 0;
        blackScore = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(othelloBoard[i][j] == SquareStates.WHITE) {
                    whiteScore++;
                } else if (othelloBoard[i][j] == SquareStates.BLACK) {
                    blackScore++;
                }
            }
        }
        return "Current Score: Black has " + blackScore + " pieces and White has " + whiteScore + " pieces";
    }

    public boolean checkForMovesLeft() {
        boolean movesLeft = false;
        boolean possibleMove = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int[] move = {i, j};
                try {
                    validateIfPossibleMove(move);
                } catch (Exception e) {
                    possibleMove = false;
                }
                if(possibleMove && isValid(move).size() > 0) {
                    movesLeft = true;
                }
                possibleMove = true;
            }
        }
        if(!movesLeft) {
            System.out.println("Game Over!");
        }
        return movesLeft;
    }

    public int[] checkForBestMove() {
        int[] bestMove;
        int bestMoveIndex = 0;
        int bestMoveScore = 0;
        updateScore();
        int currPlayerScore = whoseTurn ? blackScore : whiteScore;
        ArrayList<int[]> movesList = new ArrayList<>();
        boolean legalMove = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int[] move = {i, j};
                try {
                    validateIfPossibleMove(move);
                } catch (IllegalArgumentException ex) {
                    legalMove = false;
                }
                if(legalMove) {
                    int moveScore = isValid(move).size();
                    if (moveScore > 0) {
                        movesList.add(move);
                        if (currPlayerScore + moveScore > bestMoveScore) {
                            bestMoveIndex = movesList.size() - 1;
                            bestMoveScore = currPlayerScore + moveScore;
                        }
                    }
                }
                legalMove = true;
            }
        }
        bestMove = movesList.get(bestMoveIndex);
        return bestMove;
    }

    private boolean checkMove(int[] move) {
        SquareStates playerState = whoseTurn ? SquareStates.BLACK : SquareStates.WHITE;
        boolean validMove = false;
        validateIfPossibleMove(move);
        ArrayList<int[]> piecesToFlip = isValid(move);
        if(piecesToFlip.size() > 0) {
            //found a good move!
            validMove = true;
            othelloBoard[move[0]][move[1]] = playerState;
            flipPieces(piecesToFlip);
            switchPlayer();
        }
        piecesToFlip.clear();
        return validMove;
    }

    private ArrayList<int[]> isValid(int[] move) {
        SquareStates playerState = whoseTurn ? SquareStates.BLACK : SquareStates.WHITE;
        SquareStates notPlayerState = whoseTurn ? SquareStates.WHITE : SquareStates.BLACK;
        ArrayList<int[]> allPiecesToFlip = new ArrayList<>();
        int[][] validNextSquares = new int[8][2];
        int validSquaresCount = 0;
        for(int i = move[0] - 1; i <= move[0] + 1; i++) { //This for loop checks to see which of the surrounding squares
            for(int j = move[1] - 1; j <= move[1] + 1; j++) {  //from the suggested move contain pieces
                if(!(i == move[0] && j == move [1]) && ((i < 8 && j < 8) && (i >= 0 && j >= 0))) { //from the other player
                    if(othelloBoard[i][j] == notPlayerState) {
                        validNextSquares[validSquaresCount][0] = i;
                        validNextSquares[validSquaresCount][1] = j;
                        validSquaresCount++;
                    }
                }
            }
        }
        for(int i = 0; i < validSquaresCount; i++) {
            int[] displacementVector = new int[]{move[0] - validNextSquares[i][0], move[1] - validNextSquares[i][1]};
            //displacement vector determines the direction the potentially valid move is in (either [(1,0), (0,1),
            //(-1,0), (0,-1), (1,1), (-1,1), (1,-1), (-1, -1)]
            int checkRow = validNextSquares[i][0];
            int checkColumn = validNextSquares[i][1];
            ArrayList<int[]> piecesToFlip = new ArrayList<>();
            while (validateCheckRowAndColumn(checkRow, checkColumn) &&
                    othelloBoard[checkRow][checkColumn] == notPlayerState) {
                int flipRow = validNextSquares[i][0];
                int flipColumn = validNextSquares[i][1];
                int[] flipPosition = new int[]{flipRow, flipColumn};
                piecesToFlip.add(flipPosition);
                validNextSquares[i][0] -= displacementVector[0]; //The next square past the one we found was also
                validNextSquares[i][1] -= displacementVector[1];//The other color, so move to check the next one
                checkRow -= displacementVector[0];              //in the same direction
                checkColumn -= displacementVector[1];
            }
            if (validateCheckRowAndColumn(checkRow, checkColumn) &&
                    othelloBoard[validNextSquares[i][0]][validNextSquares[i][1]] == playerState) {
                allPiecesToFlip.addAll(piecesToFlip);
            }

        }
        return allPiecesToFlip;
    }

    private boolean validateCheckRowAndColumn(int row, int column) {
        return row >= 0 && row <= 7 && column >= 0 && column <= 7;
    }

    private void validateIfPossibleMove(int[] move) {
        if((move[0] > 7 || move[0] < 0) || (move[1] > 7 || move[1] < 0)) {
            throw new IllegalArgumentException("This is not a square");
        }
        if(othelloBoard[move[0]][move[1]] != SquareStates.EMPTY) {
            throw new IllegalArgumentException("This square is not empty");
        }
    }

    private void flipPieces(ArrayList<int[]> piecesToFlip) {
        SquareStates playerState = whoseTurn ? SquareStates.BLACK : SquareStates.WHITE;
        for(int[] x : piecesToFlip) {
            othelloBoard[x[0]][x[1]] = playerState;
        }
    }

    @Override
    public boolean getGameMode() {
        return true; //AI mode, so return true
    }

    public SquareStates[][] getBoard() {
        SquareStates[][] othelloCopy = new SquareStates[8][8];
        for (int i = 0; i < othelloBoard.length; i++) {
            System.arraycopy(othelloBoard[i], 0, othelloCopy[i], 0, othelloBoard[i].length);
        }
        return othelloCopy;
    }

}
