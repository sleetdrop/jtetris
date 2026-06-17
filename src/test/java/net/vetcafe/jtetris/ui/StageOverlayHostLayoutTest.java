package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

class StageOverlayHostLayoutTest {

    @Test
    void simpleOverlayKeepsActionButtonsAwayFromSurfaceBottom() {
        StageOverlayHost host = new StageOverlayHost();
        host.setBounds(0, 0, 680, 520);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        JLabel message = new JLabel("Exit JTetris?");
        StageOverlayHost.styleOverlayBodyLabel(message);

        JButton quit = new JButton("Quit");
        StageOverlayHost.styleOverlayActionButton(quit);
        JButton stay = new JButton("Stay");
        StageOverlayHost.styleOverlayActionButton(stay);
        JPanel actions = StageOverlayHost.createOverlayActionRow();
        actions.add(quit);
        actions.add(stay);

        content.add(message, BorderLayout.CENTER);
        content.add(actions, BorderLayout.SOUTH);

        host.showOverlay(new StageOverlayHost.OverlaySpec("exit-confirm", "Confirm Exit", content));
        host.doLayout();
        host.validate();

        Component surface = host.getComponent(0);
        assertTrue(surface.getHeight() <= 220, "simple overlay should size to content instead of using a tall fixed panel");
        Rectangle buttonBounds = javax.swing.SwingUtilities.convertRectangle(quit.getParent(), quit.getBounds(), surface);
        Rectangle messageBounds = javax.swing.SwingUtilities.convertRectangle(message.getParent(), message.getBounds(), surface);
        int messageToButtonGap = buttonBounds.y - (messageBounds.y + messageBounds.height);
        int bottomGap = surface.getHeight() - (buttonBounds.y + buttonBounds.height);

        assertTrue(messageToButtonGap <= 24, "simple overlay action button should stay visually grouped with its message");
        assertTrue(bottomGap >= 18, "overlay action button should keep a visible bottom gap");
    }

    @Test
    void leaderboardOverlayFitsInsideStageBounds() {
        StageOverlayHost host = new StageOverlayHost();
        host.setBounds(0, 0, 680, 520);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        JLabel body = new JLabel("Leaderboard rows");
        StageOverlayHost.styleOverlayBodyLabel(body);
        JButton close = new JButton("Close");
        StageOverlayHost.styleOverlayActionButton(close);
        JPanel actions = StageOverlayHost.createOverlayActionRow();
        actions.add(close);

        content.add(body, BorderLayout.CENTER);
        content.add(actions, BorderLayout.SOUTH);

        host.showOverlay(new StageOverlayHost.OverlaySpec("leaderboard", "Leaderboard", content));
        host.doLayout();
        host.validate();

        Component surface = host.getComponent(0);
        assertTrue(surface.getX() >= 8);
        assertTrue(surface.getY() >= 8);
        assertTrue(surface.getX() + surface.getWidth() <= host.getWidth() - 8);
        assertTrue(surface.getY() + surface.getHeight() <= host.getHeight() - 8);
        assertTrue(allChildrenInside(surface), "all overlay descendants should remain inside the surface");
    }

    @Test
    void largeHelpOverlayCanUseScrollableMainWindowLayer() {
        StageOverlayHost host = new StageOverlayHost();
        host.setBounds(0, 0, 680, 520);

        host.showOverlay(new StageOverlayHost.OverlaySpec(
                "help",
                "JTetris Help",
                HelpContent.create(() -> {
                }),
                StageOverlayHost.largeSize()
        ));
        host.doLayout();
        host.validate();

        Component surface = host.getComponent(0);
        assertTrue(surface.getWidth() > 420, "help overlay should be allowed to use the larger size policy");
        assertTrue(surface.getHeight() > 270, "help overlay should be allowed to use the larger size policy");
        assertTrue(surface.getX() >= 8);
        assertTrue(surface.getY() >= 8);
        assertTrue(surface.getX() + surface.getWidth() <= host.getWidth() - 8);
        assertTrue(surface.getY() + surface.getHeight() <= host.getHeight() - 8);
    }

    @Test
    void enteringOverlayStillPaintsBottomBorderInsideSurfaceBounds() {
        UiTheme.setActiveMode(UiTheme.Mode.DARK);
        StageOverlayHost host = new StageOverlayHost();
        host.setBounds(0, 0, 680, 520);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        JLabel body = new JLabel("Score: 0");
        StageOverlayHost.styleOverlayBodyLabel(body);
        JButton close = new JButton("Continue");
        StageOverlayHost.styleOverlayActionButton(close);
        JPanel actions = StageOverlayHost.createOverlayActionRow();
        actions.add(close);
        content.add(body, BorderLayout.CENTER);
        content.add(actions, BorderLayout.SOUTH);

        host.showOverlay(new StageOverlayHost.OverlaySpec("game-over-info", "Game Over", content));
        host.doLayout();
        host.validate();

        BufferedImage image = new BufferedImage(host.getWidth(), host.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        host.paint(g2d);
        g2d.dispose();

        Component surface = host.getComponent(0);
        int sampleX = surface.getX() + (surface.getWidth() / 2);
        int sampleY = surface.getY() + surface.getHeight() - 1;
        Color actual = new Color(image.getRGB(sampleX, sampleY), true);
        Color expected = UiTheme.active().overlayBorder();

        assertTrue(colorDistance(actual, expected) <= 8, "bottom overlay border should be painted during enter animation");
    }

    private static boolean allChildrenInside(Component root) {
        if (!(root instanceof Container container)) {
            return true;
        }
        for (Component child : container.getComponents()) {
            Rectangle bounds = javax.swing.SwingUtilities.convertRectangle(child.getParent(), child.getBounds(), root);
            if (bounds.x < 0 || bounds.y < 0
                    || bounds.x + bounds.width > root.getWidth()
                    || bounds.y + bounds.height > root.getHeight()) {
                return false;
            }
            if (!allChildrenInside(child)) {
                return false;
            }
        }
        return true;
    }

    private static int colorDistance(Color a, Color b) {
        return Math.abs(a.getRed() - b.getRed())
                + Math.abs(a.getGreen() - b.getGreen())
                + Math.abs(a.getBlue() - b.getBlue());
    }
}
