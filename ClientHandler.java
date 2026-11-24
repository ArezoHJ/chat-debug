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
            synchronized (clients) {
                clients.add(out);
            }
            out.println("Välkommen " + name + "! Skriv /online eller /quit.");
            System.out.println(name + " anslöt. Online: " + getOnlineCount());

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }

                if (message.equalsIgnoreCase("/online")) {
                    out.println("Online: " + getOnlineCount());
                    continue;
                }

                broadcast(name + ": " + message);
            }
        } catch (IOException e) {
            System.out.println("Klientfel: " + e.getMessage());
        } finally {
            if (out != null) {
                synchronized (clients) {
                    clients.remove(out);
                }
            }
            System.out.println(name + " lämnade. Online: " + getOnlineCount());
        }
    }

    private int getOnlineCount() {
        synchronized (clients) {
            return clients.size();
        }
    }

    private void broadcast(String message) {
        synchronized (clients) {
            for (PrintWriter receiver : clients) {
                receiver.println(message);
            }
        }
    }
}
