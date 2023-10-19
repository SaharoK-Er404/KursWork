import java.sql.*;
import java.util.logging.Level;
public class DBControl {

    public DBControl() {
    }
    public static String fetchOrders(String mode) {
        String response = mode + ";";
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/medical",
                    "root", "NEW_PASSWORD");

            Statement st;
            st = con.createStatement();

            ResultSet rs;
            rs = st.executeQuery("select * from orders");

            while (rs.next()) {
                int id = rs.getInt("id");
                String item = rs.getString("item").trim();
                String supplier = rs.getString("supplier").trim();
                int quantity = rs.getInt("quantity");
                float totalPrice = rs.getFloat("total_price");
                String status = rs.getString("status");
                response += id + ";" + item + ";" + supplier + ";" + quantity + ";" + totalPrice + ";" + status + ";";
                System.out.println(response);
            }
            rs.close();
            st.close();
            con.close();

        } catch (SQLException e) {
            Server.LOG.log(Level.WARNING, "DATABASE RETRIEVAL ERROR: " + e);
        }
        Server.LOG.log(Level.INFO, "ORDERS RETRIEVED");
        return response;
    }

    public static String fetchSupplies(String mode) {
        String response = mode + ";";
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/medical",
                    "root", "NEW_PASSWORD");

            Statement st;
            st = con.createStatement();

            ResultSet rs;
            rs = st.executeQuery("select * from in_stock");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name").trim();
                int quantity = rs.getInt("quantity");
                response += id + ";" + name + ";" + quantity + ";";
                System.out.println(response);
            }
            rs.close();
            st.close();
            con.close();

        } catch (SQLException e) {
            Server.LOG.log(Level.WARNING, "DATABASE RETRIEVAL ERROR: " + e);
        }
        return response;
    }

    public static String addUser(String userName, String password, String mode) {
        String response = "";
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/medical",
                    "root", "NEW_PASSWORD");

            Statement st;
            st = con.createStatement();
            ResultSet rs;

            switch (mode){
                case "medical"->{
                    rs = st.executeQuery("select * from users_medical");
                    while (rs.next()) {
                        String dbName = rs.getString("name").trim();
                        String dbPassword = rs.getString("pass").trim();

                        if ((dbName.equals(userName))&&(dbPassword.equals(password))){
                            response = mode;
                        }
                    }
                    if (!response.isEmpty()){
                        response = "UsrExst;" + response + ";";
                    }
                    else{
                        String sql = "insert into users_medical (name, pass) values (?, ?)";
                        PreparedStatement pst;
                        pst = con.prepareStatement(sql);

                        pst.setString(1, userName);
                        pst.setString(2, password);
                        pst.executeUpdate();
                        response = "UsrAdd;" + mode + ";";
                        pst.close();
                    }
                    con.close();
                }
                case "supplier"->{
                    rs = st.executeQuery("select * from users_supplier");
                    while (rs.next()) {
                        String dbName = rs.getString("comp_name").trim();
                        String dbPassword = rs.getString("pass").trim();

                        if ((dbName.equals(userName))&&(dbPassword.equals(password))){
                            response = dbName + ";";
                        }
                    }
                    if (!response.isEmpty()){
                        response = "UsrExst;" + response + ";";
                    }
                    else{
                        String sql = "insert into users_supplier (comp_name, pass) values (?, ?)";
                        PreparedStatement pst;
                        pst = con.prepareStatement(sql);

                        pst.setString(1, userName);
                        pst.setString(2, password);
                        pst.executeUpdate();
                        response = "UsrAdd;" + userName + ";";
                        pst.close();
                    }

                }
                default-> Server.LOG.log(Level.WARNING, "Unexpected value: " + mode);
            }
            con.close();
        }catch (SQLException e){
            Server.LOG.log(Level.WARNING, "DATABASE ADDITION ERROR: " + e);
        }
        return response;
    }

    public static String checkUser(String login, String password, String mode) {
        String response = "";
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/medical",
                    "root", "NEW_PASSWORD");

            Statement st;
            st = con.createStatement();

            ResultSet rs;
            switch (mode){
                case "medical"->{
                    rs = st.executeQuery("select * from users_medical");

                    while (rs.next()) {
                        String dbName = rs.getString("name").trim();
                        String dbPassword = rs.getString("pass").trim();
                        if ((dbName.equals(login))&&(dbPassword.equals(password))){
                            response = "ChkSuc;" + mode + ";";
                        }
                    }
                    rs.close();
                }
                case "supplier"->{
                    rs = st.executeQuery("select * from users_supplier");

                    while (rs.next()) {
                        String dbName = rs.getString("comp_name").trim();
                        String dbPassword = rs.getString("pass").trim();
                        if ((dbName.equals(login))&&(dbPassword.equals(password))){
                            response = "ChkSuc;" + dbName + ";";
                        }
                    }
                    rs.close();
                }
            }

            st.close();
            con.close();

        } catch (SQLException e) {
            Server.LOG.log(Level.WARNING, "DATABASE SEARCH ERROR: " + e);
        }
        if (response.isEmpty()){
            response = "NoUsr;" + mode + ";";
        }
        return response;
    }

    public static String updateOrders(String[] clientData){
        String response = null;
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/medical",
                    "root", "NEW_PASSWORD");

            Statement st;
            st = con.createStatement();

            ResultSet rs;
            rs = st.executeQuery("select * from orders");

            String sqlAdd = "insert into orders (item, supplier, quantity, total_price, status) values (?, ?, ?, ?, ?)";
            PreparedStatement pst;
            while (rs.next()) {
                int dbID = rs.getInt("id");
                if (dbID == Integer.parseInt(clientData[0])) {
                    String dbItem = rs.getString("item").trim();
                    String dbSupplier = rs.getString("supplier").trim();
                    int dbQuantity = rs.getInt("quantity");
                    float dbDeliDate = rs.getFloat("total_price");
                    String dbStatus = rs.getString("status");
                    if (!dbItem.equals(clientData[1])) {
                        String sqlUpdate = "update orders set item = ? where id = ?";
                        pst = con.prepareStatement(sqlUpdate);
                        pst.setInt(2, dbID);
                        pst.setString(1, clientData[1]);
                        pst.executeUpdate();
                        pst.close();
                    }
                    if (!dbSupplier.equals(clientData[2])) {
                        String sqlUpdate = "update orders set supplier = ? where id = ?";
                        pst = con.prepareStatement(sqlUpdate);
                        pst.setInt(2, dbID);
                        pst.setString(1, clientData[2]);
                        pst.executeUpdate();
                        pst.close();
                    }
                    if (!(dbQuantity == Integer.parseInt(clientData[3]))) {
                        String sqlUpdate = "update orders set quantity = ? where id = ?";
                        pst = con.prepareStatement(sqlUpdate);
                        pst.setInt(2, dbID);
                        pst.setInt(1, Integer.parseInt(clientData[3]));
                        pst.executeUpdate();
                        pst.close();
                    }
                    if (!(dbDeliDate == Float.parseFloat(clientData[4]))) {
                        String sqlUpdate = "update orders set total_price = ? where id = ?";
                        pst = con.prepareStatement(sqlUpdate);
                        pst.setInt(2, dbID);
                        pst.setFloat(1, Float.parseFloat(clientData[4]));
                        pst.executeUpdate();
                        pst.close();
                    }
                    if (!dbStatus.equals(clientData[5])) {
                        String sqlUpdate = "update orders set status = ? where id = ?";
                        pst = con.prepareStatement(sqlUpdate);
                        pst.setInt(2, dbID);
                        pst.setString(1, clientData[5]);
                        updateSupplies(clientData[1], Integer.parseInt(clientData[3]));
                        pst.executeUpdate();
                        pst.close();
                    }
                    response = "ORDER UPDATED AT ROW: " + dbID;
                }
            }
            System.out.println(response);
            if (response == null){
                pst = con.prepareStatement(sqlAdd);
                pst.setString(1, clientData[1]);
                pst.setString(2, clientData[2]);
                pst.setInt(3, Integer.parseInt(clientData[3]));
                pst.setFloat(4, Float.parseFloat(clientData[4]));
                pst.setString(5, String.valueOf(clientData[5]));
                response = "ORDER ADDED AT ROW: " + clientData[0];
                pst.executeUpdate();
                pst.close();
            }

            rs.close();
            st.close();
            con.close();

        } catch (SQLException e) {
            Server.LOG.log(Level.WARNING, "ORDERS UPDATE ERROR: " + e);
        }
        return response;
    }

    public static void updateSupplies(String item, int quantity){
        String response = null;
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/medical",
                    "root", "NEW_PASSWORD");

            Statement st;
            st = con.createStatement();

            ResultSet rs;
            rs = st.executeQuery("select * from in_stock");

            String sqlAdd = "insert into in_stock (name, quantity) values (?, ?)";
            String sqlUpd = "update in_stock set quantity = ? where name = ?";
            PreparedStatement pst;
            while (rs.next()) {
                String dbName = rs.getString("name").trim();
                if (dbName.equals(item)){
                    int dbQuantity = rs.getInt("quantity");
                    quantity += dbQuantity;
                    System.out.println(dbQuantity);
                    System.out.println(quantity);
                    pst = con.prepareStatement(sqlUpd);
                    pst.setInt(1, quantity);
                    pst.setString(2, item);
                    pst.executeUpdate();
                    pst.close();
                    response = "STOCK UPDATED AT ITEM: " + dbName;
                }
            }
            if (response == null){
                pst = con.prepareStatement(sqlAdd);
                pst.setString(1, item);
                pst.setInt(2, quantity);
                pst.executeUpdate();
                pst.close();
                response = "STOCK ADDED WITH ITEM: " + item;
            }

            rs.close();
            st.close();
            con.close();
            Server.LOG.log(Level.INFO, response);

        } catch (SQLException e) {
            Server.LOG.log(Level.WARNING, "IN STOCK UPDATE ERROR: " + e);
        }
    }
}
