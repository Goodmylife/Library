import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonalInfo {
    private final IHM i;
    private final JPanel tab;                           //  Panel for Personal Info Tab
    private final DefaultTableModel tableModel;         //  Table to show the result

    public PersonalInfo(IHM gui, JTabbedPane tabbedpane) {
        this.i = gui;
        this.tab = new JPanel();

        ///  Tab Layout
        this.tab.setLayout(new BoxLayout(this.tab, BoxLayout.Y_AXIS));

        ///  If the user is on a Red List, show a message about that
        if(this.i.redList()) {
            this.redListMessage();
        }

        //  Table Columns
        String[] columnInfo = {"Prenom", "Nom", "Email"};
        this.tableModel = new DefaultTableModel(columnInfo, 0);

        ///  Instantiate the table to show the result
        //  Instance of the table
        JTable table = new JTable(this.tableModel);
        table.setDefaultEditor(Object.class, null);


        ///  Scroll Pane
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(100,50));
        this.tab.add(sp);

        tabbedpane.addTab("Personal Info", this.tab);
    }

    public void redListMessage() {
        JPanel message = new JPanel();
        JLabel label1 = new JLabel("<html><span style='text-align:center'>You are on a RED LIST</span>");
        JLabel label2 = new JLabel();

        String text = "<html><span style='text-align:center'>From    <font color=red>" + this.i.redListStart
                        + "</font>    Until    <font color=red>" + this.i.redListEnd + "</font></span>";
        label2.setText(text);

        ///  Decoration
        message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));
        label1.setFont(new Font("Times", Font.PLAIN, 20));
        label1.setHorizontalAlignment(JLabel.CENTER);
        label2.setFont(new Font("Times", Font.PLAIN, 20));
        label2.setHorizontalAlignment(JLabel.CENTER);

        message.add(Box.createRigidArea(new Dimension(10, 10)));
        message.add(label1);
        message.add(Box.createRigidArea(new Dimension(10, 20)));
        message.add(label2);
        message.add(Box.createRigidArea(new Dimension(10, 20)));
        this.tab.add(message);
    }

    public void updatePersonalInfo() {
        //  Initialize the table
        this.tableModel.setRowCount(0);

        try {
            this.i.sb.setLength(0);
            this.i.sb.append("SELECT * FROM usager WHERE email = ");
            this.i.sb.append("'"); this.i.sb.append(this.i.user); this.i.sb.append("'");

            String sql = this.i.sb.toString();
            ResultSet result = this.i.stmt.executeQuery(sql);

            while (result.next()) {
                String prenom = result.getString("prenom");
                String nom = result.getString("nom");
                String email = result.getString("email");
                String[] row = {prenom, nom, email};
                this.tableModel.addRow(row);
            }

            result.close();
            this.i.stmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Updating personal information failed");
            e.printStackTrace();
        }
    }
}
