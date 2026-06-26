package net.vetcafe.jtetris.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import net.vetcafe.jtetris.score.ScoreManager;

final class LeaderboardContent extends JPanel {
    private final JTable table;
    private final JLabel emptyLabel;
    private final JButton deleteButton = new JButton("Delete");
    private final JButton closeButton = new JButton("Close");

    LeaderboardContent(List<ScoreManager.ScoreEntry> entries, Consumer<String> deleteAction, Runnable closeAction) {
        super(new BorderLayout(0, 10));
        setOpaque(false);

        UiTheme theme = UiTheme.active();
        if (entries.isEmpty()) {
            table = null;
            emptyLabel = new JLabel("No scores yet");
            StageOverlayHost.styleOverlayBodyLabel(emptyLabel);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            add(emptyLabel, BorderLayout.CENTER);
        } else {
            emptyLabel = null;
            DefaultTableModel model = new DefaultTableModel(new Object[] {"User", "Best"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            for (ScoreManager.ScoreEntry entry : entries) {
                model.addRow(new Object[] {entry.user(), entry.score()});
            }

            table = new JTable(model);
            table.setBackground(theme.dialogBackground());
            table.setForeground(theme.textPrimary());
            table.setGridColor(theme.tableGrid());
            table.setRowHeight(26);
            table.setIntercellSpacing(new Dimension(1, 1));
            table.setFont(UiFonts.regular(14f));
            table.getTableHeader().setBackground(theme.tableHeaderBackground());
            table.getTableHeader().setForeground(theme.tableHeaderText());
            table.getTableHeader().setFont(UiFonts.semibold(14f));
            table.getTableHeader().setBorder(BorderFactory.createLineBorder(theme.dialogBorder(), 1));
            table.getTableHeader().setReorderingAllowed(false);
            table.setFillsViewportHeight(true);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(event -> updateDeleteState());
            int visibleRows = Math.max(1, Math.min(entries.size(), 8));
            int preferredHeight = (visibleRows * table.getRowHeight())
                    + table.getTableHeader().getPreferredSize().height
                    + 8;
            table.setPreferredScrollableViewportSize(new Dimension(360, preferredHeight));

            JScrollPane scroll = new JScrollPane(table);
            scroll.getViewport().setBackground(theme.dialogBackground());
            scroll.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            add(scroll, BorderLayout.CENTER);
        }

        StageOverlayHost.styleOverlayActionButton(deleteButton);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(event -> {
            if (table == null || table.getSelectedRow() < 0) {
                return;
            }
            int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
            deleteAction.accept(table.getModel().getValueAt(modelRow, 0).toString());
        });

        StageOverlayHost.styleOverlayActionButton(closeButton);
        closeButton.addActionListener(event -> closeAction.run());

        JPanel actions = StageOverlayHost.createOverlayActionRow();
        actions.add(deleteButton);
        actions.add(closeButton);
        add(actions, BorderLayout.SOUTH);
    }

    JTable table() {
        return table;
    }

    JLabel emptyLabel() {
        return emptyLabel;
    }

    JButton deleteButton() {
        return deleteButton;
    }

    JButton closeButton() {
        return closeButton;
    }

    private void updateDeleteState() {
        deleteButton.setEnabled(table != null && table.getSelectedRow() >= 0);
    }
}
