import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server {
    static Logger LOG = Logger.getLogger(Server.class.getName());

    static {
        try{
            FileHandler fh = new FileHandler("log.txt");
            LOG.addHandler(fh);

            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            LOG.setUseParentHandlers(false);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static ServerSocket server;
    public static void main(String[] args) {
        try {
            try {
                server = new ServerSocket(8000);
                server.setReuseAddress(true);
                LOG.log(Level.INFO, "SEVER STARTED");
                while (true) {
                    Socket clientSocket = server.accept();
                    LOG.log(Level.INFO, "CLIENT CONNECTED: " + clientSocket.getInetAddress().getHostAddress());
                    Handler clientSock = new Handler(clientSocket);
                    new Thread(clientSock).start();
                }
            }finally {
                server.close();
                LOG.log(Level.INFO, "SEVER STOPPED");
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "SERVER-SIDE ERROR:" + e);
        }
    }
}