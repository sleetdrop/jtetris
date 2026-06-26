package net.vetcafe.jtetris.model;

import java.awt.Point;

public enum TetrominoType {
    I(new Point[][] {
        {p(0, 1), p(1, 1), p(2, 1), p(3, 1)},
        {p(2, 0), p(2, 1), p(2, 2), p(2, 3)},
        {p(0, 2), p(1, 2), p(2, 2), p(3, 2)},
        {p(1, 0), p(1, 1), p(1, 2), p(1, 3)}
    }),
    O(new Point[][] {
        {p(1, 0), p(2, 0), p(1, 1), p(2, 1)},
        {p(1, 0), p(2, 0), p(1, 1), p(2, 1)},
        {p(1, 0), p(2, 0), p(1, 1), p(2, 1)},
        {p(1, 0), p(2, 0), p(1, 1), p(2, 1)}
    }),
    T(new Point[][] {
        {p(1, 0), p(0, 1), p(1, 1), p(2, 1)},
        {p(1, 0), p(1, 1), p(2, 1), p(1, 2)},
        {p(0, 1), p(1, 1), p(2, 1), p(1, 2)},
        {p(1, 0), p(0, 1), p(1, 1), p(1, 2)}
    }),
    S(new Point[][] {
        {p(1, 0), p(2, 0), p(0, 1), p(1, 1)},
        {p(1, 0), p(1, 1), p(2, 1), p(2, 2)},
        {p(1, 1), p(2, 1), p(0, 2), p(1, 2)},
        {p(0, 0), p(0, 1), p(1, 1), p(1, 2)}
    }),
    Z(new Point[][] {
        {p(0, 0), p(1, 0), p(1, 1), p(2, 1)},
        {p(2, 0), p(1, 1), p(2, 1), p(1, 2)},
        {p(0, 1), p(1, 1), p(1, 2), p(2, 2)},
        {p(1, 0), p(0, 1), p(1, 1), p(0, 2)}
    }),
    J(new Point[][] {
        {p(0, 0), p(0, 1), p(1, 1), p(2, 1)},
        {p(1, 0), p(2, 0), p(1, 1), p(1, 2)},
        {p(0, 1), p(1, 1), p(2, 1), p(2, 2)},
        {p(1, 0), p(1, 1), p(0, 2), p(1, 2)}
    }),
    L(new Point[][] {
        {p(2, 0), p(0, 1), p(1, 1), p(2, 1)},
        {p(1, 0), p(1, 1), p(1, 2), p(2, 2)},
        {p(0, 1), p(1, 1), p(2, 1), p(0, 2)},
        {p(0, 0), p(1, 0), p(1, 1), p(1, 2)}
    });

    private final Point[][] rotations;

    TetrominoType(Point[][] rotations) {
        this.rotations = rotations;
    }

    public Point[] cells(int rotationIndex) {
        return rotations[rotationIndex % 4];
    }

    public static int size() {
        return values().length;
    }

    private static Point p(int x, int y) {
        return new Point(x, y);
    }
}
