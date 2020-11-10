import javax.swing.*;
import java.awt.*;
import java.sql.*;


public class IHM extends JFrame {
    protected Connection conn;
    protected Statement stmt;
    protected StringBuilder sb;

    protected JPanel panelCard;
    private final CardLayout layout;

    protected String user;         // Register the email address of the user
    protected int IDuser;          // Register the ID of the user

    protected boolean adminFlag;

    protected boolean isupdate;

    protected int nb_borrowed = 0;
    protected int max_borrowed = 0;

    protected String redListStart;
    protected String redListEnd;


    public IHM(boolean flag) {
        this.adminFlag = flag;
        this.setTitle("Bibliotheque");
        this.setSize(900, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:./Library.db");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "DATABASE CONNECTION ERROR!");
            e.printStackTrace();
        }

        this.panelCard = new JPanel();
        this.layout = new CardLayout();
        this.panelCard.setLayout(layout);

        new Home(this);

        getContentPane().add(panelCard);
    }

    public void login(String login, String password) throws SQLException {
        this.stmt = this.conn.createStatement();

        //  SQL
        this.sb = new StringBuilder();
        this.sb.append("SELECT * FROM usager WHERE email = ");
        this.sb.append("'"); this.sb.append(login); this.sb.append("'");
        String sql = this.sb.toString();
        ResultSet result = this.stmt.executeQuery(sql);

        int loopCount = 0;              //  Count the number of the login sent
        String pwd;                     //  Password obtained by SQL
        String role;                    //  Role obtained by SQL

        while(result.next()) {
                pwd = result.getString("password");
                role = result.getString("role");

                //  Authentication: check whether or not the password is correct
                if (pwd.equals(password)) {
                    //  Register the information useful for other methods
                    this.user = result.getString("email");
                    this.IDuser = Integer.parseInt(result.getString("id_usager"));

                    //  Depending on the role and the mode, the window after login differs
                    switch(role) {
                        // If the user is a member
                        case "Adherent":
                            if (!this.adminFlag) {                  // If the current mode is of member
                                JOptionPane.showMessageDialog(null, "Welcome to Member Mode");
                                new Adherent(this);             // Then Login
                                this.layout.next(this.panelCard);   // Window Change
                            } else {                                // If the user tries to login as an admin
                                JOptionPane.showMessageDialog(null, "You are NOT an administrator");
                            }
                            break;
                        // If the user is an admin
                        case "Admin":
                            if (!this.adminFlag) {                      // If the current mode is of member
                                JOptionPane.showMessageDialog(null, "Welcome to Member Mode");
                                new Adherent(this);                 // Then Login
                                this.layout.next(this.panelCard);       // Window Change
                            } else {                                    // If the current mode is of admin
                                JOptionPane.showMessageDialog(null, "Welcome to Admin Mode");
                                new Admin(this);                 // Then Login
                                this.layout.previous(this.panelCard);   // Window Change
                            }
                            break;
                        // Normally the default case never happens
                        default:
                            JOptionPane.showMessageDialog(null, "Error: You are neither a member nor an administrator");
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Password is NOT correct");
                    return;
                }
                loopCount++;
        }

        //  loopCount = 0 means there is no date corresponding to the input login
        if(loopCount == 0) {
            JOptionPane.showMessageDialog(null, "This user login is not registered");
        }

        result.close();
        this.stmt.close();
    }


    public boolean redList() {
        try {
            this.sb.setLength(0);
            this.sb.append("SELECT *, MAX(date_debut), MAX(date_fin) m FROM liste_rouge WHERE id_usager = ");
            this.sb.append(this.IDuser);
            this.sb.append(" GROUP BY id_usager HAVING MAX(date_fin) > CURRENT_DATE");
            String sql = this.sb.toString();
            ResultSet result = this.stmt.executeQuery(sql);

            if (result.next()) {
                this.redListStart = result.getString("date_debut");
                this.redListEnd = result.getString("date_fin");
                result.close();
                this.stmt.close();
                return true;

            }
            result.close();
            this.stmt.close();
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SOMETHING UNEXPECTED HAPPENS (Red List)");
            e.printStackTrace();
            return false;
        }
    }

    public boolean canBorrow() {
        try {
            this.stmt = this.conn.createStatement();
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT COUNT(id_livre) count FROM usager NATURAL JOIN emprunter WHERE date_fin >= DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME') ");
            sb.append("AND email = '"); sb.append(user); sb.append("'");
            String sql = sb.toString();
            ResultSet result = stmt.executeQuery(sql);

            result.next();
            this.nb_borrowed = result.getInt("count");

            sb = new StringBuilder();
            sb.append("SELECT * FROM usager NATURAL JOIN categorie");
            sb.append(" WHERE email = '"); sb.append(user); sb.append("'");
            sql = sb.toString();
            result = stmt.executeQuery(sql);

            result.next();
            this.max_borrowed = result.getInt("nombre_emprunts");

            result.close();
            stmt.close();

            return this.nb_borrowed < this.max_borrowed;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SOMETHING UNEXPECTED HAPPENS (Max Book)");
            e.printStackTrace();
            return false;
        }
    }

    public String sqlBookList(String keyword, String category, String table) {
        //  Initialize the query
        this.sb.setLength(0);

        // When keyword is null, it means RESET or nothing is typed
        if (keyword == null) {
            this.sb.append("SELECT * FROM ").append(table);
        } else {
            this.sb.append("SELECT * FROM ").append(table).append(" WHERE ");

            switch (category) {
                case "Title" -> {
                    this.sb.append("title like ");
                    this.sb.append("'%");
                    this.sb.append(keyword);
                    this.sb.append("%'");
                }
                case "Author" -> {
                    this.sb.append("author like ");
                    this.sb.append("'%");
                    this.sb.append(keyword);
                    this.sb.append("%'");
                }
                case "Editor" -> {
                    this.sb.append("nom_editeur like ");
                    this.sb.append("'%");
                    this.sb.append(keyword);
                    this.sb.append("%'");
                }
                case "ISBN" -> {
                    this.sb.append("ISBN like ");
                    this.sb.append("'%");
                    this.sb.append(keyword);
                    this.sb.append("%'");
                }
                case "Genre" -> {
                    this.sb.append("mot like ");
                    this.sb.append("'%");
                    this.sb.append(keyword);
                    this.sb.append("%'");
                }
                ///   Adherent Information
                case "Prenom" -> {
                    this.sb.append("prenom like ");
                    this.sb.append("'%");
                    this.sb.append(keyword);
                    this.sb.append("%'");
                }
                case "Nom" -> {
                    this.sb.append("nom like ");
                    this.sb.append("'%");
                    this.sb.append(keyword);
                    this.sb.append("%'");
                }
                case "Email" -> {
                    this.sb.append("email like ");
                    this.sb.append("'%");
                    this.sb.append(keyword);
                    this.sb.append("%'");
                }
                case "Category" -> {
                    this.sb.append("nom_categorie like ");
                    this.sb.append("'%");
                    this.sb.append(keyword);
                    this.sb.append("%'");
                }
                ///   Adherent More Information
                case "Book ID" -> {
                    this.sb.append("id_livre like ");
                    this.sb.append("'%");
                    this.sb.append(keyword);
                    this.sb.append("%'");
                }
                case "Adherent" -> {
                    this.sb.append("usager like ");
                    this.sb.append("'%");
                    this.sb.append(keyword);
                    this.sb.append("%'");
                }
            }
        }

        return this.sb.toString();
    }


    public static void main(String[] args) {
        IHM i = new IHM(false);
        i.setVisible(true);
    }
}