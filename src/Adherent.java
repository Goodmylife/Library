import javax.swing.*;
import java.sql.SQLException;

public class Adherent {
    private final IHM i;
    private final JTabbedPane tabbedpane;

    public Adherent(IHM gui) throws SQLException {
        this.i = gui;
        JPanel panel = new JPanel();
        this.tabbedpane = new JTabbedPane();

        BookList bl = new BookList(i, this.tabbedpane);
        PersonalInfo pi = new PersonalInfo(i, this.tabbedpane);
        MyBorrowing  mb= new MyBorrowing(i, this.tabbedpane);
        new Logout(i, this.tabbedpane);

        this.tabbedpane.addChangeListener(e -> {
            int selIndex = this.tabbedpane.getSelectedIndex();
            String title = this.tabbedpane.getTitleAt(selIndex);
            System.out.println("Selected tab: " + title);

            switch(title) {
                case "Book List":
                    if (this.i.isupdate) {
                        try {
                            bl.updateBookList(null, null);
                            JOptionPane.showMessageDialog(null, "The data has been updated");
                            this.i.isupdate = false;
                        } catch (SQLException e1) {
                            JOptionPane.showMessageDialog(null, "Updating failed");
                            e1.printStackTrace();
                        }
                    }
                    break;
                case  "Personal Info":
                    pi.updatePersonalInfo();
                    break;
                case "My Borrowing":
                    this.i.canBorrow();
                    mb.updateLabel();
                    mb.update();
                    break;
            }
        });
        panel.add(this.tabbedpane);
        this.i.panelCard.add(panel);
    }
}
