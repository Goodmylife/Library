import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorInfo {
    JPanel panel;
    JTextField prenomField;
    JTextField nomField;
    JTextField bYearField;

    public AuthorInfo() {
        this.panel = new JPanel();
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));

        this.prenomField = new JTextField(10);
        this.nomField = new JTextField(10);
        this.bYearField = new JTextField(10);


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
        JLabel label3 = new JLabel("Birth Year");
        label3.setPreferredSize(new Dimension(100, 10));
        panel3.add(label3);
        panel3.add(this.bYearField);
        this.panel.add(panel3);
    }

    public boolean[] authorCheck(String prenom, String nom, String bYear, IHM i) {
        if(prenom.equals("")|| nom.equals("") || bYear.equals("")) {
            JOptionPane.showMessageDialog(null, "Fill in the blanks");
        } else if(!isDigit(bYear)){
            JOptionPane.showMessageDialog(null, "Birth Year must be an integer");
        } else {
            try {
                ///    Search for Author Information
                i.sb.setLength(0);
                i.sb.append("SELECT * FROM auteur WHERE prenom = '"); i.sb.append(prenom);
                i.sb.append("' AND nom = '"); i.sb.append(nom);
                i.sb.append("' AND annee_naissance = "); i.sb.append(Integer.parseInt(bYear));

                String sql = i.sb.toString();
                ResultSet result = i.stmt.executeQuery(sql);

                ///   If the author is not registered, the count must not be 0
                int count = 0;
                while (result.next()){
                    count++;
                }

                if (count == 0) {
                    return new boolean[]{true, true};           //  New Author
                } else {
                    String[] options = {"New Author", "Registered Author"};
                    int select = JOptionPane.showOptionDialog(null,
                            "Some authors have the same name and the same birth year",
                            "Attention",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );

                    if(select == 0) {
                        return new boolean[]{true, true};       //  select = 0 corresponds to New Author
                    } else {
                        return new boolean[]{true, false};      //  select = 1 corresponds to Registered Author
                    }
                }
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(null, "SQL Error");
                e1.printStackTrace();
            }
        }
        return new boolean[]{false, false};
    }


    public static boolean isDigit(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
