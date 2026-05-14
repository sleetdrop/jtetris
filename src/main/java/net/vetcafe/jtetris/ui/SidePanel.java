package net.vetcafe.jtetris.ui;

import net.vetcafe.jtetris.model.Board;
import net.vetcafe.jtetris.model.Tetromino;
import net.vetcafe.jtetris.model.TetrominoType;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GridLayout;

public class SidePanel extends JPanel {
    private final Board board;
    private JLabel scoreLabel;
    private JLabel levelLabel;
    private JLabel linesLabel;
    private JLabel eventLabel;
    private JLabel comboLabel;
    private JLabel b2bLabel;
    private final JPanel statsPanel;
    private final NextPanel nextPanel;

    public SidePanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(200, 520));
        setBackground(new Color(24, 24, 32));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout(0, 10));

        statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.NORTH);

        nextPanel = new NextPanel(board);
        add(nextPanel, BorderLayout.CENTER);

        add(createControlsPanel(), BorderLayout.SOUTH);

        Timer timer = new Timer(200, e -> refreshLabels());
        timer.start();
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 0, 6));
        panel.setOpaque(false);
        scoreLabel = createLabel("Score: 0");
        levelLabel = createLabel("Level: 1");
        linesLabel = createLabel("Lines: 0");
        eventLabel = createLabel("Event: -");
        comboLabel = createLabel("Combo: 0");
        b2bLabel = createLabel("B2B: Off");
        panel.add(scoreLabel);
        panel.add(levelLabel);
        panel.add(linesLabel);
        panel.add(eventLabel);
        panel.add(comboLabel);
        panel.add(b2bLabel);
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(230, 230, 240));
        label.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        return label;
    }

    private void refreshLabels() {
        scoreLabel.setText("Score: " + board.getScore());
        levelLabel.setText("Level: " + board.getLevel());
        linesLabel.setText("Lines: " + board.getLinesCleared());
        eventLabel.setText("Event: " + formatEvent(board.getLastScoreEvent()));
        comboLabel.setText("Combo: " + board.getComboStreak());
        b2bLabel.setText("B2B: " + (board.isBackToBackActive() ? "On" : "Off"));
        nextPanel.repaint();
        repaint();
    }

    private String formatEvent(String raw) {
        if (raw == null || raw.isBlank() || "NONE".equals(raw) || "NO_CLEAR".equals(raw)) {
            return "-";
        }
        String pretty = raw.replace('_', ' ');
        return pretty.length() <= 16 ? pretty : pretty.substring(0, 16);
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setOpaque(false);
        area.setForeground(new Color(220, 220, 230));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setText("Controls:\n" +
                "← / →  move\n" +
                "↓       soft drop\n" +
                "↑ or Z  rotate\n" +
                "␣       hard drop\n" +
                "C       hold\n" +
                "P       pause/resume\n" +
                "R       restart\n" +
                "Esc     quit");
        area.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        panel.add(area, BorderLayout.CENTER);
        return panel;
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
            drawPreview(g2d, "Hold", board.getHold(), 16);
            drawPreview(g2d, "Next", board.getNext(), 116);
            g2d.dispose();
        }

        private void drawPreview(Graphics2D g2d, String title, Tetromino piece, int top) {
            g2d.setColor(new Color(220, 220, 230));
            g2d.drawString(title, 16, top);

            if (piece == null) return;
            TetrominoType type = piece.getType();
            Color color = ColorPalette.colorFor(type);
            int cell = 18;
            int offsetX = 20;
            int offsetY = top + 12;
            for (var cellPos : type.cells(0)) {
                int x = offsetX + (cellPos.x * cell);
                int y = offsetY + (cellPos.y * cell);
                g2d.setColor(color);
                g2d.fillRect(x, y, cell, cell);
                g2d.setColor(color.darker());
                g2d.drawRect(x, y, cell, cell);
            }
        }
    }
}


