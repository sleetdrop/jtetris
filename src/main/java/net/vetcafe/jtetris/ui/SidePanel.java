package net.vetcafe.jtetris.ui;

import net.vetcafe.jtetris.model.Board;
import net.vetcafe.jtetris.model.Tetromino;
import net.vetcafe.jtetris.model.TetrominoType;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class SidePanel extends JPanel {
    private final Board board;
    private JLabel scoreLabel;
    private JLabel levelLabel;
    private JLabel linesLabel;
    private JLabel feedbackLabel;
    private JLabel comboLabel;
    private JLabel b2bLabel;
    private JTextArea controlsArea;
    private final JPanel statsPanel;
    private final JPanel statusPanel;
    private final NextPanel nextPanel;

    public SidePanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(200, 520));
        setBackground(UiTheme.active().sidePanelBackground());
        setBorder(BorderFactory.createEmptyBorder(18, 18, 16, 18));
        setLayout(new BorderLayout(0, 14));

        statsPanel = createStatsPanel();
        statusPanel = createStatusPanel();
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(statsPanel);
        topPanel.add(Box.createVerticalStrut(14));
        topPanel.add(statusPanel);
        add(topPanel, BorderLayout.NORTH);

        nextPanel = new NextPanel(board);
        add(nextPanel, BorderLayout.CENTER);

        add(createControlsPanel(), BorderLayout.SOUTH);

        Timer timer = new Timer(200, e -> refreshLabels());
        timer.start();
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        scoreLabel = createLabel("Score: 0");
        levelLabel = createLabel("Level: 1");
        linesLabel = createLabel("Lines: 0");
        addStacked(panel, scoreLabel, 5);
        addStacked(panel, levelLabel, 5);
        addStacked(panel, linesLabel, 0);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        feedbackLabel = createStatusLabel("");
        comboLabel = createStatusLabel("Combo -");
        b2bLabel = createStatusLabel("B2B Ready");
        addStacked(panel, feedbackLabel, 6);
        addStacked(panel, comboLabel, 4);
        addStacked(panel, b2bLabel, 0);
        feedbackLabel.setVisible(false);
        return panel;
    }

    private void addStacked(JPanel panel, JLabel label, int bottomGap) {
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        if (bottomGap > 0) {
            panel.add(Box.createVerticalStrut(bottomGap));
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UiTheme.active().textPrimary());
        label.setFont(UiFonts.semibold(14f));
        return label;
    }

    private JLabel createStatusLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UiTheme.active().textMuted());
        label.setFont(UiFonts.semibold(12f));
        return label;
    }

    private void refreshLabels() {
        scoreLabel.setText("Score: " + board.getScore());
        levelLabel.setText("Level: " + board.getLevel());
        linesLabel.setText("Lines: " + board.getLinesCleared());
        String feedback = ScoreFeedbackFormatter.eventText(board.getLastScoreEvent());
        feedbackLabel.setText(feedback);
        feedbackLabel.setVisible(!feedback.isBlank());

        int combo = board.getComboStreak();
        comboLabel.setText(ScoreFeedbackFormatter.comboText(combo));
        comboLabel.setForeground(ScoreFeedbackFormatter.activeCombo(combo)
                ? UiTheme.active().textPrimary()
                : UiTheme.active().textMuted());

        boolean b2b = board.isBackToBackActive();
        b2bLabel.setText(ScoreFeedbackFormatter.backToBackText(b2b));
        b2bLabel.setForeground(ScoreFeedbackFormatter.activeBackToBack(b2b)
                ? UiTheme.active().textPrimary()
                : UiTheme.active().textMuted());
        nextPanel.repaint();
        repaint();
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        controlsArea = new JTextArea();
        controlsArea.setEditable(false);
        controlsArea.setOpaque(false);
        controlsArea.setForeground(UiTheme.active().textMuted());
        controlsArea.setFont(UiFonts.mono(11f));
        controlsArea.setText("Controls:\n" +
                "← / →  move\n" +
                "↓       soft drop\n" +
                "↑ or Z  rotate\n" +
                "␣       hard drop\n" +
                "C       hold\n" +
                "P       pause/resume\n" +
                "R       restart\n" +
                "H       help\n" +
                "Esc     quit");
        controlsArea.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        panel.add(controlsArea, BorderLayout.CENTER);
        return panel;
    }

    public void applyTheme() {
        setBackground(UiTheme.active().sidePanelBackground());
        scoreLabel.setForeground(UiTheme.active().textPrimary());
        levelLabel.setForeground(UiTheme.active().textPrimary());
        linesLabel.setForeground(UiTheme.active().textPrimary());
        feedbackLabel.setForeground(UiTheme.active().textPrimary());
        comboLabel.setForeground(ScoreFeedbackFormatter.activeCombo(board.getComboStreak())
                ? UiTheme.active().textPrimary()
                : UiTheme.active().textMuted());
        b2bLabel.setForeground(ScoreFeedbackFormatter.activeBackToBack(board.isBackToBackActive())
                ? UiTheme.active().textPrimary()
                : UiTheme.active().textMuted());
        if (controlsArea != null) {
            controlsArea.setForeground(UiTheme.active().textMuted());
        }
        repaint();
    }

    private static class NextPanel extends JPanel {
        private final Board board;

        NextPanel(Board board) {
            this.board = board;
            setPreferredSize(new Dimension(200, 200));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawDivider(g2d, 0);
            drawPreview(g2d, "Hold", board.getHold(), 26, board.isHoldAvailable());
            drawDivider(g2d, 106);
            drawPreview(g2d, "Next", board.getNext(), 132, true);
            g2d.dispose();
        }

        private void drawDivider(Graphics2D g2d, int y) {
            g2d.setColor(UiTheme.active().boardGrid());
            g2d.drawLine(0, y, getWidth(), y);
        }

        private void drawPreview(Graphics2D g2d, String title, Tetromino piece, int top, boolean available) {
            g2d.setColor(UiTheme.active().textMuted());
            g2d.setFont(UiFonts.semibold(12f));
            g2d.drawString(title, 0, top);

            if (piece == null) {
                drawEmptyPreview(g2d, top);
                return;
            }
            TetrominoType type = piece.getType();
            Color color = ColorPalette.colorFor(type);
            Color edge = ColorPalette.outlineFor(type);
            int cell = 18;
            int offsetX = 22;
            int offsetY = top + 16;
            for (var cellPos : type.cells(0)) {
                int x = offsetX + (cellPos.x * cell);
                int y = offsetY + (cellPos.y * cell);
                g2d.setColor(available ? color : muted(color));
                g2d.fillRect(x + 1, y + 1, cell - 2, cell - 2);
                g2d.setColor(available ? edge : muted(edge));
                g2d.drawRect(x + 1, y + 1, cell - 3, cell - 3);
            }
        }

        private void drawEmptyPreview(Graphics2D g2d, int top) {
            int cell = 18;
            int offsetX = 40;
            int offsetY = top + 24;
            Color grid = UiTheme.active().boardGrid();
            g2d.setColor(grid);
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 2; x++) {
                    g2d.drawRect(offsetX + (x * cell) + 1, offsetY + (y * cell) + 1, cell - 3, cell - 3);
                }
            }
        }

        private Color muted(Color color) {
            Color background = UiTheme.active().sidePanelBackground();
            int red = (int) Math.round((color.getRed() * 0.45) + (background.getRed() * 0.55));
            int green = (int) Math.round((color.getGreen() * 0.45) + (background.getGreen() * 0.55));
            int blue = (int) Math.round((color.getBlue() * 0.45) + (background.getBlue() * 0.55));
            return new Color(red, green, blue);
        }
    }
}
