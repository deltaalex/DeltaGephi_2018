package org.gephi.io.generator.plugin;

import java.awt.geom.Point2D;
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

    private int numberOfNodes = 1000;
    private int K = 0;
    private double wiringProbability = 0.33333; // 0.2

    public enum TYPE {

        WS, HK, Tv, uSF, SFE
    }
    private TYPE type = TYPE.SFE;

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
        } else if (type.equals(TYPE.SFE)) {
            createSFEdgeNetwork(cell, numberOfNodes, graphModel);
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

    /**
     * Alex & Mihai network Creates a network whose edges are added between any
     * two nodes with a probability indirect proportional to the distance
     * between the two nodes. The result is a power-law distribution of edges
     * based on distance: very few long range links -- many short range links.
     * Distance between two nodes is given by their initial random position in a
     * square of size KxK (100x100 etc.) Goal: obtain a SW network from the
     * preferential attachment principle!
     */
    private void createSFEdgeNetwork(Cell cell, int N, GraphModel graphModel) {
        Random rand = new Random();
        Node n1, n2;
        double gamma = 1 / wiringProbability;
        boolean hexagonal = false; // deprecated !

        // K used as distance here!!!      
        if (hexagonal) {
            // create the N nodes as a hexagonal spaced points on a 2D lattice       
            createHexagonTopology(cell, N, graphModel);
        } else {
            // create the N nodes with spatial coordinates (0,sqrt(N)) x (0,sqrt(N));   
            // optionally K can be used to place random centers of gravity that 
            // pull nodes towards them, creating more dense communitites
            createRandomXYToplogyWithCentroids(cell, N, graphModel, K);
        }

        // for each node, try to add edge
        for (int i = 0; i < N - 1; ++i) {
            for (int j = i + 1; j < N; ++j) {
                n1 = cell.getNodes().get(i);
                n2 = cell.getNodes().get(j);
                // probability of edge(1,2) = 1/distance(1,2)^gamma
                if (rand.nextDouble() < Math.pow(distanceXY(n1, n2), -gamma)) {
                    // create edge
                    Edge edge = graphModel.factory().newEdge(n1, n2);
                    graphModel.getGraph().addEdge(edge);
                    //Sleep some time
                    animateEdge();
                }
            }
        }
    }

    private void createRandomXYToplogyWithCentroids(Cell cell, int N, GraphModel graphModel, int numCentroids) {
        Random rand = new Random();
        int a = (int) Math.sqrt(N) + 1;

        for (int i = 0; i < N; ++i) {
            // create new node
            Node newNode = graphModel.factory().newNode();
            // initialize node
            newNode.getNodeData().setSize(NODE_SIZE);
            //newNode.getNodeData().setLabel(Integer.toString(i));
            newNode.getNodeData().setX(rand.nextFloat() * a);
            newNode.getNodeData().setY(rand.nextFloat() * a);
            // add to graph
            graphModel.getGraph().addNode(newNode);
            cell.addNode(newNode);
            //Sleep some time
            animateNode();
        }

        // create centers of gravity at random coordinates        
        if (numCentroids > 0) {
            List<Point2D.Float> centroids = new ArrayList<Point2D.Float>(numCentroids);

            // place centroids at random (x,y) coordinates within (0,a)^2
            for (int i = 0; i < numCentroids; ++i) {
                centroids.add(new Point2D.Float(rand.nextFloat() * a, rand.nextFloat() * a));
            }

            // attract each node towards each centroids with a force (dx,dy) equal to
            // dx = dist(ni, ci)^-gamma;
            for (Node node : cell.getNodes()) {
                for (Point2D.Float centroid : centroids) {
                    float dx = Math.abs(distanceOX(node, centroid));
                    float dy = Math.abs(distanceOY(node, centroid));

                    //if (dx < a / 4 && dy < a / 4) {
                        node.getNodeData().setX(node.getNodeData().x() - dx / distanceOX(node, centroid) * Math.min(dx, 1f / dx));
                        node.getNodeData().setY(node.getNodeData().y() - dy / distanceOY(node, centroid) * Math.min(dy, 1f / dy));
                    //}

                }
            }
        }
    }

    private void createHexagonTopology(Cell cell, int N, GraphModel graphModel) {
        // compute hexagonal area
        int a = (int) Math.sqrt(N) + 1;

        for (int i = 0; i < a; ++i) {
            for (int j = 0; j < a + 1; ++j) {
                if (N > 0) {
                    // create new node
                    Node newNode = graphModel.factory().newNode();
                    // initialize node
                    newNode.getNodeData().setSize(NODE_SIZE);
                    //newNode.getNodeData().setLabel(Integer.toString(i));

                    newNode.getNodeData().setX(i);

                    if (i % 2 == 0) {
                        newNode.getNodeData().setY(j);
                    } else {
                        newNode.getNodeData().setY(j + 0.5f);
                    }
                    // add to graph
                    graphModel.getGraph().addNode(newNode);
                    cell.addNode(newNode);
                    //Sleep some time
                    animateNode();
                }
                N--;
            }
        }
    }

    private double distanceXY(Node n1, Node n2) {
        return Math.sqrt((n1.getNodeData().x() - n2.getNodeData().x()) * (n1.getNodeData().x() - n2.getNodeData().x())
                + (n1.getNodeData().y() - n2.getNodeData().y()) * (n1.getNodeData().y() - n2.getNodeData().y()));
    }

    private float distanceOX(Node n1, Point2D.Float point) {
        return n1.getNodeData().x() - point.x;
    }

    private float distanceOY(Node n1, Point2D.Float point) {
        return n1.getNodeData().y() - point.y;
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
            case 5:
                this.type = TYPE.SFE;
                break;
        }
    }
    // </editor-fold>
}
