package net.vetcafe.jtetris.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TSpinDetectorTest {

    @Test
    void detectsTSpinWhenThreeCornersAreOccupiedAfterRotation() {
        TetrominoType[][] grid = new TetrominoType[Board.HEIGHT][Board.WIDTH];
        Tetromino t = new Tetromino(TetrominoType.T, 4, 5); // pivot at (5, 6)

        grid[5][4] = TetrominoType.I;
        grid[5][6] = TetrominoType.I;
        grid[7][4] = TetrominoType.I;

        assertTrue(TSpinDetector.isTSpin(t, grid, true));
    }

    @Test
    void doesNotDetectWithoutRotationAction() {
        TetrominoType[][] grid = new TetrominoType[Board.HEIGHT][Board.WIDTH];
        Tetromino t = new Tetromino(TetrominoType.T, 4, 5);

        grid[5][4] = TetrominoType.I;
        grid[5][6] = TetrominoType.I;
        grid[7][4] = TetrominoType.I;

        assertFalse(TSpinDetector.isTSpin(t, grid, false));
    }

    @Test
    void doesNotDetectForNonTPieces() {
        TetrominoType[][] grid = new TetrominoType[Board.HEIGHT][Board.WIDTH];
        Tetromino l = new Tetromino(TetrominoType.L, 4, 5);

        grid[5][4] = TetrominoType.I;
        grid[5][6] = TetrominoType.I;
        grid[7][4] = TetrominoType.I;

        assertFalse(TSpinDetector.isTSpin(l, grid, true));
    }

    @Test
    void wallCountsAsOccupiedCorner() {
        TetrominoType[][] grid = new TetrominoType[Board.HEIGHT][Board.WIDTH];
        Tetromino t = new Tetromino(TetrominoType.T, -1, 5); // pivot at (0, 6), one left corner is wall

        grid[5][1] = TetrominoType.I;
        grid[7][1] = TetrominoType.I;

        assertTrue(TSpinDetector.isTSpin(t, grid, true));
    }
}
