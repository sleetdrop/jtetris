package net.vetcafe.jtetris.model;

/**
 * Discrete deterministic actions used by seeded replay hooks.
 */
public enum ReplayAction {
    LEFT,
    RIGHT,
    SOFT_DROP,
    ROTATE_CW,
    ROTATE_CCW,
    HARD_DROP,
    HOLD,
    TICK
}
