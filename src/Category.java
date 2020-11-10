import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Category {
    protected final IHM i;
    private final JPanel tab;                      //  Panel for Book List Tab
    private DefaultTableModel tableModel;          //  Table to show the result
    private JTable table;                          //  Instance of the table
    private JTextField numField;
    private JTextField periodField;

    public Category(IHM gui, JTabbedPane tabbedPane) throws SQLException {
        this.i = gui;
        this.tab = new JPanel();
        this.tab.setLayout(new BoxLayout(this.tab, BoxLayout.Y_AXIS));

        this.outputSet();
        this.modifyButton();

        this.update();

        tabbedPane.add("Category", this.tab);
    }

    public void outputSet() {
        //  Panel for the result of search
        JPanel output = new JPanel();
        output.setPreferredSize(new Dimension(800, 300));

        //  Table Columns
        String[] columnBooks = {"Category", "Max Number of Books", "Period"};
        this.tableModel = new DefaultTableModel(columnBooks, 0);

        ///  Instantiate the table to show the result
        this.table = new JTable(this.tableModel);
        this.table.setDefaultEditor(Object.class, null);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ///  Scroll Pane
        JScrollPane sp = new JScrollPane(this.table);
        sp.setPreferredSize(new Dimension(800,300));

        output.add(sp);
        this.tab.add(output);
    }

    public void modifyButton() {
        JButton button = new JButton("Modify");
        button.setAlignmentX(0.5f);

        //////     Add Button Action      ///////
        button.addActionListener(e -> {
            int row = this.table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Choose a category");
            } else {
                String categorie = this.table.getValueAt(row, 0).toString();

                int option = JOptionPane.showConfirmDialog(null, modifyPanel(),
                        "Enter the maximum number of books and the borrowing period", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String num = this.numField.getText();
                    String period = this.periodField.getText();

                    if (num.equals("") || period.equals("")) {
                        return;
                    }

                    try {
                        this.modify(categorie, Integer.parseInt(num), Integer.parseInt(period));
                        JOptionPane.showMessageDialog(null, "Category is updated");
                    } catch  (NumberFormatException | SQLException e1) {
                        return;
                    }


                }

            }
            //AddAdherent aa = new AddAdherent();
            //aa.action(this);
        });
        this.tab.add(button);
    }

    public void update() throws SQLException {
        this.tableModel.setRowCount(0);
        String sql = "SELECT * FROM categorie";
        ResultSet result = this.i.stmt.executeQuery(sql);

        while (result.next()) {
            String categorie = result.getString("nom_categorie");
            String num = result.getString("nombre_emprunts");
            String period = result.getString("duree_emprunts");
            String[] row = {categorie, num, period};
            this.tableModel.addRow(row);
        }

        this.i.stmt.close();
    }


    public JPanel modifyPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        this.numField = new JTextField(10);
        this.periodField = new JTextField(10);

        JPanel panel1 = new JPanel();
        JLabel label1 = new JLabel("Maximum Books");
        label1.setPreferredSize(new Dimension(150, 10));
        panel1.add(label1);
        panel1.add(this.numField);
        panel.add(panel1);

        JPanel panel2 = new JPanel();
        JLabel label2 = new JLabel("Borrowing Period");
        label2.setPreferredSize(new Dimension(150, 10));
        panel2.add(label2);
        panel2.add(this.periodField);
        panel.add(panel2);

        return panel;
    }

    public void modify(String categorie, int num, int period) throws SQLException {
        this.i.sb.setLength(0);
        this.i.sb.append("UPDATE categorie SET nombre_emprunts = ");
        this.i.sb.append(num); this.i.sb.append(", duree_emprunts = "); this.i.sb.append(period);
        this.i.sb.append(" WHERE nom_categorie = '"); this.i.sb.append(categorie); this.i.sb.append("'");

        String sql = this.i.sb.toString();
        this.i.stmt.executeUpdate(sql);
        this.update();
        this.i.stmt.close();
    }
}
