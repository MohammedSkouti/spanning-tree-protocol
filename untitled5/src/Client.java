import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    public static final String COMMAND_DISPLAY_TREE ="DISPLAY_TREE" ;
    private static final int PORT = 1234;


    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the root node ID: ");
            int rootId = Integer.parseInt(reader.readLine());
            DatagramSocket socket = new DatagramSocket();
            String message = COMMAND_DISPLAY_TREE;
            byte[] buffer = message.getBytes();
            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT + rootId);
            socket.send(packet);
            buffer = new byte[1024];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String response = new String(packet.getData(), 0, packet.getLength());
            System.out.println(response);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
