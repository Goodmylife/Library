import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddAdherent {
    JPanel panel;
    JTextField prenomField;
    JTextField nomField;
    JTextField emailField;
    JTextField passwordField;
    JComboBox combo;
    AdherentInfo ai;

    public AddAdherent(AdherentInfo ai) throws SQLException {
        this.ai = ai;
        this.panel = new JPanel();
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));

        this.prenomField = new JTextField(10);
        this.nomField = new JTextField(10);
        this.emailField = new JTextField(10);
        this.passwordField = new JTextField(10);



        JPanel panel1 = new JPanel();
        JLabel label1 = new JLabel("Prenom");
        label1.setPreferredSize(new Dimension(100, 10));
        panel1.add(label1);
        panel1.add(this.prenomField);
        this.panel.add(panel1);

        JPanel panel2 = new JPanel();
        JLabel label2 = new JLabel("Nom");
        label2.setPreferredSize(new Dimension(100, 10));
        panel2.add(label2);
        panel2.add(this.nomField);
        this.panel.add(panel2);

        JPanel panel3 = new JPanel();
        JLabel label3 = new JLabel("Email");
        label3.setPreferredSize(new Dimension(100, 10));
        panel3.add(label3);
        panel3.add(this.emailField);
        this.panel.add(panel3);

        JPanel panel4 = new JPanel();
        JLabel label4 = new JLabel("Password");
        label4.setPreferredSize(new Dimension(100, 10));
        panel4.add(label4);
        panel4.add(this.passwordField);
        this.panel.add(panel4);

        String[] combodata = this.getCategory();
        this.combo = new JComboBox(combodata);
        JPanel panel5 = new JPanel();
        JLabel label5 = new JLabel("Category");
        label5.setPreferredSize(new Dimension(100, 10));
        panel5.add(label5);
        panel5.add(this.combo);
        this.panel.add(panel5);
    }

    public void action() {
        int option = JOptionPane.showConfirmDialog(null, this.panel,
                "Enter the information", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String prenom = this.prenomField.getText();
            String nom = this.nomField.getText();
            String email = this.emailField.getText();
            String password = this.passwordField.getText();
            String category = this.combo.getSelectedItem().toString();

            if (prenom.equals("") || nom.equals("") || email.equals("") || password.equals("")) {
                JOptionPane.showMessageDialog(null, "Fill in the blank");
                return;
            }
            try {
                ///   Get the user ID to assign
                ai.i.sb.setLength(0);
                ai.i.sb.append("SELECT * FROM usager ORDER BY id_usager DESC LIMIT 1");
                String sql = ai.i.sb.toString();
                ResultSet result = ai.i.stmt.executeQuery(sql);

                int count = result.getInt("id_usager") + 1;

                ///   Update usager
                ai.i.sb.setLength(0);
                ai.i.sb.append("INSERT INTO usager VALUES (");
                ai.i.sb.append(count); ai.i.sb.append(", '");
                ai.i.sb.append(prenom); ai.i.sb.append("', '");
                ai.i.sb.append(nom); ai.i.sb.append("', '");
                ai.i.sb.append(email); ai.i.sb.append("', '");
                ai.i.sb.append(category); ai.i.sb.append("', '");
                ai.i.sb.append(password); ai.i.sb.append("', '");
                ai.i.sb.append("Adherent')");
                sql = ai.i.sb.toString();
                ai.i.stmt.executeUpdate(sql);

                JOptionPane.showMessageDialog(null, "The adherent has been added");
                ai.adherentInfo(null, null);
            } catch (SQLException throwables) {
                JOptionPane.showMessageDialog(null, "Updating Error");
                throwables.printStackTrace();
            }
        }
    }
    public String[] getCategory() throws SQLException {
        List<String> list = new ArrayList<String>();
        String sql = "SELECT * FROM categorie";
        ResultSet result = this.ai.i.stmt.executeQuery(sql);

        while(result.next()) {
            list.add(result.getString("nom_categorie"));
        }
        this.ai.i.stmt.close();
        result.close();
        return list.toArray(new String[0]);
    }
}