import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdherentInfo {
    protected final IHM i;
    private final JPanel tab;                      //  Panel for Book List Tab
    private DefaultTableModel tableModel;          //  Table to show the result
    private JTable table;                          //  Instance of the table

    private JTextField rlInField;
    private JTextField rlOutField;


    public AdherentInfo(IHM gui, JTabbedPane tabbedPane) throws SQLException {
        this.i = gui;
        this.tab = new JPanel();
        this.tab.setLayout(new BoxLayout(this.tab, BoxLayout.Y_AXIS));

        this.inputSet();
        this.outputSet();
        this.RLButton();
        this.adherentInfo(null, null);
        this.addButton();
        this.deleteButton();
        this.moreInfo();

        tabbedPane.add("Adherent", this.tab);
    }

    public void inputSet() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        ///   Label Set
        JLabel label = new JLabel("Keyword");
        label.setPreferredSize(new Dimension(70, 15));

        ///  Text Field to input the keyword (Search Bar)
        JTextField searchBar = new JTextField(20);

        ///  Combo Box to choose the category of search
        String[] combodata = {"Prenom", "Nom", "Email", "Category"};
        JComboBox combo = new JComboBox(combodata);

        ///  Button for Search
        JButton searchButton = new JButton("Search");

        //////        Search Button Action        //////////
        searchButton.addActionListener(e -> {
            //  Get the keyword and the category input
            String keyword = searchBar.getText();
            String category = (String) combo.getSelectedItem();

            //  Show the result
            try {
                this.adherentInfo(keyword, category);
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(null, "Search Error");
                e1.printStackTrace();
            }
        });

        ///  Button for Reset
        JButton resetButton = new JButton("Reset");

        //////        Reset Button Action         //////
        resetButton.addActionListener(e -> {
            try {
                this.adherentInfo(null, null);
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(null, "Reset Error");
                e1.printStackTrace();
            }
        });

        panel.add(label);
        panel.add(searchBar);
        panel.add(combo);
        panel.add(searchButton);
        panel.add(resetButton);

        this.tab.add(panel);
    }

    public void outputSet() {
        //  Panel for the result of search
        JPanel output = new JPanel();
        output.setPreferredSize(new Dimension(800, 300));

        //  Table Columns
        String[] columnBooks = {"ID", "Prenom", "Nom", "Email", "Password", "Category", "Max", "Duree", "Red List From", "Red List Until"};
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

    public void RLButton() {
        JButton button = new JButton("Red List");
        button.setAlignmentX(0.5f);

        //////     Modify Button Action      ///////
        button.addActionListener(e -> {
            int row = this.table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Choose a user");
            } else {
                String id_usager = this.table.getValueAt(row, 0).toString();
                String start = String.valueOf(this.table.getValueAt(row, 8));
                String end = String.valueOf(this.table.getValueAt(row, 9));

                if (!start.equals("null")) {    // The user has been on the list
                    //  true: add the user to the list,  false: change the period of the list
                    boolean flag;

                    try {
                        Date endDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end);
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        Date now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeStamp);
                        flag = endDay.before(now);
                    } catch (ParseException e1) {
                        flag = false;
                        e1.printStackTrace();
                    }

                    ///   flag is true only if the user was on the list but is not now
                    if (flag) {
                        this.addRedList(id_usager);
                        JOptionPane.showMessageDialog(null, "The user has been registered on the Red List");
                    } else {
                        this.changeRedList(id_usager);
                        JOptionPane.showMessageDialog(null, "The information on the Red List updated");
                    }
                } else {
                    this.addRedList(id_usager);
                }
            }
        });

        this.tab.add(button);
    }

    public void addButton() {
        JButton button = new JButton("Add Adherent");
        button.setAlignmentX(0.5f);

        //////     Add Button Action      ///////
        button.addActionListener(e -> {
            AddAdherent aa = new AddAdherent();
            aa.action(this);
        });
        this.tab.add(button);
    }

    public void deleteButton() {
        JButton button = new JButton("Delete Adherent");
        button.setAlignmentX(0.5f);

        //////     Delete Button Action      ///////
        button.addActionListener(e -> {
            int row = this.table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Choose a user");
            } else {
                String id_usager = this.table.getValueAt(row, 0).toString();

                try {
                    if (!this.canDelete(id_usager)) {
                        JOptionPane.showMessageDialog(null, "This user has not returned all books");
                        return;
                    }
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null, "Error!");
                    throwables.printStackTrace();
                    return;
                }

                try {
                    this.deleteAdherent(id_usager);
                    JOptionPane.showMessageDialog(null, "The Adherent has been deleted");
                    this.adherentInfo(null, null);
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null, "Delete Error");
                    throwables.printStackTrace();
                }
            }

        });
        this.tab.add(button);
    }

    public void moreInfo() {
        JButton button = new JButton("More Info");
        button.setAlignmentX(0.5f);

        //////     More Info Button Action      ///////
        button.addActionListener(e -> {
            int row = this.table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Choose a user");
            } else {
                int id_usager = Integer.parseInt(this.table.getValueAt(row, 0).toString());

                MoreInfoAdherent mia = null;
                try {
                    mia = new MoreInfoAdherent(this.i, id_usager);
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null, "Details have not been obtained");
                    throwables.printStackTrace();
                }
                assert mia != null;
                JOptionPane.showConfirmDialog(null, mia.tab, "Details", JOptionPane.OK_CANCEL_OPTION);
            }
        });

        this.tab.add(button);
    }




    public void adherentInfo(String keyword, String category) throws SQLException {
        //  Initialize the table
        this.tableModel.setRowCount(0);

        String sql = this.i.sqlBookList(keyword, category, "Adherent");
        ResultSet result = this.i.stmt.executeQuery(sql);

        while (result.next()) {
            String id = result.getString("id_usager");
            String prenom = result.getString("prenom");
            String nom = result.getString("nom");
            String email = result.getString("email");
            String password = result.getString("password");
            String categorie = result.getString("nom_categorie");
            String max = result.getString("nombre_emprunts");
            String duree = result.getString("duree_emprunts");
            String rlFrom = result.getString("MAX(date_debut)");
            String rlUntil = result.getString("MAX(date_fin)");
            String[] row = {id, prenom, nom, email, password, categorie, max, duree, rlFrom, rlUntil};
            this.tableModel.addRow(row);
        }

        result.close();
        this.i.stmt.close();
    }



    public void addRedList(String id_usager) {
        int option = JOptionPane.showConfirmDialog(null, this.addRedListPanel(),
                "From and Until when is the user on the Red List?", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String start = this.rlInField.getText();
            String end = this.rlOutField.getText();

            if (!this.dateFormatCheck(start, "start")) {
                return;
            } else if (!this.dateFormatCheck(end, "end")) {
                return;
            }

            ///   SQL
            try {
                ///  Get the id_liste
                String sql = "SELECT *, COUNT(*) count FROM liste_rouge";
                ResultSet result = i.stmt.executeQuery(sql);
                result.next();
                int id_liste = result.getInt("count") + 1;
                result.close();
                i.stmt.close();

                ///  Update liste_rouge
                this.insertRedList(Integer.parseInt(id_usager), id_liste, start, end);
                this.adherentInfo(null, null);
            } catch (SQLException throwables) {
                JOptionPane.showMessageDialog(null, "Red List Management Error");
                throwables.printStackTrace();
            }
        }
    }

    public JPanel addRedListPanel() {
        JPanel panel = new JPanel();

        this.rlInField = new JTextField(15);
        this.rlOutField = new JTextField(15);

        this.labelPanelSet(panel, "Start", this.rlInField);
        this.labelPanelSet(panel, "End", this.rlOutField);

        panel.add(new JLabel("The format of the date is YYYY-MM-DD HH:MM:SS"));

        return panel;
    }

    public void insertRedList(int id_usager, int id_liste, String startDay, String endDay) throws SQLException {
        this.i.sb.setLength(0);
        this.i.sb.append("INSERT INTO liste_rouge VALUES(");
        this.i.sb.append(id_usager); this.i.sb.append(", ");
        this.i.sb.append(id_liste); this.i.sb.append(", '");
        this.i.sb.append(startDay); this.i.sb.append("', '");
        this.i.sb.append(endDay); this.i.sb.append("')");
        String sql = this.i.sb.toString();
        this.i.stmt.executeUpdate(sql);

        this.i.stmt.close();
    }

    public void changeRedList(String id_usager) {
        int option = JOptionPane.showConfirmDialog(null, this.changeRedListPanel(),
                "Until when is the user on the Red List?", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String end = this.rlOutField.getText();

            if (!this.dateFormatCheck(end, "end")) {
                return;
            }

            ///   SQL
            try {
                ///  Get the id_liste
                i.sb.setLength(0);
                i.sb.append("SELECT *, MAX(date_debut), MAX(date_fin) FROM liste_rouge WHERE id_usager = ");
                i.sb.append(Integer.parseInt(id_usager));
                i.sb.append(" GROUP BY id_usager");
                String sql = i.sb.toString();

                ResultSet result = i.stmt.executeQuery(sql);
                result.next();
                int id_liste = result.getInt("id_liste");
                result.close();
                i.stmt.close();

                ///  Update liste_rouge
                this.updateRedList(id_liste, end);
                this.adherentInfo(null, null);
            } catch (SQLException throwables) {
                JOptionPane.showMessageDialog(null, "Red List Management Error");
                throwables.printStackTrace();
            }
        }
    }

    public JPanel changeRedListPanel() {
        JPanel panel = new JPanel();

        this.rlOutField = new JTextField(15);

        this.labelPanelSet(panel, "End", this.rlOutField);

        panel.add(new JLabel("The format of the date is YYYY-MM-DD HH:MM:SS"));

        return panel;
    }

    public void updateRedList(int id_liste, String endDay) throws SQLException {
        this.i.sb.setLength(0);
        this.i.sb.append("UPDATE liste_rouge SET date_fin = '");
        this.i.sb.append(endDay); this.i.sb.append("' WHERE id_liste = ");
        this.i.sb.append(id_liste);
        String sql = this.i.sb.toString();
        this.i.stmt.executeUpdate(sql);

        this.i.stmt.close();
    }


    public void labelPanelSet(JPanel panel, String text, JTextField field) {
        JPanel panel1 = new JPanel();
        JLabel label1 = new JLabel(text);
        label1.setPreferredSize(new Dimension(50, 15));
        panel1.add(label1);
        panel1.add(field);
        panel.add(panel1);
    }




    public boolean canDelete(String id_usager) throws SQLException {
        this.i.sb.setLength(0);

        this.i.sb.append("SELECT * FROM WhoBorrowWhat WHERE id_usager = ");
        this.i.sb.append(Integer.parseInt(id_usager));

        String sql = this.i.sb.toString();
        ResultSet result = this.i.stmt.executeQuery(sql);

        return !result.next();
    }

    public void deleteAdherent(String id_usager) throws SQLException {
        ///   Delete from empruter
        this.deleteAction(id_usager, "emprunter");

        ///   Delete from liste_rouge
        this.deleteAction(id_usager, "liste_rouge");

        ///   Delete from liste_rouge
        this.deleteAction(id_usager, "usager");
    }

    public void deleteAction(String id_usager, String table) throws SQLException {
        this.i.sb.setLength(0);
        this.i.sb.append("DELETE FROM '");
        this.i.sb.append(table); this.i.sb.append("' ");
        this.i.sb.append("WHERE id_usager = ");
        this.i.sb.append(Integer.parseInt(id_usager));
        String sql = this.i.sb.toString();
        this.i.stmt.executeUpdate(sql);

        this.i.stmt.close();
    }



    public boolean dateFormatCheck(String date, String command) {
        ///   If the length is different, the format is wrong
        if (date.length() != "YYYY-MM-DD HH:MM:SS".length()) {
            JOptionPane.showMessageDialog(null, "The format is YYYY-MM-DD HH:MM:SS");
            return false;
        } else {
            ///   If the date is NOT later than now or cannot be cast to Date, the format is wrong
            try {
                Date end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Date now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeStamp);

                if (command.equals("end") && end.before(now)) {
                    JOptionPane.showMessageDialog(null, "The date must be later than now");
                    return false;
                }
            } catch (ParseException e1) {
                JOptionPane.showMessageDialog(null, "The format is YYYY-MM-DD HH:MM:SS");
                return false;
            }
            return true;
        }
    }
}

