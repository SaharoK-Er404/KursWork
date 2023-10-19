import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CommandControl {
    public static void CmdReviewMain(String cmd, MainWindow frm, String mode){
        switch (cmd) {
            case "Supplies List" -> new Sender("FtchSup;" + mode + ";", frm);
            case "Orders List" -> new Sender("FtchOrd;" + mode + ";", frm);
            case "Place Order" -> addNewRow(frm, frm.mainTable);
            case "Mark Delivered" -> changeRow(frm, mode);
            case "Log Out" -> {
                frm.dispose();
                LoginWindow.opMode = null;
                MainWindow.modeCheck = null;
                new OccupationWindow();
            }
            default -> new Sender(cmd + ";Incorrect Input", frm);
        }
    }

    public static void CmdReviewLogin(String cmd, String login, String pass, String mode, MainWindow frm){
        switch (cmd) {
            case "Log in" -> new Sender("Auth;" + mode + ";" + login + ";" + pass + ";", frm);
            case "Sign up" -> new Sender("Reg;" + mode + ";"+ login + ";" + pass + ";", frm);
            default -> new Sender(cmd + ";Incorrect Input", frm);
        }
    }
    public static void SrvWrdReview(String cmd, MainWindow frm){
        System.out.println(cmd);
        String option = cmd.split(";")[0];
        String mode = cmd.split(";")[1];
        String[] response = cmd.substring(option.length()+mode.length()+2).split(";");

        switch (option) {
            case "ChkSuc" -> {
                LoginWindow.reg.dispose();
                new MainWindow(mode);
            }
            case "UsrAdd" -> {
                LoginWindow.reg.dispose();
                JOptionPane.showMessageDialog(null,
                        "User successfully added",
                        "Signup success", JOptionPane.WARNING_MESSAGE);
                new MainWindow(mode);
            }
            case "UsrExst" -> {
                LoginWindow.reg.dispose();
                JOptionPane.showMessageDialog(null,
                        "User already exists!",
                        "Login Warning", JOptionPane.WARNING_MESSAGE);
                new MainWindow(mode);
            }
            case "NoUsr" -> JOptionPane.showMessageDialog(null,
                    "No such user exists!",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            case "OrdInf" -> fillTable(new String[]{"ID", "ITEM", "SUPPLIER", "QUANTITY", "TOTAL PRICE", "STATUS"}, response, mode, frm);
            case "SupInf" -> fillTable(new String[]{"ID", "NAME", "QUANTITY"}, response, mode, frm);
        }
    }
    private static void fillTable(String[] columnNames, String[] response, String mode, MainWindow frm){
        String[][] info;
        int totalRows = (response.length) / columnNames.length;
        info = new String[totalRows][columnNames.length];
            for(int j = 0; j < totalRows; j++) {
                System.arraycopy(response, j * columnNames.length, info[j], 0, columnNames.length);
        }
        frm.mainTable = frm.infoTable(info, columnNames, mode);
        frm.mainScrollPane.setViewportView(frm.mainTable);
    }

    public static void addNewRow(MainWindow frm, JTable table){
            String[] rows = new String[table.getRowCount()];
            String controlValue = String.valueOf((Integer.parseInt(frm.mainTable.getValueAt(frm.mainTable.getRowCount()-1, 0).toString()) + 1));
            rows[0] = controlValue;
            DefaultTableModel model = (DefaultTableModel)table.getModel();
            model.addRow(rows);
            model.setValueAt("in progress", table.getRowCount()-1, table.getColumnCount()-1);
            frm.mainTable.setModel(model);
    }

    public static void changeRow(MainWindow frm, String mode){
        if (frm.mainTable.getSelectedRow() != -1) {
            System.out.println(frm.mainTable.getSelectedRow());
            DefaultTableModel model = (DefaultTableModel) frm.mainTable.getModel();
            model.setValueAt("delivered", frm.mainTable.getSelectedRow(), frm.mainTable.getColumnCount());
            convertToRequest(frm.mainTable.getSelectedRow(), frm, mode);
            frm.mainTable.setModel(model);
        }else {
            JOptionPane.showMessageDialog(frm,
                    "No row selected. Try again!",
                    "Selection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void convertToRequest(int rowChanged, MainWindow frm, String mode){
        DefaultTableModel model = (DefaultTableModel) frm.mainTable.getModel();

        if (model.getValueAt(rowChanged, model.getColumnCount() - 2) != null){
            StringBuilder requestBuilder;
            requestBuilder = new StringBuilder("UpdOrd;universal;");
            if (!mode.equals("medical")){
                for (int i = 0 ; i < 2; i++){
                    requestBuilder.append(model.getValueAt(rowChanged, i)).append(";");
                }
                requestBuilder.append(mode).append(";");
                for (int i = 3 ; i < 6; i++){
                    requestBuilder.append(model.getValueAt(rowChanged, i)).append(";");
                }
            }else{
                for (int i = 0 ; i < frm.mainTable.getColumnCount(); i++){
                        requestBuilder.append(model.getValueAt(rowChanged, i)).append(";");
                }
            }
            String request = requestBuilder.toString();
            if (!request.contains("null")){
                new Sender(request, frm);
            }
        }
    }
}
