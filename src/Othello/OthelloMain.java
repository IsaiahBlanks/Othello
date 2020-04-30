package Othello;

public class OthelloMain {
    OthelloGUI othelloGUI;

    public OthelloMain() {
        OthelloModelInterface othelloModel = new OthelloModel();
        othelloGUI = new OthelloGUI(othelloModel);
    }

    public OthelloMain(String mode) {
        OthelloModelInterface othelloModel;
        if(mode.equals("OnePlayer")) {
            othelloModel = new OthelloModelOnePlayer();
            new OthelloGUI(othelloModel);
        } else if (mode.equals("TwoPlayer")) {
            othelloModel = new OthelloModel();
            new OthelloGUI(othelloModel);
        } else {
            othelloModel = new OthelloModel();
            new OthelloConsole(othelloModel);
        }

    }

    public OthelloMain(OthelloModelInterface.SquareStates[][] customBoard) {
        OthelloModelInterface othelloModel = new OthelloModel(customBoard);
        othelloGUI = new OthelloGUI(othelloModel);
    }

    public OthelloGUI getOthelloGUI() {
        return othelloGUI;
    }

    public interface OthelloModelInterface {
    enum SquareStates {EMPTY, BLACK, WHITE}

    boolean turn(int[] move);

    int[] checkForBestMove();

    boolean getCurrentTurn();

    boolean checkForMovesLeft();

    boolean getGameMode();

    OthelloModel.SquareStates[][] getBoard();

    String updateScore();
}

    public static void main(String[] args) {
        OthelloMain othello = new OthelloMain("OnePlayer");
    }


}
