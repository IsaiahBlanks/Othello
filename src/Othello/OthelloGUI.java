package Othello;

import javax.swing.*;
import java.awt.*;

import static java.awt.GridBagConstraints.*;

public class OthelloGUI extends JFrame {
    private OthelloMain.OthelloModelInterface othelloModel;
    private OthelloModel.SquareStates[][] currentBoard;
    private JPanel othelloPanel;
    private ButtonOnPoint[][] buttonOnPoints = new ButtonOnPoint[8][8];
    private JLabel gameInfoLabel;
    private JLabel errorLabel;
    private JLabel scoreLabel;


    public OthelloGUI(OthelloMain.OthelloModelInterface model) {
        othelloModel = model;
        setTitle("Othello");
        super.setSize(700, 700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        setupResetButton(c);
        setupResetButtonAI(c);
        setupLabels(c);
        setupGamePanel(c);
        setupButtons();

        this.setVisible(true);
        paintBoard();
    }

    private void setupResetButton(GridBagConstraints c) {
        JButton resetButton = new JButton("New Game");
        resetButton.addActionListener(e -> {
            OthelloMain newGame = new OthelloMain("TwoPlayer");
            OthelloGUI.this.dispose();
        });
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weighty = 0.036;
        c.ipadx = 5;
        add(resetButton, c);
    }

    private void setupResetButtonAI(GridBagConstraints c) {
        JButton resetButtonAI = new JButton("New Game vs Computer");
        resetButtonAI.addActionListener(e -> {
            OthelloMain newGame = new OthelloMain("OnePlayer");
            OthelloGUI.this.dispose();
        });
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weighty = 0.036;
        c.ipadx = 5;
        add(resetButtonAI, c);
    }

    private void setupGamePanel(GridBagConstraints c) {
        othelloPanel = new JPanel();
        othelloPanel.setLayout(new GridLayout(8, 8));
        c.gridy = 2;
        c.gridx = 0;
        c.gridheight = 2;
        c.gridwidth = 3;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = BOTH;
        add(othelloPanel, c);
    }

    private void setupLabels(GridBagConstraints c) {
        gameInfoLabel = new JLabel(" Current turn is White");
        c.fill= HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 0.75;
        add(gameInfoLabel, c);
        scoreLabel = new JLabel(" Current Score: Black has 2 pieces and White has 2 pieces");
        c.gridy = 1;
        c.weighty = 0;
        add(scoreLabel, c);
        errorLabel = new JLabel();
        c.fill = NONE;
        c.anchor = LINE_END;
        c.gridy = 1;
        c.gridx = 2;
        c.gridwidth = 1;
        c.weightx = 0.25;
        c.weighty = 0.036;
        add(errorLabel, c);
        errorLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);
        String gameModeText = "Current game mode: ";
        gameModeText += othelloModel.getGameMode() ? "One Player" : "Two Player";
        JLabel gameMode = new JLabel(gameModeText);
        c.fill = NONE;
        c.anchor = LINE_END;
        c.gridy = 0;
        c.gridx = 2;
        c.gridwidth = 1;
        c.weightx = 0.25;
        c.weighty = 0.036;
        add(gameMode, c);
        gameMode.setVisible(true);

    }

    private void setupButtons() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                ButtonOnPoint button = new ButtonOnPoint(i, j);
                addButtonActionListener(button);
                buttonOnPoints[i][j] = button;
                othelloPanel.add(button);
            }
        }
    }

    private void addButtonActionListener(ButtonOnPoint button) {
        button.addActionListener(e -> {
            int x = (int) button.getPoint().getX();
            int y = (int) button.getPoint().getY();
            doTurnAtXY(x, y);
        });
    }

    public void doTurnAtXY(int x, int y) {
        String errorText = "";
        boolean AIMode = othelloModel.getGameMode();
        try {
            othelloModel.turn(new int[]{x, y});
        } catch (IllegalStateException ex) {
            System.out.println("Error: " + ex);
            errorText = "Game over!";
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex);
            errorText = "Invalid Move! Try Again!";
        }
        paintBoard();
        String currScore = othelloModel.updateScore();
        String currTurnString = othelloModel.getCurrentTurn() ? "Black" : "White";
        gameInfoLabel.setText(" Current turn is " + currTurnString);
        scoreLabel.setText(" " + currScore);
        errorLabel.setText(errorText);
        if (errorText.length() > 1) {
            errorLabel.setVisible(true);
        } else {
            errorLabel.setVisible(false);
            if(AIMode) {
                othelloModel.turn(othelloModel.checkForBestMove());
                paintBoard();
                currTurnString = othelloModel.getCurrentTurn() ? "Black" : "White";
                currScore = othelloModel.updateScore();
                gameInfoLabel.setText(" Current turn is " + currTurnString);
                scoreLabel.setText(" " + currScore);
            }
        }
    }

    public String getScore() {
        return scoreLabel.getText();
    }

    public String getErrorText() {
        return errorLabel.getText();
    }

    public String getGameInfoText() {
        return gameInfoLabel.getText();
    }

    private void getBoard() {
        currentBoard = othelloModel.getBoard();
    }

    private void paintBoard() {
        getBoard();
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Color pointColor;
                String pointText;
                if (currentBoard[i][j] == OthelloMain.OthelloModelInterface.SquareStates.EMPTY) {
                    pointColor = Color.WHITE;
                    pointText = "";
                } else if (currentBoard[i][j] == OthelloMain.OthelloModelInterface.SquareStates.WHITE) {
                    pointColor = new Color(224, 224, 224);
                    pointText = "O";
                } else {
                    pointColor = Color.GRAY;
                    pointText = "X";
                }
                buttonOnPoints[i][j].setBackground(pointColor);
                buttonOnPoints[i][j].setText(pointText);
            }
        }
    }

    static class ButtonOnPoint extends JButton {
        private Point point;
        ButtonOnPoint(int x, int y) {
            point = new Point(x, y);
        }

        Point getPoint() {
            return point;
        }
    }


}
