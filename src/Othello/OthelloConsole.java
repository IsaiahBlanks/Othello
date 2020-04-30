package Othello;

import java.util.Scanner;

public class OthelloConsole {
    private OthelloMain.OthelloModelInterface othelloModel;

    public OthelloConsole(OthelloMain.OthelloModelInterface model) {
        othelloModel = model;
        startGame();
    }

    private int[] queueForNextMove() {
        Scanner scanner = new Scanner(System.in);
        String player = othelloModel.getCurrentTurn() ? "Black" : "White";
        System.out.println(player + ", enter row to place at: ");
        int row = scanner.nextInt();
        System.out.println(player + ", enter column to place at: ");
        int column = scanner.nextInt();
        return new int[]{row, column};
    }

    private void startGame() {
        boolean movesLeft = true;
        printBoard();
        boolean successfulTurn = false;
        while (movesLeft) {
            try {
                successfulTurn = othelloModel.turn(queueForNextMove());
            } catch (Exception e) {
                System.out.println("Invalid move!");
            }
            if(successfulTurn) {
                movesLeft = othelloModel.checkForMovesLeft();
            }
            successfulTurn = false;
            printBoard();
        }
    }

    private void printBoard() {
        OthelloMain.OthelloModelInterface.SquareStates[][] othelloBoard = othelloModel.getBoard();
        for (OthelloMain.OthelloModelInterface.SquareStates[] squareStates : othelloBoard) {
            for (OthelloMain.OthelloModelInterface.SquareStates squareState : squareStates) {
                if (squareState == OthelloMain.OthelloModelInterface.SquareStates.EMPTY) {
                    System.out.print("[ ]");
                } else if (squareState == OthelloMain.OthelloModelInterface.SquareStates.WHITE) {
                    System.out.print("[O]");
                } else {
                    System.out.print("[X]");
                }
            }
            System.out.println();
        }
    }
}
