package net.vetcafe.jtetris.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Board {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 22; // includes hidden rows at top
    private static final int LOCK_DELAY_TICKS = 1;
    private static final int NEXT_QUEUE_SIZE = 3;

    private final TetrominoType[][] grid = new TetrominoType[HEIGHT][WIDTH];
    private final PieceBag pieceBag;
    private final ArrayDeque<TetrominoType> nextQueue = new ArrayDeque<>(NEXT_QUEUE_SIZE);
    private final Long replaySeed;
    private final List<ReplayAction> replayActions = new ArrayList<>();
    private Tetromino current;
    private Tetromino hold;
    private boolean holdUsedThisTurn;
    private boolean gameOver;
    private boolean lastActionWasRotation;
    private boolean lastLockWasTSpin;
    private boolean backToBackActive;
    private int groundedTicks;
    private int comboStreak = -1;
    private int score;
    private int linesCleared;
    private int level = 1;
    private String lastScoreEvent = "NONE";
    private int lineClearEffectVersion;
    private int[] lastClearedRows = new int[0];

    public Board() {
        this(new Random(), null);
    }

    Board(Random random) {
        this(random, null);
    }

    private Board(Random random, Long replaySeed) {
        this.pieceBag = new PieceBag(random);
        this.replaySeed = replaySeed;
        spawnInitial();
    }

    public Board(long seed) {
        this(new Random(seed), seed);
    }

    public void reset() {
        pieceBag.reset();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = null;
            }
        }
        gameOver = false;
        hold = null;
        holdUsedThisTurn = false;
        lastActionWasRotation = false;
        lastLockWasTSpin = false;
        backToBackActive = false;
        comboStreak = -1;
        groundedTicks = 0;
        score = 0;
        linesCleared = 0;
        level = 1;
        lastScoreEvent = "NONE";
        lineClearEffectVersion = 0;
        lastClearedRows = new int[0];
        replayActions.clear();
        spawnInitial();
    }

    private void spawnInitial() {
        current = new Tetromino(pieceBag.next(), WIDTH / 2 - 2, 0);
        nextQueue.clear();
        fillNextQueue();
        groundedTicks = 0;
        lastActionWasRotation = false;
        lastLockWasTSpin = false;
        backToBackActive = false;
        comboStreak = -1;
        lastScoreEvent = "NONE";
        if (!isPositionValid(current)) {
            gameOver = true;
        }
    }

    public Tetromino getCurrent() {
        return current;
    }

    public Tetromino getNext() {
        TetrominoType type = nextQueue.peekFirst();
        if (type == null) {
            throw new IllegalStateException("next queue must not be empty");
        }
        return new Tetromino(type, WIDTH / 2 - 2, 0);
    }

    public List<TetrominoType> getNextQueue() {
        return List.copyOf(nextQueue);
    }

    public Tetromino getHold() {
        return hold;
    }

    public boolean isHoldAvailable() {
        return !gameOver && !holdUsedThisTurn && current != null;
    }

    public Tetromino getGhost() {
        if (gameOver || current == null) return null;
        Tetromino ghost = current.copy();
        while (true) {
            Tetromino candidate = ghost.copy();
            candidate.move(0, 1);
            if (!isPositionValid(candidate)) {
                break;
            }
            ghost = candidate;
        }
        return ghost;
    }

    public int getScore() {
        return score;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public int getLevel() {
        return level;
    }

    public boolean wasLastLockTSpin() {
        return lastLockWasTSpin;
    }

    public int getComboStreak() {
        return Math.max(comboStreak, 0);
    }

    public boolean isBackToBackActive() {
        return backToBackActive;
    }

    public String getLastScoreEvent() {
        return lastScoreEvent;
    }

    public int getLineClearEffectVersion() {
        return lineClearEffectVersion;
    }

    public int[] getLastClearedRows() {
        return Arrays.copyOf(lastClearedRows, lastClearedRows.length);
    }

    public TetrominoType[][] snapshot() {
        TetrominoType[][] copy = new TetrominoType[HEIGHT][WIDTH];
        for (int r = 0; r < HEIGHT; r++) {
            copy[r] = Arrays.copyOf(grid[r], WIDTH);
        }
        return copy;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Long getReplaySeed() {
        return replaySeed;
    }

    public List<ReplayAction> getReplayActions() {
        return List.copyOf(replayActions);
    }

    public boolean applyReplayAction(ReplayAction action) {
        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        replayActions.add(action);
        return switch (action) {
            case LEFT -> move(-1, 0);
            case RIGHT -> move(1, 0);
            case SOFT_DROP -> move(0, 1);
            case ROTATE_CW -> rotateCW();
            case ROTATE_CCW -> rotateCCW();
            case HARD_DROP -> hardDrop();
            case HOLD -> hold();
            case TICK -> tick();
        };
    }

    public static Board replayFromSeed(long seed, Iterable<ReplayAction> actions) {
        Board replay = new Board(seed);
        for (ReplayAction action : actions) {
            replay.applyReplayAction(action);
        }
        return replay;
    }

    public boolean move(int dx, int dy) {
        if (gameOver) return false;
        Tetromino moved = current.copy();
        moved.move(dx, dy);
        if (isPositionValid(moved)) {
            current = moved;
            groundedTicks = 0;
            lastActionWasRotation = false;
            return true;
        }
        return false;
    }

    public boolean rotateCW() {
        return rotate(true);
    }

    public boolean rotateCCW() {
        return rotate(false);
    }

    private boolean rotate(boolean cw) {
        if (gameOver) return false;
        int fromRotation = current.getRotation();
        Tetromino rotated = current.copy();
        if (cw) rotated.rotateCW(); else rotated.rotateCCW();

        int toRotation = rotated.getRotation();
        int[][] kicks = SrsKickTable.getKickOffsets(current.getType(), fromRotation, toRotation);
        for (int[] kick : kicks) {
            Tetromino candidate = rotated.copy();
            candidate.move(kick[0], kick[1]);
            if (isPositionValid(candidate)) {
                current = candidate;
                groundedTicks = 0;
                lastActionWasRotation = true;
                return true;
            }
        }
        return false;
    }

    public boolean hardDrop() {
        if (gameOver) return false;
        boolean moved = false;
        while (move(0, 1)) {
            moved = true;
        }
        if (!moved) return false;
        lockCurrent();
        return true;
    }

    public boolean hold() {
        if (gameOver || holdUsedThisTurn || current == null) return false;

        TetrominoType currentType = current.getType();
        if (hold == null) {
            hold = new Tetromino(currentType, WIDTH / 2 - 2, 0);
            current = new Tetromino(takeNextType(), WIDTH / 2 - 2, 0);
        } else {
            TetrominoType holdType = hold.getType();
            hold = new Tetromino(currentType, WIDTH / 2 - 2, 0);
            current = new Tetromino(holdType, WIDTH / 2 - 2, 0);
        }

        holdUsedThisTurn = true;
        groundedTicks = 0;
        lastActionWasRotation = false;
        if (!isPositionValid(current)) {
            gameOver = true;
            return false;
        }
        return true;
    }

    public boolean tick() {
        if (gameOver) return false;

        Tetromino movedDown = current.copy();
        movedDown.move(0, 1);
        if (isPositionValid(movedDown)) {
            current = movedDown;
            groundedTicks = 0;
            return true;
        }

        groundedTicks++;
        if (groundedTicks > LOCK_DELAY_TICKS) {
            lockCurrent();
        }
        return true;
    }

    private void lockCurrent() {
        lastLockWasTSpin = TSpinDetector.isTSpin(current, grid, lastActionWasRotation);
        for (var cell : current.getCells()) {
            int x = current.getX() + cell.x;
            int y = current.getY() + cell.y;
            if (y < 0 || y >= HEIGHT || x < 0 || x >= WIDTH) {
                gameOver = true;
                return;
            }
            grid[y][x] = current.getType();
        }
        clearLines();
        spawnNext();
    }

    private void spawnNext() {
        current = new Tetromino(takeNextType(), WIDTH / 2 - 2, 0);
        holdUsedThisTurn = false;
        lastActionWasRotation = false;
        groundedTicks = 0;
        if (!isPositionValid(current)) {
            gameOver = true;
        }
    }

    private TetrominoType takeNextType() {
        TetrominoType type = nextQueue.removeFirst();
        fillNextQueue();
        return type;
    }

    private void fillNextQueue() {
        while (nextQueue.size() < NEXT_QUEUE_SIZE) {
            nextQueue.addLast(pieceBag.next());
        }
    }

    private void clearLines() {
        List<Integer> clearedRows = new ArrayList<>();
        for (int y = HEIGHT - 1; y >= 0; y--) {
            if (isRowFull(y)) {
                clearedRows.add(y);
            }
        }
        int cleared = clearedRows.size();
        if (cleared == 0) {
            lastClearedRows = new int[0];
            comboStreak = -1;
            lastScoreEvent = "NO_CLEAR";
            return;
        }

        int writeRow = HEIGHT - 1;
        for (int y = HEIGHT - 1; y >= 0; y--) {
            if (!isRowFull(y)) {
                grid[writeRow] = Arrays.copyOf(grid[y], WIDTH);
                writeRow--;
            }
        }
        while (writeRow >= 0) {
            grid[writeRow] = new TetrominoType[WIDTH];
            writeRow--;
        }

        lastClearedRows = clearedRows.stream().mapToInt(Integer::intValue).toArray();
        lineClearEffectVersion++;
        linesCleared += cleared;
        int baseScore = baseScoreFor(cleared, lastLockWasTSpin);
        boolean difficult = isBackToBackEligible(cleared, lastLockWasTSpin);
        boolean b2bBonusApplied = difficult && backToBackActive;
        if (b2bBonusApplied) {
            baseScore += baseScore / 2;
        }

        comboStreak++;
        int comboBonus = comboStreak > 0 ? 50 * comboStreak * level : 0;
        score += baseScore + comboBonus;

        if (difficult) {
            backToBackActive = true;
        } else {
            backToBackActive = false;
        }

        lastScoreEvent = buildScoreEvent(cleared, lastLockWasTSpin, b2bBonusApplied, comboStreak);
        level = 1 + linesCleared / 10;
    }

    private boolean isRowFull(int y) {
        for (int x = 0; x < WIDTH; x++) {
            if (grid[y][x] == null) {
                return false;
            }
        }
        return true;
    }

    private int baseScoreFor(int cleared, boolean tspin) {
        if (tspin) {
            return switch (cleared) {
                case 1 -> 800 * level;
                case 2 -> 1200 * level;
                case 3 -> 1600 * level;
                default -> 400 * level;
            };
        }
        return switch (cleared) {
            case 1 -> 100 * level;
            case 2 -> 300 * level;
            case 3 -> 500 * level;
            case 4 -> 800 * level;
            default -> 0;
        };
    }

    private boolean isBackToBackEligible(int cleared, boolean tspin) {
        return (tspin && cleared > 0) || cleared == 4;
    }

    private String buildScoreEvent(int cleared, boolean tspin, boolean b2b, int combo) {
        String event = tspin
                ? switch (cleared) {
                    case 1 -> "TSPIN_SINGLE";
                    case 2 -> "TSPIN_DOUBLE";
                    case 3 -> "TSPIN_TRIPLE";
                    default -> "TSPIN";
                }
                : (cleared == 4 ? "TETRIS" : "LINE_CLEAR_" + cleared);

        if (b2b) {
            event += "_B2B";
        }
        if (combo > 0) {
            event += "_COMBO_" + combo;
        }
        return event;
    }

    private boolean isPositionValid(Tetromino tetromino) {
        for (var cell : tetromino.getCells()) {
            int x = tetromino.getX() + cell.x;
            int y = tetromino.getY() + cell.y;
            if (x < 0 || x >= WIDTH || y >= HEIGHT) return false;
            if (y >= 0 && grid[y][x] != null) return false;
        }
        return true;
    }
}
