package life;

import bigGrid.BigGrid;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Gomoku extends AbstractLife {
    // The computer pretend to think for a while for each move.
    protected final static int COMPUTER_THINKING_TIME_MS = 700;
    protected final static boolean SHOULD_PRETEND = false;

    protected boolean isBlackTurn = true;

    // Default GREEN color is too bright.
    protected final static Color GREEN_COLOR =
        Color.GREEN.darker();

    protected final Set<Color> PALETTE = new HashSet<>() {{
        add(GREEN_COLOR);
        add(Color.BLACK);
        add(Color.WHITE);
    }};

    // AlphaCat is a silly Gomoku AI written by me in 2018.
    // Modified to be compatible with AbstractLife.
    // The name was just for fun.
    protected final AlphaCat.IGameDelegate gameDelegate =
        new AlphaCat.IGameDelegate() {
        @Override
        public BigGrid.Boundary getBoundary() {
            return Gomoku.this.getBoundary();
        }

        @Override
        public void resetBoard() {
            Gomoku.this.reset();
        }

        @Override
        public int getPoint(int x, int y) {
            return rawValueFromColor(Gomoku.this.getColor(x, y));
        }

        @Override
        public void setPoint(int x, int y, int who) {
            Gomoku.this.grid.setCellValue(x, y, colorFromRawValue(who));
        }

        // BLACK=2, WHITE=1, GREEN=0
        private int rawValueFromColor(Color color) {
            if (color == Color.BLACK) {
                return 2;
            }
            if (color == Color.WHITE) {
                return 1;
            }
            return 0;
        }

        // BLACK=2, WHITE=1, GREEN=0
        private Color colorFromRawValue(int value) {
            return switch (value) {
                case 2 -> Color.BLACK;
                case 1 -> Color.WHITE;
                default -> GREEN_COLOR;
            };
        }
    };
    protected final AlphaCat cat = new AlphaCat(gameDelegate);

    public Gomoku() {
        super();
    }

    public AlphaCat getCat() {
        return cat;
    }

    /**
     * Did anyone just achieved a 5 combo?
     *
     * @return true: Yes. false: No.
     */
    protected boolean checkWinner() {
        final int winner = cat.the_winner_is();
        if (winner == AlphaCat.WHITE) {
            // Models should not affect back to UI
            // but this is only for convenience.
            JOptionPane.showMessageDialog(null, "White win!");
            return true;
        }
        else if (winner == AlphaCat.BLACK) {
            JOptionPane.showMessageDialog(null, "Black win!");
            return true;
        }
        return false;
    }

    @Override
    public Color getDefaultColor() {
        return GREEN_COLOR;
    }

    @Override
    public Set<Color> getPalette() {
        return PALETTE;
    }

    @Override
    protected Color getNextColor(Color of) {
        if (of == GREEN_COLOR) {
            return isBlackTurn
                ? Color.BLACK
                : Color.WHITE;
        }
        return of;
    }

    @Override
    protected int getMinimumBoundaryWidth() {
        return 10;
    }

    @Override
    protected int getMinimumBoundaryHeight() {
        return 10;
    }

    @Override
    public void step() {
        cat.computer_turn(AlphaCat.WHITE);

        // invokeLater to ensure some time for updating UI.
        SwingUtilities.invokeLater(this::checkWinner);
    }

    @Override
    public void toggle(int x, int y) {
        if (getColor(x, y) == GREEN_COLOR) {
            // super.toggle(x, y);
            cat.place(x, y, isBlackTurn ? AlphaCat.BLACK : AlphaCat.WHITE);

            final Runnable checkAndStep = () -> {
                if (!checkWinner()) {
                    step();
                }
            };

            if (SHOULD_PRETEND) {
                new Thread(() -> {
                    try {
                        Thread.sleep(COMPUTER_THINKING_TIME_MS);
                    }
                    catch (InterruptedException e) {
                        // Ignored.
                    }
                    SwingUtilities.invokeLater(checkAndStep);
                }).start();
            }
            else {
                SwingUtilities.invokeLater(checkAndStep);
            }

        }
    }

    @Override
    protected int getSurroundingAlives(int x, int y, Color color) {
        System.out.println("Warning: getSurroundingAlives is" +
            " not applicable for Gomoku.");
        return 0;
    }

    @Override
    protected void die(int x, int y) {
        System.out.println("Warning: die is" +
            " not applicable for Gomoku.");
    }

    @Override
    public void generate(int width, int height, int n) {
        System.out.println("Warning: generate is" +
            " not applicable for Gomoku.");
    }

    @Override
    public BigGrid.Boundary getBoundary() {
        BigGrid.Boundary boundary = super.getBoundary();

        // Prevent boundaries.
        return new BigGrid.Boundary(
            boundary.top() + 2,
            boundary.right() + 2,
            boundary.bottom() - 2,
            boundary.left() - 2
        );
    }
}
