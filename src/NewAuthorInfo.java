import javax.swing.*;
import javax.xml.transform.Result;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewAuthorInfo {
    private IHM i;
    JPanel panel;
    JTextField titleField;
    JTextField pYearField;
    JTextField ISBNField;
    JTextField eNameField;
    JTextField eYearField;
    JComboBox genreField;
    JTextField numberField;

    public NewAuthorInfo(IHM gui) throws SQLException {
        this.i = gui;
        this.panel = new JPanel();
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));

        this.titleField = new JTextField(10);
        this.pYearField = new JTextField(10);
        this.ISBNField = new JTextField(10);
        this.eNameField = new JTextField(10);
        this.eYearField = new JTextField(10);
        this.genreField = new JComboBox(this.getGenre());
        this.numberField = new JTextField(10);

        ///   Oeuvre
        JPanel panelOeuvre = new JPanel();
        JLabel label1 = new JLabel("Oeuvre");
        label1.setPreferredSize(new Dimension(200, 30));
        panelOeuvre.add(label1);
        JPanel panel1 = new JPanel();
        panel1.setPreferredSize(new Dimension(700, 30));
        panel1.add(Box.createHorizontalStrut(15)); // a spacer
        panel1.add(new JLabel("Title"));
        panel1.add(this.titleField);
        panel1.add(Box.createHorizontalStrut(15)); // a spacer
        panel1.add(new JLabel("Annee Parution"));
        panel1.add(this.pYearField);
        panelOeuvre.add(panel1);
        this.panel.add(panelOeuvre);

        ///   Edition
        JPanel panelEdition = new JPanel();
        JLabel label2 = new JLabel("Edition");
        label2.setPreferredSize(new Dimension(200, 30));
        panelEdition.add(label2);
        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(700, 30));
        panel2.add(Box.createHorizontalStrut(15)); // a spacer
        panel2.add(new JLabel("ISBN"));
        panel2.add(this.ISBNField);
        panel2.add(Box.createHorizontalStrut(15)); // a spacer
        panel2.add(new JLabel("Editor Name"));
        panel2.add(this.eNameField);
        panel2.add(Box.createHorizontalStrut(15)); // a spacer
        panel2.add(new JLabel("Edition Year"));
        panel2.add(this.eYearField);
        panelEdition.add(panel2);
        this.panel.add(panelEdition);

        ///   Genre -> Mots de Cles
        JPanel panelGenre  = new JPanel();
        JLabel label3 = new JLabel("Genre");
        label3.setPreferredSize(new Dimension(200, 30));
        panelGenre.add(label3);
        JPanel panel3 = new JPanel();
        panel3.setPreferredSize(new Dimension(700, 30));
        panel3.add(this.genreField);
        panelGenre.add(panel3);
        this.panel.add(panelGenre);

        ///   Number of Books
        JPanel panelNumber  = new JPanel();
        JLabel label4 = new JLabel("Number of Books to add");
        label4.setPreferredSize(new Dimension(200, 30));
        panelNumber.add(label4);
        JPanel panel4 = new JPanel();
        panel4.setPreferredSize(new Dimension(700, 30));
        panel4.add(this.numberField);
        panelNumber.add(panel4);
        this.panel.add(panelNumber);
    }

    public String[] getGenre() throws SQLException {
        String sql = "SELECT * FROM mots_cles";
        ResultSet result = this.i.stmt.executeQuery(sql);

        String[] combodata = new String[5];

        for (int i = 0; result.next(); i++) {
            combodata[i] = result.getString("mot");
        }
        return combodata;
    }
}
