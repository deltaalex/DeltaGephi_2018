package org.gephi.statistics.plugin.social;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import org.gephi.statistics.plugin.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Benchmarks node centrality in terms of spreading potential using the SIR
 * epidemic model. Implemented centralities are: degree, btw, pagerank, eigen,
 * closeness, hits, b*d, b/d, hirsch, clusterrank, leaderrank, local centrality
 *
 * @author Alexander
 */
public class SocialInfluenceBenchmark implements Statistics, LongTask {

    public static final String TAG_SIR_STATUS = "SIR_status";
    public static final String TAG_DELTA_INFECT = "Delta_infect";
    public static final String TAG_KENDALL_SCORE = "Kendall_score";
    /**
     * Remembers if the Cancel function has been called.
     */
    private boolean isCanceled;
    /**
     * Keep track of the work done.
     */
    private ProgressTicket progress;
    /**
     * Edge type
     */
    private boolean directed = false;
    /*
     * Chosen centrality for benchmark
     */
    private BenchmarkCentrality centrality;
    /**
     * The interaction algorithm to be used for the diffusion process
     */
    private DiffusionAlgorithm diffusionAlgorithm = DiffusionAlgorithm.TOLERANCE;
    /**
     * Stop condition for diffusion processes
     */
    private final int MAX_ITERATIONS = 3000;
    /**
     * How often the population should be polled during the tolerance diffusion
     */
    private final int POLL_FREQUENCY = 25;
    /**
     * Name of centrality, as found in node column definition
     */
    private String centralityTag;
    /**
     * Ratio of initial seeders
     */
    private double pSeeders = 0.05;
    /**
     * Ratio of population that needs to become recovered
     */
    private double kPopulation = 0.95;
    /*
     * Number of iterations until infected node becomes recovered
     */
    private double deltaRecover = 10; // 10!
    /**
     * Probability to become infected after contacting an infected node
     */
    private double lambdaInfect = 0.05; // 0.05!

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">   
    public void setCentrality(BenchmarkCentrality centrality) {
        this.centrality = centrality;
    }

    public BenchmarkCentrality getCentrality() {
        return centrality;
    }

    private Integer getDegree(Node node) {
        return (Integer) node.getAttributes().getValue(Degree.DEGREE);
    }

    private Double getBetweenness(Node node) {
        return (Double) node.getAttributes().getValue(GraphDistance.BETWEENNESS);
    }

    private Double getEigenvector(Node node) {
        return (Double) node.getAttributes().getValue(EigenvectorCentrality.EIGENVECTOR);
    }

    private Double getCloseness(Node node) {
        return (Double) node.getAttributes().getValue(GraphDistance.CLOSENESS);
    }

    private Double getPageRank(Node node) {
        return (Double) node.getAttributes().getValue(PageRank.PAGERANK);
    }

    private Double getHitsAuthority(Node node) {
        return (Double) node.getAttributes().getValue(Hits.AUTHORITY);
    }

    private Double getBDPower(Node node) {
        return (Double) node.getAttributes().getValue(GraphDistance.B_TIMES_D_POWER);
    }

    private Double getBDInfluence(Node node) {
        return (Double) node.getAttributes().getValue(GraphDistance.B_PER_D_POWER);
    }

    private SIRType getSIRType(Node node) {
        return (SIRType) node.getAttributes().getValue(TAG_SIR_STATUS);
    }

    private int getDeltaInfect(Node node) {
        return (Integer) node.getAttributes().getValue(TAG_DELTA_INFECT);
    }

    private int getKendallScore(Node node) {
        return (Integer) node.getAttributes().getValue(TAG_KENDALL_SCORE);
    }

    private Double getCentrality(Node node) {
        if (node.getAttributes().getValue(centralityTag) instanceof Double) {
            return (Double) node.getAttributes().getValue(centralityTag);

        } else if (node.getAttributes().getValue(centralityTag) instanceof Float) {
            return Double.valueOf((Float) node.getAttributes().getValue(centralityTag));

        } else {
            return Double.valueOf((Integer) node.getAttributes().getValue(centralityTag));
        }
    }

    // </editor-fold> 
    // <editor-fold defaultstate="collapsed" desc="Execution">
    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
        execute(graph, attributeModel);
    }

    public void execute(HierarchicalGraph graph, AttributeModel attributeModel) {

        // atributes
        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn sirCol = nodeTable.getColumn(TAG_SIR_STATUS);
        AttributeColumn deltaCol = nodeTable.getColumn(TAG_DELTA_INFECT);
        AttributeColumn kendallCol = nodeTable.getColumn(TAG_KENDALL_SCORE);

        if (sirCol == null) {
            sirCol = nodeTable.addColumn(TAG_SIR_STATUS, NbBundle.getMessage(SocialInfluenceBenchmark.class, "SocialInfluenceBenchmark.nodecolumn.SirStatus"), AttributeType.INT, AttributeOrigin.COMPUTED, new Integer(0));
        }
        if (deltaCol == null) {
            deltaCol = nodeTable.addColumn(TAG_DELTA_INFECT, NbBundle.getMessage(SocialInfluenceBenchmark.class, "SocialInfluenceBenchmark.nodecolumn.DeltaInfect"), AttributeType.INT, AttributeOrigin.COMPUTED, new Integer(0));
        }
        if (kendallCol == null) {
            kendallCol = nodeTable.addColumn(TAG_KENDALL_SCORE, NbBundle.getMessage(SocialInfluenceBenchmark.class, "SocialInfluenceBenchmark.nodecolumn.KendallScore"), AttributeType.INT, AttributeOrigin.COMPUTED, new Integer(0));
        }

        // list of nodes
        List<Node> nodes = new ArrayList<Node>();
        for (Node node : graph.getNodes()) {
            nodes.add(node);
        }

        graph.readLock();
        Progress.start(progress);
        progress.switchToIndeterminate();

        //
        // 1) compute centrality
        //
        // <editor-fold defaultstate="collapsed" desc="select centrality">
        switch (centrality) {
            case DEGREE:
                centralityTag = Degree.DEGREE;
                break;
            case BETWEENNESS:
                //runBetweenness(graph, attributeModel);
                centralityTag = GraphDistance.BETWEENNESS;
                break;
            case EIGENVECTOR:
                //runEigenvector(graph, attributeModel);
                centralityTag = EigenvectorCentrality.EIGENVECTOR;
                break;
            case CLOSENESS:
                //runBetweenness(graph, attributeModel);
                centralityTag = GraphDistance.CLOSENESS;
                break;
            case PAGERANK:
                //runPageRank(graph, attributeModel);
                centralityTag = PageRank.PAGERANK;
                break;
            case HITS:
                //runHits(graph, attributeModel);
                centralityTag = Hits.AUTHORITY;
                break;
            case BDPOWER:
                //runBetweenness(graph, attributeModel);
                centralityTag = GraphDistance.B_TIMES_D_POWER;
                break;
            case BDINFLUENCE:
                //runBetweenness(graph, attributeModel);
                centralityTag = GraphDistance.B_PER_D_POWER;
                break;
            case HINDEX:
                centralityTag = InfluenceRankings.HINDEX;
                break;
            case CLUSTERRANK:
                centralityTag = InfluenceRankings.CLUSTERRANK;
                break;
            case LEADERRANK:
                centralityTag = InfluenceRankings.LEADERRANK;
                break;
            case LOCALCENTRALITY:
                centralityTag = InfluenceRankings.LOCALCENTRALITY;
                break;
        }
        // </editor-fold>

        // check centrality was already run
        if (nodes.get(0).getAttributes().getValue(centralityTag) == null) {
            throw new IllegalStateException("You must first run " + centralityTag.toString() + " centrality on the graph.");
        }

        progress.switchToDeterminate(100);
        progress.finish();
        graph.readUnlockAll();

        //
        // 2) sort nodes by centrality
        //      

        Collections.sort(nodes, new Comparator<Node>() {
            // sort by used centrality
            public int compare(Node n1, Node n2) {
                if (n1.getAttributes().getValue(centralityTag) instanceof Double) {
                    Double c1 = (Double) n1.getAttributes().getValue(centralityTag);
                    Double c2 = (Double) n2.getAttributes().getValue(centralityTag);
                    return c2.compareTo(c1);
                } else if (n1.getAttributes().getValue(centralityTag) instanceof Float) {
                    Float c1 = (Float) n1.getAttributes().getValue(centralityTag);
                    Float c2 = (Float) n2.getAttributes().getValue(centralityTag);
                    return c2.compareTo(c1);
                } else {
                    Integer c1 = (Integer) n1.getAttributes().getValue(centralityTag);
                    Integer c2 = (Integer) n2.getAttributes().getValue(centralityTag);
                    return c2.compareTo(c1);

                }
            }
        });

        //
        // 3) infect top pSeeders% nodes
        //        
        List<Node> infectiousList = new ArrayList<Node>();

        for (int i = 0; i < nodes.size(); ++i) {
            Node node = nodes.get(i);
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();

            // set attributes
            if (i <= pSeeders * nodes.size()) {
                row.setValue(sirCol, SIRType.INFECTED);
                row.setValue(deltaCol, 0);
                infectiousList.add(node);
            } else {
                row.setValue(sirCol, SIRType.SUSCEPTIBLE);
            }
        }

        //
        // 4) run simulation until k population is recovered
        // page4: https://arxiv.org/ftp/arxiv/papers/1403/1403.1011.pdf
        // or tolerance model
        //

        // prepare log
        try {
            File tmp = new File(System.getProperty("user.home") + "/Desktop/sir.txt");
            PrintWriter pw = new PrintWriter(tmp);

            switch (diffusionAlgorithm) {
                case SIR:
                    runSIR(graph, nodes, infectiousList, sirCol, deltaCol);
                    break;
                case SIR_INDIVIDUAL:
                    runSIRForEachNode(graph, nodes, infectiousList, sirCol, deltaCol, kendallCol);
                    break;
                case TOLERANCE:
                    runTolerance(graph, nodes, infectiousList, sirCol, deltaCol);
                    break;
            }

            /*pw.println("Recovered: " + recoveredList.size() + " (" + (100.0 * recoveredList.size() / nodes.size()) + " %)");
             pw.println("Ended after " + iteration + " iterations.");
             pw.println("End condition: " + endCondition.toString());

             pw.println("Infected");
             for (int inf : infCounter) {
             pw.println(inf);
             }
             pw.println("Recovered");
             for (int rec : recCounter) {
             pw.println(rec);
             }
             pw.close();*/
            tmp.deleteOnExit(); // no-log on desktop
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void runSIR(HierarchicalGraph graph, List<Node> nodes, List<Node> infectiousList, AttributeColumn sirCol, AttributeColumn deltaCol) {
        int iteration = 0;
        Random rand = new Random();
        EndCondition endCondition = EndCondition.ITERATIONS;
        List<Node> changeToInfectious = new ArrayList<Node>();
        List<Node> recoveredList = new ArrayList<Node>();

        List<Integer> recCounter = new ArrayList<Integer>();
        List<Integer> infCounter = new ArrayList<Integer>();

        while (++iteration < 10000) {
            // any node infected longer than delta will become recovered
            for (Node node : infectiousList.toArray(new Node[]{})) {
                if (getDeltaInfect(node) >= deltaRecover) {
                    // remove from infectious list
                    infectiousList.remove(node);
                    // set as recovered
                    AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
                    row.setValue(sirCol, SIRType.RECOVERED);
                    if (!recoveredList.contains(node)) {
                        recoveredList.add(node);
                    }
                }
            }

            // check if outbreak died
            if (infectiousList.size() == 0) {
                endCondition = EndCondition.OUTBREAKDIED;
                break;
            }

            // check how many nodes are reocvered
            if (recoveredList.size() >= kPopulation * nodes.size()) {
                endCondition = EndCondition.KPOPULATION;
                break;
            }

            recCounter.add(recoveredList.size());
            infCounter.add(infectiousList.size());

            // infect neighobrs
            changeToInfectious.clear();
            for (Node node : infectiousList) {
                // go throug all neighbors of eahc infected node
                for (Node neighbor : graph.getNeighbors(node)) {
                    // if neighbor is susceptible
                    if (getSIRType(neighbor).equals(SIRType.SUSCEPTIBLE)) {
                        if (rand.nextDouble() < lambdaInfect) {
                            if (!changeToInfectious.contains(neighbor)) {
                                changeToInfectious.add(neighbor);
                            }
                        }
                    }
                }

                // update infectious lifetime
                AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
                int delta = getDeltaInfect(node);
                row.setValue(deltaCol, delta + 1);
            }

            // change nodes to infectious
            for (Node node : changeToInfectious) {
                // set as infected
                AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
                row.setValue(sirCol, SIRType.INFECTED);
                row.setValue(deltaCol, 0);

                if (!infectiousList.contains(node)) {
                    infectiousList.add(node);
                }
            }
        }

        errorReport = "Recovered: " + recoveredList.size() + " (" + (100.0 * recoveredList.size() / nodes.size()) + " %)\n";
        errorReport += "Ended after " + iteration + " iterations\n";
        errorReport += "End condition: " + endCondition.toString() + "\n\n";
//            errorReport += "Infected\n";
//            for (int inf : infCounter) {
//                errorReport += inf;
//            }
        errorReport += "Recovered\n";
        for (int rec : recCounter) {
            errorReport += rec + "\n";
        }
    }

    private void runTolerance(HierarchicalGraph graph, List<Node> nodes, List<Node> stubbornAgents, AttributeColumn sirCol, AttributeColumn deltaCol) {
        int iteration = -1; // due to initial ++ increment
        Random rand = new Random();
        // reactivation interval for nodes
        final int minSleep = 5, maxSleep = 50;
        // tolerance modifications ratio after each interaction	 
        final float epsilon0 = 0.001f, epsilon1 = 0.01f;

        final Map<Node, ExtraNodeData> nodeDataMap = new HashMap<Node, ExtraNodeData>();
        final boolean COMPLEX_DIFFUSION = false; // one friend vs all friends

        // init: attach extra node data to all nodes
        for (Node node : nodes) {
            // default nodes have opinion=0, half tolerance and are non-stubborn
            ExtraNodeData data = new ExtraNodeData(0f, 0.5f, getRandomSleep(rand, minSleep, maxSleep), false);
            nodeDataMap.put(node, data);
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            row.setValue(sirCol, 0);
        }
        for (Node node : stubbornAgents) {
            // spreader nodes have opinion=1 (opposite), irrelevant tolerance and are stubborn
            ExtraNodeData data = new ExtraNodeData(1f, 0f, getRandomSleep(rand, minSleep, maxSleep), true);
            nodeDataMap.put(node, data);
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            row.setValue(sirCol, 1);
        }

        ExtraNodeData nodeData;
        Node[] neighbours;
        Node neighbour;
        float neighbourOpinion, oldOpinion;
        List<Integer> convinced = new ArrayList<Integer>();

        // long-term stop condition (1k)
        while (iteration++ < MAX_ITERATIONS) {
            // iterate nodes
            for (Node node : nodes) {
                // if node is stubborn, then ignore
                nodeData = nodeDataMap.get(node);
                if (nodeData.isStubborn) {
                    // just ignore them, stubborn agents do not change at all
                } else {
                    // if has slept enough
                    if (nodeData.sleep <= 0) {
                        // get list of neighbours
                        neighbours = graph.getNeighbors(node).toArray();

                        if (neighbours.length > 0) {
                            // store average neighborhood opinion / or single neighbour's opinion
                            neighbourOpinion = 0f;
                            // store old opinion of node for later tolerance update
                            oldOpinion = nodeData.opinion;

                            // pick average opinion of all neighbours or or one single random neighobur
                            if (COMPLEX_DIFFUSION) {
                                // iterate through all friends
                                for (Node _neighbour : graph.getNeighbors(node)) {
                                    // get friend's opinion
                                    neighbourOpinion += nodeDataMap.get(_neighbour).opinion;
                                }
                                neighbourOpinion /= (1f * graph.getNeighbors(node).toArray().length);
                            } else {
                                // pick one random friend from the vicinity of the node
                                neighbour = neighbours[rand.nextInt(neighbours.length)];
                                // get friend's opinion
                                neighbourOpinion = nodeDataMap.get(neighbour).opinion;
                            }

                            // update node opinion
                            nodeData.opinion = nodeData.tolerance * neighbourOpinion + (1 - nodeData.tolerance) * nodeData.opinion;
                            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
                            row.setValue(deltaCol, nodeData.getNodeState() ? 1 : 0);

                            ///// update node tolerance
                            // 1) states of interacting nodes is equal => tolerance drops
                            if (nodeData.getNodeState() == nodeData.getNodeState(neighbourOpinion)) {
                                nodeData.tolerance = Math.max(nodeData.tolerance - epsilon0 * nodeData.scaling0, 0);
                            } // 2) states of interacting nodes is different => tolerance increases
                            else {
                                nodeData.tolerance = Math.min(nodeData.tolerance + epsilon1 * nodeData.scaling1, 1);
                            }

                            if (nodeData.getNodeState(oldOpinion) == nodeData.getNodeState()) {
                                nodeData.scaling0++;
                                nodeData.scaling1 = 1;
                            } else {
                                nodeData.scaling0 = 1;
                                nodeData.scaling1++;
                            }
                            ///// end update node tolerance

                        } // node has neighbours
                    }// end node is not sleeping

                    nodeData.sleep--; // decrease sleep

                } // end node change state
            } // end one iteration

            // run poll every 100 iterations
            if (iteration % POLL_FREQUENCY == 0) {
                // measure number of nodes with state == true (>0.5)
                int count = 0;
                for (Node node : nodes) {
                    if (nodeDataMap.get(node).getNodeState()) {
                        count++;
                    }
                }
                convinced.add(count);
            }
        } // end simulation

        errorReport = "Convinced: " + convinced.get(convinced.size() - 1) + " (" + (100.0 * convinced.get(convinced.size() - 1) / nodes.size()) + " %)\n";
        errorReport += "Ended after " + iteration + " iterations\n";
        //errorReport += "End condition: " + endCondition.toString() + "\n\n";
//            errorReport += "Infected\n";
//            for (int inf : infCounter) {
//                errorReport += inf;
//            }
        errorReport += "\nReach evolution:\n\n";
        for (int rec : convinced) {
            errorReport += rec + "\n";
        }
    }

    /**
     * Repeatedly run SIR by assigning each node (regardless of centrality
     * score) as a single spreader in the network. Saves the reach (number of
     * nodes) in TAG_KENDALL_SCORE to be later used to compute Kendall's tau
     */
    private void runSIRForEachNode(HierarchicalGraph graph, List<Node> nodes, List<Node> infectiousList, AttributeColumn sirCol, AttributeColumn deltaCol, AttributeColumn kendallCol) {
        AttributeRow row;

        for (int currentIndex = 0; currentIndex < nodes.size(); ++currentIndex) {

            // set all nodes to susceptible except node 'i' which is infected (single source)
            for (Node node : nodes) {
                row = (AttributeRow) node.getNodeData().getAttributes();
                row.setValue(sirCol, SIRType.SUSCEPTIBLE);
            }

            Node source = nodes.get(currentIndex);
            row = (AttributeRow) source.getNodeData().getAttributes();
            row.setValue(sirCol, SIRType.INFECTED);
            row.setValue(deltaCol, 0);
            infectiousList.clear(); // clean previous
            infectiousList.add(source); // add only this node

            int iteration = 0;
            Random rand = new Random();
            //EndCondition endCondition = EndCondition.ITERATIONS;
            List<Node> changeToInfectious = new ArrayList<Node>();
            List<Node> recoveredList = new ArrayList<Node>();

            while (++iteration < 10) {
                // any node infected longer than delta will become recovered
                for (Node node : infectiousList.toArray(new Node[]{})) {
                    if (getDeltaInfect(node) >= deltaRecover) {
                        // remove from infectious list
                        infectiousList.remove(node);
                        // set as recovered
                        row = (AttributeRow) node.getNodeData().getAttributes();
                        row.setValue(sirCol, SIRType.RECOVERED);
                        if (!recoveredList.contains(node)) {
                            recoveredList.add(node);
                        }
                    }
                }

                // check if outbreak died
                // ... no more

                // check how many nodes are reocvered
                // ... no more            

                // infect neighobrs
                changeToInfectious.clear();
                for (Node node : infectiousList) {
                    // go throug all neighbors of eahc infected node
                    for (Node neighbor : graph.getNeighbors(node)) {
                        // if neighbor is susceptible
                        if (getSIRType(neighbor).equals(SIRType.SUSCEPTIBLE)) {
                            if (rand.nextDouble() < lambdaInfect) {
                                if (!changeToInfectious.contains(neighbor)) {
                                    changeToInfectious.add(neighbor);
                                }
                            }
                        }
                    }

                    // update infectious lifetime
                    row = (AttributeRow) node.getNodeData().getAttributes();
                    int delta = getDeltaInfect(node);
                    row.setValue(deltaCol, delta + 1);
                }

                // change nodes to infectious
                for (Node node : changeToInfectious) {
                    // set as infected
                    row = (AttributeRow) node.getNodeData().getAttributes();
                    row.setValue(sirCol, SIRType.INFECTED);
                    row.setValue(deltaCol, 0);

                    if (!infectiousList.contains(node)) {
                        infectiousList.add(node);
                    }
                }
            } // end sir simulation

            row = (AttributeRow) source.getNodeData().getAttributes();
            row.setValue(kendallCol, recoveredList.size());
        }

        // after simulation, compute Kendall's tau on all nodes       
        int nc = 0, nd = 0, n = nodes.size();
        Double c1, c2; // node centralities
        int k1, k2; // kendall scores for nodes
        double x;

        // compare all pairs of nodes i<j<n
        for (int i = 0; i < nodes.size() - 1; ++i) {
            Node n1 = nodes.get(i);
            for (int j = i + 1; j < nodes.size() - 1; ++j) {
                Node n2 = nodes.get(j);

                c1 = getCentrality(n1);
                c2 = getCentrality(n2);
                k1 = getKendallScore(n1);
                k2 = getKendallScore(n2);

                if ((c1 > c2 && k1 > k2) || (c1 < c2 && k1 < k2)) {
                    nc++; // concordant
                }
                if ((c1 > c2 && k1 < k2) || (c1 < c2 && k1 > k2)) {
                    nd++; // disconcordant
                }
            }
        }

        double kendall = (nc - nd) / (0.5 * n * (n - 1));

        errorReport = "Kendall's tau: " + kendall + " for " + centralityTag + "\n";

    }
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc="Misc Area">
    private String errorReport = "";

    public String getReport() {
        String report = "<HTML> <BODY> <h1>Social Influence Benchmark Report </h1> "
                + "<hr><br>";

        report += errorReport + "</BODY></HTML>";

        return report;
    }

    /**
     *
     * @return
     */
    public boolean cancel() {
        this.isCanceled = true;
        return true;
    }

    private boolean isCanceled() {
        return isCanceled;
    }

    /**
     *
     * @param progressTicket
     */
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }

    public static enum BenchmarkCentrality {

        DEGREE, BETWEENNESS, EIGENVECTOR, CLOSENESS, PAGERANK, HITS, BDPOWER, BDINFLUENCE, HINDEX, CLUSTERRANK, LEADERRANK, LOCALCENTRALITY
    }

    public static enum DiffusionAlgorithm {

        SIR, TOLERANCE, SIR_INDIVIDUAL
    }

    private enum SIRType {

        SUSCEPTIBLE, INFECTED, RECOVERED
    }

    private enum EndCondition {

        KPOPULATION, ITERATIONS, OUTBREAKDIED
    }

    private class ExtraNodeData {

        int sleep = 0;
        float tolerance = 1f;
        float opinion = 0.5f;
        boolean isStubborn = false;
        // tolerance modification scaling used for intolerance (0)	 
        int scaling0 = 1;
        // tolerance modification scaling used for tolerance (1)	 
        int scaling1 = 1;

        ExtraNodeData(float opinion, float tolerance, int sleep, boolean isStubborn) {
            this.opinion = opinion;
            this.tolerance = tolerance;
            this.sleep = sleep;
            this.isStubborn = isStubborn;
        }

        boolean getNodeState() {
            return opinion >= 0.5f;
        }

        boolean getNodeState(float otherState) {
            return otherState >= 0.5f;
        }
    }

    private int getRandomSleep(Random rand, int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }

    private void runDegree(HierarchicalGraph graph, AttributeModel attributeModel) {
        // degree                
        Degree degree = new Degree();
        degree.setProgressTicket(progress);
        degree.execute(graph.getGraphModel(), attributeModel);
        degree.getAverageDegree();
    }

    private void runBetweenness(HierarchicalGraph graph, AttributeModel attributeModel) {
        // betweenness, closeness, bdpower, bdinfluence
        GraphDistance distance = new GraphDistance();
        distance.setNormalized(false);
        distance.setDirected(directed);
        distance.setProgressTicket(progress);
        distance.execute(graph.getGraphModel(), attributeModel);
        distance.getPathLength();
    }

    private void runEigenvector(HierarchicalGraph graph, AttributeModel attributeModel) {
        // eigenvector  
        EigenvectorCentrality eigenvector = new EigenvectorCentrality();
        eigenvector.setDirected(directed);
        eigenvector.setProgressTicket(progress);
        eigenvector.execute(graph.getGraphModel(), attributeModel);
        String ev = eigenvector.getReport();
    }

    private void runPageRank(HierarchicalGraph graph, AttributeModel attributeModel) {
        // pagerank  
        PageRank pagerank = new PageRank();
        pagerank.setDirected(directed);
        pagerank.setUseEdgeWeight(true);
        pagerank.setProgressTicket(progress);
        pagerank.execute(graph.getGraphModel(), attributeModel);
        String pr = pagerank.getReport();
    }

    private void runHits(HierarchicalGraph graph, AttributeModel attributeModel) {
        // hits
        Hits hits = new Hits();
        hits.setProgressTicket(progress);
        hits.execute(graph.getGraphModel(), attributeModel);
        String hi = hits.getReport();
    }
    // </editor-fold>
}
