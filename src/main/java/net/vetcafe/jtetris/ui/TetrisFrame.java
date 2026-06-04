package net.vetcafe.jtetris.ui;

import net.vetcafe.jtetris.model.Board;
import net.vetcafe.jtetris.score.ScoreManager;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
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
import javax.swing.ButtonGroup;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TetrisFrame extends JFrame {
    private static final String APP_NAME = "JTetris";
    private static final String FLATLAF_LIGHT = "com.formdev.flatlaf.FlatLightLaf";
    private static final String FLATLAF_DARK = "com.formdev.flatlaf.FlatDarkLaf";
    private static final String FLATLAF_BASE = "com.formdev.flatlaf.FlatLaf";
    private static final int GRAVITY_TICK_MS = 700;
    private static final int INPUT_POLL_MS = 16;
    private static final int DAS_MS = 130;
    private static final int ARR_MS = 35;
    private static final int SOFT_DROP_REPEAT_MS = 40;

    static {
        // On macOS this merges the menu bar into the system bar when supported
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_NAME);
        installInitialLookAndFeel();
        UiTheme.refreshFromSystem();
    }

    private static void installInitialLookAndFeel() {
        UiTheme.setActiveMode(UiTheme.modeOverride());
        UiTheme.refreshFromSystem();
        applyFlatLafForActiveTheme(false);
    }

    private static void applyFlatLafForActiveTheme(boolean updateUi) {
        try {
            if (!isClassPresent(FLATLAF_LIGHT) || !isClassPresent(FLATLAF_DARK)) {
                return;
            }
            String lafClassName = UiTheme.active().isDark() ? FLATLAF_DARK : FLATLAF_LIGHT;
            UIManager.setLookAndFeel(lafClassName);
            if (updateUi) {
                if (!invokeStaticNoArg(FLATLAF_BASE, "updateUI")) {
                    UIManager.getLookAndFeelDefaults().put("ClassLoader", TetrisFrame.class.getClassLoader());
                }
            }
        } catch (Exception ignored) {
            // Keep current/default LAF if FlatLaf setup fails unexpectedly.
        }
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, TetrisFrame.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean invokeStaticNoArg(String className, String methodName) {
        try {
            Class<?> type = Class.forName(className, false, TetrisFrame.class.getClassLoader());
            type.getMethod(methodName).invoke(null);
            return true;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    private final Board board = new Board();
    private final GamePanel gamePanel = new GamePanel(board);
    private final StageOverlayHost overlayHost = new StageOverlayHost();
    private final SidePanel sidePanel = new SidePanel(board);
    private final ScoreManager scoreManager = new ScoreManager();
    private final InputRepeater horizontalRepeater = new InputRepeater(DAS_MS, ARR_MS);
    private final SoftDropRepeater softDropRepeater = new SoftDropRepeater(SOFT_DROP_REPEAT_MS);
    // ensure score dialog is shown once per game
    private boolean scorePrompted;
    private boolean lastGameOverProcessed;
    private String userName; // null means not tracking this run
    private int pendingGameOverScore;
    private JComboBox<String> scoreEntryExistingUsers;
    private JTextField scoreEntryNewUserField;
    private String scoreEntryFeedbackMessage;
    private boolean paused;
    private boolean modalActive;

    public TetrisFrame() {
        super(APP_NAME);
        UiTheme.refreshFromSystem();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UiTheme.active().frameBackground());
        gamePanel.setLayout(new BorderLayout());
        gamePanel.add(overlayHost, BorderLayout.CENTER);
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
                    pendingGameOverScore = board.getScore();
                    showScoreEntryOverlay();
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
        JMenuItem overlayDemo = new JMenuItem("Overlay Demo (F1)");
        overlayDemo.setFont(UiFonts.regular(13f));
        overlayDemo.addActionListener(e -> toggleOverlayDemo());
        JMenuItem exit = new JMenuItem("Quit (Esc)");
        exit.setFont(UiFonts.regular(13f));
        exit.addActionListener(e -> requestExit());
        gameMenu.add(pause);
        gameMenu.add(restart);
        gameMenu.add(hold);
        gameMenu.add(overlayDemo);
        gameMenu.add(exit);

        JMenu scores = new JMenu("Scores");
        scores.setFont(UiFonts.regular(13f));
        JMenuItem viewBoard = new JMenuItem("Leaderboard (L)");
        viewBoard.setFont(UiFonts.regular(13f));
        viewBoard.addActionListener(e -> showLeaderboard());
        scores.add(viewBoard);

        JMenu themeMenu = createThemeMenu();

        bar.add(gameMenu);
        bar.add(scores);
        bar.add(themeMenu);
        return bar;
    }

    private JMenu createThemeMenu() {
        JMenu themeMenu = new JMenu("Theme");
        themeMenu.setFont(UiFonts.regular(13f));
        ButtonGroup group = new ButtonGroup();

        JCheckBoxMenuItem auto = createThemeMenuItem("Auto", UiTheme.Mode.AUTO, group);
        JCheckBoxMenuItem light = createThemeMenuItem("Light", UiTheme.Mode.LIGHT, group);
        JCheckBoxMenuItem dark = createThemeMenuItem("Dark", UiTheme.Mode.DARK, group);

        themeMenu.add(auto);
        themeMenu.add(light);
        themeMenu.add(dark);
        return themeMenu;
    }

    private JCheckBoxMenuItem createThemeMenuItem(String label, UiTheme.Mode mode, ButtonGroup group) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(label);
        item.setFont(UiFonts.regular(13f));
        item.setSelected(UiTheme.activeMode() == mode);
        item.addActionListener(e -> applyThemeMode(mode));
        group.add(item);
        return item;
    }

    private void applyThemeMode(UiTheme.Mode mode) {
        UiTheme.setActiveMode(mode);
        UiTheme.refreshFromSystem();
        applyFlatLafForActiveTheme(true);
        UiTheme theme = UiTheme.active();
        getContentPane().setBackground(theme.frameBackground());
        gamePanel.applyTheme();
        overlayHost.applyTheme();
        sidePanel.applyTheme();
        setJMenuBar(createMenuBar());
        revalidate();
        repaint();
        if (!isModalLayerActive()) {
            focusGame();
        }
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

    private void finalizeScoreEntryFromOverlay(boolean confirm) {
        int current = pendingGameOverScore;
        if (!confirm) {
            scoreEntryFeedbackMessage = "Score not recorded\nScore: " + current;
            return;
        }

        String chosenUser = extractScoreEntryCandidate();
        if (chosenUser == null) {
            scoreEntryFeedbackMessage = "Score not recorded\nScore: " + current;
            return;
        }

        userName = chosenUser;
        int best = scoreManager.updateIfHigher(userName, current);
        scoreEntryFeedbackMessage = userName + " score: " + current + "\nBest: " + best;
    }

    private void showScoreFeedbackOverlay(String message) {
        StageOverlayHost.OverlaySpec active = overlayHost.activeOverlay();
        if (active != null && "game-over-info".equals(active.id())) {
            return;
        }

        UiTheme theme = UiTheme.active();
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);

        JLabel info = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        info.setFont(UiFonts.regular(16f));
        info.setForeground(theme.textPrimary());

        JButton next = new JButton("Continue");
        next.setFont(UiFonts.regular(14f));
        next.addActionListener(e -> confirmOverlayIfVisible());

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.add(next);

        content.add(info, BorderLayout.CENTER);
        content.add(actions, BorderLayout.SOUTH);

        overlayHost.showOverlay(new StageOverlayHost.OverlaySpec(
                "game-over-info",
                "Game Over",
                content,
                new StageOverlayHost.OverlayLifecycle() {
                    @Override
                    public void onOpened() {
                        clearHeldInputs();
                    }

                    @Override
                    public void onClosed() {
                        clearHeldInputs();
                        focusGame();
                        showGameOverOverlay();
                    }
                }
        ));
    }

    private void showGameOverOverlay() {
        StageOverlayHost.OverlaySpec active = overlayHost.activeOverlay();
        if (active != null && "game-over-restart".equals(active.id())) {
            return;
        }

        UiTheme theme = UiTheme.active();
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);

        JLabel message = new JLabel("<html>Game over.<br>Start a new game?</html>");
        message.setFont(UiFonts.regular(16f));
        message.setForeground(theme.textPrimary());

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        JButton restart = new JButton("Restart");
        restart.setFont(UiFonts.regular(14f));
        restart.addActionListener(e -> confirmOverlayIfVisible());
        JButton cancel = new JButton("Stay");
        cancel.setFont(UiFonts.regular(14f));
        cancel.addActionListener(e -> cancelOverlayIfVisible());
        actions.add(restart);
        actions.add(cancel);

        content.add(message, BorderLayout.CENTER);
        content.add(actions, BorderLayout.SOUTH);

        overlayHost.showOverlay(new StageOverlayHost.OverlaySpec(
                "game-over-restart",
                "Game Over",
                content,
                new StageOverlayHost.OverlayLifecycle() {
                    @Override
                    public void onOpened() {
                        clearHeldInputs();
                    }

                    @Override
                    public void onClosed() {
                        clearHeldInputs();
                        focusGame();
                    }
                }
        ));
    }

    private void showScoreEntryOverlay() {
        StageOverlayHost.OverlaySpec active = overlayHost.activeOverlay();
        if (active != null && "score-entry".equals(active.id())) {
            return;
        }

        UiTheme theme = UiTheme.active();
        var users = scoreManager.getUsers();
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel scoreInfo = new JLabel("Score: " + pendingGameOverScore);
        scoreInfo.setForeground(theme.textPrimary());
        scoreInfo.setFont(UiFonts.semibold(16f));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        scoreEntryExistingUsers = new JComboBox<>(users.toArray(String[]::new));
        scoreEntryExistingUsers.setEditable(false);
        scoreEntryExistingUsers.setFont(UiFonts.regular(16f));
        scoreEntryExistingUsers.setBackground(theme.dialogBackground());
        scoreEntryExistingUsers.setForeground(theme.textPrimary());

        scoreEntryNewUserField = new JTextField();
        scoreEntryNewUserField.setFont(UiFonts.regular(16f));
        scoreEntryNewUserField.setBackground(theme.dialogBackground());
        scoreEntryNewUserField.setForeground(theme.textPrimary());
        scoreEntryNewUserField.setCaretColor(theme.textPrimary());
        scoreEntryNewUserField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.dialogBorder(), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JLabel lblExisting = new JLabel("Choose existing:");
        lblExisting.setForeground(theme.textPrimary());
        lblExisting.setFont(UiFonts.regular(16f));
        JLabel lblNew = new JLabel("Or enter new:");
        lblNew.setForeground(theme.textPrimary());
        lblNew.setFont(UiFonts.regular(16f));

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblExisting, gbc);
        gbc.gridx = 1; formPanel.add(scoreEntryExistingUsers, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblNew, gbc);
        gbc.gridx = 1; formPanel.add(scoreEntryNewUserField, gbc);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        JButton record = new JButton("Record");
        record.setFont(UiFonts.regular(14f));
        record.addActionListener(e -> confirmOverlayIfVisible());
        JButton skip = new JButton("Skip");
        skip.setFont(UiFonts.regular(14f));
        skip.addActionListener(e -> cancelOverlayIfVisible());
        actions.add(record);
        actions.add(skip);

        panel.add(scoreInfo, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);

        scoreEntryFeedbackMessage = null;

        overlayHost.showOverlay(new StageOverlayHost.OverlaySpec(
                "score-entry",
                "Record score",
                panel,
                new StageOverlayHost.OverlayLifecycle() {
                    @Override
                    public void onOpened() {
                        clearHeldInputs();
                        if (scoreEntryNewUserField != null) {
                            scoreEntryNewUserField.requestFocusInWindow();
                        }
                    }

                    @Override
                    public void onClosed() {
                        clearHeldInputs();
                        if (scoreEntryFeedbackMessage != null) {
                            String feedback = scoreEntryFeedbackMessage;
                            scoreEntryFeedbackMessage = null;
                            scoreEntryExistingUsers = null;
                            scoreEntryNewUserField = null;
                            showScoreFeedbackOverlay(feedback);
                            return;
                        }
                        scoreEntryExistingUsers = null;
                        scoreEntryNewUserField = null;
                        showGameOverOverlay();
                    }
                }
        ));
    }

    private String extractScoreEntryCandidate() {
        String candidate = "";
        if (scoreEntryNewUserField != null) {
            candidate = scoreEntryNewUserField.getText().trim();
        }
        if (candidate.isEmpty() && scoreEntryExistingUsers != null) {
            Object sel = scoreEntryExistingUsers.getSelectedItem();
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

    private boolean isModalLayerActive() {
        return modalActive || overlayHost.isOverlayVisible();
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
        registerAction(im, am, "hardDropOrOverlayConfirm", KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), this::onSpacePressed);
        registerAction(im, am, "overlayConfirm", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), this::onEnterPressed);
        registerAction(im, am, "hold", KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), this::holdIfActive);
        registerAction(im, am, "pause", KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), this::togglePause);
        registerAction(im, am, "restart", KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), this::restartGame);
        registerAction(im, am, "leaderboard", KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), this::showLeaderboard);
        registerAction(im, am, "overlayDemo", KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), this::toggleOverlayDemo);
        registerAction(im, am, "quitOrOverlayCancel", KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), this::onEscapePressed);
    }

    private void toggleOverlayDemo() {
        StageOverlayHost.OverlaySpec active = overlayHost.activeOverlay();
        if (active != null && "demo-info".equals(active.id())) {
            overlayHost.dismissOverlay();
            return;
        }

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        JLabel message = new JLabel("<html>Stage overlay foundation demo.<br>Gameplay input is blocked while this panel is visible.</html>");
        message.setFont(UiFonts.regular(15f));
        message.setForeground(UiTheme.active().textPrimary());

        JButton close = new JButton("Close");
        close.setFont(UiFonts.regular(14f));
        close.addActionListener(e -> dismissOverlayIfVisible());

        content.add(message, BorderLayout.CENTER);
        content.add(close, BorderLayout.SOUTH);

        overlayHost.showOverlay(new StageOverlayHost.OverlaySpec(
                "demo-info",
                "Overlay Demo",
                content,
                new StageOverlayHost.OverlayLifecycle() {
                    @Override
                    public void onOpened() {
                        clearHeldInputs();
                    }

                    @Override
                    public void onClosed() {
                        clearHeldInputs();
                        focusGame();
                    }
                }
        ));
    }

    private void onSpacePressed() {
        if (overlayHost.isOverlayVisible()) {
            confirmOverlayIfVisible();
            return;
        }
        hardDropIfActive();
    }

    private void onEnterPressed() {
        confirmOverlayIfVisible();
    }

    private void onEscapePressed() {
        if (overlayHost.isOverlayVisible()) {
            cancelOverlayIfVisible();
            return;
        }
        requestExit();
    }

    private void confirmOverlayIfVisible() {
        if (!overlayHost.isOverlayVisible() || overlayHost.state() == StageOverlayHost.State.EXITING) {
            return;
        }
        StageOverlayHost.OverlaySpec active = overlayHost.activeOverlay();
        if (active != null && "game-over-restart".equals(active.id())) {
            restartGame();
        } else if (active != null && "score-entry".equals(active.id())) {
            finalizeScoreEntryFromOverlay(true);
        } else if (active != null && "game-over-info".equals(active.id())) {
            // Continue to restart/cancel prompt via lifecycle close hook.
        }
        dismissOverlayIfVisible();
    }

    private void cancelOverlayIfVisible() {
        if (!overlayHost.isOverlayVisible() || overlayHost.state() == StageOverlayHost.State.EXITING) {
            return;
        }
        StageOverlayHost.OverlaySpec active = overlayHost.activeOverlay();
        if (active != null && "score-entry".equals(active.id())) {
            finalizeScoreEntryFromOverlay(false);
        }
        dismissOverlayIfVisible();
    }

    private void dismissOverlayIfVisible() {
        if (!overlayHost.isOverlayVisible()) {
            return;
        }
        clearHeldInputs();
        overlayHost.dismissOverlay();
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
                if (!isModalLayerActive()) {
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
        return !paused && !isModalLayerActive() && !board.isGameOver();
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
