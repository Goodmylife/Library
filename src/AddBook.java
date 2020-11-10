import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddBook {
    private final IHM i;
    private final JPanel tab;                      //  Panel for Book List Tab
    private DefaultTableModel tableModel;          //  Table to show the result
    private JTable table;                          //  Instance of the table

    public AddBook(IHM gui, JTabbedPane tabbedpane) throws SQLException {
        this.i = gui;
        this.tab = new JPanel();
        this.tab.setLayout(new BoxLayout(this.tab, BoxLayout.Y_AXIS));

        this.message();
        this.inputSet(null, 0);
        this.outputSet();
        this.addButton();
        this.tab.add(Box.createRigidArea(new Dimension(1,30)));
        this.completeAddition();
        this.moreInfo();

        this.allBookList(null, null);

        tabbedpane.add("Book", this.tab);
    }

    public AddBook(IHM gui, String author, int bYear) throws SQLException {
        this.i = gui;
        this.tab = new JPanel();
        this.tab.setLayout(new BoxLayout(this.tab, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Select the author");
        label.setAlignmentX(0.5f);
        this.tab.add(label);

        this.inputSet(author, bYear);
        this.outputSet();

        this.authorBookList(null, null, author, bYear);
    }

    public void message() {
        JLabel label = new JLabel("If you want to add books which Library has, choose one and click Add1");
        label.setAlignmentX(0.5f);
        this.tab.add(label);
    }

    public void inputSet(String author, int bYear) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        ///   Label Set
        JLabel label = new JLabel("Keyword");
        label.setPreferredSize(new Dimension(70, 15));

        ///  Text Field to input the keyword (Search Bar)
        JTextField searchBar = new JTextField(20);

        ///  Combo Box to choose the category of search
        String[] combodata;
        String[] data;
        if (author == null) {
            data = new String[]{"Title", "Author", "Editor", "ISBN", "Genre"};
        } else {
            data = new String[]{"Title", "Editor", "ISBN", "Genre"};
        }
        combodata = data;
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
                if (author == null) {
                    this.allBookList(keyword, category);
                } else {
                    this.authorBookList(keyword, category, author, bYear);
                }
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
                if(author == null) {
                    this.allBookList(null, null);
                } else {
                    this.authorBookList(null, null, author, bYear);
                }
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
        String[] columnBooks = {"Title", "Author", "Birth Year", "Edition Year", "Editor", "ISBN", "Genre", "Number"};
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

    public void addButton() {
        JButton button = new JButton("Add1");
        button.setAlignmentX(0.5f);

        //////     Add Button 1 Action      ///////
        button.addActionListener(e -> {
            int row = this.table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Choose a book");
            } else {
                int ISBN = Integer.parseInt(this.table.getValueAt(row, 5).toString());
                try {
                    ///   Get the maximum book ID
                    String sql = "SELECT id_livre FROM livre ORDER BY id_livre DESC LIMIT 1";
                    ResultSet result = this.i.stmt.executeQuery(sql);

                    ///   Get the book ID to assign
                    result.next();
                    int count = result.getInt("id_livre") + 1;

                    this.i.stmt.close();
                    result.close();

                    ///   Update the database
                    this.i.sb.setLength(0);
                    this.i.sb.append("INSERT INTO livre VALUES (");
                    this.i.sb.append(count); this.i.sb.append(", ");
                    this.i.sb.append(ISBN); this.i.sb.append(")");

                    sql = this.i.sb.toString();
                    this.i.stmt.executeUpdate(sql);
                    this.i.stmt.close();

                    JOptionPane.showMessageDialog(null, "The book has been added");

                    ///   Update the table
                    this.allBookList(null, null);
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(null, "Addition failed");
                    e1.printStackTrace();
                }
            }
        });

        this.tab.add(button);
    }

    public void allBookList(String keyword, String category) throws SQLException {
        //  Initialize the table
        this.tableModel.setRowCount(0);

        String sql = this.i.sqlBookList(keyword, category, "AdminBook");
        ResultSet result = this.i.stmt.executeQuery(sql);

        while (result.next()) {
            String title = result.getString("title");
            String author = result.getString("author");
            String b_year = result.getString("annee_naissance");
            String e_year = result.getString("annee_edition");
            String editor = result.getString("nom_editeur");
            String ISBN = result.getString("ISBN");
            String genre = result.getString("mot");
            String count = result.getString("count");
            String[] row = {title, author, b_year, e_year, editor, ISBN, genre, count};
            this.tableModel.addRow(row);
        }

        result.close();
        this.i.stmt.close();
    }


    public void authorBookList(String keyword, String category, String author, int bYear) throws SQLException {
        //  Initialize the table
        this.tableModel.setRowCount(0);

        //  Initialize the query
        this.i.sb.setLength(0);

        this.i.sb.append("SELECT * FROM AdminBook WHERE author = '").append(author);
        this.i.sb.append("' AND annee_naissance = ");
        this.i.sb.append(bYear);

        // When keyword is null, it means RESET or nothing is typed
        if(keyword != null) {
            this.i.sb.append(" AND ");

            switch (category) {
                case "Title" -> {
                    this.i.sb.append("title like '%");
                    this.i.sb.append(keyword);
                    this.i.sb.append("%'");
                }
                case "Editor" -> {
                    this.i.sb.append("nom_editeur like '%");
                    this.i.sb.append(keyword);
                    this.i.sb.append("%'");
                }
                case "ISBN" -> {
                    this.i.sb.append("ISBN like '%");
                    this.i.sb.append(keyword);
                    this.i.sb.append("%'");
                }
                case "Genre" -> {
                    this.i.sb.append("mot like '%");
                    this.i.sb.append(keyword);
                    this.i.sb.append("%'");
                }
            }
        }

        String sql = this.i.sb.toString();
        ResultSet result = this.i.stmt.executeQuery(sql);

        while (result.next()) {
            String title = result.getString("title");
            String author1 = result.getString("author");
            String b_year1 = result.getString("annee_naissance");
            String e_year = result.getString("annee_edition");
            String editor = result.getString("nom_editeur");
            String ISBN = result.getString("ISBN");
            String genre = result.getString("mot");
            String count = result.getString("count");
            String[] row = {title, author1, b_year1, e_year, editor, ISBN, genre, count};
            this.tableModel.addRow(row);
        }
    }


    public void completeAddition() {
        JLabel label = new JLabel("If you want to add books which Library does NOT have, click Add2");
        label.setAlignmentX(0.5f);
        this.tab.add(label);

        JButton button = new JButton("Add2");
        button.setAlignmentX(0.5f);

        //////     Add Button 2 Action      ///////
        button.addActionListener(e -> {
            AuthorInfo ai = new AuthorInfo();

            UIManager.put("OptionPane.cancelButtonText", "Cancel");
            UIManager.put("OptionPane.okButtonText", "OK");

            int option = JOptionPane.showConfirmDialog(null, ai.panel,
                    "Enter the information on the author", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String prenom = ai.prenomField.getText();
                String nom = ai.nomField.getText();
                String bYear = ai.bYearField.getText();

                boolean[] check = ai.authorCheck(prenom, nom, bYear, i);

                if(check[0]) {
                    if(check[1]) {          //  New Author
                        try {
                            this.newAuthor(prenom, nom, Integer.parseInt(bYear), 0);
                        } catch (SQLException throwables) {
                            JOptionPane.showMessageDialog(null, "New Author Error");
                            throwables.printStackTrace();
                        }
                    } else {                        //  Registered Author
                        try {
                            this.registeredAuthor(prenom, nom, Integer.parseInt(bYear));
                        } catch (SQLException e1) {
                            JOptionPane.showMessageDialog(null, "Registered Author Error");
                            e1.printStackTrace();
                        }
                    }
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
                JOptionPane.showMessageDialog(null, "Choose a book");
            } else {
                int ISBN = Integer.parseInt(this.table.getValueAt(row, 5).toString());

                MoreInfoBook mib = null;
                try {
                    mib = new MoreInfoBook(this.i, ISBN);
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null, "Details have not been obtained");
                    throwables.printStackTrace();
                }
                assert mib != null;
                JOptionPane.showConfirmDialog(null, mib.tab, "Details", JOptionPane.OK_CANCEL_OPTION);
            }
        });

        this.tab.add(button);
    }


    public void newAuthor(String prenom, String nom, int bYear, int id_auteur) throws SQLException {
        NewAuthorInfo nai = new NewAuthorInfo(this.i);

        int option = JOptionPane.showConfirmDialog(null, nai.panel,
                "Enter the information", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String title = nai.titleField.getText();
            String p_year = nai.pYearField.getText();
            String isbn = nai.ISBNField.getText();
            String eName = nai.eNameField.getText();
            String e_year = nai.eYearField.getText();
            String genre = (String)nai.genreField.getSelectedItem();
            String number = nai.numberField.getText();

            if (title.equals("") || p_year.equals("") || isbn.equals("") || eName.equals("") || e_year.equals("") || genre.equals("") || number.equals("")) {
                JOptionPane.showMessageDialog(null, "Fill in the blanks");
            } else if (!AuthorInfo.isDigit(p_year) || !AuthorInfo.isDigit(isbn) || !AuthorInfo.isDigit(e_year) || !AuthorInfo.isDigit(number)) {
                JOptionPane.showMessageDialog(null, "Annee Parution, ISBN, Edition Name, Edition Year and Number must be integers");
            } else {
                try {
                    int num = Integer.parseInt(number);
                    int pYear = Integer.parseInt(p_year);
                    int ISBN = Integer.parseInt(isbn);
                    int eYear = Integer.parseInt(e_year);

                    //   ISBN Check
                    this.i.sb.setLength(0);
                    this.i.sb.append("SELECT * FROM edition WHERE ISBN = ");
                    this.i.sb.append(ISBN);
                    String sql = this.i.sb.toString();
                    ResultSet result = this.i.stmt.executeQuery(sql);

                    if (result.next()) {
                        JOptionPane.showMessageDialog(null, "This ISBN has already been used");
                        return;
                    }

                    if (id_auteur == 0) {
                        //   Get id_auteur
                        sql = "SELECT COUNT(id_auteur) count FROM auteur";
                        result = this.i.stmt.executeQuery(sql);
                        result.next();
                        id_auteur = result.getInt("count") + 1;

                        //   Auteur Update
                        this.updateAuthor(id_auteur, prenom, nom, bYear);
                    }

                    //   Get id_oeuvre
                    sql = "SELECT * FROM oeuvre ORDER BY id_oeuvre DESC LIMIT 1";
                    result = this.i.stmt.executeQuery(sql);
                    result.next();
                    int id_oeuvre = result.getInt("id_oeuvre") + 1;

                    // Oeuvre - Keyword Update
                    this.updateOeuvreKeyword(id_oeuvre, genre);

                    // Oeuvre Update
                    this.updateOeuvre(id_oeuvre, title, pYear);

                    // Rediger Update
                    this.updateRediger(id_auteur, id_oeuvre);

                    // Edition Update
                    this.updateEdition(ISBN, eName, eYear, id_oeuvre);

                    // id_livre
                    sql = "SELECT id_livre FROM livre ORDER BY id_livre DESC LIMIT 1";
                    result = this.i.stmt.executeQuery(sql);
                    result.next();
                    int id_livre = result.getInt("id_livre") + 1;

                    // Livre Update
                    this.updateLivre(num, id_livre, ISBN);


                    JOptionPane.showMessageDialog(null, "The book has been added");

                    ///   Update the table
                    allBookList(null, null);
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(null, "Addition failed");
                    e1.printStackTrace();
                }
            }
        }

    }

    public void registeredAuthor(String prenom, String nom, int bYear) throws SQLException {
        AddBook ab = new AddBook(this.i, prenom + " " + nom, bYear);

        int option = JOptionPane.showConfirmDialog(null, ab.tab,
                "Choose the author", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION){
            int row = ab.table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Choose a book");
            } else {
                int ISBN = Integer.parseInt(ab.table.getValueAt(row, 5).toString());

                ///   Get the author ID
                this.i.sb.setLength(0);
                this.i.sb.append("SELECT * FROM AdminBook WHERE ISBN = ");
                this.i.sb.append(ISBN);
                String sql =  this.i.sb.toString();
                ResultSet result = this.i.stmt.executeQuery(sql);
                result.next();
                int id_auteur = result.getInt("id_auteur");


                this.newAuthor(prenom, nom, bYear, id_auteur);
            }

        }
    }


    public void updateAuthor(int id_auteur, String prenom, String nom, int bYear) throws SQLException {
        i.sb.setLength(0);
        i.sb.append("INSERT INTO auteur VALUES (");
        i.sb.append(id_auteur); i.sb.append(", '");
        i.sb.append(prenom);  i.sb.append("', '");
        i.sb.append(nom);  i.sb.append("', ");
        i.sb.append(bYear);  i.sb.append(")");

        String sql = i.sb.toString();
        i.stmt.executeUpdate(sql);

        i.stmt.close();
    }

    public void updateOeuvreKeyword(int id_oeuvre, String genre) throws SQLException {
        i.sb.setLength(0);
        i.sb.append("INSERT INTO oeuvre_mots_cles VALUES (");
        i.sb.append(id_oeuvre); i.sb.append(", '");
        i.sb.append(genre); i.sb.append("')");
        String sql = i.sb.toString();

        i.stmt.executeUpdate(sql);
        i.stmt.close();
    }

    public void updateOeuvre(int id_oeuvre, String title, int pYear) throws SQLException {
        String title_escape = title.replace("'", "''");

        i.sb.setLength(0);
        i.sb.append("INSERT INTO oeuvre VALUES (");
        i.sb.append(id_oeuvre);  i.sb.append(", '");
        i.sb.append(title_escape);   i.sb.append("', ");
        i.sb.append(pYear);   i.sb.append(")");

        String sql =  i.sb.toString();
        i.stmt.executeUpdate(sql);

        i.stmt.close();
    }

    public void updateRediger(int id_auteur, int id_oeuvre) throws SQLException {
        i.sb.setLength(0);
        i.sb.append("INSERT INTO rediger VALUES (");
        i.sb.append(id_auteur); i.sb.append(", ");
        i.sb.append(id_oeuvre); i.sb.append(")");

        String sql = i.sb.toString();
        i.stmt.executeUpdate(sql);

        i.stmt.close();
    }

    public void updateEdition(int ISBN, String eName, int eYear, int id_oeuvre) throws SQLException {
        i.sb = new StringBuilder();
        i.sb.append("INSERT INTO Edition VALUES (");
        i.sb.append(ISBN); i.sb.append(", '");
        i.sb.append(eName); i.sb.append("', ");
        i.sb.append(eYear); i.sb.append(", ");
        i.sb.append(id_oeuvre); i.sb.append(")");

        String sql = i.sb.toString();
        i.stmt.executeUpdate(sql);
    }

    public void updateLivre(int num, int id_livre, int ISBN) throws SQLException {
        while (num > 0) {
            i.sb = new StringBuilder();
            i.sb.append("INSERT INTO livre VALUES (");
            i.sb.append(id_livre); i.sb.append(", ");
            i.sb.append(ISBN);i. sb.append(")");
            String sql = i.sb.toString();

            i.stmt.executeUpdate(sql);
            num--;
            id_livre++;
        }
    }
}
