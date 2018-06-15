package org.gephi.io.generator.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.*;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.statistics.plugin.ClusteringCoefficient;
import org.gephi.statistics.plugin.EigenvectorCentrality;
import org.gephi.statistics.plugin.GraphDistance;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Creates a Scale-Free network, as defined by
 * http://en.wikipedia.org/wiki/Barab%C3%A1si%E2%80%93Albert_model#Algorithm
 *
 * @author Alexandru Topirceanu
 */
@ServiceProvider(service = Generator.class)
public class ScaleFreeGraph extends AbstractGraph implements Generator {

    protected int numberOfNodes = 1000;
    protected Metric[] metrics = {Metric.Degree};
    protected int randomGraphSize = 200;
    protected double pRandomWiring = 0.1;
    protected double targetDegree = 6.241;
    protected boolean directed = false;
    protected boolean weighted = false;
    protected boolean powerLawWeights = false;
    protected int fractalSize = 0;
    private boolean simulateGrowth = false;
    private final boolean DEBUG_GENERATION = true;

    @Override
    protected int initialize() {
        return numberOfNodes;
    }

    @Override
    protected void runGeneration(GraphModel graphModel, Random random) {

        // nodes
        List<Node> nodeArray = new ArrayList<Node>(numberOfNodes);
        Graph graph = graphModel.getGraph();
        graph.clear();

        // initialize random graph seed
        initializeRandomGraph(nodeArray, graph, randomGraphSize, pRandomWiring, weighted, powerLawWeights);

        progress.switchToIndeterminate();

        if (DEBUG_GENERATION) {
            //metrics = new Metric[]{Metric.Eigenvector};



            if (metrics[0].equals(Metric.Degree)) {
                /**
                 * DEGREE PREFERENTIAL ATTACHMENT
                 */
                for (int i = randomGraphSize; i < numberOfNodes; ++i) {
                    // node 'i' to be linked to one existing node
                    Node newNode = null;
                    // total fitness in graph
                    int totalDegree = 0;
                    for (Node node : nodeArray) {
                        totalDegree += graph.getDegree(node);
                    }

                    boolean success = false;
                    boolean enoughDegree = false;
                    //while (!enoughDegree) {
                        for (Node node : nodeArray) {
                            double p = random.nextDouble();
                            // try to connect to each node with probability 'p'
                            if (p < 1.0 * graph.getDegree(node) / totalDegree) {
                                if (newNode == null) {
                                    newNode = createNode(graphModel, i, true);
                                    success = true;
                                }
                                createAddDirectedEdge(graphModel, newNode, node);
                                createAddDirectedEdge(graphModel, node, newNode);
                                enoughDegree = graph.getDegree(newNode) >= targetDegree;
                            }

                            if (enoughDegree) {
                                break;
                            }
                        }
                        if (success) {
                            nodeArray.add(newNode);
                            success = false;
                        }
                    //}
                }
            } /**
             * BETWEENNESS PREFERENTIAL ATTACHMENT
             */
            else if (metrics[0].equals(Metric.Betweenness)) {
                for (int i = randomGraphSize; i < numberOfNodes; ++i) {
                    // node 'i' to be linked to one existing node
                    Node newNode = null;
                    // save time, compute fitness only after adding 10 new nodes
                    if (i % 10 == 0) {
                        computeMetricOnGraph(graphModel, metrics);
                    }
                    // total fitness in graph                
                    double totalBtw = 0.0;
                    for (Node node : nodeArray) {
                        if (node.getAttributes().getValue(GraphDistance.BETWEENNESS) != null) {
                            totalBtw += (Double) node.getAttributes().getValue(GraphDistance.BETWEENNESS);
                        }
                    }

                    boolean success = false;
                    boolean enoughDegree = false;
                    //while (!enoughDegree) {
                        for (Node node : nodeArray) {
                            double p = random.nextDouble();
                            // try to connect to each node with probability 'p'
                            if (node.getAttributes().getValue(GraphDistance.BETWEENNESS) == null) {
                                continue;
                            }

                            if (p < (Double) node.getAttributes().getValue(GraphDistance.BETWEENNESS) / totalBtw) {
                                if (newNode == null) {
                                    newNode = createNode(graphModel, i, true);
                                    success = true;
                                }
                                createAddDirectedEdge(graphModel, newNode, node);
                                createAddDirectedEdge(graphModel, node, newNode);
                                enoughDegree = graph.getDegree(newNode) >= targetDegree;
                            }

                            if (enoughDegree) {
                                break;
                            }
                        }
                        if (success) {
                            nodeArray.add(newNode);
                            success = false;
                        }
                    //}
                }
            } /**
             * EIGENVECTOR PREFERENTIAL ATTACHMENT
             */
            else if (metrics[0].equals(Metric.Eigenvector)) {
                for (int i = randomGraphSize; i < numberOfNodes; ++i) {
                    // node 'i' to be linked to one existing node
                    Node newNode = null;
                    // save time, compute fitness only after adding 10 new nodes
                    if (i % 5 == 0) {
                        computeMetricOnGraph(graphModel, metrics);
                    }
                    // total fitness in graph                
                    double totalEigen = 0.0;
                    for (Node node : nodeArray) {
                        totalEigen += (Double) node.getAttributes().getValue(EigenvectorCentrality.EIGENVECTOR);
                    }

                    boolean success = false;
                    boolean enoughDegree = false;
                    //while (!enoughDegree) {
                        for (Node node : nodeArray) {
                            double p = random.nextDouble();
                            // try to connect to each node with probability 'p'
                            if (p < (Double) node.getAttributes().getValue(EigenvectorCentrality.EIGENVECTOR) / totalEigen) {
                                if (newNode == null) {
                                    newNode = createNode(graphModel, i, true);
                                    success = true;
                                }
                                createAddDirectedEdge(graphModel, newNode, node);
                                createAddDirectedEdge(graphModel, node, newNode);
                                enoughDegree = graph.getDegree(newNode) >= targetDegree;
                            }

                            if (enoughDegree) {
                                break;
                            }
                        }
                        if (success) {
                            nodeArray.add(newNode);
                            success = false;
                        }
                    //}
                }
            } /**
             * CLOSENESS PREFERENTIAL ATTACHMENT
             */
            else if (metrics[0].equals(Metric.Closeness)) {
                for (int i = randomGraphSize; i < numberOfNodes; ++i) {
                    // node 'i' to be linked to one existing node
                    Node newNode = null;
                    // save time, compute fitness only after adding 10 new nodes
                    if (i % 10 == 0) {
                        computeMetricOnGraph(graphModel, metrics);
                    }
                    // total fitness in graph                
                    double totalCls = 0.0;
                    for (Node node : nodeArray) {
                        if (node.getAttributes().getValue(GraphDistance.CLOSENESS) != null) {
                            totalCls += (Double) node.getAttributes().getValue(GraphDistance.CLOSENESS);
                        }
                    }

                    boolean success = false;
                    boolean enoughDegree = false;
                    //while (!enoughDegree) {
                        for (Node node : nodeArray) {
                            double p = random.nextDouble();
                            // try to connect to each node with probability 'p'
                            if (node.getAttributes().getValue(GraphDistance.CLOSENESS) == null) {
                                continue;
                            }

                            if (p < (Double) node.getAttributes().getValue(GraphDistance.CLOSENESS) / totalCls) {
                                if (newNode == null) {
                                    newNode = createNode(graphModel, i, true);
                                    success = true;
                                }
                                createAddDirectedEdge(graphModel, newNode, node);
                                createAddDirectedEdge(graphModel, node, newNode);
                                enoughDegree = graph.getDegree(newNode) >= targetDegree;
                            }

                            if (enoughDegree) {
                                break;
                            }
                        }
                        if (success) {
                            nodeArray.add(newNode);
                            success = false;
                        }
                    //}
                }
            } /**
             * CLUSTERING PREFERENTIAL ATTACHMENT
             */
            else if (metrics[0].equals(Metric.Clustering)) {
                for (int i = randomGraphSize; i < numberOfNodes; ++i) {
                    // node 'i' to be linked to one existing node
                    Node newNode = null;
                    // save time, compute fitness only after adding 10 new nodes
                    if (i % 10 == 0) {
                        computeMetricOnGraph(graphModel, metrics);
                    }
                    // total fitness in graph                
                    double totalClustering = 0.0;
                    for (Node node : nodeArray) {
                        totalClustering += (Double) node.getAttributes().getValue(ClusteringCoefficient.CLUSTERING_COEFF);
                    }

                    boolean success = false;
                    boolean enoughDegree = false;
                    //while (!enoughDegree) {
                        for (Node node : nodeArray) {
                            double p = random.nextDouble();
                            // try to connect to each node with probability 'p'
                            if (p < (Double) node.getAttributes().getValue(ClusteringCoefficient.CLUSTERING_COEFF) / totalClustering) {
                                if (newNode == null) {
                                    newNode = createNode(graphModel, i, true);
                                    success = true;
                                }
                                createAddDirectedEdge(graphModel, newNode, node);
                                createAddDirectedEdge(graphModel, node, newNode);
                                enoughDegree = graph.getDegree(newNode) >= targetDegree;
                            }

                            if (enoughDegree) {
                                break;
                            }
                        }
                        if (success) {
                            nodeArray.add(newNode);
                            success = false;
                        }
                    //}
                }
            }
            progress.switchToDeterminate(100);
        } // <editor-fold defaultstate="collapsed" desc="Code that sucks">         
        else {
            double[] sumpk = null;

            // initialize other nodes and edges
            for (int i = randomGraphSize; i < 2 * numberOfNodes && nodeArray.size() < numberOfNodes; ++i) {
                Node newNode = null;

                /**
                 * Define optimization metric(s)
                 */
                if (i % 10 == 0) {
                    sumpk = getMetricsFromGraph(graphModel, metrics);
                }

                boolean success = false;
                //while (!success) {

                // try to connect the new node to nodes in the network
                for (Node node : nodeArray) {

                    double[] pi = new double[metrics.length];
                    // metric of current sfNode
                    for (int m = 0; m < metrics.length; ++m) {
                        pi[m] = getNodeMetric(graph, node, metrics[m]);
                    }

                    // get random value
                    double p = random.nextDouble();
                    // weight of each metric
                    double w = 1.0 / metrics.length;

                    double fitness = 0.0;
                    for (int m = 0; m < metrics.length; ++m) {
                        fitness += w * pi[m] / sumpk[m];
                    }

                    // connect if p < probability
                    if (p < fitness) {
                        // add new node to graph only when it recieves >=1 edge
                        if (newNode == null) {
                            newNode = createNode(graphModel, i, true);
                            success = true;
                        }

                        if (weighted) {
                            double weight = powerLawWeights ? getPowerDistributedDoubleValue(random, 0, 1) : random.nextFloat();
                            Edge edge = createAddDirectedEdge(graphModel, newNode, node);
                            edge.setWeight((float) weight);
                        } else {
                            createAddDirectedEdge(graphModel, newNode, node);
                        }

                        if (weighted) {
                            double weight = powerLawWeights ? getPowerDistributedDoubleValue(random, 0, 1) : random.nextFloat();
                            Edge edge = createAddDirectedEdge(graphModel, node, newNode);
                            edge.setWeight((float) weight);
                        } else {
                            createAddDirectedEdge(graphModel, node, newNode);
                        }

                        //success = true;
                    }
                }
                //}
                if (success) {
                    nodeArray.add(newNode);
                }

                //progressTick();
            }
            if (1 == 1) {
                progress.switchToDeterminate(100);
                return;
            }

            // add edges to graph until desired average degree is reached
            final int averageDegree = 10;
            simulateGrowth = false;
            if (simulateGrowth && weighted) {
                // 1. for eeach node, add (averageDegree - degree) new edges
                for (Node node : nodeArray) {
                    // get friends of a current node
                    Node[] myFriends = graph.getNeighbors(node).toArray();
                    if (myFriends.length == 0) {
                        continue;
                    }
                    // max new edges can be either the desired degree minus the current degree or
                    // the max number of friends
                    int newEdges = Math.max(averageDegree - graph.getDegree(node), 0);

                    // re-compute metrics on graph
                    double[] sumPk = getMetricsFromGraph(graphModel, metrics);

                    // 2. add edge by choosing the best unlinked fitting friend of a neighbor
                    boolean tryConnect = true;
                    for (int e = 0; e < newEdges && tryConnect; ++e) {

                        // get friends of a random neighbor                    
                        Node myFriend = myFriends[random.nextInt(myFriends.length)];
                        Node[] friendFriends = graph.getNeighbors(myFriend).toArray();
                        if (friendFriends.length <= 1) {
                            continue;
                        }

                        // try to connect to friends of target                
                        while (tryConnect) {
                            // todo preferential!
                            Node friendFriend = friendFriends[random.nextInt(friendFriends.length)];

                            // if no edge exists yet
                            if (!node.equals(friendFriend) && existsNoEdge(graphModel, friendFriend, node)) {
                                Edge newEdge = createUndirectedEdge(graphModel, node, friendFriend);

                                // 3. put weight W on new edge
                                double[] pi = new double[metrics.length];
                                // metric of current target
                                for (int m = 0; m < metrics.length; ++m) {
                                    pi[m] = getNodeMetric(graph, friendFriend, metrics[m]);
                                }

                                // weight of each metric
                                double w = 1.0 / metrics.length;

                                double fitness = 0.0;
                                for (int m = 0; m < metrics.length; ++m) {
                                    fitness += w * pi[m] / sumPk[m];
                                }

                                // set weight proportional to fitness                            
                                newEdge.setWeight((float) fitness);

                                // 4. weight W is subtracted evenly from all neighbors of the current node
                                float W = (float) fitness / myFriends.length;
                                for (Node friend : myFriends) {
                                    Edge edge = graph.getEdge(node, friend);
                                    // reduce all weights by W
                                    if (edge != null) {
                                        edge.setWeight(edge.getWeight() - W);

                                        if (edge.getWeight() <= 0) {
                                            graph.removeEdge(edge);
                                        }
                                    }
                                }

                                break;
                            }

                            // hack: check for possible links                    
                            tryConnect = false;
                            for (Node f : friendFriends) {
                                if (!node.equals(f)) {
                                    if (existsNoEdge(graphModel, f, node)) {
                                        tryConnect = true;
                                    }
                                }
                            }
                        }
                    }

                }
            }

            // replicate the whole network fD times
            if (fractalSize > 0) {
                // debug only
//            File flog = new File(System.getProperty("user.home") + "/Desktop/gephi-log.txt");
//            PrintWriter log = null;
//            try {
//                log = new PrintWriter(flog);
//            } catch (FileNotFoundException ex) {
//                Exceptions.printStackTrace(ex);
//                System.exit(13);
//            }

                Node[][] clusters = new Node[fractalSize][nodeArray.size()];

                // original cluster
                for (int i = 0; i < nodeArray.size(); ++i) {
                    clusters[0][i] = nodeArray.get(i);
                }

                // copy other clusters
                for (int c = 1; c < fractalSize; ++c) {
                    // add nodes
                    for (int i = 0; i < nodeArray.size(); ++i) {
                        // create node
                        Node aNewNode = createNode(graphModel, c * numberOfNodes + i, true);
                        clusters[c][i] = aNewNode;
                    }

                    // copy edges            
                    for (int i = 0; i < nodeArray.size(); ++i) {
                        Node source = clusters[c][i];

                        // for each node's neighbor
                        for (Node neighbor : graph.getNeighbors(nodeArray.get(i)).toArray()) {
                            Node dest = clusters[c][neighbor.getId() - 1];
                            Edge edge = createAddDirectedEdge(graphModel, source, dest);
                            edge = createAddDirectedEdge(graphModel, dest, source);
                        }
                    }
                }

                // connect clusters                        
                // compute sum of all metrics in network
                double[] sumPk = new double[metrics.length];
                for (int m = 0; m < metrics.length; ++m) {
                    sumPk[m] = 0.0;
                    for (Node node : graphModel.getGraph().getNodes()) {
                        sumPk[m] += getNodeMetric(graphModel.getGraph(), node, metrics[m]);
                    }
                }

                for (Node source : graph.getNodes().toArray()) {
                    for (Node dest : graph.getNodes().toArray()) {
                        // for all nodes to each other
                        if (!source.equals(dest)) {

                            double[] pi = new double[metrics.length];
                            // metric of current sfNode
                            for (int m = 0; m < metrics.length; ++m) {
                                pi[m] = getNodeMetric(graph, dest, metrics[m]);
                            }

                            // get random value
                            double p = random.nextDouble();
                            // weight of each metric
                            double w = 1.0 / metrics.length;

                            double fitness = 0.0;
                            for (int m = 0; m < metrics.length; ++m) {
                                fitness += w * pi[m] / sumPk[m];
                            }

                            // connect if p < probability
                            if (p < fitness) {
                                Edge edge = createAddDirectedEdge(graphModel, source, dest);
                                if (weighted) {
                                    double weight = powerLawWeights ? getPowerDistributedDoubleValue(random, 0, 1) : random.nextFloat();
                                    edge.setWeight((float) weight);
                                }
                                edge = createAddDirectedEdge(graphModel, dest, source);
                                if (weighted) {
                                    double weight = powerLawWeights ? getPowerDistributedDoubleValue(random, 0, 1) : random.nextFloat();
                                    edge.setWeight((float) weight);
                                }
                            }
                        }
                    }
                }

                clusters = null;
                //log.close();
            }

            nodeArray = null;
            progress.switchToDeterminate(100);
            progress.finish();
        }
        // </editor-fold> 
    }

    protected final double[] getMetricsFromGraph(GraphModel graphModel, Metric[] metrics) {

        // compute metrics on the graph
        computeMetricOnGraph(graphModel, metrics);

        // compute sum of all metrics in network
        double[] sumpk = new double[metrics.length];
        for (int m = 0; m < metrics.length; ++m) {
            sumpk[m] = 0.0;
            for (Node node : graphModel.getGraph().getNodes()) {
                sumpk[m] += getNodeMetric(graphModel.getGraph(), node, metrics[m]);
            }
        }

        return sumpk;
    }

    protected final void computeMetricOnGraph(GraphModel graphModel, Metric[] metrics) {

        for (Metric metric : metrics) {

            if (metric.equals(Metric.Degree)) {
                return;

            } else if (metric.equals(Metric.Betweenness)) {
                GraphDistance statistic = new GraphDistance();
                statistic.setNormalized(false);
                statistic.setDirected(directed);
                statistic.setProgressTicket(progress);
                AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
                statistic.execute(graphModel, attributeModel);
                statistic.getDiameter();
                return;

            } else if (metric.equals(Metric.Eigenvector)) {
                EigenvectorCentrality statistic = new EigenvectorCentrality();
                statistic.setNumRuns(100);
                statistic.setDirected(directed);
                statistic.setProgressTicket(progress);
                AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
                statistic.execute(graphModel, attributeModel);
                statistic.getNumRuns();
                return;

            } else if (metric.equals(Metric.Closeness)) {
                GraphDistance statistic = new GraphDistance();
                statistic.setNormalized(false);
                statistic.setDirected(directed);
                statistic.setProgressTicket(progress);
                AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
                statistic.execute(graphModel, attributeModel);
                statistic.getDiameter();
                return;

            } else if (metric.equals(Metric.Clustering)) {
                ClusteringCoefficient statistic = new ClusteringCoefficient();
                statistic.setDirected(directed);
                statistic.setProgressTicket(progress);
                AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
                statistic.execute(graphModel, attributeModel);
                statistic.getAverageClusteringCoefficient();
                return;

            } else if (metric.equals(Metric.APL)) {
                GraphDistance statistic = new GraphDistance();
                statistic.setNormalized(false);
                statistic.setDirected(directed);
                statistic.setProgressTicket(progress);
                AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
                statistic.execute(graphModel, attributeModel);
                statistic.getPathLength();
                return;

            } else if (metric.equals(Metric.Modularity)) {
                Modularity statistic = new Modularity();
                statistic.setRandom(true);
                statistic.setResolution(1.0);
                statistic.setUseWeight(false);
                statistic.setProgressTicket(progress);
                AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
                statistic.execute(graphModel, attributeModel);
                statistic.getModularity();
                return;

            } else {
                throw new IllegalArgumentException("Metric not supported: " + metric.toString());
            }
        }
    }

    protected final double getNodeMetric(Graph graph, Node node, Metric metric) {

        if (metric.equals(Metric.Degree)) {
            return graph.getDegree(node);

        } else if (metric.equals(Metric.Betweenness)) {
            Double x = (Double) node.getAttributes().getValue(GraphDistance.BETWEENNESS);
            return x == 0 ? 0.0 : x;

        } else if (metric.equals(Metric.Eigenvector)) {
            return (Double) node.getAttributes().getValue(EigenvectorCentrality.EIGENVECTOR);

        } else if (metric.equals(Metric.Closeness)) {
            return (Double) node.getAttributes().getValue(GraphDistance.CLOSENESS);

        } else if (metric.equals(Metric.Clustering)) {
            Object o = node.getAttributes().getValue(ClusteringCoefficient.CLUSTERING_COEFF);
            try {
                return (Double) o;
            } catch (ClassCastException e1) {
                try {
                    return (Float) o;
                } catch (Exception e2) {
                    e2.getMessage();
                    return 0;
                }
            }

        } else {
            throw new IllegalArgumentException("Metric not supported: " + metric.toString());
        }
    }

    public enum Metric {

        Degree, Betweenness, Closeness, Eigenvector, Clustering, APL, Modularity;
    }

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public String getName() {
        return NbBundle.getMessage(ScaleFreeGraph.class, "ScaleFreeGraph.name");
    }

    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(ScaleFreeGraphUI.class);
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public void setSeedSize(int seedSize) {
        this.randomGraphSize = seedSize;
    }

    public void setSeedWiring(double seedWiring) {
        this.pRandomWiring = seedWiring;
    }

    public void setFractalSize(int fractalSize) {
        this.fractalSize = fractalSize;
    }

    public void setWeighted(boolean weighted) {
        this.weighted = weighted;
    }

    public void setPowerLawWeights(boolean powerLawWeights) {
        this.powerLawWeights = powerLawWeights;
    }

    public void setMetrics(Metric[] metrics) {
        ArrayList<Metric> filtered = new ArrayList<Metric>();

        for (Metric m : metrics) {
            if (m != null) {
                filtered.add(m);
            }
        }

        this.metrics = filtered.toArray(new Metric[]{});
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public int getSeedSize() {
        return randomGraphSize;
    }

    public double getSeedWiring() {
        return pRandomWiring;
    }

    public int getFractalSize() {
        return fractalSize;
    }

    public boolean getWeighted() {
        return weighted;
    }

    public boolean getPowerLawWeights() {
        return powerLawWeights;
    }

    public Metric[] getMetrics() {
        return metrics;
    }

    /**
     * Find if metric is enabled in fitness
     *
     * @return
     */
    public boolean getMetric(Metric metric) {

        for (Metric m : this.metrics) {
            if (m.equals(metric)) {
                return true;
            }
        }
        return false;
    }
    // </editor-fold>   
}
