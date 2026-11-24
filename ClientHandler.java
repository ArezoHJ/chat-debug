import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final List<PrintWriter> clients;

    public ClientHandler(Socket socket, List<PrintWriter> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        PrintWriter out = null;
        String name = "Okänd";

        try (Socket client = socket) {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            String firstLine = in.readLine();
            if (firstLine == null || firstLine.isBlank()) {
                return;
            }

            name = firstLine.trim();
            clients.add(out);
            out.println("Välkommen " + name + "! Skriv /online eller /quit.");
            System.out.println(name + " anslöt. Online: " + clients.size());

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }

                if (message == "/online") {
                    out.println("Online: " + clients.size());
                    continue;
                }

                for (PrintWriter receiver : clients) {
                    if (receiver == out) {
                        receiver.println(name + ": " + message);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Klientfel: " + e.getMessage());
        } finally {
            System.out.println(name + " lämnade. Online: " + clients.size());
        }
    }
}
