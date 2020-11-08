import javax.swing.*;

public class Logout {
    private final IHM i;

    public Logout(IHM gui, JTabbedPane tabbedpane) {
        this.i = gui;
        JPanel tab = new JPanel();

        ///   Logout Button
        JButton button = new JButton("Logout");

        ///   Logout Button Action
        button.addActionListener(e -> {
            try {
                this.i.conn.close();
            } catch (Exception e1) {
                System.out.println("Error");
            }
            IHM newIHM = new IHM(this.i.adminFlag);
            this.i.setVisible(false);
            newIHM.setVisible(true);
        });

        tab.add(button);

        tabbedpane.add("Logout", tab);
    }
}
