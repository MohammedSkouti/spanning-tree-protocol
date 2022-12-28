import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClusterNode {
    private int id;
    private List<Integer> neighbors;

    private int parent;
    private int root;
    private boolean isRoot;
    private DatagramSocket socket;
    private static final int PORT = 1234;
    private static final Random rand = new Random();

    public ClusterNode(int id) {
        this.id = id;
        this.neighbors = new ArrayList<>();
        this.parent = -1;
        this.root = id;
        this.isRoot = true;
        try {
            this.socket = new DatagramSocket(PORT + id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNeighbor(int id) {
        this.neighbors.add(id);
    }

    public void receive() {
        new Thread(() -> {
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    if (message.equals(Client.COMMAND_DISPLAY_TREE)) {
                        String response = getSpanningTreeString();
                        buffer = response.getBytes();
                        InetAddress address = packet.getAddress();
                        int port = packet.getPort();
                        packet = new DatagramPacket(buffer, buffer.length, address, port);
                        socket.send(packet);
                    } else {
                        String[] parts = message.split(":");
                        int sender = Integer.parseInt(parts[0]);
                        int senderRoot = Integer.parseInt(parts[1]);
                        if (senderRoot < this.root) {
                            this.root = senderRoot;
                            this.parent = sender;
                            this.isRoot = false;
                            send();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void send() {
        new Thread(() -> {
            for (int neighbor : neighbors) {
                if (neighbor != this.parent) {
                    try {
                        // Generate a random delay between 0 and 1000 milliseconds
                        Thread.sleep(rand.nextInt(1000));
                        String message = this.id + ":" + this.root;
                        byte[] buffer = message.getBytes();
                        InetAddress address = InetAddress.getByName("localhost");
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT + neighbor);
                        socket.send(packet);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


}

    public void start() {
        receive();
        send();
    }
    public String getSpanningTreeString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.id + ": ");
        if (this.parent == -1) {
            sb.append("null");
        } else {
            sb.append(this.parent);
        }
        sb.append("\n");
        for (int neighbor : this.neighbors) {
            if (neighbor != this.parent) {
                sb.append(neighbor + ": " + this.id + "\n");
            }
        }
        return sb.toString();
    }

    public void printSpanningTree() {
        System.out.println("Node " + this.id + ": root = " + this.root + ", parent = " + this.parent);
    }

    public DatagramSocket getSocket() {
        return socket;
    }
    public int getId() {
        return id;
    }
    public int getRoot() {
        return root;
    }
    public int getParent() {
        return parent;
    }
    public void setRoot(int root) {
        this.root = root;
    }

}

