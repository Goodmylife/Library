import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyBorrowing {
    private final IHM i;
    private final JPanel tab;                           //  Panel for Personal Info Tab
    private  JLabel label;
    private DefaultTableModel currentModel;
    private DefaultTableModel pastModel;

    public MyBorrowing(IHM gui, JTabbedPane tabbedpane) {
        this.i = gui;
        this.tab = new JPanel();
        this.tab.setLayout(new BoxLayout(this.tab, BoxLayout.Y_AXIS));

        this.labelMessage();
        this.updateLabel();
        this.currentInfo();
        this.pastInfo();

        tabbedpane.addTab("My Borrowing", this.tab);
    }

    public void labelMessage() {
        ///   Message about how many additional books the user can borrow
        this.label = new JLabel();
        this.label.setAlignmentX(0.5f);
        this.tab.add(label);
    }

    public void updateLabel() {
        String rest = Integer.toString(this.i.max_borrowed - this.i.nb_borrowed);
        this.label.setText("You can borrow " + rest + " books");
    }

    public void currentInfo() {
        ///   Label
        JLabel label = new JLabel("You are borrowing the following books");
        label.setAlignmentX(0.5f);
        this.tab.add(Box.createRigidArea(new Dimension(10, 10)));
        this.tab.add(label);

        ///   Table
        String[] columnBorrows = {"Title", "ISBN", "From", "Until", "Book ID"};
        this.currentModel = new DefaultTableModel(columnBorrows, 0);
        JTable tableCurrent = new JTable(this.currentModel);
        ///   Make it impossible to edit the cells
        tableCurrent.setDefaultEditor(Object.class, null);
        ///   Only one row can be selected
        tableCurrent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ///   Scroll Pane
        JScrollPane sp = new JScrollPane(tableCurrent);
        sp.setPreferredSize(new Dimension(800,150));
        this.tab.add(sp);

        ///   Return Button
        JButton button = new JButton("Return");
        button.setAlignmentX(0.5f);

        //////      Return Button Action      //////
        button.addActionListener(e -> {
            int row = tableCurrent.getSelectedRow();

            ///  If any row is not chosen, show an error message
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Choose a book");
            } else {
                ///   Get the book ID
                String id_book = tableCurrent.getValueAt(row, 4).toString();

                this.returnBook(id_book);

                this.updateLabel();
                this.update();
            }
        });

        this.tab.add(button);
    }

    public void pastInfo() {
        ///   Label
        JLabel label = new JLabel("History");
        label.setAlignmentX(0.5f);
        this.tab.add(Box.createRigidArea(new Dimension(10, 10)));
        this.tab.add(label);

        ///   Table
        String[] columnBorrows = {"Title", "ISBN", "From", "Until", "Book ID"};
        this.pastModel = new DefaultTableModel(columnBorrows, 0);
        JTable tablePast = new JTable(this.pastModel);
        ///   Make it impossible to edit the cells
        tablePast.setDefaultEditor(Object.class, null);
        ///   Only one row can be selected
        tablePast.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ///   Scroll Pane
        JScrollPane sp = new JScrollPane(tablePast);
        sp.setPreferredSize(new Dimension(800,150));
        this.tab.add(sp);
    }


    public void returnBook(String id_book) {
        try {
            this.i.sb.setLength(0);

            ///   Update the database
            this.i.sb.append("UPDATE emprunter SET date_fin = ");
            this.i.sb.append("DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME') ");
            this.i.sb.append("WHERE id_livre = ");
            this.i.sb.append(Integer.parseInt(id_book));
            this.i.sb.append(" AND id_usager = "); this.i.sb.append(this.i.IDuser);

            String sql = this.i.sb.toString();
            this.i.stmt.executeUpdate(sql);

            JOptionPane.showMessageDialog(null, "You have returned the book");
            this.i.nb_borrowed--;

            this.i.stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Returning failed");
            e.printStackTrace();
        }
    }

    public void update() {
        this.updateBorrowing(this.currentModel, true);      //  True -> Current Information
        this.updateBorrowing(this.pastModel, false);        //  False -> Past Information
    }

    public void updateBorrowing(DefaultTableModel tableModel, boolean flag) {
        tableModel.setRowCount(0);

        try {
            this.i.sb.setLength(0);

            ///   Search relevant information
            this.i.sb.append("SELECT * FROM WhoBorrowWhat ");
            this.i.sb.append("WHERE email = ");
            this.i.sb.append("'"); this.i.sb.append(this.i.user); this.i.sb.append("' ");

            if(flag) {
                this.i.sb.append("AND date_fin > DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')");
            } else {
                this.i.sb.append("AND date_fin <= DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')");
            }

            String sql = this.i.sb.toString();
            ResultSet result = this.i.stmt.executeQuery(sql);

            while (result.next()) {
                String title = result.getString("title");
                String ISBN = result.getString("ISBN");
                String from = result.getString("date_debut");
                String until = result.getString("date_fin");
                String id_book = result.getString("id_livre");
                String[] row = {title, ISBN, from, until, id_book};
                tableModel.addRow(row);
            }

            result.close();
            this.i.stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Update failed");
            e.printStackTrace();
        }
    }
}
