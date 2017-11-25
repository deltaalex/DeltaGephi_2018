package org.gephi.io.generator.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Small-world network as defined by Watts-Strogatz <br>
 * http://en.wikipedia.org/wiki/Watts_and_Strogatz_model#Algorithm
 *
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = Generator.class)
public class SmallWorldGraph extends AbstractGraph implements Generator {

    private int numberOfNodes = 320;
    private int K = 5;
    private double wiringProbability = 0.2;

    public enum TYPE {

        WS, HK, Tv, uSF
    }
    private TYPE type = TYPE.WS;

    @Override
    protected int initialize() {
        return numberOfNodes + 1;
    }

    @Override
    protected void runGeneration(GraphModel graphModel, Random random) {

        Cell cell = new Cell(numberOfNodes);

        if (type.equals(TYPE.HK)) {
            createHolmeKimNetwork(cell, numberOfNodes, graphModel);
            return;
        } else if (type.equals(TYPE.Tv)) {
            createToivonenNetwork(cell, numberOfNodes, graphModel);
            return;
        } else if (type.equals(TYPE.uSF)) {
            createUncorrelatedSFNetwork(cell, numberOfNodes, graphModel);
            return;
        }

        // create nodes
        for (int i = 0; i < numberOfNodes; ++i) {

            // create node
            Node node = graphModel.factory().newNode();
            // initialize node
            node.getNodeData().setSize(NODE_SIZE);
            node.getNodeData().setLabel("" + (i));
            // add to graph
            graphModel.getGraph().addNode(node);
            cell.addNode(node);

            //Sleep some time
            animateNode();
            progressTick();
        }

        createSmallWorldCommunity(cell, graphModel, random, K, K, wiringProbability, false, false);

//        // dbg       
//        try {
//            File fedges = new File("C:\\Users\\Alexander\\Desktop\\sw_" + numberOfNodes + "_K_" + K + ".in");
//            PrintWriter pw = new PrintWriter(fedges);
//
//            for (Edge edge : graphModel.getGraph().getEdges()) {
//                pw.println(edge.getSource().getId() + " " + edge.getTarget().getId());
//            }
//
//            pw.close();
//        } catch (FileNotFoundException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        // dbg

        progressTick();
    }

    private void createHolmeKimNetwork(Cell cell, int N, GraphModel graphModel) {
        // size of seed network
        int n0 = 3;
        Random rand = new Random();

        // initialize random graph seed        
        initializeRandomGraph(cell.getNodes(), graphModel.getGraph(), n0, 1, false, false);

        for (int i = n0; i < N; ++i) {
            // create new node
            Node newNode = graphModel.factory().newNode();
            // initialize node
            newNode.getNodeData().setSize(NODE_SIZE);
            newNode.getNodeData().setLabel("" + (i));
            // add to graph
            graphModel.getGraph().addNode(newNode);
            cell.addNode(newNode);
            //Sleep some time
            animateNode();

            // measure candidate node degrees
            int sumk = 0;
            int degree[] = new int[cell.getNodes().size()];
            for (int j = 0; j < cell.getNodes().size(); ++j) {
                int k = graphModel.getGraph().getNeighbors(cell.getNodes().get(j)).toArray().length;
                degree[j] = k;
                sumk += k;
            }

            // every node must be connected !
            boolean connected = false;

            while (!connected) {
                // preferential attachment
                for (int j = 0; j < cell.getNodes().size(); ++j) {
                    int p = rand.nextInt(sumk);
                    // connect
                    if (p <= degree[j]) {
                        Edge edge = graphModel.factory().newEdge(newNode, cell.getNodes().get(j));
                        graphModel.getGraph().addEdge(edge);
                        connected = true;

                        //Sleep some time
                        animateEdge();

                        // (TF} connect to a neighbor as well
                        // current neighbors of candidate
                        List<Node> allNeighbors = Arrays.asList(graphModel.getGraph().getNeighbors(cell.getNodes().get(j)).toArray());
                        List<Node> neighbors = new ArrayList<Node>();
                        // filter out adjacent neighbors
                        for (Node n : allNeighbors) {
                            if (!n.equals(newNode)) {
                                if (!graphModel.getGraph().isAdjacent(newNode, n) && !graphModel.getGraph().isAdjacent(n, newNode)) {
                                    neighbors.add(n);
                                }
                            }
                        }

                        if (neighbors.size() > 0) {

                            // pick one random non-adjacent neighbor to connect to
                            Node neighbor = neighbors.get(rand.nextInt(neighbors.size()));

                            edge = graphModel.factory().newEdge(newNode, neighbor);
                            graphModel.getGraph().addEdge(edge);

                            //Sleep some time
                            animateEdge();
                        }
                    }
                }
            }
        }
    }

    private void createToivonenNetwork(Cell cell, int N, GraphModel graphModel) {
        // size of seed network
        int n0 = 20;
        // probability of picking 1 contact vs 2 contacts
        double pmr1 = 0.95;
        // maximum number of secondary contacts (+1)
        int maxs = 4;
        Random rand = new Random();

        // initialize random graph seed        
        initializeRandomGraph(cell.getNodes(), graphModel.getGraph(), n0, 0.25, false, false);

        for (int i = n0; i < N; ++i) {
            int mr = rand.nextDouble() < pmr1 ? 1 : 2;

            // pick random initial contacts
            List<Node> contacts = new ArrayList<Node>();
            List<Node> contacts2 = new ArrayList<Node>();

            for (int j = 0; j < mr; ++j) {
                Node contact = null;
                while (contact == null || contacts.contains(contact)) {
                    contact = cell.getNodes().get(rand.nextInt(cell.getNodes().size()));
                }
                contacts.add(contact);

                // pick random secondary contact                
                for (int jj = 0; jj < rand.nextInt(maxs) + 1; ++jj) {
                    int s = graphModel.getGraph().getNeighbors(contact).toArray().length;
                    if (s > 0) {
                        Node contact2 = graphModel.getGraph().getNeighbors(contact).toArray()[rand.nextInt(s)];
                        contacts2.add(contact2);
                    }
                }
            }

            // create new node
            Node newNode = graphModel.factory().newNode();
            // initialize node
            newNode.getNodeData().setSize(NODE_SIZE);
            newNode.getNodeData().setLabel("" + (i));
            // add to graph
            graphModel.getGraph().addNode(newNode);
            cell.addNode(newNode);
            //Sleep some time
            animateNode();

            // connect to initials contacts
            for (Node contact : contacts) {
                Edge edge = graphModel.factory().newEdge(newNode, contact);
                graphModel.getGraph().addEdge(edge);

                //Sleep some time
                animateEdge();
            }

            // connect to initials contacts
            for (Node contact2 : contacts2) {
                Edge edge = graphModel.factory().newEdge(newNode, contact2);
                graphModel.getGraph().addEdge(edge);

                //Sleep some time
                animateEdge();
            }
        }
    }

    /**
     * Based on
     * http://complex.ffn.ub.es/ckfinder/userfiles/files/PhysRevE_71_027103.pdf
     */
    private void createUncorrelatedSFNetwork(Cell cell, int N, GraphModel graphModel) {
        Random rand = new Random();

        // create a power-law distribution of nodes: from 1 to N-1
        int[] degrees = new int[N];
        int sum = 0;
        for (int i = 0; i < N; ++i) {
            degrees[i] = getPowerDistributedIntegerValue(rand, 1, (int) Math.sqrt(N));
            sum += degrees[i];
        }

        // make sure sum of degree is even
        if (sum % 2 != 0) {
            degrees[0]++;
        }

        // candidate nodes with degree > 0
        List<Node> candidates = new ArrayList<Node>();

        for (int i = 0; i < N; ++i) {
            // create new node
            Node newNode = graphModel.factory().newNode();
            // initialize node
            newNode.getNodeData().setSize(NODE_SIZE);
            newNode.getNodeData().setLabel("" + (i));
            // add to graph
            graphModel.getGraph().addNode(newNode);
            cell.addNode(newNode);
            candidates.add(newNode);
            //Sleep some time
            animateNode();
        }

        // connect nodes at random
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < degrees[i]; ++j) {
                Node current = cell.getNodes().get(i);
                int index = i;
                while (i == index) {
                    index = rand.nextInt(candidates.size());
                }
                Node other = cell.getNodes().get(index);

                Edge edge = graphModel.factory().newEdge(current, other);
                graphModel.getGraph().addEdge(edge);

                // update degrees of both nodes and remove from candidate nodes if necessary
                degrees[i]--;
                if (degrees[i] <= 0) {
                    candidates.remove(current);
                }
                degrees[index]--;
                if (degrees[index] <= 0) {
                    candidates.remove(other);
                }

                //Sleep some time
                animateEdge();
            }
        }

    }

// <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public String getName() {
        return NbBundle.getMessage(SmallWorldGraph.class, "SmallWorldGraph.name");
    }

    public TYPE getType() {
        return type;
    }

    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(SmallWorldGraphUI.class);
    }

    public void setNumberOfNodes(int numberOfNodes) {
        if (numberOfNodes < 0) {
            throw new IllegalArgumentException("# of nodes must be greater than 0");
        }
        this.numberOfNodes = numberOfNodes;
    }

    public void setKNeighbors(int K) {
        if (K < 0) {
            throw new IllegalArgumentException("# of neighbors must be greater than 0");
        }
        this.K = K;
    }

    public void setWiringProbability(double wiringProbability) {
        if (wiringProbability < 0 || wiringProbability > 1) {
            throw new IllegalArgumentException("Wiring probability must be between 0 and 1");
        }
        this.wiringProbability = wiringProbability;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public int getKNeighbors() {
        return K;
    }

    public double getWiringProbability() {
        return wiringProbability;
    }

    public void setType(int type) {
        switch (type) {
            case 1:
                this.type = TYPE.WS;
                break;
            case 2:
                this.type = TYPE.HK;
                break;
            case 3:
                this.type = TYPE.Tv;
                break;
            case 4:
                this.type = TYPE.uSF;
                break;
        }
    }
    // </editor-fold>
}
