package org.gephi.statistics.plugin.social;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.statistics.plugin.ClusteringCoefficient;
import org.gephi.statistics.plugin.ConnectedComponents;
import org.gephi.statistics.plugin.Degree;
import org.gephi.statistics.plugin.EigenvectorCentrality;
import org.gephi.statistics.plugin.GraphDistance;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;

/**
 * Given a topology G=(V,E), edges are removed at every iteration, and some
 * edges are recreated. In time, the network resilience to attack is measured by
 * monitoring: the GC size, components number, diameter as f(density). Attacks
 * (edge removal): random, highest centrality (%) - m edges are removed
 * Resilience (edge recreation): less than m edges are recreated based on:
 * random, highest degree first, lowest degree first. Output: Gc size, nr GC,
 * (diameter).
 *
 * @author Alexander
 */
public class NetworkRobustness implements Statistics, LongTask {

    /**
     * Defines type of attack on edges: either by picking random edges, or based
     * on adjacent node centrality.
     */
    private ATTACK_TYPE attackType = ATTACK_TYPE.RANDOM;
    /**
     * Defines repair strategy for adding new edges to: random nodes, or based
     * on highest degree, or lowest degree first.
     */
    private REPAIR_TYPE repairType = REPAIR_TYPE.NONE;
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
    /**
     * Stop condition for simulation
     */
    private int maxIterations = 100;
    /**
     * Percentage of destroyed edges per iteration
     */
    private double attackRatio = 0.05;
    /**
     * Percentage of recreated edges, based on the number of removed edges, per
     * iteration
     */
    private double repairRatio = 0.25;
    private Random rand;

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">         
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public void setAttackType(int index) {
        this.attackType = ATTACK_TYPE.values()[index];
    }

    public void setRepairType(int index) {
        this.repairType = REPAIR_TYPE.values()[index];
    }

    public void setAttackRatio(double attackRatio) {
        this.attackRatio = attackRatio;
    }

    public void setRepairRatio(double repairRatio) {
        this.repairRatio = repairRatio;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public int getAttackType() {
        return attackType.ordinal();
    }

    public int getRepairType() {
        return repairType.ordinal();
    }

    public double getAttackRatio() {
        return attackRatio;
    }

    public double getRepairRatio() {
        return repairRatio;
    }

    // </editor-fold> 
    // <editor-fold defaultstate="collapsed" desc="Execution">
    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
        execute(graph, attributeModel);
    }

    public void execute(HierarchicalGraph graph, AttributeModel attributeModel) {

        // list of nodes and edges      
        List<Node> nodes = new ArrayList<Node>();
        for (Node node : graph.getNodes()) {
            nodes.add(node);
        }
        //List<Edge> edges = new ArrayList<Edge>();
        //for (Edge edge : graph.getEdges()) {
        //edges.add(edge);
        //}
        //int N = nodes.size();
        //int E = edges.size();
        rand = new Random();

        //graph.readLock();
        Progress.start(progress);
        progress.switchToIndeterminate();

        //
        // simulation: attack, then repair
        //        

        List<Edge> edgesToRemove = new ArrayList<Edge>();
        List<Node> affectedNodes = new ArrayList<Node>();

        try {
            File tmp = new File(System.getProperty("user.home") + "/Desktop/robustness.csv");
            PrintWriter pw = new PrintWriter(tmp);
            pw.println("removedEdges,gcSize,numCC");

            for (int t = 0; t < maxIterations; ++t) {

                // 1) attack

                switch (attackType) {
                    case RANDOM:
                        edgesToRemove = pickEdgesAtRandom(graph, attributeModel);
                        break;
                    default:
                        edgesToRemove = pickEdgesByCentrality(graph, attributeModel);
                        break;
                }

                // remove edges
                affectedNodes = affectNodesByEdgeRemoval(graph, edgesToRemove);
                for (Edge edge : edgesToRemove) {
                    graph.removeEdge(edge);
                }

                // 2) repair

                switch (repairType) {
                    case RANDOM:
                        affectedNodes = pickNodesAtRandom(affectedNodes, (int) (edgesToRemove.size() * repairRatio));
                        break;
                    case HIGHEST_DEGREE_FIRST:
                        affectedNodes = pickNodesByDegree(affectedNodes, (int) (edgesToRemove.size() * repairRatio), true);
                        break;
                    case LOWEST_DEGREE_FIRST:
                        affectedNodes = pickNodesByDegree(affectedNodes, (int) (edgesToRemove.size() * repairRatio), false);
                        break;
                    case NONE:
                        break;
                }

                // add one edge to each affected node
                if (!repairType.equals(REPAIR_TYPE.NONE)) {
                    switch (attackType) {
                        case RANDOM:
                            addNewEdgesAtRandom(graph, nodes, affectedNodes);
                            break;
                        default:
                            addNewEdgesByCentrality(graph, nodes, affectedNodes);
                            break;
                    }
                }

                // )3 measure impact: GC, nr components

                // run CC on graph
                ConnectedComponents components = runConnectedComponents(graph, attributeModel);
                // count components
                int numComponents = components.getConnectedComponentsCount();
                int gcIndex = components.getGiantComponent();
                int gcSize = 0;
                for (Node node : graph.getNodes()) {
                    if ((Integer) node.getAttributes().getValue(ConnectedComponents.WEAKLY) == gcIndex) {
                        gcSize++;
                    }
                }

                //errorReport += edgesToRemove.size() /*+ " [" + (1.0 * edgesToRemove.size() / graph.getTotalEdgeCount()) + "]\n"*/ + "\n";
                errorReport += gcSize + "\n";
                pw.println(edgesToRemove.size() + "," + gcSize + "," + numComponents);
            }

            pw.close();
            //tmp.deleteOnExit(); // no-log on desktop
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }

        progress.switchToDeterminate(100);
        progress.finish();
        graph.readUnlockAll();
    }

    private List<Edge> pickEdgesAtRandom(HierarchicalGraph graph, AttributeModel attributeModel) {
        List<Edge> removedEdges = new ArrayList<Edge>();
        double p;

        for (Edge edge : graph.getEdges()) {
            p = rand.nextDouble();
            // attack succeded            
            if (p < attackRatio) {
                removedEdges.add(edge);
            }
        }

        return removedEdges;
    }

    private List<Node> pickNodesAtRandom(List<Node> nodes, int nodesToKeep) {
        List<Node> affectedNodes = new ArrayList<Node>();
        Node node;

        if (nodes.size() <= nodesToKeep) {
            return nodes;
        } else {
            while (affectedNodes.size() < nodesToKeep) {
                node = nodes.get(rand.nextInt(nodes.size()));
                affectedNodes.add(node);
                nodes.remove(node);
            }

            return affectedNodes;
        }
    }

    private List<Node> pickNodesByDegree(List<Node> nodes, int nodesToKeep, final boolean highDegreeFirst) {
        List<Node> affectedNodes = new ArrayList<Node>();

        Collections.sort(nodes, new Comparator<Node>() {
            // sort by node degree
            public int compare(Node n1, Node n2) {
                if (highDegreeFirst) {
                    return -1 * ((Integer) (n1.getAttributes().getValue(Degree.DEGREE)))
                            .compareTo((Integer) (n2.getAttributes().getValue(Degree.DEGREE)));
                } else {
                    return +1 * ((Integer) (n1.getAttributes().getValue(Degree.DEGREE)))
                            .compareTo((Integer) (n2.getAttributes().getValue(Degree.DEGREE)));
                }
            }
        });

        // add first nodes to affected list
        for (int i = 0; i < nodesToKeep && i < nodes.size(); ++i) {
            affectedNodes.add(nodes.get(i));
        }

        return affectedNodes;
    }

    private List<Edge> pickEdgesByCentrality(HierarchicalGraph graph, AttributeModel attributeModel) {
        List<Edge> removedEdges = new ArrayList<Edge>();
        Map<Edge, ExtraEdgeData> map = new HashMap<Edge, ExtraEdgeData>();
        double fitness = 0.0, totalFitness = 0.0, p;
        Node node1, node2;

        List<Edge> edges = new ArrayList<Edge>();
        for (Edge edge : graph.getEdges()) {
            edges.add(edge);
        }
        final double attackConstant = attackRatio * edges.size();

        // 1. compute centrality of each edge
        for (Edge edge : edges) {
            node1 = edge.getSource();
            node2 = edge.getTarget();

            switch (attackType) {
                case DEGREE:
                    // compute average degree of adjacent nodes
                    fitness = (Integer) (node1.getAttributes().getValue(Degree.DEGREE)) + (Integer) (node2.getAttributes().getValue(Degree.DEGREE));
                    fitness /= 2.0;
                    break;
                case BETWEENNESS:
                    // compute average betweenness of adjacent nodes
                    fitness = (Double) (node1.getAttributes().getValue(GraphDistance.BETWEENNESS)) + (Double) (node2.getAttributes().getValue(GraphDistance.BETWEENNESS));
                    fitness /= 2.0;
                    break;
                case CLUSTERING:
                    // compute average clustering coefficient of adjacent nodes
                    fitness = (Double) (node1.getAttributes().getValue(ClusteringCoefficient.CLUSTERING_COEFF)) + (Double) (node2.getAttributes().getValue(ClusteringCoefficient.CLUSTERING_COEFF));
                    fitness /= 2.0;
                    break;
                case EIGENVECTOR:
                    // compute average eigenvector centrality of adjacent nodes
                    fitness = (Double) (node1.getAttributes().getValue(EigenvectorCentrality.EIGENVECTOR)) + (Double) (node2.getAttributes().getValue(EigenvectorCentrality.EIGENVECTOR));
                    fitness /= 2.0;
                    break;
            }

            totalFitness += fitness;
            map.put(edge, new ExtraEdgeData(fitness));
        }

        // 2. sort in descending order
        sortEdgesByCentrality(edges, map, true);

        // 3. remove edges with probabilty
        for (Edge edge : edges) {
            p = rand.nextDouble();
            // attack succeded            
            // average removal = 1/iteration
            // we need a% => (a%*E) / iteration
            if (p < attackConstant * map.get(edge).fitness / totalFitness) {
                removedEdges.add(edge);
            }

            // stop attacking once attackRation is reached
            if (removedEdges.size() / edges.size() > attackRatio) {
                break;
            }
        }

        map.clear();
        map = null;
        return removedEdges;
    }

    private void sortEdgesByCentrality(List<Edge> edges, final Map<Edge, ExtraEdgeData> map, final boolean descending) {
        Collections.sort(edges, new Comparator<Edge>() {
            // sort by used centrality
            public int compare(Edge e1, Edge e2) {
                if (descending) {
                    return -1 * map.get(e1).fitness.compareTo(map.get(e2).fitness);
                } else {
                    return +1 * map.get(e1).fitness.compareTo(map.get(e2).fitness);
                }
            }
        });
    }

    private List<Node> affectNodesByEdgeRemoval(HierarchicalGraph graph, List<Edge> edgesToRemove) {
        List<Node> affectedNodes = new ArrayList<Node>();

        for (Edge edge : edgesToRemove) {
            affectedNodes.add(edge.getSource());
            affectedNodes.add(edge.getTarget());
        }

        // remove duplicate nodes
        Set<Node> hs = new HashSet<Node>();
        hs.addAll(affectedNodes);
        affectedNodes.clear();
        affectedNodes.addAll(hs);

        return affectedNodes;
    }

    private ConnectedComponents runConnectedComponents(HierarchicalGraph graph, AttributeModel attributeModel) {
        // pagerank  
        ConnectedComponents cc = new ConnectedComponents();
        cc.setDirected(directed);
        cc.setProgressTicket(progress);
        cc.execute(graph.getGraphModel(), attributeModel);
        return cc;
    }

    private void addNewEdgesAtRandom(HierarchicalGraph graph, List<Node> allNodes, List<Node> affectedNodes) {
        Node otherNode;

        // add random edge for each affected node
        for (Node node : affectedNodes) {
            // pick random partner
            otherNode = allNodes.get(rand.nextInt(allNodes.size()));
            // avoid self loops and duplicate edges
            while (otherNode.equals(node) || graph.isAdjacent(node, otherNode)) {
                otherNode = allNodes.get(rand.nextInt(allNodes.size()));
            }

            // create new edge            
            graph.addEdge(graph.getGraphModel().factory().newEdge(node, otherNode));
        }
    }

    private void addNewEdgesByCentrality(HierarchicalGraph graph, List<Node> allNodes, List<Node> affectedNodes) {
        Map<Node, ExtraEdgeData> map = new HashMap<Node, ExtraEdgeData>();
        double fitness = 0, totalFitness = 0.0;

        // sort all nodes by descending centrality
        Collections.sort(allNodes, new Comparator<Node>() {
            public int compare(Node n1, Node n2) {
                switch (attackType) {
                    case DEGREE:
                        return -1 * ((Integer) (n1.getAttributes().getValue(Degree.DEGREE)))
                                .compareTo(((Integer) (n2.getAttributes().getValue(Degree.DEGREE))));
                    case BETWEENNESS:
                        return -1 * ((Double) (n1.getAttributes().getValue(GraphDistance.BETWEENNESS)))
                                .compareTo(((Double) (n2.getAttributes().getValue(GraphDistance.BETWEENNESS))));
                    case CLUSTERING:
                        return -1 * ((Double) (n1.getAttributes().getValue(ClusteringCoefficient.CLUSTERING_COEFF)))
                                .compareTo(((Double) (n2.getAttributes().getValue(ClusteringCoefficient.CLUSTERING_COEFF))));
                    case EIGENVECTOR:
                        return -1 * ((Double) (n1.getAttributes().getValue(EigenvectorCentrality.EIGENVECTOR)))
                                .compareTo(((Double) (n2.getAttributes().getValue(EigenvectorCentrality.EIGENVECTOR))));
                    default:
                        return 0;
                }
            }
        });

        // compute fitness of all nodes based on attack centrality
        for (Node node : allNodes) {
            switch (attackType) {
                case DEGREE:
                    fitness = (Integer) node.getAttributes().getValue(Degree.DEGREE) + 0.0;
                    break;
                case BETWEENNESS:
                    fitness = (Double) node.getAttributes().getValue(GraphDistance.BETWEENNESS);
                    break;
                case CLUSTERING:
                    fitness = (Double) node.getAttributes().getValue(ClusteringCoefficient.CLUSTERING_COEFF);
                    break;
                case EIGENVECTOR:
                    fitness = (Double) node.getAttributes().getValue(EigenvectorCentrality.EIGENVECTOR);
                    break;
            }

            totalFitness += fitness;
            map.put(node, new ExtraEdgeData(fitness));
        }


        // for each node in list, add one single edge to first matching target node, 
        // based on target centrality
        boolean success;
        for (Node node : affectedNodes) {
            // repeat for each node until success
            success = false;
            while (!success) {
                for (Node otherNode : allNodes) {
                    // skip self and existing edges
                    if (node.equals(otherNode) || graph.isAdjacent(node, otherNode)) {
                        continue;
                    }

                    // create edge
                    if (rand.nextDouble() < map.get(otherNode).fitness / totalFitness) {
                        graph.addEdge(graph.getGraphModel().factory().newEdge(node, otherNode));
                        success = true;
                        break;
                    }
                }
            }
        }
    }
    // </editor-fold>   
    // <editor-fold defaultstate="collapsed" desc="Misc Area">
    private String errorReport = "";
    private int edgesRemoved = 0, failedActivationsPerIteration = 0;

    public String getReport() {
        // debug
        if (1 == 1) {
            return getCopyPasteReport();
        }

        String report = "<HTML> <BODY> <h1>Edge weight evolution</h1> "
                + "<hr><br>";

        report += "Iterations: " + maxIterations + "<br>";
        report += "Failed iterations to activate any edge: " + failedActivationsPerIteration + "<br>";
        report += "Successful iterations to activate any edge: " + (maxIterations - failedActivationsPerIteration) + "<br>";

        report += errorReport + "</BODY></HTML>";

        return report;
    }

    public String getCopyPasteReport() {
        String report = "<HTML> <BODY>" + repairRatio + "\n";
        report += errorReport + "</BODY></HTML>";

        return report.trim();
    }

    private class ExtraEdgeData {

        Double fitness;

        ExtraEdgeData(Double fitness) {
            this.fitness = fitness;
        }
    }

    public boolean cancel() {
        this.isCanceled = true;
        return true;
    }

    private boolean isCanceled() {
        return isCanceled;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;


    }

    public enum ATTACK_TYPE {

        RANDOM, DEGREE, BETWEENNESS, EIGENVECTOR, CLUSTERING, PAGERANK, HITS
    }

    public enum REPAIR_TYPE {

        RANDOM, HIGHEST_DEGREE_FIRST, LOWEST_DEGREE_FIRST, NONE
    }
// </editor-fold>
}
