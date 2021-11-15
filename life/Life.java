package life;

import bigGrid.BigGrid;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Life extends AbstractLife {
    public Life() {
        super();
    }

    @Override
    @SuppressWarnings("SpellCheckingInspection")
    public void step() {
        record Point(int x, int y) { }
        List<Point> pendingList = new ArrayList<>();

        // +--------------------------------------+
        // | Start Rules of Conway's Game of Life |
        // +--------------------------------------+
        BigGrid.Boundary boundary = super.grid.getBoundary();
        for (int x = boundary.left() - 1; x <= boundary.right() + 1; ++x) {
            for (int y = boundary.bottom() - 1; y <= boundary.top() + 1; ++y) {
                // For each point...
                final boolean isAlive = super.grid.getCellValue(x, y) != getDefaultColor();
                final int surroundingAlives = getSurroundingAlives(x, y, null);
                final Point point = new Point(x, y);

                if (isAlive) {
                    // When alive...
                    if (surroundingAlives < 2) {
                        // Die.
                        pendingList.add(point);
                    }
                    // else if (surroundingAlives < 4) {
                        // Keep.
                    // }
                    else if (surroundingAlives > 3) {
                        // Starve.
                        pendingList.add(point);
                    }
                }
                else {
                    // When died...
                    if (surroundingAlives == 3) {
                        // Birth.
                        pendingList.add(point);
                    }
                }
            }
        }
        // +--------------------------------------+
        // |  End Rules of Conway's Game of Life  |
        // +--------------------------------------+


        for (Point c : pendingList) {
            toggle(c.x, c.y);
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
            add(Color.BLACK);
        }};
    }

    @Override
    protected Color getNextColor(Color of) {
        return of == Color.WHITE
            ? Color.BLACK
            : Color.WHITE;
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
