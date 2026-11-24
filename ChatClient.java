import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient implements Runnable {

    private final BufferedReader in;

    public ChatClient(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Anslutningen stängdes.");
        }
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000);
             Scanner keyboard = new Scanner(System.in);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.print("Ditt namn: ");
            String name = keyboard.nextLine();
            out.println(name);

            Thread receiver = new Thread(new ChatClient(in));
            receiver.setDaemon(true);
            receiver.start();

            while (keyboard.hasNextLine()) {
                String message = keyboard.nextLine();
                out.println(message);

                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Kunde inte ansluta: " + e.getMessage());
        }
    }
}
