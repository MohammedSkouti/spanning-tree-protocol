import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

import org.junit.Test;

public class ClusterNodeTest {
    private static final int PORT = 1234;
    private static final Random rand = new Random();

    @Test
    public void testReceive() throws IOException, InterruptedException {
        // Create a cluster node with ID 0
        ClusterNode node = new ClusterNode(0);

        // Start a new thread to receive messages on the cluster node's socket
        new Thread(() -> {
            while (true) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    node.getSocket().receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Wait for the receive thread to start
        Thread.sleep(100);

        // Send a message to the cluster node
        String message = "1:1";
        byte[] buffer = message.getBytes();
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT + node.getId());
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);

        // Wait for the message to be processed
        Thread.sleep(100);

        // Check that the cluster node's root and parent were updated correctly
        assertEquals(1, node.getRoot());
        assertEquals(1, node.getParent());
    }

    @Test
    public void testSend() throws IOException, InterruptedException {
        // Create a cluster node with ID 0 and a neighbor with ID 1
        ClusterNode node0 = new ClusterNode(0);
        ClusterNode node1 = new ClusterNode(1);
        node0.addNeighbor(1);

        // Start a new thread to receive messages on node 1's socket
        new Thread(() -> {
            while (true) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    node1.getSocket().receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Wait for the receive thread to start
        Thread.sleep(100);

        // Set node 0's root to 1
        node0.setRoot(1);

        // Start node 0's send thread
        node0.send();

        // Wait for the message to be sent
        Thread.sleep(100);

        // Check that node 1 received the message
        assertEquals(1, node1.getRoot());
    }
}