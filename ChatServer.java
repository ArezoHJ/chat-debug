import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {


    /*
    TODO AS A GROUP
     1. Flera Klienter anslutna samtidigt
     2. Meddelande skickas till samtliga klienter
     3. Kommandot /online ska fungera korrekt
     4. Klienter kan kopplas ned när de skriver /quit
     5- Server håller rätt på antal anslutna klienter

     */





    public static void main(String[] args) {
        List<PrintWriter> clients = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(10);

        try (ServerSocket server = new ServerSocket(5000)) {
            System.out.println("Servern kör på port 5000");

            while (true) {
                Socket socket = server.accept();
                ClientHandler handler = new ClientHandler(socket, clients);
                handler.run();
            }
        } catch (IOException e) {
            System.out.println("Serverfel: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
