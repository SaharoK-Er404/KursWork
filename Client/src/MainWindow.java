import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class MainWindow extends JFrame implements ActionListener {
    static String modeCheck;
    JButton seeOrders = new JButton("Orders List");
    //JButton seeUsers = new JButton("Fetch Employees");
    JButton seeSupplies = new JButton("Supplies List");
    JButton logOut = new JButton("Log Out");
    JButton placeOrder = new JButton("Place Order");
    JButton markDelivered = new JButton("Mark Delivered");
    //ImageIcon icon = new ImageIcon(Objects.requireNonNull(LoginWindow.class.getResource("img/menuicon.png")));
    JScrollPane mainScrollPane = new JScrollPane();
    JTable mainTable = new JTable();
    MainWindow frm;

    MainWindow(String mode) {
        frm = this;
        modeCheck = mode;
        this.setTitle("Medical Administration Utility");
        this.setSize(1024, 768);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setVisible(true);
        //this.setIconImage(icon.getImage());
        this.add(Button(seeOrders, 35, 35, 200));

        if (modeCheck.equals("medical")){
            this.add(Button(seeSupplies, 35, 95, 200));
            //this.add(Button(seeUsers, 35, 155, 200));
            this.add(Button(placeOrder, 274,650, 170));

            //seeUsers.addActionListener(this);

            placeOrder.addActionListener(this);
            seeSupplies.addActionListener(this);
        }
        else{
            this.add(Button(markDelivered, 274,650, 170));
            markDelivered.addActionListener(this);
        }

        this.add(Button(logOut, 824, 650, 150));
        this.add(scrollPane(mainScrollPane, 274,35,700,600));

        seeOrders.addActionListener(this);
        logOut.addActionListener(this);
    }

    JButton Button(JButton b, int x, int y, int w){
        b.setBounds(x, y, w, 40);
        b.setFont(new Font("Inter", Font.PLAIN,20));
        b.setForeground(new Color(85, 85, 85));
        b.setBackground(new Color(217, 217, 217));

        return b;
    }

    public JScrollPane scrollPane(JScrollPane sp, int x, int y, int w, int h){
        sp.setBounds(x, y, w, h);
        return sp;
    }

    public JTable infoTable(String[][] info, String[] columnNames, String mode) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);
        this.mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE){
                int rowChanged = e.getLastRow();
                System.out.println("changed");
                CommandControl.convertToRequest(rowChanged, this, mode);
            }
        });
        if (!mode.equals("medical")){
            for (String[] strings : info) {
                int fillCheck = 0;
                for (String string : strings) {
                    if (string.isBlank()) {
                        fillCheck++;
                    }
                }
                if ((fillCheck != strings.length) && (mode.equals(strings[2]))) {
                    model.addRow(strings);
                }
            }
            this.mainTable = new JTable(model){
                public boolean editCellAt(int row, int column, java.util.EventObject e) {
                    return false;
                }
            };
            TableColumn col = mainTable.getColumn(mainTable.getColumnName(2));
            this.mainTable.removeColumn(col);
        }
        else {
            for (String[] strings : info) {
                model.addRow(strings);
            }
        }
        this.mainTable.setModel(model);
        return this.mainTable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        CommandControl.CmdReviewMain(cmd, this, modeCheck);
    }
}
