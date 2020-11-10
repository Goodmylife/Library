import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookList {
    private final IHM i;
    private final JPanel tab;                     //  Panel for Book List Tab
    private DefaultTableModel tableModel;         //  Table to show the result
    private JTable table;                         //  Instance of the table

    public BookList(IHM gui, JTabbedPane tabbedpane) throws SQLException {
        this.i = gui;
        this.tab = new JPanel();

        ///  Decoration
        this.tab.setLayout(new BoxLayout(this.tab, BoxLayout.Y_AXIS));

        this.inputSet();
        this.outputSet();
        this.borrowButton();
        this.updateBookList(null, null);

        tabbedpane.addTab("Book List", this.tab);
    }

    public void inputSet() {
        //  Panel for input to search books
        JPanel input = new JPanel();
        input.setLayout(new FlowLayout());

        ///  Label with a text "Keyword"
        JLabel label = new JLabel("Keyword");
        label.setPreferredSize(new Dimension(70, 15));

        ///  Text Field to input the keyword (Search Bar)
        JTextField searchBar = new JTextField(20);

        ///  Combo Box to choose the category of search
        String[] combodata = {"Title", "Author", "Editor", "ISBN", "Genre"};
        JComboBox combo = new JComboBox(combodata);

        ///  Button for Search
        JButton searchButton = new JButton("Search");

        //////        Search Button Action        //////
        searchButton.addActionListener(e -> {
            //  Get the keyword and the category input
            String keyword = searchBar.getText();
            String category = (String)combo.getSelectedItem();

            //  Show the result
            try {
                this.updateBookList(keyword, category);
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
                this.updateBookList(null, null);
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(null, "Updating failed");
                e1.printStackTrace();
            }
        });

        input.add(label);
        input.add(searchBar);
        input.add(combo);
        input.add(searchButton);
        input.add(resetButton);

        this.tab.add(input);
    }

    public void outputSet() {
        //  Panel for the result of search
        JPanel output = new JPanel();

        //  Table Columns
        String[] columnBooks = {"Title", "Author", "Edition Year", "Editor", "ISBN", "Genre"};
        this.tableModel = new DefaultTableModel(columnBooks, 0);

        ///  Instantiate the table to show the result
        this.table = new JTable(this.tableModel);
        this.table.setDefaultEditor(Object.class, null);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ///  Scroll Pane
        JScrollPane sp = new JScrollPane(this.table);
        sp.setPreferredSize(new Dimension(800,500));

        output.add(sp);
        this.tab.add(output);
    }

    public void updateBookList(String keyword, String category) throws SQLException {
        //  Initialize the table
        this.tableModel.setRowCount(0);

        String sql = this.i.sqlBookList(keyword, category, "BookList");
        ResultSet result = this.i.stmt.executeQuery(sql);

        while (result.next()) {
                String title = result.getString("title");
                String author = result.getString("author");
                String e_year = result.getString("annee_edition");
                String editor = result.getString("nom_editeur");
                String ISBN = result.getString("ISBN");
                String genre = result.getString("mot");
                String[] row = {title, author, e_year, editor, ISBN, genre};
                this.tableModel.addRow(row);
            }

        result.close();
        this.i.stmt.close();
    }

    public void borrowButton() {
        ///  Button to borrow a book
        JButton button = new JButton("Borrow");
        button.setAlignmentX(0.5f);

        //////      Borrow Button Action       //////
        button.addActionListener(e -> {
            ///   Check whether the user is on the Red List
            if (this.i.redList()) {
                JOptionPane.showMessageDialog(null, "You are on a RED LIST\nYou CANNOT borrow books now");
            }
            ///   Even if the user is NOT on the Red List, he/she may have already borrowed as many books as possible
            else if (!this.i.canBorrow()) {
                JOptionPane.showMessageDialog(null, "You have already borrowed as many books as possible\nReturn some if you want to borrow another");
            } else {
                int row = this.table.getSelectedRow();

                if (row == -1) {
                    JOptionPane.showMessageDialog(null, "Choose a book");
                } else {
                    String ISBN = this.table.getValueAt(row, 4).toString();
                    this.borrowBook(ISBN);

                    try {
                        this.updateBookList(null, null);
                        JOptionPane.showMessageDialog(null, "The data has been updated");
                        this.i.isupdate = false;
                    } catch (SQLException e1) {
                        JOptionPane.showMessageDialog(null, "Updating failed");
                        e1.printStackTrace();
                    }

                }
            }
        });

        this.tab.add(button);
    }

    public void borrowBook(String ISBN) {
        try {
            this.i.sb.setLength(0);

            ///  Get the smallest Book ID
            this.i.sb.append("SELECT * FROM AvailableBook WHERE ISBN = ");
            this.i.sb.append("'"); this.i.sb.append(ISBN); this.i.sb.append("'");

            String sql = this.i.sb.toString();
            ResultSet result = this.i.stmt.executeQuery(sql);
            result.next();
            int ID_book = result.getInt("id_livre");


            ///  Get how long the user can borrow a book
            this.i.sb.setLength(0);
            this.i.sb.append("SELECT * FROM Duree WHERE email = ");
            this.i.sb.append("'"); this.i.sb.append(this.i.user); this.i.sb.append("'");

            sql = this.i.sb.toString();
            result = this.i.stmt.executeQuery(sql);
            result.next();
            int duree = result.getInt("duree_emprunts");


            ///  Register the information on borrowing
            this.i.sb.setLength(0);
            this.i.sb.append("INSERT INTO emprunter VALUES(");
            this.i.sb.append(ID_book); this.i.sb.append(", ");
            this.i.sb.append(this.i.IDuser); this.i.sb.append(", ");
            this.i.sb.append("DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'), ");
            this.i.sb.append("DATETIME(CURRENT_TIMESTAMP, '+"); this.i.sb.append(duree); this.i.sb.append(" DAYS', 'LOCALTIME'))");

            sql = this.i.sb.toString();
            this.i.stmt.executeUpdate(sql);

            JOptionPane.showMessageDialog(null, "You have borrowed the book");
            this.i.nb_borrowed++;

            result.close();
            this.i.stmt.close();

            this.i.isupdate = true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Borrowing failed");
            e.printStackTrace();
        }
    }
}