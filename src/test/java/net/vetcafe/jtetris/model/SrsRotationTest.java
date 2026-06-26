package net.vetcafe.jtetris.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SrsRotationTest {

    @Test
    void iPieceUsesDedicatedKickTable() {
        int[][] kicks = SrsKickTable.getKickOffsets(TetrominoType.I, 1, 2);
        assertArrayEquals(new int[] {0, 0}, kicks[0]);
        assertArrayEquals(new int[] {-1, 0}, kicks[1]);
        assertArrayEquals(new int[] {2, 0}, kicks[2]);
    }

    @Test
    void jlstzUsesStandardKickTable() {
        int[][] kicks = SrsKickTable.getKickOffsets(TetrominoType.T, 0, 1);
        assertArrayEquals(new int[] {0, 0}, kicks[0]);
        assertArrayEquals(new int[] {-1, 0}, kicks[1]);
        assertArrayEquals(new int[] {-1, -1}, kicks[2]);
    }

    @Test
    void iPieceCanRotateAtRightWallByKick() {
        Board board = new Board(7L);
        moveToCurrentType(board, TetrominoType.I);

        assertTrue(board.rotateCW());
        while (board.move(1, 0)) {
            // slide to right wall
        }

        assertTrue(board.rotateCW(), "I-piece should rotate with wall kick near right boundary");
    }

    private static void moveToCurrentType(Board board, TetrominoType target) {
        for (int i = 0; i < 28 && board.getCurrent().getType() != target; i++) {
            board.hardDrop();
        }
        assertTrue(board.getCurrent().getType() == target, "Target piece was not found in several 7-bag cycles");
    }
}
