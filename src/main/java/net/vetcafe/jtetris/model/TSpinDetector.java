package net.vetcafe.jtetris.model;

final class TSpinDetector {
    private TSpinDetector() {}

    static boolean isTSpin(Tetromino tetromino, TetrominoType[][] grid, boolean lastActionWasRotation) {
        if (!lastActionWasRotation || tetromino == null || tetromino.getType() != TetrominoType.T) {
            return false;
        }

        int pivotX = tetromino.getX() + 1;
        int pivotY = tetromino.getY() + 1;
        int occupiedCorners = 0;
        int[][] corners = {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};

        for (int[] corner : corners) {
            int x = pivotX + corner[0];
            int y = pivotY + corner[1];
            if (isOccupiedOrWall(grid, x, y)) {
                occupiedCorners++;
            }
        }

        return occupiedCorners >= 3;
    }

    private static boolean isOccupiedOrWall(TetrominoType[][] grid, int x, int y) {
        if (x < 0 || x >= Board.WIDTH || y >= Board.HEIGHT) {
            return true;
        }
        if (y < 0) {
            return false;
        }
        return grid[y][x] != null;
    }
}
