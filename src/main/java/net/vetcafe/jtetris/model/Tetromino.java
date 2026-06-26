package net.vetcafe.jtetris.model;

import java.awt.Point;

public class Tetromino {
    private final TetrominoType type;
    private int rotation;
    private int x;
    private int y;

    public Tetromino(TetrominoType type, int x, int y) {
        this.type = type;
        this.rotation = 0;
        this.x = x;
        this.y = y;
    }

    public TetrominoType getType() {
        return type;
    }

    public Point[] getCells() {
        return type.cells(rotation);
    }

    public int getRotation() {
        return rotation;
    }

    public void rotateCW() {
        rotation = (rotation + 1) % 4;
    }

    public void rotateCCW() {
        rotation = (rotation + 3) % 4;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public Tetromino copy() {
        Tetromino t = new Tetromino(type, x, y);
        t.rotation = this.rotation;
        return t;
    }
}
