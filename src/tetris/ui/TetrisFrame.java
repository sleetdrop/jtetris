package tetris.ui;

import tetris.model.Board;
import tetris.score.ScoreManager;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import javax.swing.BorderFactory;
import java.util.function.Supplier;

public class TetrisFrame extends JFrame {
    private static final int GRAVITY_TICK_MS = 700;
    private static final int INPUT_POLL_MS = 16;
    private static final int DAS_MS = 130;
    private static final int ARR_MS = 35;
    private static final int SOFT_DROP_REPEAT_MS = 40;

    static {
        // On macOS this merges the menu bar into the system bar when supported
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Tetris");
    }

    private final Board board = new Board();
    private final GamePanel gamePanel = new GamePanel(board);
    private final SidePanel sidePanel = new SidePanel(board);
    private final ScoreManager scoreManager = new ScoreManager();
    private final InputRepeater horizontalRepeater = new InputRepeater(DAS_MS, ARR_MS);
    private final SoftDropRepeater softDropRepeater = new SoftDropRepeater(SOFT_DROP_REPEAT_MS);
    // ensure score dialog is shown once per game
    private boolean scorePrompted;
    private boolean lastGameOverProcessed;
    private String userName; // null means not tracking this run
    private boolean paused;
    private boolean modalActive;

    public TetrisFrame() {
        super("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
        setJMenuBar(createMenuBar());
        pack();
        setResizable(false);
        setLocationRelativeTo(null);

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                focusGame();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                clearHeldInputs();
            }
        });

        installKeyBindings();

        Timer inputTimer = new Timer(INPUT_POLL_MS, e -> processHeldInput());
        inputTimer.start();

        Timer timer = new Timer(GRAVITY_TICK_MS, e -> {
            if (paused) return;
            boolean over = board.isGameOver();
            if (over) {
                if (!scorePrompted || !lastGameOverProcessed) {
                    scorePrompted = true;
                    lastGameOverProcessed = true;
                    maybeRecordScore();
                    promptNewGame();
                }
                return;
            } else {
                lastGameOverProcessed = false;
            }
            if (!board.tick()) return;
            gamePanel.repaint();
        });
        timer.start();

        focusGame();
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem pause = new JMenuItem("Pause/Resume (P)");
        pause.addActionListener(e -> togglePause());
        JMenuItem restart = new JMenuItem("Restart (R)");
        restart.addActionListener(e -> restartGame());
        JMenuItem hold = new JMenuItem("Hold (C)");
        hold.addActionListener(e -> holdIfActive());
        JMenuItem exit = new JMenuItem("Quit (Esc)");
        exit.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        gameMenu.add(pause);
        gameMenu.add(restart);
        gameMenu.add(hold);
        gameMenu.add(exit);

        JMenu scores = new JMenu("Scores");
        JMenuItem viewBoard = new JMenuItem("Leaderboard (L)");
        viewBoard.addActionListener(e -> showLeaderboard());
        scores.add(viewBoard);

        bar.add(gameMenu);
        bar.add(scores);
        return bar;
    }

    private void togglePause() {
        paused = !paused;
        clearHeldInputs();
        setTitle("Tetris" + (paused ? " (Paused)" : ""));
    }

    private void restartGame() {
        paused = false;
        clearHeldInputs();
        scorePrompted = false;
        lastGameOverProcessed = false;
        setTitle("Tetris");
        board.reset();
        gamePanel.repaint();
        focusGame();
    }

    private void maybeRecordScore() {
        int current = board.getScore();
        String chosenUser = chooseOrCreateUser();
        if (chosenUser == null) {
            showStyledMessage("Score not recorded\nScore: " + current, "Game Over");
            return;
        }
        userName = chosenUser;
        int best = scoreManager.updateIfHigher(userName, current);
        showStyledMessage(userName + " score: " + current + "\nBest: " + best, "Game Over");
    }

    private void promptNewGame() {
        int choice = showConfirmDialogModal(this, "Start a new game?", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        }
    }

    private String chooseOrCreateUser() {
        var users = scoreManager.getUsers();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.DARK_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> existing = new JComboBox<>(users.toArray(String[]::new));
        existing.setEditable(false);
        JTextField newUser = new JTextField();

        JLabel lblExisting = new JLabel("Choose existing:");
        lblExisting.setForeground(Color.WHITE);
        JLabel lblNew = new JLabel("Or enter new:");
        lblNew.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblExisting, gbc);
        gbc.gridx = 1; panel.add(existing, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblNew, gbc);
        gbc.gridx = 1; panel.add(newUser, gbc);

        int result = showConfirmDialogModal(this, panel, "Record score", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        String candidate = newUser.getText().trim();
        if (candidate.isEmpty()) {
            Object sel = existing.getSelectedItem();
            candidate = sel == null ? "" : sel.toString().trim();
        }
        return candidate.isEmpty() ? null : candidate;
    }

    private void showStyledMessage(String msg, String title) {
        JLabel label = new JLabel("<html>" + msg.replace("\n", "<br>") + "</html>");
        label.setForeground(Color.WHITE);
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        panel.add(label);
        showMessageDialogModal(this, panel, title, JOptionPane.INFORMATION_MESSAGE);
        focusGame();
    }

    private void showLeaderboard() {
        var entries = scoreManager.getLeaderboard();
        if (entries.isEmpty()) {
            showStyledMessage("No scores yet", "Leaderboard");
            return;
        }
        DefaultTableModel model = new DefaultTableModel(new Object[]{"User", "Best"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (var e : entries) {
            model.addRow(new Object[]{e.user(), e.score()});
        }
        JTable table = new JTable(model);
        table.setBackground(Color.DARK_GRAY);
        table.setForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setEnabled(false);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.DARK_GRAY);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);
        panel.add(scroll, BorderLayout.CENTER);
        showMessageDialogModal(this, panel, "Leaderboard", JOptionPane.PLAIN_MESSAGE);
        focusGame();
    }

    private int showConfirmDialogModal(java.awt.Component parent, Object message, String title, int optionType, int messageType) {
        return withModalInputBlocked(() -> JOptionPane.showConfirmDialog(parent, message, title, optionType, messageType));
    }

    private void showMessageDialogModal(java.awt.Component parent, Object message, String title, int messageType) {
        withModalInputBlocked(() -> {
            JOptionPane.showMessageDialog(parent, message, title, messageType);
            return null;
        });
    }

    private <T> T withModalInputBlocked(Supplier<T> dialogAction) {
        clearHeldInputs();
        modalActive = true;
        try {
            return dialogAction.get();
        } finally {
            modalActive = false;
            clearHeldInputs();
        }
    }

    private void focusGame() {
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        this.requestFocusInWindow();
    }

    private void installKeyBindings() {
        var root = getRootPane();
        var im = root.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        var am = root.getActionMap();

        registerAction(im, am, "leftPressed", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), this::onLeftPressed);
        registerAction(im, am, "leftReleased", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), this::onLeftReleased);
        registerAction(im, am, "rightPressed", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), this::onRightPressed);
        registerAction(im, am, "rightReleased", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), this::onRightReleased);
        registerAction(im, am, "downPressed", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), this::onDownPressed);
        registerAction(im, am, "downReleased", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), this::onDownReleased);
        registerAction(im, am, "rotateCW", KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), () -> rotateIfActive(true));
        registerAction(im, am, "rotateCCW", KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), () -> rotateIfActive(false));
        registerAction(im, am, "hardDrop", KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), this::hardDropIfActive);
        registerAction(im, am, "hold", KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), this::holdIfActive);
        registerAction(im, am, "pause", KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), this::togglePause);
        registerAction(im, am, "restart", KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), this::restartGame);
        registerAction(im, am, "leaderboard", KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), this::showLeaderboard);
        registerAction(im, am, "quit", KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), () -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
    }

    private void registerAction(javax.swing.InputMap im, javax.swing.ActionMap am, String name, KeyStroke key, Runnable action) {
        im.put(key, name);
        am.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
                if (!modalActive) {
                    focusGame();
                }
            }
        });
    }

    private void onLeftPressed() {
        applyHorizontalSteps(horizontalRepeater.pressLeft(nowMs()));
    }

    private void onLeftReleased() {
        applyHorizontalSteps(horizontalRepeater.releaseLeft(nowMs()));
    }

    private void onRightPressed() {
        applyHorizontalSteps(horizontalRepeater.pressRight(nowMs()));
    }

    private void onRightReleased() {
        applyHorizontalSteps(horizontalRepeater.releaseRight(nowMs()));
    }

    private void onDownPressed() {
        applySoftDropSteps(softDropRepeater.press(nowMs()));
    }

    private void onDownReleased() {
        softDropRepeater.release();
    }

    private void processHeldInput() {
        if (!isGameplayInputEnabled()) return;
        long now = nowMs();
        applyHorizontalSteps(horizontalRepeater.poll(now));
        applySoftDropSteps(softDropRepeater.poll(now));
    }

    private boolean isGameplayInputEnabled() {
        return !paused && !modalActive && !board.isGameOver();
    }

    private long nowMs() {
        return System.currentTimeMillis();
    }

    private void clearHeldInputs() {
        horizontalRepeater.reset();
        softDropRepeater.reset();
    }

    private void applyHorizontalSteps(int signedSteps) {
        if (signedSteps == 0 || !isGameplayInputEnabled()) return;
        int direction = signedSteps > 0 ? 1 : -1;
        int steps = Math.abs(signedSteps);
        boolean moved = false;
        for (int i = 0; i < steps; i++) {
            if (!board.move(direction, 0)) {
                break;
            }
            moved = true;
        }
        if (moved) {
            gamePanel.repaint();
        }
    }

    private void applySoftDropSteps(int steps) {
        if (steps <= 0 || !isGameplayInputEnabled()) return;
        boolean moved = false;
        for (int i = 0; i < steps; i++) {
            if (!board.move(0, 1)) {
                break;
            }
            moved = true;
        }
        if (moved) {
            gamePanel.repaint();
        }
    }

    private void rotateIfActive(boolean cw) {
        if (!isGameplayInputEnabled()) return;
        if (cw) board.rotateCW(); else board.rotateCCW();
        gamePanel.repaint();
    }

    private void hardDropIfActive() {
        if (!isGameplayInputEnabled()) return;
        board.hardDrop();
        gamePanel.repaint();
    }

    private void holdIfActive() {
        if (!isGameplayInputEnabled()) return;
        board.hold();
        gamePanel.repaint();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new TetrisFrame().setVisible(true));
    }
}
