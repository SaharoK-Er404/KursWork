import java.io.*;
import java.net.Socket;

public class Sender {
    private static Socket clientSocket;
    private static BufferedReader in;
    private static BufferedWriter out;

    Sender(String msg, MainWindow frm){
        String serverWord;
        try {
            try {
                clientSocket = new Socket("localhost", 8000);
                in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));
                out = new BufferedWriter(
                        new OutputStreamWriter(
                                clientSocket.getOutputStream()));
                out.write(msg + "\n");
                out.flush();
                serverWord = in.readLine();
                System.out.println(serverWord);
                CommandControl.SrvWrdReview(serverWord, frm);

            } finally {
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }


}
