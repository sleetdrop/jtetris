package net.vetcafe.jtetris.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FlowLayout;
import java.awt.Insets;

/**
 * In-stage overlay host that manages HUD-style panel lifecycle and basic enter/exit motion.
 */
public final class StageOverlayHost extends JPanel {
    private static final int MIN_WIDTH = 320;
    private static final int MAX_WIDTH = 420;
    private static final int MIN_HEIGHT = 150;
    private static final int MAX_HEIGHT = 270;
    private static final int ANIMATION_MS = 140;
    private static final int TICK_MS = 16;
    private static final int SURFACE_PADDING_X = 16;
    private static final int SURFACE_PADDING_Y = 14;
    private static final int HEADER_BOTTOM_GAP = 10;
    private static final int TITLE_INSET_X = 1;
    private static final int TITLE_INSET_TOP = 1;
    private static final int TITLE_INSET_BOTTOM = 6;
    private static final int BODY_PADDING = 8;
    private static final float TITLE_FONT_SIZE = 16f;
    private static final float BODY_FONT_SIZE = 14f;
    private static final float BUTTON_FONT_SIZE = 13f;
    private static final int ENTER_SLIDE_PX = 12;
    private static final int EXIT_SLIDE_PX = 10;

    public enum State {
        HIDDEN,
        ENTERING,
        VISIBLE,
        EXITING
    }

    public interface OverlayLifecycle {
        default void onOpened() {
        }

        default void onClosed() {
        }
    }

    public record OverlaySpec(String id, String title, JComponent content, OverlayLifecycle lifecycle) {
        public OverlaySpec {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("overlay id is required");
            }
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("overlay title is required");
            }
            if (content == null) {
                throw new IllegalArgumentException("overlay content is required");
            }
            lifecycle = lifecycle == null ? new OverlayLifecycle() {
            } : lifecycle;
        }

        public OverlaySpec(String id, String title, JComponent content) {
            this(id, title, content, null);
        }
    }

    private final OverlaySurface surface = new OverlaySurface();
    private final JLabel titleLabel = new JLabel();
    private final Timer animationTimer;
    private State state = State.HIDDEN;
    private OverlaySpec activeOverlay;
    private long animationStartAtMs;

    public StageOverlayHost() {
        setOpaque(false);
        setVisible(false);
        setLayout(null);
        add(surface);

        titleLabel.setFont(UiFonts.semibold(TITLE_FONT_SIZE));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(TITLE_INSET_TOP, TITLE_INSET_X, TITLE_INSET_BOTTOM, TITLE_INSET_X));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titleLabel, BorderLayout.WEST);

        surface.setLayout(new BorderLayout(0, HEADER_BOTTOM_GAP));
        surface.setBorder(BorderFactory.createEmptyBorder(SURFACE_PADDING_Y, SURFACE_PADDING_X, SURFACE_PADDING_Y, SURFACE_PADDING_X));
        surface.add(header, BorderLayout.NORTH);

        animationTimer = new Timer(TICK_MS, e -> advanceAnimation());
        applyTheme();
    }

    public void showOverlay(OverlaySpec spec) {
        activeOverlay = spec;
        surface.setContent(spec.content());
        titleLabel.setText(spec.title());
        state = State.ENTERING;
        animationStartAtMs = System.currentTimeMillis();
        setVisible(true);
        surface.setVisual(1f, ENTER_SLIDE_PX);
        updateSurfaceBounds();
        animationTimer.start();
    }

    public void dismissOverlay() {
        if (activeOverlay == null || state == State.EXITING || state == State.HIDDEN) {
            return;
        }
        state = State.EXITING;
        animationStartAtMs = System.currentTimeMillis();
        animationTimer.start();
    }

    public State state() {
        return state;
    }

    public boolean isOverlayVisible() {
        return state != State.HIDDEN;
    }

    public static void styleOverlayBodyLabel(JLabel label) {
        label.setFont(UiFonts.regular(BODY_FONT_SIZE));
        label.setForeground(UiTheme.active().overlayText());
    }

    public static void styleOverlayActionButton(JButton button) {
        button.setFont(UiFonts.regular(BUTTON_FONT_SIZE));
        button.setMargin(new Insets(5, 14, 5, 14));
        button.setFocusPainted(true);
        button.setFocusable(true);
    }

    public static JPanel createOverlayActionRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(14, 0, 4, 0));
        return row;
    }

    public OverlaySpec activeOverlay() {
        return activeOverlay;
    }

    public void applyTheme() {
        UiTheme theme = UiTheme.active();
        titleLabel.setForeground(theme.overlayText());
        surface.setPanelColors(theme.overlayBackground(), theme.overlaySurface(), theme.overlayBorder(), theme.overlayAccent());
        repaint();
    }

    @Override
    public void doLayout() {
        super.doLayout();
        updateSurfaceBounds();
    }

    private void updateSurfaceBounds() {
        int availableWidth = Math.max(1, getWidth() - 16);
        int availableHeight = Math.max(1, getHeight() - 16);
        Dimension preferred = surface.getPreferredSize();
        int contentWidth = Math.max(MIN_WIDTH, preferred.width + 24);
        int contentHeight = Math.max(MIN_HEIGHT, preferred.height + 8);
        int width = Math.min(clamp(contentWidth, MIN_WIDTH, MAX_WIDTH), availableWidth);
        int height = Math.min(clamp(contentHeight, MIN_HEIGHT, MAX_HEIGHT), availableHeight);
        int x = (getWidth() - width) / 2;
        int y = (getHeight() - height) / 2;
        surface.setBaseBounds(x, y, width, height);
    }

    private void advanceAnimation() {
        if (state == State.HIDDEN) {
            animationTimer.stop();
            return;
        }

        float elapsed = (System.currentTimeMillis() - animationStartAtMs) / (float) ANIMATION_MS;
        float progress = Math.max(0f, Math.min(1f, elapsed));

        if (state == State.ENTERING) {
            surface.setVisual(1f, (int) ((1f - progress) * ENTER_SLIDE_PX));
            if (progress >= 1f) {
                state = State.VISIBLE;
                surface.setVisual(1f, 0);
                if (activeOverlay != null) {
                    activeOverlay.lifecycle().onOpened();
                }
                animationTimer.stop();
            }
        } else if (state == State.EXITING) {
            surface.setVisual(1f, (int) (progress * EXIT_SLIDE_PX));
            if (progress >= 1f) {
                OverlaySpec closing = activeOverlay;
                activeOverlay = null;
                surface.clearContent();
                setVisible(false);
                state = State.HIDDEN;
                if (closing != null) {
                    closing.lifecycle().onClosed();
                }
                animationTimer.stop();
            }
        }
        repaint();
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class OverlaySurface extends JPanel {
        private float alpha = 1f;
        private int offsetY;
        private int baseX;
        private int baseY;
        private JComponent body;

        private void setContent(JComponent content) {
            if (body != null) {
                remove(body);
            }
            body = content;
            add(body, BorderLayout.CENTER);
            revalidate();
            repaint();
        }

        private void clearContent() {
            if (body != null) {
                remove(body);
                body = null;
            }
            revalidate();
            repaint();
        }

        private void setVisual(float alpha, int offsetY) {
            this.alpha = Math.max(0f, Math.min(1f, alpha));
            this.offsetY = offsetY;
            setBounds(baseX, baseY + offsetY, getWidth(), getHeight());
        }

        private void setBaseBounds(int x, int y, int width, int height) {
            baseX = x;
            baseY = y;
            setBounds(baseX, baseY + offsetY, width, height);
        }

        private void setPanelColors(Color background, Color surfaceColor, Color border, Color accent) {
            setBackground(background);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(border, 1),
                    BorderFactory.createEmptyBorder(BODY_PADDING, BODY_PADDING, BODY_PADDING, BODY_PADDING)
            ));
            if (body != null) {
                body.setBackground(surfaceColor);
            }
            putClientProperty("overlayAccent", accent);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paint(g2d);
            g2d.dispose();
        }
    }
}
