package life.helper;

import bigGrid.BigGrid;


public class GOLHelper {
    public record Point(int x, int y) {
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Point p) {
                return p.x == this.x && p.y == this.y;
            }
            return false;
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static Point blockPosFromRelativeCoord(
        int x,
        int y,
        int blockSize,
        BigGrid.Boundary boundary
    ) {
        return new Point(
            x / blockSize + boundary.left(),
            y / blockSize + boundary.bottom()
        );
    }

    public static Point blockPosFromRelativeCoord(
        int x,
        int y,
        int blockSize
    ) {
        return new Point(
            x / blockSize,
            y / blockSize
        );
    }
}
