package net.vetcafe.jtetris.model;

/**
 * Super Rotation System (SRS) wall-kick offsets.
 *
 * <p>Offsets are expressed in screen coordinates where +x is right and +y is down.
 */
final class SrsKickTable {
    private static final int[][] JLSTZ_0_TO_R = {{0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}};
    private static final int[][] JLSTZ_R_TO_0 = {{0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2}};
    private static final int[][] JLSTZ_R_TO_2 = {{0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2}};
    private static final int[][] JLSTZ_2_TO_R = {{0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2}};
    private static final int[][] JLSTZ_2_TO_L = {{0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2}};
    private static final int[][] JLSTZ_L_TO_2 = {{0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2}};
    private static final int[][] JLSTZ_L_TO_0 = {{0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}};
    private static final int[][] JLSTZ_0_TO_L = {{0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2}};

    private static final int[][] I_0_TO_R = {{0, 0}, {-2, 0}, {1, 0}, {-2, 1}, {1, -2}};
    private static final int[][] I_R_TO_0 = {{0, 0}, {2, 0}, {-1, 0}, {2, -1}, {-1, 2}};
    private static final int[][] I_R_TO_2 = {{0, 0}, {-1, 0}, {2, 0}, {-1, -2}, {2, 1}};
    private static final int[][] I_2_TO_R = {{0, 0}, {1, 0}, {-2, 0}, {1, 2}, {-2, -1}};
    private static final int[][] I_2_TO_L = {{0, 0}, {2, 0}, {-1, 0}, {2, -1}, {-1, 2}};
    private static final int[][] I_L_TO_2 = {{0, 0}, {-2, 0}, {1, 0}, {-2, 1}, {1, -2}};
    private static final int[][] I_L_TO_0 = {{0, 0}, {1, 0}, {-2, 0}, {1, 2}, {-2, -1}};
    private static final int[][] I_0_TO_L = {{0, 0}, {-1, 0}, {2, 0}, {-1, -2}, {2, 1}};

    private static final int[][] NO_KICK = {{0, 0}};

    private SrsKickTable() {}

    static int[][] getKickOffsets(TetrominoType type, int fromRotation, int toRotation) {
        int from = Math.floorMod(fromRotation, 4);
        int to = Math.floorMod(toRotation, 4);
        if (from == to || type == TetrominoType.O) {
            return NO_KICK;
        }

        if (type == TetrominoType.I) {
            return switch ((from << 2) | to) {
                case 0b0001 -> I_0_TO_R;
                case 0b0100 -> I_R_TO_0;
                case 0b0110 -> I_R_TO_2;
                case 0b1001 -> I_2_TO_R;
                case 0b1011 -> I_2_TO_L;
                case 0b1110 -> I_L_TO_2;
                case 0b1100 -> I_L_TO_0;
                case 0b0011 -> I_0_TO_L;
                default -> NO_KICK;
            };
        }

        return switch ((from << 2) | to) {
            case 0b0001 -> JLSTZ_0_TO_R;
            case 0b0100 -> JLSTZ_R_TO_0;
            case 0b0110 -> JLSTZ_R_TO_2;
            case 0b1001 -> JLSTZ_2_TO_R;
            case 0b1011 -> JLSTZ_2_TO_L;
            case 0b1110 -> JLSTZ_L_TO_2;
            case 0b1100 -> JLSTZ_L_TO_0;
            case 0b0011 -> JLSTZ_0_TO_L;
            default -> NO_KICK;
        };
    }
}
