package org.gephi.statistics.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalDirectedGraph;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;

/**
 * Implements other advanced influence ranking methods: Hirsch-Index (h-index)
 * Hirsch index: ClusterRank: "Identifying Influential Nodes in Large-Scale
 * Directed Networks: The Role of Clustering", Chen 2013 LeaderRank: "Leaders in
 * Social Networks, the Delicious Case", Lu 2011 C
 *
 * @author Alexander
 */
public class InfluenceRankings implements Statistics, LongTask {

    public static final String HINDEX = "HIndex";
    public static final String CLUSTERRANK = "ClusterRank";
    public static final String LEADERRANK = "LeaderRank";
    public static final String COMMUNITYLEADERRANK = "CommunityLeaderRank";
    public static final String LOCALCENTRALITY = "LocalCentrality";
    public static final String EDGECENTRALITY = "EdgeCentrality";
    public static final String KSHELL = "KShell";
    /**
     *
     */
    private ProgressTicket progress;
    /**
     *
     */
    private boolean isCanceled;
    /**
     * Metric that is currently selected
     */
    private InfluenceMetricEnum metric = InfluenceMetricEnum.EDGECENTRALITY;
    private boolean useEdgeWeight = false;
    /**
     *
     */
    private boolean isDirected;

    public InfluenceRankings() {
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        if (graphController != null && graphController.getModel() != null) {
            isDirected = graphController.getModel().isDirected();
        }
    }

    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        HierarchicalGraph graph;
        if (isDirected) {
            graph = graphModel.getHierarchicalDirectedGraphVisible();
        } else {
            graph = graphModel.getHierarchicalUndirectedGraphVisible();
        }
        execute(graph, attributeModel);
    }

    public void execute(HierarchicalGraph hgraph, AttributeModel attributeModel) {
        isCanceled = false;

        //hgraph.readLock();

        switch (metric) {
            case HINDEX:
                runHirsch(hgraph, attributeModel);
                break;
            case CLUSTERRANK:
                runClusterRank(hgraph, attributeModel);
                break;
            case LEADERRANK:
                runLeaderRank(hgraph, attributeModel);
                break;
            case LOCALCENTRALITY:
                runLocalCentrality(hgraph, attributeModel);
                break;
            case EDGECENTRALITY:
                runEdgeCentrality(hgraph, attributeModel);
                break;
            case COMMUNITYLEADERRANK:
                runCommunityLeaderRank(hgraph, attributeModel);
                break;
            case KSHELL:
                runKShellDecomposition(hgraph, attributeModel);
                break;
            default:
                break;
        }

        hgraph.readUnlockAll();
    }

    private void runHirsch(HierarchicalGraph hgraph, AttributeModel attributeModel) {

        Progress.start(progress);
        int N = hgraph.getNodeCount();
        int hIndex = 0;

        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn hIndexCol = nodeTable.getColumn(HINDEX);
        if (hIndexCol == null) {
            hIndexCol = nodeTable.addColumn(HINDEX, HINDEX, AttributeType.INT, AttributeOrigin.COMPUTED, new Integer(0));
        }

        // for each node in the graph
        for (Node node : hgraph.getNodes()) {
            List<Integer> degrees = new ArrayList<Integer>();

            // get neighbour degrees
            for (Node neighbour : hgraph.getNeighbors(node)) {
                degrees.add(hgraph.getDegree(neighbour));
            }

            // sort neighbour degrees in >descending< order
            Collections.sort(degrees, new Comparator<Integer>() {
                public int compare(Integer o1, Integer o2) {
                    return -o1.compareTo(o2);
                }
            });

            // iterate through sorted list until (index+1)>list[index], return index as h-index
            hIndex = degrees.size(); // default value
            for (int i = 0; i < degrees.size(); ++i) {
                if (i + 1 > degrees.get(i)) {
                    hIndex = i;
                    break;
                }
            }

            // save value to attributes
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            row.setValue(hIndexCol, hIndex);
        }
    }

    private void runClusterRank(HierarchicalGraph hgraph, AttributeModel attributeModel) {

        Progress.start(progress);

        // run clusterign coefficient
        ClusteringCoefficient ccTask = new ClusteringCoefficient();
        ccTask.setDirected(isDirected);
        ccTask.setProgressTicket(progress);
        ccTask.execute(hgraph, attributeModel);
        ccTask.getAverageClusteringCoefficient();

        int sumNeighbourDegree = 0;
        double clusterRank = 0.0;

        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn clusterRankCol = nodeTable.getColumn(CLUSTERRANK);
        if (clusterRankCol == null) {
            clusterRankCol = nodeTable.addColumn(CLUSTERRANK, CLUSTERRANK, AttributeType.DOUBLE, AttributeOrigin.COMPUTED, new Double(0.0));
        }

        // for each node in the graph        
        for (Node node : hgraph.getNodes()) {
            // get neighbour degrees
            for (Node neighbour : hgraph.getNeighbors(node)) {
                sumNeighbourDegree += hgraph.getDegree(neighbour) + 1;
            }

            // multiply sum by cc(i)
            Object cc = node.getAttributes().getValue(ClusteringCoefficient.CLUSTERING_COEFF);
            if (cc instanceof Float) {
                clusterRank = Math.pow(10, -1.0 * (Float) cc);
            } else if (cc instanceof Double) {
                clusterRank = Math.pow(10, -1.0 * (Double) cc);
            } else if (cc instanceof Integer) {
                clusterRank = Math.pow(10, -1.0 * (Integer) cc);
            } else {
                clusterRank = -1;
            }

            clusterRank *= sumNeighbourDegree;

            // save value to attributes
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            row.setValue(clusterRankCol, clusterRank);
        }
    }

    private void runLeaderRank(HierarchicalGraph hgraph, AttributeModel attributeModel) {
        int N = hgraph.getNodeCount();
        Map<Node, Double> leaderRanks = new HashMap<Node, Double>();

        Progress.start(progress);
        double[] weights = null;
        if (useEdgeWeight) {
            weights = new double[N];
        }

        // create ground node (but don't add to graph)
        Node groundNode = hgraph.getGraphModel().factory().newNode();
        // initialize ground node
        //groundNode.getNodeData().setSize(5f);
        //groundNode.getNodeData().setLabel("Ground node");
        //hgraph.getGraphModel().getGraph().addNode(groundNode);

        // create LR column table
        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn leaderRanksCol = nodeTable.getColumn(LEADERRANK);
        if (leaderRanksCol == null) {
            leaderRanksCol = nodeTable.addColumn(LEADERRANK, LEADERRANK, AttributeType.DOUBLE, AttributeOrigin.COMPUTED, new Double(0));
        }

        // 1: init all nodes with score=1; ground=0
        for (Node node : hgraph.getNodes()) {
            setAttribute(node, leaderRanksCol, 1.0);
        }
        setAttribute(groundNode, leaderRanksCol, 0.0);
        int count = 1000;
        boolean done;

        // 2: iterate by redistributing the score evenly to all neighbours, i.e. give my score / number of neighbors to everone around
        while (count-- > 0) {
            done = true;

            // copy leaderranks to temp map            
            for (Node node : hgraph.getNodes()) {
                leaderRanks.put(node, (Double) getAttribute(node, LEADERRANK));
            }
            leaderRanks.put(groundNode, (Double) getAttribute(groundNode, LEADERRANK));

            // repeat for whole graph (except ground node)
            for (Node node : hgraph.getNodes()) {
                // current node's original (t-1) leaderrank
                double leaderRank = (Double) getAttribute(node, LEADERRANK);

                // quanta to share; +1 comes from ground node; else case is implicit /=1;
                if (isDirected) {
                    if (((HierarchicalDirectedGraph) hgraph).getTotalOutDegree(node) > 0) {
                        leaderRank /= (((HierarchicalDirectedGraph) hgraph).getTotalOutDegree(node) + 1);
                    }
                } else {
                    if (hgraph.getTotalDegree(node) > 0) {
                        leaderRank /= (hgraph.getTotalDegree(node) + 1);
                    }
                }

                // add quanta to neighbors
                for (Node neighbour : hgraph.getNeighbors(node)) {
                    // get neighbour's updated leaderrank
                    double neighbourLR = leaderRanks.get(neighbour);
                    // update neighbour's leaderrank in temp
                    leaderRanks.put(neighbour, neighbourLR + leaderRank);
                }

                // give quanta to ground node as well; temp map
                double groundLR = leaderRanks.get(groundNode);
                leaderRanks.put(groundNode, groundLR + leaderRank);
            }

            // make ground node also share his original swag (t-1) with everyone
            double groundLR = (Double) getAttribute(groundNode, LEADERRANK);
            groundLR /= N;

            for (Node node : hgraph.getNodes()) {
                // work with temp map
                double nodeLR = leaderRanks.get(node);
                leaderRanks.put(node, nodeLR + groundLR);
            }

            // check convergence/ stop constition
            for (Node node : hgraph.getNodes()) {
                if ((leaderRanks.get(node) - 2 * (Double) getAttribute(node, LEADERRANK)) / (Double) getAttribute(node, LEADERRANK) >= 0.01) {
                    done = false;
                }
            }

            // update leaderanks of all nodes; temp -> original (t)
            // !!! subtract original score from current one!
            for (Node node : hgraph.getNodes()) {
                setAttribute(node, leaderRanksCol, leaderRanks.get(node) - (Double) getAttribute(node, LEADERRANK));
            }
            // !!! subtract original score from ground node!
            setAttribute(groundNode, leaderRanksCol, leaderRanks.get(groundNode) - (Double) getAttribute(groundNode, LEADERRANK));

            if (done || isCanceled) {
                hgraph.readUnlockAll();
                break;
            }
        }

        // at the end, evenly distribute ground node's LR to everyone else
        double remainingLR = (Double) getAttribute(groundNode, LEADERRANK) / N;
        for (Node node : hgraph.getNodes()) {
            setAttribute(node, leaderRanksCol, (Double) getAttribute(node, LEADERRANK) + remainingLR);
        }
    }

    private void runCommunityLeaderRank(HierarchicalGraph hgraph, AttributeModel attributeModel) {
        int N = hgraph.getNodeCount();
        Map<Node, Double> leaderRanks = new HashMap<Node, Double>();

        Progress.start(progress);

        // create ground node (but don't add to graph)
        Node groundNode = hgraph.getGraphModel().factory().newNode();
        // initialize ground node
        //groundNode.getNodeData().setSize(5f);
        //groundNode.getNodeData().setLabel("Ground node");
        //hgraph.getGraphModel().getGraph().addNode(groundNode);

        // create LR column table
        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn communityRanksCol = nodeTable.getColumn(COMMUNITYLEADERRANK);
        if (communityRanksCol == null) {
            communityRanksCol = nodeTable.addColumn(COMMUNITYLEADERRANK, COMMUNITYLEADERRANK, AttributeType.DOUBLE, AttributeOrigin.COMPUTED, new Double(0));
        }

        // 1: init all nodes with score=1; ground=0
        for (Node node : hgraph.getNodes()) {
            setAttribute(node, communityRanksCol, 1.0);
        }
        setAttribute(groundNode, communityRanksCol, 0.0);
        int count = 1000;
        boolean done;

        // 2: iterate by redistributing the score evenly to all neighbours, i.e. give my score / number of neighbors to everone around
        while (count-- > 0) {
            done = true;

            // copy leaderranks to temp map            
            for (Node node : hgraph.getNodes()) {
                leaderRanks.put(node, (Double) getAttribute(node, COMMUNITYLEADERRANK));
            }
            leaderRanks.put(groundNode, (Double) getAttribute(groundNode, COMMUNITYLEADERRANK));

            // repeat for whole graph (except ground node)
            for (Node node : hgraph.getNodes()) {
                // current node's original (t-1) leaderrank
                double leaderRank = (Double) getAttribute(node, COMMUNITYLEADERRANK);

                // quanta to share; +1 comes from ground node; else case is implicit /=1;
                if (isDirected) {
                    if (((HierarchicalDirectedGraph) hgraph).getTotalOutDegree(node) > 0) {
                        leaderRank /= (((HierarchicalDirectedGraph) hgraph).getTotalOutDegree(node) + 1);
                    }
                } else {
                    if (hgraph.getTotalDegree(node) > 0) {
                        leaderRank /= (hgraph.getTotalDegree(node) + 1);
                    }
                }

                // add quanta to neighbors
                for (Node neighbour : hgraph.getNeighbors(node)) {
                    // get neighbour's updated leaderrank
                    double neighbourLR = leaderRanks.get(neighbour);
                    // update neighbour's leaderrank in temp
                    leaderRanks.put(neighbour, neighbourLR + leaderRank);
                }

                // give quanta to ground node as well; temp map
                double groundLR = leaderRanks.get(groundNode);
                leaderRanks.put(groundNode, groundLR + leaderRank);
            }

            // make ground node also share his original swag (t-1) with everyone
            double groundLR = (Double) getAttribute(groundNode, COMMUNITYLEADERRANK);
            groundLR /= N;

            for (Node node : hgraph.getNodes()) {
                // work with temp map
                double nodeLR = leaderRanks.get(node);
                leaderRanks.put(node, nodeLR + groundLR);
            }

            // check convergence/ stop constition
            for (Node node : hgraph.getNodes()) {
                if ((leaderRanks.get(node) - 2 * (Double) getAttribute(node, COMMUNITYLEADERRANK)) / (Double) getAttribute(node, COMMUNITYLEADERRANK) >= 0.01) {
                    done = false;
                }
            }

            // update leaderanks of all nodes; temp -> original (t)
            // !!! subtract original score from current one!
            for (Node node : hgraph.getNodes()) {
                setAttribute(node, communityRanksCol, leaderRanks.get(node) - (Double) getAttribute(node, COMMUNITYLEADERRANK));
            }
            // !!! subtract original score from ground node!
            setAttribute(groundNode, communityRanksCol, leaderRanks.get(groundNode) - (Double) getAttribute(groundNode, COMMUNITYLEADERRANK));

            if (done || isCanceled) {
                hgraph.readUnlockAll();
                break;
            }
        }

        // at the end, evenly distribute ground node's LR to everyone else
        double remainingLR = (Double) getAttribute(groundNode, COMMUNITYLEADERRANK) / N;
        for (Node node : hgraph.getNodes()) {
            setAttribute(node, communityRanksCol, (Double) getAttribute(node, COMMUNITYLEADERRANK) + remainingLR);
        }

        //////////////////////////
        // Community normalization
        //////////////////////////

        // run modularity algorithm
        Modularity modularityAlgo = new Modularity();
        modularityAlgo.setResolution(1.0);
        modularityAlgo.setRandom(true);
        modularityAlgo.setUseWeight(true);
        modularityAlgo.setProgressTicket(progress);
        modularityAlgo.execute(hgraph.getGraphModel(), attributeModel);
        List<Modularity.Community> communities = modularityAlgo.getCommunities();

        // measure community sizes
        int communitySize[] = new int[communities.size()];
        for (int i = 0; i < communitySize.length; ++i) {
            communitySize[i] = 0;
        }

        for (Node node : hgraph.getNodes()) {
            communitySize[(Integer) getAttribute(node, Modularity.MODULARITY_CLASS)]++;
        }

        // apply community sizes normalization
        Integer comId;
        Double nodeLR;
        for (Node node : hgraph.getNodes()) {
            // retrieve community of node
            comId = (Integer) getAttribute(node, Modularity.MODULARITY_CLASS);
            // retrieve LR of node
            nodeLR = (Double) getAttribute(node, COMMUNITYLEADERRANK);
            // normalize
            nodeLR /= communitySize[comId];
            // save updated leaderrank
            setAttribute(node, communityRanksCol, nodeLR);
        }
    }

    private void runLocalCentrality(HierarchicalGraph hgraph, AttributeModel attributeModel) {
        int N = hgraph.getNodeCount();
        // set N of vecinity of distance <=2 for each node
        Map<Node, Integer> nearestNextNearest = new HashMap<Node, Integer>();

        Progress.start(progress);

        // create LR column table
        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn localCentralityCol = nodeTable.getColumn(LOCALCENTRALITY);
        if (localCentralityCol == null) {
            localCentralityCol = nodeTable.addColumn(LOCALCENTRALITY, LOCALCENTRALITY, AttributeType.INT, AttributeOrigin.COMPUTED, new Integer(0));
        }

        // 1: compute N for all nodes (ego-network of distance 2), recursively
        Collection<Node> visited = new HashSet<Node>();
        for (Node node : hgraph.getNodes()) {
            // visit ego-network of distance 1
            for (Node nearest : hgraph.getNeighbors(node)) {
                visited.add(nearest);
                // visit ego-network of distance 2; keep nodes once only - automatically done by the sorted set
                for (Node nextNearest : hgraph.getNeighbors(nearest)) {
                    visited.add(nextNearest);
                }
            }

            // just ot be safe, always
            if (visited.contains(node)) {
                visited.remove(node);
            }

            // save set size
            nearestNextNearest.put(node, visited.size());
            visited.clear(); // important!
        }

        // 2: sum up N's to get Q for each node        
        Map<Node, Integer> qSet = new HashMap<Node, Integer>();

        for (Node node : hgraph.getNodes()) {
            int sum = 0;
            // visit ego-network and add up N's
            for (Node neighbour : hgraph.getNeighbors(node)) {
                sum += nearestNextNearest.get(neighbour);
            }
            // save as Q
            qSet.put(node, sum);
        }

        // 3: sum up q's to get CL for each node        
        Map<Node, Integer> clSet = new HashMap<Node, Integer>();

        for (Node node : hgraph.getNodes()) {
            int sum = 0;
            // visit ego-network and add up Q's
            for (Node neighbour : hgraph.getNeighbors(node)) {
                sum += qSet.get(neighbour);
            }
            // save as CL
            clSet.put(node, sum);
        }

        // 4 (optional): with a coefficient to obtain LCL instead of LC
        Map<Node, Double> lclSet = new HashMap<Node, Double>();

        // run clustering coefficient
        ClusteringCoefficient ccTask = new ClusteringCoefficient();
        ccTask.setDirected(isDirected);
        ccTask.setProgressTicket(progress);
        ccTask.execute(hgraph, attributeModel);
        ccTask.getAverageClusteringCoefficient();

        // multiply LC by cc(i)
        for (Node node : hgraph.getNodes()) {
            Object cc = getAttribute(node, ClusteringCoefficient.CLUSTERING_COEFF);
            if (cc instanceof Float) {
                lclSet.put(node, Math.pow(Math.E, -1.0 * (Float) cc) * clSet.get(node));
            } else if (cc instanceof Double) {
                lclSet.put(node, Math.pow(Math.E, -1.0 * (Double) cc) * clSet.get(node));
            } else if (cc instanceof Integer) {
                lclSet.put(node, Math.pow(Math.E, -1.0 * (Integer) cc) * clSet.get(node));
            } else {
                lclSet.put(node, -1.0);
            }
        }

        // save local centrality metric to nodes
        for (Node node : hgraph.getNodes()) {
            setAttribute(node, localCentralityCol, lclSet.get(node));
        }
    }

    private void runEdgeCentrality(HierarchicalGraph hgraph, AttributeModel attributeModel) {

        Progress.start(progress);

        double edgeCentrality = 0.0;
        float x1, y1, x2, y2, weight = 0;
        double distance;

        // create EC column table
        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn edgeCentralityCol = nodeTable.getColumn(EDGECENTRALITY);
        if (edgeCentralityCol == null) {
            edgeCentralityCol = nodeTable.addColumn(EDGECENTRALITY, EDGECENTRALITY, AttributeType.DOUBLE, AttributeOrigin.COMPUTED, new Double(0.0));
        }

        // for each node in the graph        
        for (Node node : hgraph.getNodes()) {
            // get (x,y) of current node
            x1 = node.getNodeData().x();
            y1 = node.getNodeData().y();
            edgeCentrality = 0.0;

            // get (weighted) distances to neighbours
            for (Node neighbour : hgraph.getNeighbors(node)) {
                x2 = neighbour.getNodeData().x();
                y2 = neighbour.getNodeData().y();
                if (hgraph.getEdge(node, neighbour) != null) {
                    weight = hgraph.getEdge(node, neighbour).getWeight();
                }

                // compute euclidean distance
                distance = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

                if (weight > 0) {
                    distance *= weight;
                }

                edgeCentrality += distance;
            }

            // save value to attributes
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            row.setValue(edgeCentralityCol, edgeCentrality);
        }
    }

    private void runKShellDecomposition(HierarchicalGraph hgraph, AttributeModel attributeModel) {

        Progress.start(progress);

        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn kShellCol = nodeTable.getColumn(KSHELL);
        if (kShellCol == null) {
            kShellCol = nodeTable.addColumn(KSHELL, KSHELL, AttributeType.INT, AttributeOrigin.COMPUTED, new Integer(0));
        }

        // make a copy of the graph
        SimpleGraph sGraph = new SimpleGraph(hgraph);
        List<SimpleNode> toRemove = new ArrayList<SimpleNode>();

        int currentShell = 0;
        int count = 0;

        // repeat while graph is not empty
        while (!sGraph.isEmpty() && currentShell < hgraph.getNodeCount()) {
            // repeat removing nodes on current shell until there are no nodes left            
            do {
                count = 0;
                for (SimpleNode node : sGraph.getNodes()) {
                    if (node.getDegree() == currentShell) {
                        node.shell = currentShell;
                        Node hNode = hgraph.getNode(node.getId());
                        AttributeRow row = (AttributeRow) hNode.getNodeData().getAttributes();
                        row.setValue(kShellCol, currentShell);
                        toRemove.add(node);
                        count++;
                    }
                }
                for (SimpleNode node : toRemove) {
                    sGraph.removeNode(node);
                }
                toRemove.clear();
            } while (count > 0);
            currentShell++;
        }
    }

    /**
     *
     * @return
     */
    public String getReport() {
        //distribution of values
        /*Map<Double, Integer> dist = new HashMap<Double, Integer>();
         for (int i = 0; i < leaderRanks.length; i++) {
         Double d = leaderRanks[i];
         if (dist.containsKey(d)) {
         Integer v = dist.get(d);
         dist.put(d, v + 1);
         } else {
         dist.put(d, 1);
         }
         }

         //Distribution series
         XYSeries dSeries = ChartUtils.createXYSeries(dist, "PageRanks");

         XYSeriesCollection dataset = new XYSeriesCollection();
         dataset.addSeries(dSeries);

         JFreeChart chart = ChartFactory.createXYLineChart(
         "PageRank Distribution",
         "Score",
         "Count",
         dataset,
         PlotOrientation.VERTICAL,
         true,
         false,
         false);
         chart.removeLegend();
         ChartUtils.decorateChart(chart);
         ChartUtils.scaleChart(chart, dSeries, true);
         String imageFile = ChartUtils.renderChart(chart, "pageranks.png");

         String report = "<HTML> <BODY> <h1>PageRank Report </h1> "
         + "<hr> <br />"
         + "<h2> Parameters: </h2>"
         + "<br> <h2> Results: </h2>"
         + imageFile
         + "<br /><br />" + "<h2> Algorithm: </h2>"
         + "Sergey Brin, Lawrence Page, <i>The Anatomy of a Large-Scale Hypertextual Web Search Engine</i>, in Proceedings of the seventh International Conference on the World Wide Web (WWW1998):107-117<br />"
         + "</BODY> </HTML>";

         return report;*/

        return "success";

    }

    /**
     *
     * @return
     */
    public boolean cancel() {
        isCanceled = true;
        return true;
    }

    /**
     *
     * @param progressTicket
     */
    public void setProgressTicket(ProgressTicket progressTicket) {
        progress = progressTicket;
    }

    public boolean isUseEdgeWeight() {
        return useEdgeWeight;
    }

    public void setUseEdgeWeight(boolean useEdgeWeight) {
        this.useEdgeWeight = useEdgeWeight;
    }

    public void setDirected(boolean isDirected) {
        this.isDirected = isDirected;
    }

    public void setSelectedMetric(InfluenceMetricEnum metric) {
        this.metric = metric;
    }

    public boolean getDirected() {
        return isDirected;
    }

    private void setAttribute(Node node, AttributeColumn column, Object value) {
        ((AttributeRow) node.getNodeData().getAttributes()).setValue(column, value);
    }

    private Object getAttribute(Node node, String key) {
        return node.getAttributes().getValue(key);
    }

    public InfluenceMetricEnum getSelectedMetric() {
        return metric;
    }

    class SimpleGraph {

        private HashMap<Integer, SimpleNode> nodes;
        private HierarchicalGraph hGraph;

        public SimpleGraph(HierarchicalGraph hGraph) {
            this.hGraph = hGraph;
            nodes = new HashMap<Integer, SimpleNode>();

            // duplicate nodes by 'id'
            for (Node node : hGraph.getNodes()) {
                nodes.put(node.getId(), new SimpleNode(node.getId()));
            }

            // duplicate edges by adjacent nodes' ids            
            SimpleNode n1, n2;
            for (Edge edge : hGraph.getEdges()) {
                int sourceId = edge.getSource().getId();
                int targetId = edge.getTarget().getId();

                n1 = getSimpleNodeById(sourceId);
                n2 = getSimpleNodeById(targetId);

                n1.addNeighbour(n2);
            }
        }

        public List<SimpleNode> getNodes() {
            return new ArrayList<SimpleNode>(nodes.values());
        }

        public SimpleNode getSimpleNodeById(int id) {
            return nodes.get(id);
        }

        public boolean isEmpty() {
            return nodes.isEmpty();
        }

        public void removeNode(SimpleNode node) {
            // remove node's neighbours
            for (SimpleNode neighbour : node.getNeighbours().toArray(new SimpleNode[]{})) {
                node.removeNeighbour(neighbour);
            }

            // remove from hash map
            int _key = -1;
            for (Integer key : nodes.keySet()) {
                if (nodes.get(key).equals(node)) {
                    _key = key;
                    break;
                }
            }

            // node was found in hash map
            if (_key != -1) {
                nodes.remove(_key);
            }
        }
    }

    class SimpleNode {

        private int id;
        public int shell;
        private List<SimpleNode> neighbours;

        public SimpleNode(int id) {
            this.id = id;
            neighbours = new ArrayList<SimpleNode>();
        }

        public int getId() {
            return id;
        }

        public List<SimpleNode> getNeighbours() {
            return neighbours;
        }

        public void addNeighbour(SimpleNode neighbour) {
            if (!neighbours.contains(neighbour)) {
                neighbours.add(neighbour);
                neighbour.addNeighbour(this);
            }
        }

        public void removeNeighbour(SimpleNode node) {
            if (neighbours.contains(node)) {
                neighbours.remove(node);
                node.removeNeighbour(this);
            }
        }

        public int getDegree() {
            return neighbours.size();
        }
    }
}
