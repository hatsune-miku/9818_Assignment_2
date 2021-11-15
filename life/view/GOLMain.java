package life.view;

import bigGrid.BigGrid;
import life.*;
import life.helper.GOLHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GOLMain extends JFrame {
    protected LifeLike life;

    // ====================
    // Game configurations.
    // ====================

    // How many milliseconds to refresh UI?
    protected final static int CLOCK_RATE_MS = 40;

    // How many milliseconds to go one frame of game?
    protected final static int FRAME_RATE_MS = CLOCK_RATE_MS * 3;
    protected int clockCounter = 0;

    protected final static Dimension SCREEN_SIZE =
        Toolkit.getDefaultToolkit().getScreenSize();

    // Window width and height.
    protected final static int WINDOW_WIDTH = 800;
    protected final static int WINDOW_HEIGHT = 600;

    // The default side length, thickness and minimum size limit
    // of each unit(cell) on the board.
    protected final static int DEFAULT_BLOCK_SIZE = 32;
    protected final static int BLOCK_BORDER_WIDTH = 1;
    protected final static int BLOCK_SIZE_MINIMUM = 8;

    // Mouse hover hightlight color.
    protected final static Color HIGHLIGHT_COLOR = Color.GRAY;

    // Dynamically set cell size.
    protected int blockSize = DEFAULT_BLOCK_SIZE;
    protected GOLHelper.Point lastToggledPoint = null;
    protected BigGrid.Boundary cachedBoundary;


    // ==============
    // State control.
    // ==============

    // Should it run the game frame by frame automatically?
    protected boolean isPlaying = false;

    // (Deprecated) Used to implement drag-to-draw.
    protected boolean isMousePressing = false;

    // (Gomoku Game Only) Is the first time user uses undo?
    // Used to show tips.
    protected boolean gomokuIsFirstUndo = true;

    // Where highlight point to draw.
    protected GOLHelper.Point hightlightPosition = null;

    // User interfaces.
    protected final JPanel panel;
    protected final JButton buttonShuffle;
    protected final JButton buttonStart;
    protected final JButton buttonStep;
    protected final JButton buttonEnd;
    protected final JButton buttonClear;
    protected final JButton buttonUndo;
    protected final JButton buttonHelp;
    protected final JRadioButton radioLife;
    protected final JRadioButton radioLifeAndDeath;
    protected final JRadioButton radioGomokuPve;
    protected final JPanel toolBar;
    protected final JPanel headerBar;
    protected final JTextField textGenerateAmount;


    public GOLMain(LifeLike life) {
        super();
        assert life != null;

        this.life = life;
        cachedBoundary = life.getBoundary();

        // Title and make center screen.
        setTitle("Game of Li😈fE");
        setResizable(false);
        setLocation(
            SCREEN_SIZE.width / 2 - WINDOW_WIDTH / 2,
            SCREEN_SIZE.height / 2 - WINDOW_HEIGHT / 2
        );

        // Exit on close.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Make panel double-buffered and drawable.
        panel = new JPanel(true) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                draw(g);
            }
        };

        // Init controls.
        toolBar              = new JPanel();
        headerBar            = new JPanel();
        buttonShuffle        = new JButton("Generate");
        buttonStart          = new JButton("Start");
        buttonStep           = new JButton("Step");
        buttonEnd            = new JButton("End");
        buttonClear          = new JButton("Clear");
        buttonUndo           = new JButton("Gomoku: Undo");
        buttonHelp           = new JButton("?");

        textGenerateAmount   = new JTextField("50", 6);
        radioLife            = new JRadioButton("Life", true);
        radioLifeAndDeath    = new JRadioButton("Life and Death", false);
        radioGomokuPve       = new JRadioButton("Gomoku (Human vs. Computer)", false);

        // Update window size.
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT + toolBar.getHeight());

        setupComponents();
    }

    private void setupComponents() {
        // Layout.
        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane header = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        toolBar.add(new JLabel("Random Cells: "));
        toolBar.add(textGenerateAmount);
        toolBar.add(buttonShuffle);
        toolBar.add(buttonStart);
        toolBar.add(buttonStep);
        toolBar.add(buttonEnd);
        toolBar.add(buttonClear);
        toolBar.add(buttonUndo);

        headerBar.add(new JLabel("Select Game: "));
        headerBar.add(radioLife);
        headerBar.add(radioLifeAndDeath);
        headerBar.add(radioGomokuPve);

        header.setTopComponent(headerBar);
        header.setBottomComponent(toolBar);

        pane.setDividerSize(0);
        pane.setTopComponent(header);
        pane.setBottomComponent(panel);
        setContentPane(pane);

        // Init state.
        buttonEnd.setEnabled(false);
        buttonUndo.setVisible(false);
        buttonUndo.setEnabled(false);
        gameRegenerate();

        // Events.
        buttonShuffle.addActionListener(action -> gameRegenerate());
        buttonStart.addActionListener(action -> gameStartPlaying());
        buttonStep.addActionListener(action -> {
            life.step();
            updateBoundary();
            panel.updateUI();
        });
        buttonEnd.addActionListener(action -> gameStopPlaying());
        buttonClear.addActionListener(action -> gameClearScreen());
        buttonUndo.addActionListener(action -> {
            if (life instanceof Gomoku gomoku) {
                gomoku.getCat().undo();
                panel.updateUI();
                buttonUndo.setEnabled(false);

                if (gomokuIsFirstUndo) {
                    gomokuIsFirstUndo = false;
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        this, """
                            Undo finished.
                            Tips: You can only undo last one operation.

                            (√) Do not show again"""
                    ));
                }
            } // if gomoku
        }); // lambda

        radioLife.addActionListener(a -> gameSwitchToLife());
        radioLifeAndDeath.addActionListener(a -> gameSwitchToLifeAndDeath());
        radioGomokuPve.addActionListener(a -> gameSwitchToGomoku());

        // On panel clicked...
        panel.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) {
                if (isPlaying) {
                    return;
                }

                updateBoundary();
                GOLHelper.Point point = GOLHelper.blockPosFromRelativeCoord(
                    e.getX(), e.getY(), getBlockSize(), cachedBoundary
                );

                buttonUndo.setEnabled(true);

                // Try perform toggle.
                life.toggle(point.x(), point.y());

                hightlightPosition = null;
                lastToggledPoint = point;
                panel.updateUI();
            }
            @Override public void mousePressed(MouseEvent e) {
                isMousePressing = true;
            }
            @Override public void mouseReleased(MouseEvent e) {
                isMousePressing = false;
            }
            @Override public void mouseEntered(MouseEvent e) { }
            @Override public void mouseExited(MouseEvent e) {
                hightlightPosition = null;
                panel.updateUI();
            }
        });

        panel.addMouseMotionListener(new MouseMotionListener() {
            @SuppressWarnings("GrazieInspection")
            @Override
            public void mouseDragged(MouseEvent e) {
                // Drag-to-draw is deprecated
                // because of very occasionally used.
                /*
                *if (isPlaying) {
                    return;
                }

                GOLHelper.Point point = GOLHelper.blockPosFromRelativeCoord(
                    e.getX(), e.getY(), getBlockSize()
                );

                if (
                    isMousePressing && (
                        lastToggledPoint == null
                        || lastToggledPoint.x() != point.x()
                        || lastToggledPoint.y() != point.y()
                    )
                ) {
                    life.toggle(point.x(), point.y());
                    lastToggledPoint = point;
                    panel.updateUI();
                }
                 */
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (isPlaying) {
                    return;
                }

                updateBoundary();
                GOLHelper.Point pointAbsolute = GOLHelper.blockPosFromRelativeCoord(
                    e.getX(), e.getY(), getBlockSize()
                );
                GOLHelper.Point pointRelative = GOLHelper.blockPosFromRelativeCoord(
                    e.getX(), e.getY(), getBlockSize(), cachedBoundary
                );

                if (life.getColor(pointRelative.x(), pointRelative.y()) == life.getDefaultColor()) {
                    if (hightlightPosition == null || !hightlightPosition.equals(pointAbsolute)) {
                        hightlightPosition = pointAbsolute;
                        panel.updateUI();
                    }
                }
                else {
                    hightlightPosition = null;
                }
                // panel.updateUI();
            }
        });
    }

    private void updateBoundary() {
        cachedBoundary = life.getBoundary();
    }

    protected void gameSwitchToLife() {
        // Exclusive radio button.
        radioLifeAndDeath.setSelected(false);
        radioGomokuPve.setSelected(false);
        radioLife.setSelected(true);

        buttonUndo.setVisible(false);

        life = new Life();
        restoreInitButtonState();
        gameStopPlaying();
        gameRegenerate();
    }

    protected void gameSwitchToLifeAndDeath() {
        // Exclusive radio button.
        radioGomokuPve.setSelected(false);
        radioLife.setSelected(false);
        radioLifeAndDeath.setSelected(true);

        buttonUndo.setVisible(false);

        life = new LifeAndDeath();
        restoreInitButtonState();
        gameStopPlaying();
        gameRegenerate();
    }

    protected void gameSwitchToGomoku() {
        // Exclusive radio button.
        radioLife.setSelected(false);
        radioLifeAndDeath.setSelected(false);
        radioGomokuPve.setSelected(true);

        buttonUndo.setVisible(true);

        life = new Gomoku();
        gameStopPlaying();
        restoreInitButtonState();
        gameRegenerate();
    }

    protected void restoreInitButtonState() {
        if (radioLife.isSelected()) {
            buttonStart.setEnabled(true);
            buttonClear.setEnabled(true);
            buttonShuffle.setEnabled(true);
            buttonStep.setEnabled(true);
        }
        else if (radioLifeAndDeath.isSelected()) {
            buttonStart.setEnabled(true);
            buttonClear.setEnabled(true);
            buttonShuffle.setEnabled(true);
            buttonStep.setEnabled(true);
        }
        else if (radioGomokuPve.isSelected()) {
            buttonStart.setEnabled(false);
            buttonClear.setEnabled(true);
            buttonShuffle.setEnabled(false);
            buttonStep.setEnabled(false);
        }
    }

    @SuppressWarnings("BusyWait")
    protected void gameStartPlaying() {
        buttonEnd.setEnabled(true);
        buttonClear.setEnabled(false);
        buttonStart.setEnabled(false);
        buttonShuffle.setEnabled(false);
        buttonStep.setEnabled(false);

        isPlaying = true;

        new Thread(() -> {
            while (isPlaying) {
                try {
                    Thread.sleep(CLOCK_RATE_MS);
                }
                catch (InterruptedException e) {
                    break;
                }

                clockCounter += CLOCK_RATE_MS;
                if (clockCounter >= FRAME_RATE_MS) {
                    life.step();
                    clockCounter = 0;
                    updateBoundary();
                }

                SwingUtilities.invokeLater(panel::updateUI);
            }
            SwingUtilities.invokeLater(this::restoreInitButtonState);
        }).start();
    }

    protected void gameStopPlaying() {
        buttonEnd.setEnabled(false);
        buttonShuffle.setEnabled(true);
        isPlaying = false;
    }

    protected void gameClearScreen() {
        life.reset();

        this.blockSize = DEFAULT_BLOCK_SIZE;

        panel.updateUI();
    }

    protected void gameRegenerate() {
        this.blockSize = DEFAULT_BLOCK_SIZE;

        int amount;
        try {
            amount = Integer.parseInt(textGenerateAmount.getText());
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount!");
            return;
        }

        if (amount >= getGenerateAmountLimit()) {
            JOptionPane.showMessageDialog(this, "Amount too large! (No bigger than " + getGenerateAmountLimit() + ")");
            return;
        }

        if (life instanceof AbstractLife abstractLife) {
            abstractLife.reset();
            abstractLife.generate(
                WINDOW_WIDTH / blockSize,
                WINDOW_HEIGHT / blockSize,
                amount
            );
        }

        updateBoundary();
        panel.updateUI();
    }

    protected int getGenerateAmountLimit() {
        return WINDOW_WIDTH * WINDOW_HEIGHT / blockSize / blockSize;
    }

    protected void setBlockSize(int blockSize) {
        // System.out.println("GOLMain.setBlockSize: " + blockSize);
        if (blockSize < BLOCK_SIZE_MINIMUM) {
            return;
        }
        this.blockSize = blockSize;
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    void draw(Graphics g) {
        // Clear screen.
        g.setColor(life.getDefaultColor());
        g.clearRect(0, 0, panel.getWidth(), panel.getHeight());
        // g.fillRect(0, 0, panel.getWidth(), panel.getHeight());

        BigGrid.Boundary gameBoundary = life.getBoundary();

        // Draw grid.
        g.setColor(Color.lightGray);
        for (int y = 0; y < panel.getHeight(); y += blockSize) {
            g.drawLine(0, y, panel.getWidth(), y);
        }
        for (int x = 0; x < panel.getWidth(); x += blockSize) {
            g.drawLine(x, 0, x, panel.getHeight());
        }

        for (int i = gameBoundary.left(); i <= gameBoundary.right(); ++i) {
            for (int j = gameBoundary.bottom(); j <= gameBoundary.top(); ++j) {
                final int x = i - (gameBoundary.left());
                final int y = j - (gameBoundary.bottom());

                if (x >= 0 && y >= 0) {
                    g.setColor(life.getColor(i, j));
                    g.fillRect(
                        x * blockSize + BLOCK_BORDER_WIDTH,
                        y * blockSize + BLOCK_BORDER_WIDTH,
                        blockSize - 2 * BLOCK_BORDER_WIDTH,
                        blockSize - 2 * BLOCK_BORDER_WIDTH
                    );
                }
            }
        }

        // Draw highlight.
        if (hightlightPosition != null && life.getColor(hightlightPosition.x(), hightlightPosition.y()) == life.getDefaultColor()) {
            g.setColor(HIGHLIGHT_COLOR);
            g.fillRect(
                hightlightPosition.x() * blockSize + BLOCK_BORDER_WIDTH,
                hightlightPosition.y() * blockSize + BLOCK_BORDER_WIDTH,
                blockSize - 2 * BLOCK_BORDER_WIDTH,
                blockSize - 2 * BLOCK_BORDER_WIDTH
            );
        }

        tryUpdateViewArea(gameBoundary);
    }

    public void present() {
        setVisible(true);
    }

    protected void tryUpdateViewArea(BigGrid.Boundary gameBoundary) {
        // Update view area.
        int width = (gameBoundary.right() - gameBoundary.left()) + 6;
        int height = (gameBoundary.top() - gameBoundary.bottom()) + 6;
        int requiredBlockSize = getBlockSize();

        if (width > WINDOW_WIDTH / getBlockSize()) {
            requiredBlockSize = Math.min(
                requiredBlockSize, WINDOW_WIDTH / width
            );
        }

        if (height > WINDOW_HEIGHT / getBlockSize()) {
            requiredBlockSize = Math.min(
                requiredBlockSize, WINDOW_HEIGHT / height
            );
        }

        setBlockSize(requiredBlockSize);
    }
}
