package life;

import bigGrid.BigGrid;
import bigGrid.BigGridFactory;

import java.awt.*;
import java.util.Random;
import java.util.Set;

public abstract class AbstractLife implements LifeLike {
    public final BigGrid<Color> grid;

    /**
     * Deprecated. Used to implement a fixed boundary board.
     */
    @Deprecated
    protected BigGrid.Boundary worldBoundary = null;

    // Transfered from interface.
    @Override
    public abstract Color getDefaultColor();
    @Override
    public abstract Set<Color> getPalette();

    /**
     * Next color should be determined by subclasses.
     *
     * @return The color to be displayed after `of`.
     */
    protected abstract Color getNextColor(Color of);

    /**
     * AbstractLife will try to
     * center align the current boundary within MinWidth*MinHeight.
     *
     * @return The min boundary width required.
     */
    protected abstract int getMinimumBoundaryWidth();

    /**
     * AbstractLife will try to
     * center align the current boundary within MinWidth*MinHeight.
     *
     * @return The min boundary height required.
     */
    protected abstract int getMinimumBoundaryHeight();

    public AbstractLife() {
        grid = new BigGridFactory<Color>()
            .make(getDefaultColor());
        reset();
    }

    // One frame of Game of Life.
    @Override
    public abstract void step();

    /**
     * How many alive cells with color=`color` near (x, y)?
     * By passing `null` to `color` can wildcard all alive cells near by.
     *
     * @param color Specify what color to be count.
     *             Null indicates that any non-default color is ok.
     * @return Range from 0 inclusive to 8 inclusive.
     */
    @SuppressWarnings("SpellCheckingInspection")
    protected int getSurroundingAlives(int x, int y, Color color) {
        final int[][] turns = {
            { -1, 0 }, // left
            { -1, 1 }, // left-up
            {  0, 1 }, // up
            {  1, 1 }, // right-up
            {  1, 0 }, // right
            {  1,-1 }, // right-bottom
            {  0,-1 }, // bottom
            { -1,-1 }, // left-bottom
        };
        int ans = 0;
        boolean revert = false;

        if (color == null) {
            // Look for all non-default colors.
            color = getDefaultColor();
            revert = true;
        }

        for (int[] pair : turns) {
            final int px = x + pair[0];
            final int py = y + pair[1];

            /*
            *if (!isPointValid(px, py)) {
                continue;
            }
             */

            if (grid.getCellValue(px, py) == color) {
                ++ans;
            }
        } // for
        return revert ? 8 - ans : ans;
    }

    /**
     * Make a cell die by setting its color to its default color.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    protected void die(int x, int y) {
        grid.setCellValue(x, y, getDefaultColor());
    }

    /**
     * Generate `n` cells in area (0, 0) to (width, height) by random.
     * Each cell share the same possibility to be affected.
     */
    public void generate(int width, int height, int n) {
        Random random = new Random();
        Color[] palette = getPalette().toArray(new Color[0]);

        for (int i = 0; i < n; ++i) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            grid.setCellValue(x, y, palette[1 + random.nextInt(palette.length - 1)]);
        }
    }

    @Deprecated
    public void setWorldBoundary(BigGrid.Boundary worldBoundary) {
        this.worldBoundary = worldBoundary;
    }

    @Deprecated
    protected boolean isPointValid(int x, int y) {
        BigGrid.Boundary worldBoundary = getWorldBoundary();
        if (worldBoundary == null) {
            // No limit on points.
            return true;
        }

        return x > worldBoundary.left()
            && x < worldBoundary.right()
            && y > worldBoundary.bottom()
            && y < worldBoundary.top();
    }

    @Deprecated
    public BigGrid.Boundary getWorldBoundary() {
        return worldBoundary;
    }

    /**
     * Clear the board.
     */
    @Override
    public void reset() {
        grid.setDefaultValue(getDefaultColor());
    }

    /**
     * Toggle a cell according to `getNextColor`.
     */
    @Override
    public void toggle(int x, int y) {
        grid.setCellValue(
            x, y, getNextColor(grid.getCellValue(x, y))
        );
    }

    @Override
    public void setColor(int x, int y, Color color) {
        grid.setCellValue(x, y, color);
    }

    @Override
    public BigGrid.Boundary getBoundary() {
        BigGrid.Boundary res = grid.getBoundary();

        int left         = res.left();
        int top          = res.top();
        int right        = res.right();
        int bottom       = res.bottom();

        final int width  = right - left;
        final int height = top - bottom;

        // Center align.
        if (width < getMinimumBoundaryWidth()) {
            int delta = (getMinimumBoundaryWidth() - width) / 2 - 1;
            left -= delta;
            right += delta;
        }

        // Center align.
        if (height < getMinimumBoundaryHeight()) {
            int delta = (getMinimumBoundaryHeight() - height) / 2 - 1;
            top += delta;
            bottom -= delta;
        }

        return new BigGrid.Boundary(top, right, bottom, left);
    }

    @Override
    public Color getColor(int x, int y) {
        return grid.getCellValue(x, y);
    }
}
