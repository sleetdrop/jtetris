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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

public class SidePanel extends JPanel {
    private final Board board;
    private JLabel scoreLabel;
    private JLabel levelLabel;
    private JLabel linesLabel;
    private JLabel feedbackLabel;
    private JLabel comboLabel;
    private JLabel b2bLabel;
    private final JPanel statsPanel;
    private final JPanel statusPanel;
    private final PreviewPanel previewPanel;

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

        previewPanel = new PreviewPanel(board);
        add(previewPanel, BorderLayout.CENTER);

        Timer timer = new Timer(200, e -> refreshLabels());
        timer.start();
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        scoreLabel = createLabel("Score: 0");
        scoreLabel.setFont(UiFonts.semibold(18f));
        levelLabel = createCompactLabel("Level: 1");
        linesLabel = createCompactLabel("Lines: 0");
        addStacked(panel, scoreLabel, 8);
        addStacked(panel, levelLabel, 4);
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

    private JLabel createCompactLabel(String text) {
        JLabel label = createLabel(text);
        label.setFont(UiFonts.semibold(13f));
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
        previewPanel.repaint();
        repaint();
    }

    List<TetrominoType> displayedNextTypes() {
        return previewPanel.displayedNextTypes();
    }

    static Color sectionTitleColor() {
        return UiTheme.active().textPrimary();
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
        repaint();
    }

    private static class PreviewPanel extends JPanel {
        private static final int HOLD_TITLE_Y = 28;
        private static final int HOLD_PIECE_Y = 42;
        private static final int NEXT_DIVIDER_Y = 104;
        private static final int NEXT_TITLE_Y = 134;
        private static final int PRIMARY_NEXT_Y = 150;
        private static final int SECONDARY_NEXT_Y = 216;
        private static final int TERTIARY_NEXT_Y = 274;

        private final Board board;

        PreviewPanel(Board board) {
            this.board = board;
            setPreferredSize(new Dimension(200, 320));
            setOpaque(false);
        }

        List<TetrominoType> displayedNextTypes() {
            return board.getNextQueue();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawDivider(g2d, 0);
            drawTitle(g2d, "Hold", HOLD_TITLE_Y);
            drawHold(g2d, board.getHold(), HOLD_PIECE_Y, board.isHoldAvailable());
            drawDivider(g2d, NEXT_DIVIDER_Y);
            drawTitle(g2d, "Next", NEXT_TITLE_Y);

            List<TetrominoType> nextTypes = displayedNextTypes();
            if (!nextTypes.isEmpty()) {
                drawPiece(g2d, nextTypes.get(0), PRIMARY_NEXT_Y, 18, true);
            }
            if (nextTypes.size() > 1) {
                drawPiece(g2d, nextTypes.get(1), SECONDARY_NEXT_Y, 15, true);
            }
            if (nextTypes.size() > 2) {
                drawPiece(g2d, nextTypes.get(2), TERTIARY_NEXT_Y, 15, true);
            }
            g2d.dispose();
        }

        private void drawDivider(Graphics2D g2d, int y) {
            g2d.setColor(UiTheme.active().boardGrid());
            g2d.drawLine(0, y, getWidth(), y);
        }

        private void drawTitle(Graphics2D g2d, String title, int baseline) {
            g2d.setColor(sectionTitleColor());
            g2d.setFont(UiFonts.semibold(12f));
            g2d.drawString(title, 0, baseline);
        }

        private void drawHold(Graphics2D g2d, Tetromino piece, int top, boolean available) {
            if (piece == null) {
                drawEmptyPreview(g2d, top);
                return;
            }
            drawPiece(g2d, piece.getType(), top, 18, available);
        }

        private void drawPiece(Graphics2D g2d, TetrominoType type, int top, int cell, boolean available) {
            Color color = ColorPalette.colorFor(type);
            Color edge = ColorPalette.outlineFor(type);
            var cells = type.cells(0);
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            for (var cellPos : cells) {
                minX = Math.min(minX, cellPos.x);
                maxX = Math.max(maxX, cellPos.x);
            }
            int pieceWidth = (maxX - minX + 1) * cell;
            int offsetX = ((getWidth() - pieceWidth) / 2) - (minX * cell);
            for (var cellPos : type.cells(0)) {
                int x = offsetX + (cellPos.x * cell);
                int y = top + (cellPos.y * cell);
                g2d.setColor(available ? color : muted(color));
                g2d.fillRect(x + 1, y + 1, cell - 2, cell - 2);
                g2d.setColor(available ? edge : muted(edge));
                g2d.drawRect(x + 1, y + 1, cell - 3, cell - 3);
            }
        }

        private void drawEmptyPreview(Graphics2D g2d, int top) {
            int cell = 18;
            int offsetX = (getWidth() - (cell * 2)) / 2;
            Color grid = UiTheme.active().boardGrid();
            g2d.setColor(grid);
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 2; x++) {
                    g2d.drawRect(offsetX + (x * cell) + 1, top + (y * cell) + 1, cell - 3, cell - 3);
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
