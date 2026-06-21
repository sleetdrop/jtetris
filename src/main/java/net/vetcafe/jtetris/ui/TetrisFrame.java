package net.vetcafe.jtetris.ui;

import net.vetcafe.jtetris.logging.LoggingBootstrap;
import net.vetcafe.jtetris.model.Board;
import net.vetcafe.jtetris.score.ScoreManager;

import javax.swing.JComboBox;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
import javax.swing.UIManager;
import javax.swing.ButtonGroup;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import java.awt.Component;

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

    private enum LeaderboardTransition {
        NONE,
        DELETE_CONFIRM,
        REFRESH,
        DELETE_FAILURE
    }

    static {
        // On macOS this merges the menu bar into the system bar when supported
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_NAME);
        System.setProperty("flatlaf.useNativeLibrary", System.getProperty("flatlaf.useNativeLibrary", "false"));
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
    private final GameSessionTimer sessionTimer = new GameSessionTimer();
    private final SidePanel sidePanel = new SidePanel(board, sessionTimer::elapsedMillis);
    private final ScoreManager scoreManager = new ScoreManager();
    private final GameplayInputController inputController = new GameplayInputController(
            board,
            DAS_MS,
            ARR_MS,
            SOFT_DROP_REPEAT_MS,
            () -> System.nanoTime() / 1_000_000L
    );
    // ensure score dialog is shown once per game
    private boolean scorePrompted;
    private boolean lastGameOverProcessed;
    private String userName; // null means not tracking this run
    private int pendingGameOverScore;
    private JComboBox<String> scoreEntryExistingUsers;
    private JTextField scoreEntryNewUserField;
    private String scoreEntryFeedbackMessage;
    private String pendingLeaderboardDeleteUser;
    private LeaderboardTransition leaderboardTransition = LeaderboardTransition.NONE;
    private boolean paused;

    public TetrisFrame() {
        super(APP_NAME);
        UiTheme.refreshFromSystem();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UiTheme.active().frameBackground());
        gamePanel.setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
        setGlassPane(overlayHost);
        overlayHost.setVisible(false);
        overlayHost.setBlockingVisibilityListener(visible -> syncSessionTimer());
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
            boolean over = board.isGameOver();
            if (over) {
                syncSessionTimer();
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
            syncSessionTimer();
            if (!shouldRunSessionTimer(paused, isModalLayerActive(), false)) return;
            if (!board.tick()) return;
            syncSessionTimer();
            gamePanel.repaint();
        });
        timer.start();

        syncSessionTimer();
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
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(UiFonts.regular(13f));
        JMenuItem help = new JMenuItem("JTetris Help (H)");
        help.setFont(UiFonts.regular(13f));
        help.addActionListener(e -> showHelp());
        helpMenu.add(help);

        bar.add(gameMenu);
        bar.add(scores);
        bar.add(themeMenu);
        bar.add(helpMenu);
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
        syncSessionTimer();
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
        sessionTimer.resetAndStart();
        syncSessionTimer();
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

        JLabel info = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        StageOverlayHost.styleOverlayBodyLabel(info);
        info.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        JButton next = new JButton("Continue");
        StageOverlayHost.styleOverlayActionButton(next);
        next.addActionListener(e -> confirmOverlayIfVisible());

        JPanel actions = StageOverlayHost.createOverlayActionRow();
        actions.add(next);

        JPanel content = createSimpleOverlayContent(info, actions);

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

        JLabel message = new JLabel("<html>Game over.<br>Start a new game?</html>");
        StageOverlayHost.styleOverlayBodyLabel(message);
        message.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        JButton restart = new JButton("Restart");
        StageOverlayHost.styleOverlayActionButton(restart);
        restart.addActionListener(e -> confirmOverlayIfVisible());
        JButton cancel = new JButton("Stay");
        StageOverlayHost.styleOverlayActionButton(cancel);
        cancel.addActionListener(e -> cancelOverlayIfVisible());
        JPanel actions = StageOverlayHost.createOverlayActionRow();
        actions.add(restart);
        actions.add(cancel);

        JPanel content = createSimpleOverlayContent(message, actions);

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
        StageOverlayHost.styleOverlayBodyLabel(scoreInfo);
        scoreInfo.setFont(UiFonts.semibold(14f));
        scoreInfo.setBorder(BorderFactory.createEmptyBorder(1, 4, 4, 4));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        scoreEntryExistingUsers = new JComboBox<>(users.toArray(String[]::new));
        scoreEntryExistingUsers.setEditable(false);
        scoreEntryExistingUsers.setFont(UiFonts.regular(14f));
        scoreEntryExistingUsers.setBackground(theme.dialogBackground());
        scoreEntryExistingUsers.setForeground(theme.textPrimary());
        scoreEntryExistingUsers.setPrototypeDisplayValue("Player name");

        scoreEntryNewUserField = new JTextField();
        scoreEntryNewUserField.setColumns(12);
        scoreEntryNewUserField.setFont(UiFonts.regular(14f));
        scoreEntryNewUserField.setBackground(theme.dialogBackground());
        scoreEntryNewUserField.setForeground(theme.textPrimary());
        scoreEntryNewUserField.setCaretColor(theme.textPrimary());
        scoreEntryNewUserField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.dialogBorder(), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JLabel lblExisting = new JLabel("Choose existing:");
        StageOverlayHost.styleOverlayBodyLabel(lblExisting);
        JLabel lblNew = new JLabel("Or enter new:");
        StageOverlayHost.styleOverlayBodyLabel(lblNew);
        lblExisting.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
        lblNew.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; formPanel.add(lblExisting, gbc);
        gbc.gridx = 1; formPanel.add(scoreEntryExistingUsers, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblNew, gbc);
        gbc.gridx = 1; formPanel.add(scoreEntryNewUserField, gbc);

        JPanel actions = StageOverlayHost.createOverlayActionRow();
        JButton record = new JButton("Record");
        StageOverlayHost.styleOverlayActionButton(record);
        record.addActionListener(e -> confirmOverlayIfVisible());
        JButton skip = new JButton("Skip");
        StageOverlayHost.styleOverlayActionButton(skip);
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

    private void showLeaderboard() {
        StageOverlayHost.OverlaySpec active = overlayHost.activeOverlay();
        if (active != null && "leaderboard".equals(active.id())) {
            return;
        }

        LeaderboardContent content = new LeaderboardContent(
                scoreManager.getLeaderboard(),
                this::requestLeaderboardDelete,
                this::dismissOverlayIfVisible
        );

        overlayHost.showOverlay(new StageOverlayHost.OverlaySpec(
                "leaderboard",
                "Leaderboard",
                content,
                new StageOverlayHost.OverlayLifecycle() {
                    @Override
                    public void onOpened() {
                        clearHeldInputs();
                    }

                    @Override
                    public void onClosed() {
                        clearHeldInputs();
                        if (leaderboardTransition == LeaderboardTransition.DELETE_CONFIRM) {
                            leaderboardTransition = LeaderboardTransition.NONE;
                            showLeaderboardDeleteConfirm();
                            return;
                        }
                        focusGame();
                    }
                }
        ));
    }

    private void requestLeaderboardDelete(String user) {
        pendingLeaderboardDeleteUser = user;
        leaderboardTransition = LeaderboardTransition.DELETE_CONFIRM;
        dismissOverlayIfVisible();
    }

    private void showLeaderboardDeleteConfirm() {
        String user = pendingLeaderboardDeleteUser;
        if (user == null || user.isBlank()) {
            showLeaderboard();
            return;
        }

        JLabel message = new JLabel("Delete all score data for \"" + user + "\"?");
        StageOverlayHost.styleOverlayBodyLabel(message);
        message.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        JButton delete = new JButton("Delete");
        StageOverlayHost.styleOverlayActionButton(delete);
        delete.addActionListener(event -> confirmOverlayIfVisible());

        JButton cancel = new JButton("Cancel");
        StageOverlayHost.styleOverlayActionButton(cancel);
        cancel.addActionListener(event -> cancelOverlayIfVisible());

        JPanel actions = StageOverlayHost.createOverlayActionRow();
        actions.add(delete);
        actions.add(cancel);

        overlayHost.showOverlay(new StageOverlayHost.OverlaySpec(
                "score-delete-confirm",
                "Delete score",
                createSimpleOverlayContent(message, actions),
                new StageOverlayHost.OverlayLifecycle() {
                    @Override
                    public void onOpened() {
                        clearHeldInputs();
                    }

                    @Override
                    public void onClosed() {
                        clearHeldInputs();
                        LeaderboardTransition next = leaderboardTransition;
                        leaderboardTransition = LeaderboardTransition.NONE;
                        if (next == LeaderboardTransition.DELETE_FAILURE) {
                            showLeaderboardDeleteFailure();
                            return;
                        }
                        showLeaderboard();
                    }
                }
        ));
    }

    private void confirmLeaderboardDelete() {
        boolean deleted = scoreManager.deleteUser(pendingLeaderboardDeleteUser);
        pendingLeaderboardDeleteUser = null;
        leaderboardTransition = deleted
                ? LeaderboardTransition.REFRESH
                : LeaderboardTransition.DELETE_FAILURE;
    }

    private void showLeaderboardDeleteFailure() {
        JLabel message = new JLabel("Score data could not be deleted.");
        StageOverlayHost.styleOverlayBodyLabel(message);
        message.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        JButton close = new JButton("Close");
        StageOverlayHost.styleOverlayActionButton(close);
        close.addActionListener(event -> dismissOverlayIfVisible());

        JPanel actions = StageOverlayHost.createOverlayActionRow();
        actions.add(close);

        overlayHost.showOverlay(new StageOverlayHost.OverlaySpec(
                "score-delete-failure",
                "Delete score",
                createSimpleOverlayContent(message, actions),
                new StageOverlayHost.OverlayLifecycle() {
                    @Override
                    public void onOpened() {
                        clearHeldInputs();
                    }

                    @Override
                    public void onClosed() {
                        clearHeldInputs();
                        showLeaderboard();
                    }
                }
        ));
    }

    private void showExitConfirmOverlay() {
        StageOverlayHost.OverlaySpec active = overlayHost.activeOverlay();
        if (active != null && "exit-confirm".equals(active.id())) {
            return;
        }

        JLabel message = new JLabel("Exit JTetris?");
        StageOverlayHost.styleOverlayBodyLabel(message);
        message.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        JButton quit = new JButton("Quit");
        StageOverlayHost.styleOverlayActionButton(quit);
        quit.addActionListener(e -> confirmOverlayIfVisible());

        JButton stay = new JButton("Stay");
        StageOverlayHost.styleOverlayActionButton(stay);
        stay.addActionListener(e -> cancelOverlayIfVisible());

        JPanel actions = StageOverlayHost.createOverlayActionRow();
        actions.add(quit);
        actions.add(stay);

        JPanel content = createSimpleOverlayContent(message, actions);

        overlayHost.showOverlay(new StageOverlayHost.OverlaySpec(
                "exit-confirm",
                "Confirm Exit",
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

    private boolean isModalLayerActive() {
        return overlayHost.isOverlayVisible();
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
        registerAction(im, am, "help", KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), this::showHelp);
        registerAction(im, am, "overlayDemo", KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), this::toggleOverlayDemo);
        registerAction(im, am, "quitOrOverlayCancel", KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), this::onEscapePressed);
    }

    private void showHelp() {
        StageOverlayHost.OverlaySpec active = overlayHost.activeOverlay();
        if (active != null && "help".equals(active.id())) {
            return;
        }
        if (overlayHost.isOverlayVisible()) {
            return;
        }

        clearHeldInputs();

        overlayHost.showOverlay(new StageOverlayHost.OverlaySpec(
                "help",
                "JTetris Help",
                HelpContent.create(this::dismissOverlayIfVisible),
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
                },
                StageOverlayHost.largeSize()
        ));
    }

    private void toggleOverlayDemo() {
        StageOverlayHost.OverlaySpec active = overlayHost.activeOverlay();
        if (active != null && "demo-info".equals(active.id())) {
            overlayHost.dismissOverlay();
            return;
        }

        JLabel message = new JLabel("<html>Stage overlay foundation demo.<br>Gameplay input is blocked while this panel is visible.</html>");
        StageOverlayHost.styleOverlayBodyLabel(message);
        message.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        JButton close = new JButton("Close");
        StageOverlayHost.styleOverlayActionButton(close);
        close.addActionListener(e -> dismissOverlayIfVisible());

        JPanel actions = StageOverlayHost.createOverlayActionRow();
        actions.add(close);
        JPanel content = createSimpleOverlayContent(message, actions);

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

    private JPanel createSimpleOverlayContent(JLabel message, JPanel actions) {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        message.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, actions.getPreferredSize().height));

        content.add(message);
        content.add(Box.createVerticalStrut(12));
        content.add(actions);
        return content;
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
        } else if (active != null && "exit-confirm".equals(active.id())) {
            dispose();
            System.exit(0);
        } else if (active != null && "score-delete-confirm".equals(active.id())) {
            confirmLeaderboardDelete();
        } else if (active != null && "help".equals(active.id())) {
            // Close the help overlay.
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
        } else if (active != null && "score-delete-confirm".equals(active.id())) {
            pendingLeaderboardDeleteUser = null;
            leaderboardTransition = LeaderboardTransition.REFRESH;
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
        showExitConfirmOverlay();
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
        if (!isGameplayInputEnabled()) return;
        repaintGameIfChanged(inputController.pressLeft());
    }

    private void onLeftReleased() {
        if (!isGameplayInputEnabled()) return;
        repaintGameIfChanged(inputController.releaseLeft());
    }

    private void onRightPressed() {
        if (!isGameplayInputEnabled()) return;
        repaintGameIfChanged(inputController.pressRight());
    }

    private void onRightReleased() {
        if (!isGameplayInputEnabled()) return;
        repaintGameIfChanged(inputController.releaseRight());
    }

    private void onDownPressed() {
        if (!isGameplayInputEnabled()) return;
        repaintGameIfChanged(inputController.pressSoftDrop());
    }

    private void onDownReleased() {
        inputController.releaseSoftDrop();
    }

    private void processHeldInput() {
        if (!isGameplayInputEnabled()) return;
        repaintGameIfChanged(inputController.poll());
    }

    private boolean isGameplayInputEnabled() {
        return !paused && !isModalLayerActive() && !board.isGameOver();
    }

    static boolean shouldRunSessionTimer(boolean paused, boolean overlayVisible, boolean gameOver) {
        return !paused && !overlayVisible && !gameOver;
    }

    private void syncSessionTimer() {
        sessionTimer.syncRunning(shouldRunSessionTimer(
                paused,
                isModalLayerActive(),
                board.isGameOver()
        ));
    }

    private void clearHeldInputs() {
        inputController.reset();
    }

    private void repaintGameIfChanged(boolean changed) {
        if (changed) {
            gamePanel.repaint();
        }
    }

    private void rotateIfActive(boolean cw) {
        if (!isGameplayInputEnabled()) return;
        boolean changed = cw
                ? inputController.rotateClockwise()
                : inputController.rotateCounterclockwise();
        repaintGameIfChanged(changed);
    }

    private void hardDropIfActive() {
        if (!isGameplayInputEnabled()) return;
        boolean changed = inputController.hardDrop();
        syncSessionTimer();
        repaintGameIfChanged(changed);
    }

    private void holdIfActive() {
        if (!isGameplayInputEnabled()) return;
        boolean changed = inputController.hold();
        syncSessionTimer();
        repaintGameIfChanged(changed);
    }

    public static void main(String[] args) {
        LoggingBootstrap.initialize();
        javax.swing.SwingUtilities.invokeLater(() -> new TetrisFrame().setVisible(true));
    }
}
