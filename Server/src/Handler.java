import java.io.*;
import java.net.Socket;
import java.util.logging.Level;


public class Handler implements Runnable {
    private final Socket clientSocket;
    BufferedReader reader;
    BufferedWriter writer;

    public Handler(Socket socket){
        clientSocket = socket;
    }

    public void run(){
        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(
                            clientSocket.getOutputStream()));
            reader = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
        } catch (IOException e) {
            Server.LOG.log(Level.WARNING, "READER/WRITER FAILURE:" + e);
            throw new RuntimeException(e);
        }
            String response;
            String[] request;
            try {
                String serverInput = reader.readLine();
                System.out.println(serverInput);
                Server.LOG.log(Level.INFO,"READER STARTED");
                String option = serverInput.split(";")[0];
                String mode = serverInput.split(";")[1];
                Server.LOG.log(Level.INFO,"CLIENT REQUEST ACCEPTED: " + option + " " + mode);
                System.out.println(serverInput.substring(option.length()+mode.length()+2));
                request = serverInput.substring(option.length()+mode.length()+2).split(";");
                switch (option) {
                    case "FtchOrd" -> response = "OrdInf;" + DBControl.fetchOrders(mode);
                    case "FtchSup" -> response = "SupInf;" + DBControl.fetchSupplies(mode);
                    case "UpdOrd" -> {
                        response = "OrdUpd;universal;";
                        Server.LOG.log(Level.INFO, DBControl.updateOrders(request));
                    }
                    case "Reg" -> response = DBControl.addUser(request[0], request[1], mode);
                    case "Auth" -> response = DBControl.checkUser(request[0], request[1], mode);
                    default ->{
                        response = "Incorrect request from user! ";
                        Server.LOG.log(Level.WARNING, "INCORRECT CLIENT REQUEST");
                    }
                }
                writer.write(response);
                Server.LOG.log(Level.INFO, "SERVER RESPONSE TO CLIENT: " + response);
                writer.flush();
                writer.newLine();
            } catch (IOException e) {
                Server.LOG.log(Level.INFO, "CLIENT REQUEST HANDLING ERROR: " + e);
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                        Server.LOG.log(Level.INFO, "WRITER CLOSED");
                    }
                    if (reader != null) {
                        reader.close();
                        clientSocket.close();
                        Server.LOG.log(Level.INFO, "CLIENT SOCKET CLOSED. READER CLOSED");
                    }
                } catch (IOException e) {
                    Server.LOG.log(Level.WARNING,"READER/WRITER/SOCKET CLOSURE ERROR:" + e);
                }
            }
    }
}
