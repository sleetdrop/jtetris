package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.JComboBox;
import org.junit.jupiter.api.Test;

class TetrisFrameScoreEntryTest {
    @Test
    void scoreEntryDefaultUserMatchesExistingUserCaseInsensitively() {
        JComboBox<String> users = new JComboBox<>(new String[] {"alice", "bob"});

        TetrisFrame.selectScoreEntryDefaultUser(users, "Bob");

        assertEquals("bob", users.getSelectedItem());
    }
}
