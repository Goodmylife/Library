import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Home {
    private final IHM i;
    private JPanel panel;

    public Home(IHM gui) {
        this.i = gui;
        this.center();
        this.top();
        this.inst();
        this.loginButton();
        this.bottom();
        this.i.panelCard.add(this.panel);
    }

    ///  Main Panel
    public void center() {
        panel = new JPanel();
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
    }

    ///  Panel Top
    public void top() {
        JPanel panelTop = new JPanel();
        panelTop.setBackground(Color.LIGHT_GRAY);
        panel.add(panelTop);
    }

    ///  Panel for instructions
    public void inst() {
        JPanel panelInst = new JPanel();

        JLabel label;

        ///  Depending on the role (i.e. the mode), a different message is shown
        if (!i.adminFlag) {
            ImageIcon icon = new ImageIcon("./Book.jpg");
            label = new JLabel("Welcome to My Library", icon, JLabel.CENTER);
        } else {
            ImageIcon icon = new ImageIcon("./Admin.png");
            label = new JLabel("This page is for administrators", icon, JLabel.CENTER);

        }

        //   Label Decoration
        label.setFont(new Font("Times", Font.PLAIN, 28));
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setIconTextGap(40);

        panelInst.add(label);
        this.panel.add(panelInst);
    }

    ///  Panel for Login
    public JTextField login() {
        JPanel panelLogin = new JPanel();

        //  Panel Decoration
        panelLogin.setLayout(new FlowLayout());
        panelLogin.setPreferredSize(new Dimension(700, 0));

        //  Label for Login
        JLabel labelLogin = new JLabel("Login");
        labelLogin.setPreferredSize(new Dimension(70, 15));

        //  Text Field for Login
        JTextField passLogin = new JTextField(20);

        panelLogin.add(labelLogin);
        panelLogin.add(passLogin);
        this.panel.add(panelLogin);

        return passLogin;
    }

    ///  Panel for Password
    public JPasswordField password() {
        JPanel panelPass = new JPanel();

        //  Panel Decoration
        panelPass.setLayout(new FlowLayout());
        panelPass.setPreferredSize(new Dimension(700, 0));

        //  Label for Password
        JLabel labelPass = new JLabel("Password");

        //  Label Decoration
        labelPass.setPreferredSize(new Dimension(70, 15));

        // Password Field
        JPasswordField passPass = new JPasswordField(20);
        panelPass.add(labelPass);
        panelPass.add(passPass);
        this.panel.add(panelPass);

        return passPass;
    }

    ///  Login Button
    public void loginButton() {
        JTextField loginField = this.login();
        JPasswordField passwordField = this.password();

        JPanel panelLoginButton = new JPanel();

        // Panel Decoration
        panelLoginButton.setLayout(new FlowLayout());
        panelLoginButton.setPreferredSize(new Dimension(700, 0));

        // Login Button
        JButton loginButton = new JButton("Login");

        ///  Action triggered by clicking the button
        loginButton.addActionListener(e -> {
            //  Get login and password
            String login = loginField.getText();
            char[] password = passwordField.getPassword();
            String passwordstr = new String(password);

            //  Check if login and password are filled in
            if (nullCheck(login, passwordstr)) {
                try {
                    this.i.login(login, passwordstr);
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(null, "DB connection error");
                    e1.printStackTrace();
                }
            }
        });

        panelLoginButton.add(loginButton);
        this.panel.add(panelLoginButton);
    }

    ///  Panel Bottom
    public void bottom(){
        JPanel panelBottom = new JPanel();

        //  Panel Decoration
        panelBottom.setBackground(Color.LIGHT_GRAY);

        //  Button for Mode Change
        JButton modeButton;
        if (!this.i.adminFlag) {
            modeButton = new JButton("Admin Mode");
        } else {
            modeButton = new JButton("Member Mode");
        }

        modeButton.addActionListener(e -> {
            try {
                this.i.conn.close();                 //  Close an Old Connection
            } catch (Exception e1) {
                System.out.println("Error");         //  Error Message
            }
            IHM newIHM = new IHM(!this.i.adminFlag);  //  Create a New Window
            this.i.setVisible(false);                //  Make it visible
            newIHM.setVisible(true);                 //  Make an older one invisible (close window)
        });

        panelBottom.add(modeButton);
        this.panel.add(panelBottom);
    }

    ///  Check whether login or password or both are null
    public boolean nullCheck(String login, String password) {
        if ("".equals(login)) {
            JOptionPane.showMessageDialog(null, "Fill in Login");
            return false;
        }
        if ("".equals(password)) {
            JOptionPane.showMessageDialog(null, "Fill in Password");
            return false;
        }
        return true;
    }
}



