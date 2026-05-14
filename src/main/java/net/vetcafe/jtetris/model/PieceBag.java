package net.vetcafe.jtetris.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Generates tetromino types using a 7-bag randomizer.
 */
public class PieceBag {
    private final Random random;
    private final ArrayDeque<TetrominoType> queue = new ArrayDeque<>();

    public PieceBag(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public TetrominoType next() {
        if (queue.isEmpty()) {
            refillBag();
        }
        return queue.removeFirst();
    }

    public void reset() {
        queue.clear();
    }

    private void refillBag() {
        List<TetrominoType> shuffled = new ArrayList<>(List.of(TetrominoType.values()));
        Collections.shuffle(shuffled, random);
        queue.addAll(shuffled);
    }
}



