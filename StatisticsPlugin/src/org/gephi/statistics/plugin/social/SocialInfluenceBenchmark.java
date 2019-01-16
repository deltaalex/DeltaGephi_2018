package org.gephi.statistics.plugin.social;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import org.gephi.statistics.plugin.*;
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
    public static final String TAG_OPINION = "Opinion";
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
    private DiffusionAlgorithm diffusionAlgorithm = DiffusionAlgorithm.TOLERANCE_DEPLETE;
    /**
     * Stop condition for diffusion processes
     */
    private final int MAX_ITERATIONS = 2000;
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
    private double pSeeders = 0.0003; // .0001, .0003, .001, .003, .01
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

    public void setSeeders(double pSeeders) {
        this.pSeeders = pSeeders;
    }

    public BenchmarkCentrality getCentrality() {
        return centrality;
    }

    public double getSeeders() {
        return pSeeders;
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

        //final int REPEAT = 10;
        //for (int i = 0; i < REPEAT; ++i) { // dbg: repeat and average iterations and coverage
        execute(graph, attributeModel);
        //errorReport = "Average coverage: " + 1f * _coverage / REPEAT;
        //errorReport += "\nAverage time: " + 1f * _iterations / REPEAT;
        //}
    }

    public void execute(HierarchicalGraph graph, AttributeModel attributeModel) {

        // atributes
        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn sirCol = nodeTable.getColumn(TAG_SIR_STATUS); // infected or stubborn agent(=1)
        AttributeColumn deltaCol = nodeTable.getColumn(TAG_DELTA_INFECT);
        AttributeColumn kendallCol = nodeTable.getColumn(TAG_KENDALL_SCORE);
        AttributeColumn opinionCol = nodeTable.getColumn(TAG_OPINION); // opinion in tolerance model; <0.5->A, >=0.5->B
        if (sirCol == null) {
            sirCol = nodeTable.addColumn(TAG_SIR_STATUS, NbBundle.getMessage(SocialInfluenceBenchmark.class, "SocialInfluenceBenchmark.nodecolumn.SirStatus"), AttributeType.INT, AttributeOrigin.COMPUTED, new Integer(0));
        }
        if (deltaCol == null) {
            deltaCol = nodeTable.addColumn(TAG_DELTA_INFECT, NbBundle.getMessage(SocialInfluenceBenchmark.class, "SocialInfluenceBenchmark.nodecolumn.DeltaInfect"), AttributeType.INT, AttributeOrigin.COMPUTED, new Integer(0));
        }
        if (kendallCol == null) {
            kendallCol = nodeTable.addColumn(TAG_KENDALL_SCORE, NbBundle.getMessage(SocialInfluenceBenchmark.class, "SocialInfluenceBenchmark.nodecolumn.KendallScore"), AttributeType.INT, AttributeOrigin.COMPUTED, new Integer(0));
        }
        if (opinionCol == null) {
            opinionCol = nodeTable.addColumn(TAG_OPINION, NbBundle.getMessage(SocialInfluenceBenchmark.class, "SocialInfluenceBenchmark.nodecolumn.Opinion"), AttributeType.FLOAT, AttributeOrigin.COMPUTED, new Float(0f));
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
        centralityTag = getCentralityTag(centrality); // ignored & hardcoded for TOLERANCE_COMPETE

        // check centrality was already run
        if (nodes.get(0).getAttributes().getValue(centralityTag) == null) {
            throw new IllegalStateException("You must first run " + centralityTag.toString() + " centrality on the graph.");
        }

        //
        // 2) sort nodes by centrality
        //      

        //sortRandom(nodes);
        sortByCentrality(nodes, centralityTag);

        //
        // 3) infect top pSeeders% nodes
        //                
        List<Node> infectiousList = new ArrayList<Node>();

        if (!diffusionAlgorithm.equals(DiffusionAlgorithm.TOLERANCE_COMPETE)) {
            initNodes(nodes, infectiousList, sirCol, deltaCol);
        } else {
            List<Node> infectiousListA = new ArrayList<Node>();
            List<Node> infectiousListB = new ArrayList<Node>();

            centralityTag = getCentralityTag(BenchmarkCentrality.LEADERRANK);  // dbg  
            sortByCentrality(nodes, centralityTag);
            initNodesByColoring(graph, nodes, infectiousListA, sirCol, deltaCol);

            centralityTag = getCentralityTag(BenchmarkCentrality.BETWEENNESS);  // dbg  
            sortByCentrality(nodes, centralityTag);
            initNodesByColoring(graph, nodes, infectiousListB, sirCol, deltaCol);

            infectiousList = mergeOpinions(infectiousListA, infectiousListB);
        }

        //
        // 4) run simulation until k population is recovered
        // page4: https://arxiv.org/ftp/arxiv/papers/1403/1403.1011.pdf
        // or tolerance model
        //

        // prepare log        
        try {
            File outputFolder = new File(System.getProperty("user.home") + "/Desktop/tolerance");
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }
            File tmp = new File(outputFolder.getAbsolutePath() + "/tolerance_benchmark" + new Date().getTime() + ".csv");
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
                case TOLERANCE_DEPLETE:
                    runToleranceDeplete(graph, nodes, infectiousList, sirCol, deltaCol, pw);
                    break;
                case TOLERANCE_COMPETE:
                    runToleranceCompete(graph, nodes, infectiousList, sirCol, opinionCol);
                    break;
            }

//            pw.println("Recovered: " + recoveredList.size() + " (" + (100.0 * recoveredList.size() / nodes.size()) + " %)");
//             pw.println("Ended after " + iteration + " iterations.");
//             pw.println("End condition: " + endCondition.toString());
//
//             pw.println("Infected");
//             for (int inf : infCounter) {
//             pw.println(inf);
//             }
//             pw.println("Recovered");
//             for (int rec : recCounter) {
//             pw.println(rec);
//             }
//             pw.close();

//            for (Node inf : infectiousList) {
//                pw.println(inf.getId());
//            }

            /*BenchmarkCentrality[] centralities = {BenchmarkCentrality.DEGREE, BenchmarkCentrality.CLOSENESS, BenchmarkCentrality.BETWEENNESS, BenchmarkCentrality.HITS, BenchmarkCentrality.PAGERANK, BenchmarkCentrality.HINDEX, BenchmarkCentrality.LEADERRANK, BenchmarkCentrality.KSHELL, BenchmarkCentrality.LOCALCENTRALITY, BenchmarkCentrality.EIGENVECTOR};
             for (BenchmarkCentrality c1 : centralities) {
             for (BenchmarkCentrality c2 : centralities) {
             if (c1.equals(c2)) {
             continue;
             }
             if (!c1.equals(BenchmarkCentrality.KSHELL) && !c2.equals(BenchmarkCentrality.KSHELL)) {
             continue;
             }

             if (diffusionAlgorithm.equals(DiffusionAlgorithm.TOLERANCE_COMPETE)) {
             List<Node> infectiousListA = new ArrayList<Node>();
             List<Node> infectiousListB = new ArrayList<Node>();

             centralityTag = getCentralityTag(c1);  // dbg  
             sortByCentrality(nodes, centralityTag);
             initNodes(nodes, infectiousListA, sirCol, deltaCol);
             pw.print(centralityTag + "-");

             centralityTag = getCentralityTag(c2);  // dbg  
             sortByCentrality(nodes, centralityTag);
             initNodes(nodes, infectiousListB, sirCol, deltaCol);
             pw.print(centralityTag + ":");

             infectiousList = mergeOpinions(infectiousListA, infectiousListB);

             runToleranceCompete(graph, nodes, infectiousList, sirCol, opinionCol);
             pw.print(shortReport);
             pw.println();
             }
             }
             }*/
            pw.close();

            //tmp.deleteOnExit(); // no-log on desktop
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }

        progress.switchToDeterminate(100);
        progress.finish();

        graph.readUnlockAll();
    }
    private float _iterations = 0f, _coverage = 0f; // dbg

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
                // go throug all neighbors of each infected node
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

        errorReport += "Recovered\n";
        for (int rec : recCounter) {
            errorReport += rec + "\n";
        }

        _iterations += iteration;
        _coverage += (100.0 * recoveredList.size() / nodes.size());
    }

    // runs the classic tolerance model with either single (one random neighbour) or complex (average all neighbours) diffusion
    // and updates tolerance according to opinjon change patterns
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
            ExtraNodeData data = new ExtraNodeData(0f, 1f, getRandomSleep(rand, minSleep, maxSleep), false);
            nodeDataMap.put(node, data);
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            row.setValue(sirCol, 0);
        }
        for (Node node : stubbornAgents) {
            // spreader nodes always have opinion = 0 or 1 (opposite), irrelevant tolerance and are stubborn
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
                            //row.setValue(deltaCol, nodeData.opinion);

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

    // Runs the tolerance model with one single opinion and default depletion (for regular agents)
    // Now, in complex mode a regular node will increase its opinion by +0.151 if the average neighbor opinioon is higher than his,
    // else the node will decrease his opinion by -0.155, i.e. slightly faster decrease (by 2.6%)
    private void runToleranceDeplete(HierarchicalGraph graph, List<Node> nodes, List<Node> stubbornAgents, AttributeColumn sirCol, AttributeColumn deltaCol, PrintWriter pw) {
        int iteration = 0; // due to initial ++ increment
        Random rand = new Random();
        final float IGNORE = 1f; // tolerance is ignored in this model
        // reactivation interval for nodes
        final int minSleep = 1, maxSleep = 10;
        // tolerance modifications ratio after each interaction	 
        final float epsilon0 = 0.001f, epsilon1 = 0.01f;
        // opinion modifications ratio after each interaction	 
        final float omega0 = 0.155f, omega1 = 0.151f;
        // period and fill factor
        final int INJECT_PERIOD = 200;
        final float FILLING_FACTOR = 1f; // i.e, will be active the first 50 iterations of a 100 period        

        final Map<Node, ExtraNodeData> nodeDataMap = new HashMap<Node, ExtraNodeData>();
        final boolean COMPLEX_DIFFUSION = false; // one friend vs all friends

        // init: attach extra node data to all nodes
        for (Node node : nodes) {
            // default nodes have opinion=0, and are non-stubborn; -999 = ignore
            ExtraNodeData data = new ExtraNodeData(0f, IGNORE, getRandomSleep(rand, minSleep, maxSleep), false);
            nodeDataMap.put(node, data);
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            row.setValue(sirCol, 0);
        }
        for (Node node : stubbornAgents) {
            // spreader nodes always have opinion = 1 (fully induced), irrelevant tolerance and are stubborn
            ExtraNodeData data = new ExtraNodeData(1f, IGNORE, getRandomSleep(rand, minSleep, maxSleep), true);
            nodeDataMap.put(node, data);
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            row.setValue(sirCol, 1);
        }

        ExtraNodeData nodeData;
        Node[] neighbours;
        Node neighbour;
        float neighbourOpinion;
        List<Integer> convinced = new ArrayList<Integer>();

        // long-term stop condition (2k)
        while (iteration < MAX_ITERATIONS) {
            // stubborn agents activation            
            if ((iteration - (int) (INJECT_PERIOD * FILLING_FACTOR)) % (int) (INJECT_PERIOD) == 0) { // set inactive and let opinion drop
                for (Node node : stubbornAgents) {
                    nodeDataMap.get(node).isStubborn = false; // deactivate stubborn agents
                }
            }
            if (iteration % (int) (INJECT_PERIOD) == 0) { // set active & opinion = 1
                for (Node node : stubbornAgents) {
                    nodeDataMap.get(node).isStubborn = true; // activate stubborn agents
                    nodeDataMap.get(node).opinion = 1f;
                }
            }

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
                            if (neighbourOpinion > nodeData.opinion) {
                                nodeData.opinion += omega1; // +0.151
                            } else {
                                nodeData.opinion -= omega0; // -0.155
                            }
                            nodeData.opinion = Math.min(1f, Math.max(0f, nodeData.opinion)); // keep within [0,1]

                            //nodeData.opinion = nodeData.tolerance * neighbourOpinion + (1 - nodeData.tolerance) * nodeData.opinion;
                            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
                            row.setValue(deltaCol, nodeData.getNodeState() ? 1 : 0);
                            //row.setValue(deltaCol, nodeData.opinion);                          

                        } // node has neighbours
                    }// end node is not sleeping

                    nodeData.sleep--; // decrease sleep

                } // end node change state
            } // end one iteration

            // run poll every 100 iterations
            //if (iteration % POLL_FREQUENCY == 0) {
            // measure number of nodes with state == true (>0.5)
            int count = 0;
            for (Node node : nodes) {
                if (nodeDataMap.get(node).getNodeState()) {
                    count++;
                }
            }
            convinced.add(count);
            //}
            iteration++; // next 'day'
        } // end simulation

        errorReport = "Stubborn agents count: " + stubbornAgents.size() + "\n";
        errorReport += "Indoctrinated count: " + convinced.get(convinced.size() - 1) + " (" + (100.0 * convinced.get(convinced.size() - 1) / nodes.size()) + " %)\n";
        errorReport += "Ended after " + iteration + " iterations\n";

        errorReport += "\nReach evolution:\n\n";
        for (int rec : convinced) {
            pw.println(rec);
            //errorReport += rec + "\n";
        }
    }

    private void runToleranceCompete(HierarchicalGraph graph, List<Node> nodes, List<Node> stubbornAgents, AttributeColumn sirCol, AttributeColumn deltaCol) {
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
            ExtraNodeData data = new ExtraNodeData(rand.nextFloat(), 1f, getRandomSleep(rand, minSleep, maxSleep), false);
            nodeDataMap.put(node, data);
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            row.setValue(sirCol, 0);
        }
        int _s = 0;
        for (Node node : stubbornAgents) {
            // spreader nodes always have opinion = 0 or 1 (opposite), tolerance=0 always, and are stubborn
            ExtraNodeData data = new ExtraNodeData((_s % 2 == 0) ? 0f : 1f, 0f, getRandomSleep(rand, minSleep, maxSleep), true);
            nodeDataMap.put(node, data);
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            row.setValue(sirCol, 1);
            _s++;
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
                    AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
                    row.setValue(deltaCol, nodeData.opinion);
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
                            //row.setValue(deltaCol, nodeData.getNodeState() ? 1 : 0);
                            row.setValue(deltaCol, nodeData.opinion);

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
        shortReport = "\t\t\t" + (100.0 * convinced.get(convinced.size() - 1) / nodes.size()) + " %\n";
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

            while (++iteration < 15) {
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
    private String shortReport = "";

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

        DEGREE, BETWEENNESS, EIGENVECTOR, CLOSENESS, PAGERANK, HITS, BDPOWER, BDINFLUENCE, HINDEX, CLUSTERRANK, LEADERRANK, LOCALCENTRALITY, KSHELL
    }

    public static enum DiffusionAlgorithm {

        SIR, TOLERANCE, SIR_INDIVIDUAL, TOLERANCE_DEPLETE, TOLERANCE_COMPETE
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

    private String getCentralityTag(BenchmarkCentrality centrality) {
        switch (centrality) {
            case DEGREE:
                return Degree.DEGREE;
            case BETWEENNESS:
                //runBetweenness(graph, attributeModel);
                return GraphDistance.BETWEENNESS;
            case EIGENVECTOR:
                //runEigenvector(graph, attributeModel);
                return EigenvectorCentrality.EIGENVECTOR;
            case CLOSENESS:
                //runBetweenness(graph, attributeModel);
                return GraphDistance.CLOSENESS;
            case PAGERANK:
                //runPageRank(graph, attributeModel);
                return PageRank.PAGERANK;
            case HITS:
                //runHits(graph, attributeModel);
                return Hits.AUTHORITY;
            case BDPOWER:
                //runBetweenness(graph, attributeModel);
                return GraphDistance.B_TIMES_D_POWER;
            case BDINFLUENCE:
                //runBetweenness(graph, attributeModel);
                return GraphDistance.B_PER_D_POWER;
            case HINDEX:
                return InfluenceRankings.HINDEX;
            case CLUSTERRANK:
                return InfluenceRankings.CLUSTERRANK;
            case LEADERRANK:
                return InfluenceRankings.LEADERRANK;
            case LOCALCENTRALITY:
                return InfluenceRankings.LOCALCENTRALITY;
            case KSHELL:
                return InfluenceRankings.KSHELL;
            default:
                return null;
        }
    }

    private void sortByCentrality(List<Node> nodes, final String centralityTag) {
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
                    if (c1 != null && c2 != null) {
                        return c2.compareTo(c1);
                    } else {
                        return 0;
                    }
                }
            }
        });
    }

    private void sortRandom(List<Node> nodes) {
        final Random rand = new Random();
        Collections.sort(nodes, new Comparator<Node>() {
            public int compare(Node n1, Node n2) {
                return rand.nextInt(3) - 1; //[-1,0,1]
            }
        });
    }

    /**
     * Selects top 'pSeeders' sorted nodes as seeders
     *
     * @param nodes - the whole graph; sorted by a centralityTag
     * @param infectiousList - empty list to be populated with selected seeders
     * @param sirCol - marks seeders with 'INFECTED' status (top nodes)
     * @param deltaCol - holds seeder specific information (e.g. lifetime,
     * opinion)
     */
    private void initNodes(List<Node> nodes, List<Node> infectiousList, AttributeColumn sirCol, AttributeColumn deltaCol) {
        for (int i = 0; i < nodes.size(); ++i) {
            Node node = nodes.get(i);
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();

            // set attributes for top seeder nodes
            if (i < Math.ceil(pSeeders * nodes.size())) {
                row.setValue(sirCol, SIRType.INFECTED);
                row.setValue(deltaCol, 0);
                infectiousList.add(node);
            } else {
                row.setValue(sirCol, SIRType.SUSCEPTIBLE);
            }
        }
    }

    // Welsh-Powell coloring algorithm
    // http://graphstream-project.org/doc/Algorithms/Welsh-Powell/
    /* Step 1: All vertices are sorted according to the decreasing value of their degree in a list V.
     Step 2: Colors are ordered in a list C.
     Step 3: The first non colored vertex v in V is colored with the first available color in C. Available means a color that was not previously used by the algorithm.
     Step 4: The remaining part of the ordered list V is traversed and the same color is allocated to every vertex for which no adjacent vertex has the same color.
     Step 5: Steps 3 and 4 are applied iteratively until all the vertices have been colored.
     */
    private void initNodesByColoring(HierarchicalGraph graph, List<Node> nodes, List<Node> infectiousList, AttributeColumn sirCol, AttributeColumn deltaCol) {
        int color = 0;
        int counter = 0; // counts how many nodes were colored 
        AttributeRow row;
        Map<Integer, List<Node>> colored = new HashMap<Integer, List<Node>>();
        colored.put(color, new ArrayList<Node>());

        while (counter < nodes.size()) {
            for (int i = 0; i < nodes.size(); ++i) {
                Node node = nodes.get(i);

                // if node is already colored then skip it
                if (colored.get(color).contains(node)) {
                    continue;
                }

                // if node is adjacent to a colored node with the current color then skip it
                boolean hasAdjacentNode = false;
                for (Node coloredNode : colored.get(color).toArray(new Node[]{})) {
                    if (graph.isAdjacent(node, coloredNode) || graph.isAdjacent(coloredNode, node)) {
                        hasAdjacentNode = true;
                    }
                }
                if (!hasAdjacentNode) {
                    colored.get(color).add(node);
                    counter++;
                }
            }
            color++;
            colored.put(color, new ArrayList<Node>());
        }

        // select seeders from top color set (0) [hardcoded]
        // VERY DANGEROUS CODE BELOW
        for (int i = 0; i < colored.get(0).size(); ++i) {
            Node node = colored.get(0).get(i);
            row = (AttributeRow) node.getNodeData().getAttributes();

            // set attributes for top seeder nodes
            if (i < Math.ceil(pSeeders * nodes.size())) {
                row.setValue(sirCol, SIRType.INFECTED);
                row.setValue(deltaCol, 0);
                infectiousList.add(node);
            } else {
                row.setValue(sirCol, SIRType.SUSCEPTIBLE);
            }
        }
    }

    /**
     * Merge stubborn agent sets A and B by assigning one list element from A,
     * followed by one from B, and so on, alternatively. After each element is
     * inserted, the other list is cleared of the other node, if it exists.
     *
     * @param infectiousListA
     * @param infectiousListB
     * @return
     */
    private List<Node> mergeOpinions(List<Node> infectiousListA, List<Node> infectiousListB) {
        List<Node> infectiousList = new ArrayList<Node>();
        Node candidate;
        int cA = 0, cB = 0;

        while (infectiousListA.size() > 0 && infectiousListB.size() > 0) {
            if (infectiousListA.size() > 0) {
                // move first node from A to infectious list
                candidate = infectiousListA.get(0);
                infectiousList.add(candidate);
                // remove from top of list
                infectiousListA.remove(0);
                // and remove it from the other ranking method list (B)
                removeNode(infectiousListB, candidate);
                cA++;
            }
            if (infectiousListB.size() > 0) {
                // move first node from A to infectious list
                candidate = infectiousListB.get(0);
                infectiousList.add(candidate);
                // remove from top of list
                infectiousListB.remove(0);
                // and remove it from the other ranking method list (B)
                removeNode(infectiousListA, candidate);
                cB++;
            }
        }

        return infectiousList;
    }

    private void removeNode(List<Node> nodes, Node toRemove) {
        Node _node = null;
        for (Node node : nodes) {
            if (toRemove.getId() == node.getId()) {
                _node = node;
                break;
            }
        }

        if (_node != null) {
            nodes.remove(_node);
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
