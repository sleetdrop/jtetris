package net.vetcafe.jtetris.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

final class HelpContent {
    private static final int PREFERRED_WIDTH = 560;
    private static final int PREFERRED_HEIGHT = 360;

    private HelpContent() {}

    static JComponent create(Runnable closeAction) {
        UiTheme theme = UiTheme.active();

        JTextPane content = new JTextPane();
        content.setContentType("text/html");
        content.setEditable(false);
        content.setFocusable(false);
        content.setFont(UiFonts.regular(13f));
        content.setBackground(theme.overlaySurface());
        content.setForeground(theme.textPrimary());
        content.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        content.setText(helpHtml(theme));
        content.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        scrollPane.setBorder(BorderFactory.createLineBorder(theme.dialogBorder(), 1));
        scrollPane.getViewport().setBackground(theme.overlaySurface());
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);

        JButton close = new JButton("Close");
        StageOverlayHost.styleOverlayActionButton(close);
        close.addActionListener(e -> closeAction.run());

        JPanel actions = StageOverlayHost.createOverlayActionRow();
        actions.add(close);

        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setOpaque(false);
        root.add(scrollPane, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        return root;
    }

    static String helpHtml(UiTheme theme) {
        String text = color(theme.textPrimary());
        String background = color(theme.overlaySurface());
        String accent = color(theme.accent());
        return """
                <html>
                <body style='font-family: sans-serif; color: %s; background: %s;'>
                <h2 style='font-size: 15px; color: %s;'>Controls</h2>
                <table cellspacing='4' cellpadding='2'>
                  <tr><td><b>Left / Right</b></td><td>Move the active piece.</td></tr>
                  <tr><td><b>Down</b></td><td>Soft drop.</td></tr>
                  <tr><td><b>Up or Z</b></td><td>Rotate clockwise or counter-clockwise.</td></tr>
                  <tr><td><b>Space</b></td><td>Hard drop.</td></tr>
                  <tr><td><b>C</b></td><td>Hold or swap the active piece.</td></tr>
                  <tr><td><b>P</b></td><td>Pause or resume.</td></tr>
                  <tr><td><b>R</b></td><td>Restart.</td></tr>
                  <tr><td><b>L</b></td><td>Show leaderboard.</td></tr>
                  <tr><td><b>H</b></td><td>Show this Help page.</td></tr>
                  <tr><td><b>Esc</b></td><td>Quit or close the active prompt.</td></tr>
                </table>
                <h2 style='font-size: 15px; color: %s;'>Endless Marathon</h2>
                <p>A run continues until top-out. Score is the primary result; Level and Lines show progression.
                Time measures active play and excludes pauses and blocking prompts.</p>
                <h2 style='font-size: 15px; color: %s;'>Playfield</h2>
                <p>The ghost piece shows where the active piece will land. Next shows the next three pieces in
                play order. Hold stores one piece for later; after you use Hold, it is unavailable until the
                current piece locks.</p>
                <h2 style='font-size: 15px; color: %s;'>Scoring Feedback</h2>
                <p><b>Single</b>, <b>Double</b>, <b>Triple</b>, and <b>Tetris</b> describe clearing 1, 2, 3,
                or 4 lines at once.</p>
                <p><b>Combo</b> means consecutive pieces cleared lines. It resets after a piece locks without
                clearing a line.</p>
                <p><b>Back-to-Back</b> tracks consecutive difficult clears such as Tetris and T-Spin line clears.
                It stays ready through non-clearing pieces and breaks on ordinary line clears.</p>
                <p><b>T-Spin</b> is awarded when a T piece locks after a rotation in a tight corner setup.
                JTetris currently implements baseline T-Spin scoring.</p>
                </body>
                </html>
                """
                .formatted(text, background, accent, accent, accent, accent);
    }

    private static String color(java.awt.Color color) {
        return "#%02x%02x%02x".formatted(color.getRed(), color.getGreen(), color.getBlue());
    }
}
