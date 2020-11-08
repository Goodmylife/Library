import javax.swing.*;
import java.sql.SQLException;

public class Admin {
    private final JTabbedPane tabbedpane;

    public Admin(IHM gui) throws SQLException {
        JPanel panel = new JPanel();
        this.tabbedpane = new JTabbedPane();

        AddBook ab = new AddBook(gui, this.tabbedpane);
        new DeleteBook(gui, this.tabbedpane);
        AdherentInfo ai = new AdherentInfo(gui, this.tabbedpane);
        new Logout(gui, this.tabbedpane);


        this.tabbedpane.addChangeListener(e -> {
            int selIndex = this.tabbedpane.getSelectedIndex();
            String title = this.tabbedpane.getTitleAt(selIndex);
            System.out.println("Selected tab: " + title);

            switch(title) {
                case "Book":
                    try {
                        ab.allBookList(null, null);
                    } catch (SQLException e1) {
                        JOptionPane.showMessageDialog(null, "Updating failed");
                        e1.printStackTrace();
                    }
                    break;
                case  "Delete Book":
                    break;
                case  "Adherent":
                    try {
                        ai.adherentInfo(null, null);
                    } catch (SQLException throwables) {
                        JOptionPane.showMessageDialog(null, "Updating failed");
                        throwables.printStackTrace();
                    }
                    break;
                case "Logout":
                    break;
            }
        });
        panel.add(this.tabbedpane);
        gui.panelCard.add(panel);

    }
}
