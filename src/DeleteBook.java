import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteBook {
    private final IHM i;

    public DeleteBook(IHM gui, JTabbedPane tabbedpane) {
        this.i = gui;
        //  Panel for Book List Tab
        JPanel tab = new JPanel();

        JPanel panel = new JPanel();

        panel.add(new JLabel("Book ID"));
        JTextField searchBar = new JTextField(20);
        panel.add(searchBar);
        JButton button = new JButton("Delete");

        button.addActionListener(e -> {
            String id_livre = searchBar.getText();

            if (id_livre.equals("")) {
                return;
            }

            try {
                this.i.sb.setLength(0);
                this.i.sb.append("SELECT * FROM Borrowed WHERE id_livre = ");
                this.i.sb.append(id_livre);
                String sql = this.i.sb.toString();
                ResultSet result =  this.i.stmt.executeQuery(sql);

                if (result.next()) {
                    JOptionPane.showMessageDialog(null, "This book is being borrowed!");
                } else {
                    ///   Delete from emprunter
                    this.i.sb.setLength(0);
                    this.i.sb.append("DELETE FROM emprunter WHERE id_livre = ");
                    this.i.sb.append(id_livre);
                    sql = this.i.sb.toString();
                    this.i.stmt.executeUpdate(sql);

                    ///   Delete from livre
                    this.i.sb.setLength(0);
                    this.i.sb.append("DELETE FROM livre WHERE id_livre = ");
                    this.i.sb.append(id_livre);
                    sql = this.i.sb.toString();
                    this.i.stmt.executeUpdate(sql);
                }

                JOptionPane.showMessageDialog(null, "The Book has been deleted");
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(null, "Deletion Error");
                e1.printStackTrace();
            }
        });

        panel.add(button);
        tab.add(panel);

        tabbedpane.add("Delete Book", tab);
    }
}
