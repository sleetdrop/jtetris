package net.vetcafe.jtetris.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class ScoringRulesTest {

    @Test
    void comboAddsBonusOnConsecutiveLineClears() throws Exception {
        Board board = new Board(7L);

        setFilledRows(board, 1);
        invokeClearLines(board);
        assertEquals(100, board.getScore());
        assertEquals(0, board.getComboStreak());

        setFilledRows(board, 1);
        invokeClearLines(board);
        assertEquals(250, board.getScore(), "Second consecutive single should include combo bonus");
        assertEquals(1, board.getComboStreak());
    }

    @Test
    void backToBackTetrisGetsBonusAndSurvivesNoClear() throws Exception {
        Board board = new Board(7L);

        setFilledRows(board, 4);
        invokeClearLines(board);
        assertEquals(800, board.getScore());
        assertTrue(board.isBackToBackActive());

        clearGrid(board);
        invokeClearLines(board); // no clear should reset combo but keep B2B chain
        assertTrue(board.isBackToBackActive());
        assertEquals(0, board.getComboStreak());

        setFilledRows(board, 4);
        invokeClearLines(board);
        assertEquals(2000, board.getScore(), "Second tetris in B2B should gain 1.5x base score");
        assertTrue(board.getLastScoreEvent().contains("B2B"));
    }

    @Test
    void nonDifficultClearBreaksBackToBackChain() throws Exception {
        Board board = new Board(7L);

        setFilledRows(board, 4);
        invokeClearLines(board);
        assertTrue(board.isBackToBackActive());

        setFilledRows(board, 1);
        invokeClearLines(board);
        assertFalse(board.isBackToBackActive());

        setFilledRows(board, 4);
        invokeClearLines(board);
        assertEquals(1850, board.getScore(), "Second tetris after chain break should not get B2B bonus but still keeps combo bonus");
    }

    @Test
    void tSpinSingleUsesTSpinBaseScore() throws Exception {
        Board board = new Board(7L);

        setFilledRows(board, 1);
        setLastLockWasTSpin(board, true);
        invokeClearLines(board);

        assertEquals(800, board.getScore());
        assertTrue(board.getLastScoreEvent().startsWith("TSPIN_SINGLE"));
    }

    private static void invokeClearLines(Board board) throws Exception {
        Method clearLines = Board.class.getDeclaredMethod("clearLines");
        clearLines.setAccessible(true);
        clearLines.invoke(board);
    }

    private static void setFilledRows(Board board, int rowCount) throws Exception {
        TetrominoType[][] grid = getGrid(board);
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                grid[y][x] = null;
            }
        }
        for (int y = Board.HEIGHT - rowCount; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                grid[y][x] = TetrominoType.I;
            }
        }
        setLastLockWasTSpin(board, false);
    }

    private static void clearGrid(Board board) throws Exception {
        TetrominoType[][] grid = getGrid(board);
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                grid[y][x] = null;
            }
        }
        setLastLockWasTSpin(board, false);
    }

    private static TetrominoType[][] getGrid(Board board) throws Exception {
        Field field = Board.class.getDeclaredField("grid");
        field.setAccessible(true);
        return (TetrominoType[][]) field.get(board);
    }

    private static void setLastLockWasTSpin(Board board, boolean value) throws Exception {
        Field field = Board.class.getDeclaredField("lastLockWasTSpin");
        field.setAccessible(true);
        field.setBoolean(board, value);
    }
}


