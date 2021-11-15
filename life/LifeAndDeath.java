package life;

import bigGrid.BigGrid;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LifeAndDeath extends AbstractLife {
    public LifeAndDeath() {
        super();
    }

    @Override
    @SuppressWarnings("SpellCheckingInspection")
    public void step() {
        record Point(int x, int y) { }
        List<Point> pendingToggleList = new ArrayList<>();
        List<Point> dyingList = new ArrayList<>();

        // +--------------------------------------+
        // | Start Rules of Conway's Game of Life |
        // +--------------------------------------+
        BigGrid.Boundary boundary = super.grid.getBoundary();
        for (int x = boundary.left() - 1; x <= boundary.right() + 1; ++x) {
            for (int y = boundary.bottom() - 1; y <= boundary.top() + 1; ++y) {
                // For each point...
                final Color color = super.grid.getCellValue(x, y);
                final Point point = new Point(x, y);

                // White...
                if (color == Color.WHITE) {
                    if (getSurroundingAlives(x, y, Color.GREEN) >= 3) {
                        // g >= 3, W->G, Birth of greenery.
                        pendingToggleList.add(point);
                    }
                    // otherwise, keep.
                }
                // Green...
                else if (color == Color.GREEN) {
                    final int surrB = getSurroundingAlives(x, y, Color.BLACK);
                    final int surrG = getSurroundingAlives(x, y, Color.GREEN);

                    if (surrB == 1) {
                        // b = 1, G->W, Greenery eaten.
                        dyingList.add(point);
                    }
                    else if (surrG >= 6) {
                        // g >= 6, G->W, Overcrowding.
                        dyingList.add(point);
                    }
                    else if (surrB > 1) {
                        // b >= 2, G->B, Birth of a herbivore.
                        pendingToggleList.add(point);
                    }
                    // otherwise, keep.
                }
                // BLACK...
                else if (color == Color.BLACK) {
                    final int surrB = getSurroundingAlives(x, y, Color.BLACK);
                    final int surrG = getSurroundingAlives(x, y, Color.GREEN);
                    final int surrR = getSurroundingAlives(x, y, Color.RED);

                    if (surrR >= 2) {
                        // b >= 1 and r = 2, B->W, Herbivore eaten.
                        pendingToggleList.add(point);
                    }
                    else if (surrG == 0) {
                        // g = 0, B->W, Herbivore starves.
                        dyingList.add(point);
                    }
                    else if (surrB == 0) {
                        // b = 0, B->W, Dies of loneliness.
                        dyingList.add(point);
                    }
                }
                // Red...
                else if (color == Color.RED) {
                    final int surrB = getSurroundingAlives(x, y, Color.BLACK);
                    final int surrG = getSurroundingAlives(x, y, Color.GREEN);

                    if (surrB == 0 && surrG == 0) {
                        // r = 0, R->W, Loneliness.
                        dyingList.add(point);
                    }
                    // otherwise, keep.
                }
            }
        }
        // +--------------------------------------+
        // |  End Rules of Conway's Game of Life  |
        // +--------------------------------------+


        for (Point c : pendingToggleList) {
            toggle(c.x, c.y);
        }

        for (Point c : dyingList) {
            die(c.x, c.y);
        }
    }

    @Override
    public Color getDefaultColor() {
        return Color.WHITE;
    }

    @Override
    public Set<Color> getPalette() {
        return new HashSet<>() {{
            add(Color.WHITE);
            add(Color.GREEN);
            add(Color.BLACK);
            add(Color.RED);
        }};
    }

    @Override
    protected Color getNextColor(Color of) {
        // W G B R
        if (of == Color.WHITE) {
            return Color.GREEN;
        }
        if (of == Color.GREEN) {
            return Color.BLACK;
        }
        if (of == Color.BLACK) {
            return Color.RED;
        }
        if (of == Color.RED) {
            return Color.WHITE;
        }
        System.out.println("Warning: color not in palette. Restarting from WHITE.");
        return Color.WHITE;
    }

    @Override
    protected int getMinimumBoundaryWidth() {
        return 10;
    }

    @Override
    protected int getMinimumBoundaryHeight() {
        return 10;
    }
}
