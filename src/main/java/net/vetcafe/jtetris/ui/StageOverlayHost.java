package net.vetcafe.jtetris.ui;

import javax.swing.BorderFactory;
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

/**
 * In-stage overlay host that manages HUD-style panel lifecycle and basic enter/exit motion.
 */
public final class StageOverlayHost extends JPanel {
    private static final int MIN_WIDTH = 300;
    private static final int MAX_WIDTH = 520;
    private static final int MIN_HEIGHT = 140;
    private static final int MAX_HEIGHT = 320;
    private static final int ANIMATION_MS = 140;
    private static final int TICK_MS = 16;

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

        titleLabel.setFont(UiFonts.semibold(18f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 2));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titleLabel, BorderLayout.WEST);

        surface.setLayout(new BorderLayout(0, 4));
        surface.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
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

    public OverlaySpec activeOverlay() {
        return activeOverlay;
    }

    public void applyTheme() {
        UiTheme theme = UiTheme.active();
        titleLabel.setForeground(theme.textPrimary());
        surface.setPanelColors(theme.dialogSurface(), theme.dialogBorder());
        repaint();
    }

    @Override
    public void doLayout() {
        super.doLayout();
        updateSurfaceBounds();
    }

    private void updateSurfaceBounds() {
        int width = clamp((int) (getWidth() * 0.72), MIN_WIDTH, MAX_WIDTH);
        int height = clamp((int) (getHeight() * 0.56), MIN_HEIGHT, MAX_HEIGHT);
        int x = (getWidth() - width) / 2;
        int y = (getHeight() - height) / 2;
        surface.setBounds(x, y, width, height);
    }

    private void advanceAnimation() {
        if (state == State.HIDDEN) {
            animationTimer.stop();
            return;
        }

        float elapsed = (System.currentTimeMillis() - animationStartAtMs) / (float) ANIMATION_MS;
        float progress = Math.max(0f, Math.min(1f, elapsed));

        if (state == State.ENTERING) {
            surface.setVisual(0.35f + (0.65f * progress), (int) ((1f - progress) * 12));
            if (progress >= 1f) {
                state = State.VISIBLE;
                surface.setVisual(1f, 0);
                if (activeOverlay != null) {
                    activeOverlay.lifecycle().onOpened();
                }
                animationTimer.stop();
            }
        } else if (state == State.EXITING) {
            surface.setVisual(1f - (0.65f * progress), (int) (progress * 10));
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
        }

        private void setPanelColors(Color background, Color border) {
            setBackground(background);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(border, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(0, offsetY);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paint(g2d);
            g2d.dispose();
        }
    }
}

