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

public class TetrisFrame extends JFrame {
    static {
        // On macOS this merges the menu bar into the system bar when supported
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Tetris");
    }

    private final Board board = new Board();
    private final GamePanel gamePanel = new GamePanel(board);
    private final SidePanel sidePanel = new SidePanel(board);
    private final ScoreManager scoreManager = new ScoreManager();
    // ensure score dialog is shown once per game
    private boolean scorePrompted;
    private boolean lastGameOverProcessed;
    private String userName; // null means not tracking this run
    private boolean paused;

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
        });

        installKeyBindings();

        int delay = 700; // ms; will speed up with levels
        Timer timer = new Timer(delay, e -> {
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
        JMenuItem exit = new JMenuItem("Quit (Esc)");
        exit.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        gameMenu.add(pause);
        gameMenu.add(restart);
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
        setTitle("Tetris" + (paused ? " (Paused)" : ""));
    }

    private void restartGame() {
        paused = false;
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
        int choice = JOptionPane.showConfirmDialog(this, "Start a new game?", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
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

        int result = JOptionPane.showConfirmDialog(this, panel, "Record score", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
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
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.INFORMATION_MESSAGE);
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
        JOptionPane.showMessageDialog(this, panel, "Leaderboard", JOptionPane.PLAIN_MESSAGE);
        focusGame();
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

        registerAction(im, am, "left", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), () -> moveIfActive(-1, 0));
        registerAction(im, am, "right", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), () -> moveIfActive(1, 0));
        registerAction(im, am, "down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), () -> moveIfActive(0, 1));
        registerAction(im, am, "rotateCW", KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), () -> rotateIfActive(true));
        registerAction(im, am, "rotateCCW", KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), () -> rotateIfActive(false));
        registerAction(im, am, "hardDrop", KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), this::hardDropIfActive);
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
                focusGame();
            }
        });
    }

    private void moveIfActive(int dx, int dy) {
        if (paused || board.isGameOver()) return;
        board.move(dx, dy);
        gamePanel.repaint();
    }

    private void rotateIfActive(boolean cw) {
        if (paused || board.isGameOver()) return;
        if (cw) board.rotateCW(); else board.rotateCCW();
        gamePanel.repaint();
    }

    private void hardDropIfActive() {
        if (paused || board.isGameOver()) return;
        board.hardDrop();
        gamePanel.repaint();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new TetrisFrame().setVisible(true));
    }
}
