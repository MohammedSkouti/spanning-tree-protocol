import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<ClusterNode> nodes = new ArrayList<>();
        for (int i = 0; i <4; i++) {
            ClusterNode node = new ClusterNode(i);
            nodes.add(node);
        }

        nodes.get(0).addNeighbor(1);
        nodes.get(0).addNeighbor(2);
        nodes.get(1).addNeighbor(0);
        nodes.get(1).addNeighbor(2);
        nodes.get(1).addNeighbor(3);
        nodes.get(2).addNeighbor(0);
        nodes.get(2).addNeighbor(1);
        nodes.get(2).addNeighbor(3);
        nodes.get(3).addNeighbor(1);
        nodes.get(3).addNeighbor(2);
    //    nodes.get(3).addNeighbor(4);
       // nodes.get(4).addNeighbor(3);

        for (ClusterNode node : nodes) {
            node.start();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (ClusterNode node : nodes) {
            node.printSpanningTree();
        }
    }
}
