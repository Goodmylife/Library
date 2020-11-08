import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MoreInfoAdherent {
    private final IHM i;
    protected final JPanel tab;                      //  Panel for Book List Tab
    private DefaultTableModel tableModel;          //  Table to show the result
    private final int id_usager;

    public MoreInfoAdherent(IHM gui, int id_usager) throws SQLException {
        this.i = gui;
        this.tab = new JPanel();
        this.tab.setLayout(new BoxLayout(this.tab, BoxLayout.Y_AXIS));
        this.id_usager = id_usager;

        this.inputSet();
        this.outputSet();
        this.bookInfo(null, null);
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
        String[] combodata = new String[]{"ISBN", "Title"};

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
                this.bookInfo(keyword, category);
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(null, "Searching failed");
                e1.printStackTrace();
            }
        });

        ///  Button for Reset
        JButton resetButton = new JButton("Reset");

        //////        Reset Button Action         //////
        resetButton.addActionListener(e -> {
            try {
                this.bookInfo(null, null);
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(null, "Updating failed");
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
        String[] columnBooks = {"Book ID", "ISBN", "Title", "Start", "End"};
        this.tableModel = new DefaultTableModel(columnBooks, 0);

        ///  Instantiate the table to show the result
        //  Instance of the table
        JTable table = new JTable(this.tableModel);
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ///  Scroll Pane
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(800,300));

        output.add(sp);
        this.tab.add(output);
    }

    public void bookInfo(String keyword, String category) throws SQLException {
        //  Initialize the table
        this.tableModel.setRowCount(0);

        String text = this.i.sqlBookList(keyword, category, "WhoBorrowWhat");

        this.i.sb.setLength(0);
        this.i.sb.append(text);
        System.out.println(this.i.sb);
        if (keyword == null) {
            this.i.sb.append(" WHERE ");
        } else {
            this.i.sb.append(" AND ");
        }
        this.i.sb.append("id_usager = ");
        this.i.sb.append(this.id_usager);

        String sql = this.i.sb.toString();
        System.out.println(sql);
        ResultSet result = this.i.stmt.executeQuery(sql);

        while (result.next()) {
            String id_livre = result.getString("id_livre");
            String ISBN = result.getString("ISBN");
            String title = result.getString("title");
            String start = result.getString("date_debut");
            String end = result.getString("date_fin");
            String[] row = {id_livre, ISBN, title, start, end};
            this.tableModel.addRow(row);
        }

        result.close();
        this.i.stmt.close();
    }
}
