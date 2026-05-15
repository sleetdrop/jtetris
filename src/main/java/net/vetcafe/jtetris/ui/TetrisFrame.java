package net.vetcafe.jtetris.ui;

import net.vetcafe.jtetris.model.Board;
import net.vetcafe.jtetris.score.ScoreManager;

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
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TetrisFrame extends JFrame {
    private static final String APP_NAME = "JTetris";
    private static final int GRAVITY_TICK_MS = 700;
    private static final int INPUT_POLL_MS = 16;
    private static final int DAS_MS = 130;
    private static final int ARR_MS = 35;
    private static final int SOFT_DROP_REPEAT_MS = 40;

    static {
        // On macOS this merges the menu bar into the system bar when supported
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_NAME);
        UiTheme.refreshFromSystem();
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
        super(APP_NAME);
        UiTheme.refreshFromSystem();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UiTheme.active().frameBackground());
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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                requestExit();
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
        UiTheme theme = UiTheme.active();
        JMenuBar bar = new JMenuBar();
        bar.setFont(UiFonts.regular(13f));
        bar.setBackground(theme.frameBackground());
        bar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setFont(UiFonts.regular(13f));
        JMenuItem pause = new JMenuItem("Pause/Resume (P)");
        pause.setFont(UiFonts.regular(13f));
        pause.addActionListener(e -> togglePause());
        JMenuItem restart = new JMenuItem("Restart (R)");
        restart.setFont(UiFonts.regular(13f));
        restart.addActionListener(e -> restartGame());
        JMenuItem hold = new JMenuItem("Hold (C)");
        hold.setFont(UiFonts.regular(13f));
        hold.addActionListener(e -> holdIfActive());
        JMenuItem exit = new JMenuItem("Quit (Esc)");
        exit.setFont(UiFonts.regular(13f));
        exit.addActionListener(e -> requestExit());
        gameMenu.add(pause);
        gameMenu.add(restart);
        gameMenu.add(hold);
        gameMenu.add(exit);

        JMenu scores = new JMenu("Scores");
        scores.setFont(UiFonts.regular(13f));
        JMenuItem viewBoard = new JMenuItem("Leaderboard (L)");
        viewBoard.setFont(UiFonts.regular(13f));
        viewBoard.addActionListener(e -> showLeaderboard());
        scores.add(viewBoard);

        bar.add(gameMenu);
        bar.add(scores);
        return bar;
    }

    private void togglePause() {
        paused = !paused;
        clearHeldInputs();
        setTitle(APP_NAME + (paused ? " (Paused)" : ""));
    }

    private void restartGame() {
        paused = false;
        clearHeldInputs();
        scorePrompted = false;
        lastGameOverProcessed = false;
        setTitle(APP_NAME);
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
        UiTheme theme = UiTheme.active();
        var users = scoreManager.getUsers();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(theme.dialogSurface());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.dialogBorder(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> existing = new JComboBox<>(users.toArray(String[]::new));
        existing.setEditable(false);
        existing.setFont(UiFonts.regular(16f));
        existing.setBackground(theme.dialogBackground());
        existing.setForeground(theme.textPrimary());
        JTextField newUser = new JTextField();
        newUser.setFont(UiFonts.regular(16f));
        newUser.setBackground(theme.dialogBackground());
        newUser.setForeground(theme.textPrimary());
        newUser.setCaretColor(theme.textPrimary());
        newUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.dialogBorder(), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JLabel lblExisting = new JLabel("Choose existing:");
        lblExisting.setForeground(theme.textPrimary());
        lblExisting.setFont(UiFonts.regular(16f));
        JLabel lblNew = new JLabel("Or enter new:");
        lblNew.setForeground(theme.textPrimary());
        lblNew.setFont(UiFonts.regular(16f));

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
        UiTheme theme = UiTheme.active();
        JLabel label = new JLabel("<html>" + msg.replace("\n", "<br>") + "</html>");
        label.setForeground(theme.textPrimary());
        label.setFont(UiFonts.regular(18f));
        label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JPanel panel = new JPanel();
        panel.setBackground(theme.dialogSurface());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.dialogBorder(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.add(label);
        showMessageDialogModal(this, panel, title, JOptionPane.INFORMATION_MESSAGE);
        focusGame();
    }

    private void showLeaderboard() {
        UiTheme theme = UiTheme.active();
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
        table.setBackground(theme.dialogBackground());
        table.setForeground(theme.textPrimary());
        table.setGridColor(theme.tableGrid());
        table.setRowHeight(28);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFont(UiFonts.regular(16f));
        table.getTableHeader().setBackground(theme.tableHeaderBackground());
        table.getTableHeader().setForeground(theme.tableHeaderText());
        table.getTableHeader().setFont(UiFonts.semibold(16f));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(theme.dialogBorder(), 1));
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);
        table.setEnabled(false);
        int visibleRows = Math.max(1, Math.min(entries.size(), 8));
        int preferredHeight = (visibleRows * table.getRowHeight()) + table.getTableHeader().getPreferredSize().height + 8;
        table.setPreferredScrollableViewportSize(new Dimension(520, preferredHeight));
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(theme.dialogBackground());
        scroll.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(theme.dialogSurface());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.dialogBorder(), 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        panel.add(scroll, BorderLayout.CENTER);
        showMessageDialogModal(this, panel, "Leaderboard", JOptionPane.PLAIN_MESSAGE);
        focusGame();
    }

    private int showConfirmDialogModal(java.awt.Component parent, Object message, String title, int optionType, int messageType) {
        return withModalInputBlocked(() -> withDialogUiDefaults(() ->
                JOptionPane.showConfirmDialog(parent, message, title, optionType, messageType)
        ));
    }

    private void showMessageDialogModal(java.awt.Component parent, Object message, String title, int messageType) {
        withModalInputBlocked(() -> {
            withDialogUiDefaults(() -> {
                JOptionPane.showMessageDialog(parent, message, title, messageType);
                return null;
            });
            return null;
        });
    }

    private <T> T withDialogUiDefaults(Supplier<T> action) {
        Map<String, Object> backup = installDialogUiDefaults();
        try {
            return action.get();
        } finally {
            restoreUiDefaults(backup);
        }
    }

    private Map<String, Object> installDialogUiDefaults() {
        UiTheme theme = UiTheme.active();
        Map<String, Object> previous = new HashMap<>();
        putUiDefault(previous, "OptionPane.background", theme.dialogSurface());
        putUiDefault(previous, "Panel.background", theme.dialogSurface());
        putUiDefault(previous, "OptionPane.messageForeground", theme.textPrimary());
        putUiDefault(previous, "OptionPane.messageFont", UiFonts.regular(17f));
        putUiDefault(previous, "Label.foreground", theme.textPrimary());
        putUiDefault(previous, "Label.font", UiFonts.regular(15f));
        putUiDefault(previous, "Button.font", UiFonts.regular(15f));
        putUiDefault(previous, "Button.background", theme.accent());
        putUiDefault(previous, "Button.foreground", theme.textPrimary());
        putUiDefault(previous, "Button.select", theme.accent().darker());
        putUiDefault(previous, "OptionPane.minimumSize", new Dimension(420, 180));
        putUiDefault(previous, "OptionPane.buttonPadding", 8);
        putUiDefault(previous, "TextField.font", UiFonts.regular(16f));
        putUiDefault(previous, "TextField.background", theme.dialogBackground());
        putUiDefault(previous, "TextField.foreground", theme.textPrimary());
        putUiDefault(previous, "ComboBox.font", UiFonts.regular(16f));
        putUiDefault(previous, "ComboBox.background", theme.dialogBackground());
        putUiDefault(previous, "ComboBox.foreground", theme.textPrimary());
        putUiDefault(previous, "Table.font", UiFonts.regular(15f));
        putUiDefault(previous, "Table.foreground", theme.textPrimary());
        putUiDefault(previous, "Table.background", theme.dialogBackground());
        putUiDefault(previous, "TableHeader.font", UiFonts.semibold(15f));
        return previous;
    }

    private void putUiDefault(Map<String, Object> previous, String key, Object value) {
        previous.put(key, UIManager.get(key));
        UIManager.put(key, value);
    }

    private void restoreUiDefaults(Map<String, Object> previous) {
        for (var entry : previous.entrySet()) {
            UIManager.put(entry.getKey(), entry.getValue());
        }
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
        registerAction(im, am, "quit", KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), this::requestExit);
    }

    private void requestExit() {
        int choice = showConfirmDialogModal(this, "Exit JTetris?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        } else {
            focusGame();
        }
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


