import Othello.OthelloGUI;
import Othello.OthelloMain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class OthelloTests {

    @Test
    void othelloMove() {
        OthelloMain main = new OthelloMain();
        OthelloGUI gui = main.getOthelloGUI();
        gui.doTurnAtXY(3, 5);
        assertEquals(gui.getScore(), " Current Score: Black has 1 pieces and White has 4 pieces");

    }

    @Test
    void othelloGameOver() {
        OthelloMain.OthelloModelInterface.SquareStates[][] customBoard =
                new OthelloMain.OthelloModelInterface.SquareStates[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                customBoard[i][j] = OthelloMain.OthelloModelInterface.SquareStates.BLACK;
            }
        }
        OthelloMain main = new OthelloMain(customBoard);
        OthelloGUI gui = main.getOthelloGUI();
        gui.doTurnAtXY(3, 5);
        assertEquals(gui.getErrorText(), "Game over!");
    }

    @Test
    void othelloCurrTurn() {
        OthelloMain main = new OthelloMain();
        OthelloGUI gui = main.getOthelloGUI();
        assertEquals(gui.getGameInfoText(), " Current turn is White");
        gui.doTurnAtXY(3, 5);
        assertEquals(gui.getGameInfoText(), " Current turn is Black");

    }
}
