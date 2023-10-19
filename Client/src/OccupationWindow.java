import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OccupationWindow extends JFrame implements ActionListener{
    JButton medButton = new JButton("Medical Worker");
    JButton supButton = new JButton("Supply Organization");
    JLabel name = new JLabel("Select Operation Mode");
    OccupationWindow occ;

    OccupationWindow() {
        occ = this;
        occ.setBackground(new Color(228, 239, 216));
        occ.setTitle("Mode Selection");
        occ.setSize(300,200);
        occ.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        occ.setResizable(false);
        occ.setLayout(null);
        occ.setVisible(true);
        occ.add(Button(medButton, 45, 40, 210, 40));
        occ.add(Button(supButton, 45, 100, 210, 40));
        occ.add(Label(name, 40, 2, 220, 40));
        medButton.addActionListener(occ);
        supButton.addActionListener(occ);
    }

    static JButton Button(JButton b, int x, int y, int w, int h) {
        b.setBounds(x, y, w, h);
        b.setFont(new Font("Inter", Font.PLAIN,20));
        b.setForeground(new Color(90, 98, 90));
        b.setBackground(new Color(217, 217, 217));

        return b;
    }

    JLabel Label(JLabel l, int x, int y, int w, int h){
        l.setBounds(x, y, w, h);
        l.setFont(new Font("Inter", Font.BOLD, 20));
        l.setForeground(new Color(90, 98, 90));
        return l;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String mode = e.getActionCommand();
        if (mode.equals("Medical Worker")){
            new LoginWindow("medical");
            this.dispose();
        } else if (mode.equals("Supply Organization")) {
            new LoginWindow("supplier");
            this.dispose();
        }
    }
}
